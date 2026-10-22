package com.palaksinghal.mysaarthi.data.repository

import android.content.Context
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.palaksinghal.mysaarthi.core.utils.toAppException
import com.palaksinghal.mysaarthi.domain.model.AppException
import com.palaksinghal.mysaarthi.domain.model.NearbySeeker
import com.palaksinghal.mysaarthi.domain.model.NearbyTemple
import com.palaksinghal.mysaarthi.domain.repository.AuthenticationRepo
import com.palaksinghal.mysaarthi.domain.repository.LocationRepository
import com.palaksinghal.mysaarthi.domain.repository.NearbyRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// Rungs we widen to when the user's own area has too few seekers:
// roughly city → region → state → country.
private val FALLBACK_RADII_KM = listOf(50.0, 200.0, 1000.0, 3000.0)

// Stop widening once we have at least this many.
private const val MIN_SEEKER_RESULTS = 10

// Cap docs read per geohash range so a nationwide query can't become
// a full-collection scan as the user base grows.
private const val MAX_DOCS_PER_RANGE = 20L

class NearbyRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val placesClient: PlacesClient,
    private val firestore: FirebaseFirestore,
    private val locationRepository: LocationRepository,
    private val authRepo: AuthenticationRepo
) : NearbyRepository{

    override suspend fun getNearbySeekers(radiusKm: Double): Result<List<NearbySeeker>> {
        return try {
            val currentUid = authRepo.getCurrentUserId()
                ?: return Result.failure(AppException.UserNotFoundException)

            val myLocation = locationRepository.getUserLocation().getOrElse { throwable ->
                return Result.failure(throwable)
            }

            val center = GeoLocation(myLocation.lat, myLocation.lng)
            // Start at the caller's radius, widen only while we're short on results.
            val radii = listOf(radiusKm) + FALLBACK_RADII_KM.filter { it > radiusKm }

            var seekers: List<NearbySeeker> = emptyList()

            for (radius in radii) {
                seekers = querySeekersWithin(center, currentUid, radius)
                if (seekers.size >= MIN_SEEKER_RESULTS) break
            }


            Result.success(seekers.sortedBy { it.distanceKm })
        } catch (e: Exception) {
            Result.failure(e.toAppException(context))
        }
    }

    private suspend fun querySeekersWithin(
        center: GeoLocation,
        currentUid: String,
        radiusKm: Double
    ): List<NearbySeeker> =coroutineScope {
        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusKm * 1000)

        // Fire all geohash ranges in parallel instead of one after another
        val allDocs = bounds.map { bound ->
            async {
                firestore.collection("users")
                    .whereEqualTo("openToSatsang", true)
                    .orderBy("geohash")
                    .startAt(bound.startHash)
                    .endAt(bound.endHash)
                    .limit(MAX_DOCS_PER_RANGE)
                    .get()
                    .await()
            }
        }.awaitAll()

        val seekers = mutableListOf<NearbySeeker>()

        allDocs.forEach { querySnapshot ->
            querySnapshot.documents.forEach { doc ->
                val uid = doc.id
                if (uid == currentUid) return@forEach

                val isOpenToSatsang = doc.getBoolean("openToSatsang") ?: false
                if (!isOpenToSatsang) return@forEach

                val lat = doc.getDouble("lat") ?: return@forEach
                val lng = doc.getDouble("lng") ?: return@forEach

                // Geohash bounds are a box, not a circle — filter precisely here
                val distanceKm =
                    GeoFireUtils.getDistanceBetween(center, GeoLocation(lat, lng)) / 1000.0
                if (distanceKm > radiusKm) return@forEach

                @Suppress("UNCHECKED_CAST")
                val practices = doc.get("practices") as? List<String> ?: emptyList()

                seekers.add(
                    NearbySeeker(
                        uid = uid,
                        displayName = doc.getString("displayName") ?: "",
                        practices = practices,
                        howLongOnPath = doc.getString("howLongOnPath") ?: "",
                        spiritualIntro = doc.getString("spiritualIntro") ?: "",
                        lat = lat,
                        lng = lng,
                        distanceKm = distanceKm
                    )
                )
            }
        }

        seekers
    }
    override suspend fun getNearbyTemples(radiusKm: Double): Result<List<NearbyTemple>> {

        return try {
            val myLocation = locationRepository.getUserLocation().getOrElse { throwable ->
                return Result.failure(throwable)
            }

            val center = GeoLocation(myLocation.lat, myLocation.lng)
            val circle = CircularBounds.newInstance(LatLng(myLocation.lat, myLocation.lng), radiusKm * 1000)

            val placesField= listOf(
                Place.Field.ID,
                Place.Field.DISPLAY_NAME,
                Place.Field.FORMATTED_ADDRESS,
                Place.Field.LOCATION
            )

            val request = SearchNearbyRequest.builder(circle,placesField)
                .setIncludedTypes(listOf("hindu_temple"))
                .setMaxResultCount(20)
                .setRankPreference(SearchNearbyRequest.RankPreference.DISTANCE)
                .build()

            val response=placesClient.searchNearby(request).await()

            val temples= response.places.map{place ->

                val placeLat = place.location?.latitude ?: 0.0
                val placeLng = place.location?.longitude ?: 0.0
                val placeLocation = GeoLocation(placeLat, placeLng)
                val distanceKm = GeoFireUtils.getDistanceBetween(center, placeLocation) / 1000.0

                NearbyTemple(
                    placeId = place.id ?: "",
                    name = place.displayName ?: "Unknown place",
                    address = place.formattedAddress ?: "",
                    lat = place.location?.latitude ?: 0.0,
                    lng = place.location?.longitude ?: 0.0,
                    distanceKm =  distanceKm
                )
            }

            Result.success(temples)

        }catch (e: Exception) {
            android.util.Log.e("NearbyTemples", "Raw exception: ${e.javaClass.simpleName}")
            android.util.Log.e("NearbyTemples", "Message: ${e.message}")
            if (e is com.google.android.gms.common.api.ApiException) {
                android.util.Log.e("NearbyTemples", "Status code: ${e.statusCode}")
                android.util.Log.e("NearbyTemples", "Status message: ${e.status.statusMessage}")
            }
            Result.failure(e.toAppException(context))

        }
    }
}