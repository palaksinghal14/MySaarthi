package com.palaksinghal.mysaarthi.data.repository

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
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation

class NearbyRepositoryImpl @Inject constructor(
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
            val radiusMeters = radiusKm * 1000

            // GeoFireUtils gives us the geohash ranges to query
            val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusMeters)

            // Run one Firestore query per geohash range, since Firestore
            // can't do a single "within radius" query directly
            val allDocs = bounds.map { bound ->
                firestore.collection("users")
                    .whereEqualTo("openToSatsang", true)
                    .orderBy("geohash")
                    .startAt(bound.startHash)
                    .endAt(bound.endHash)
                    .get()
                    .await()
            }

            val seekers = mutableListOf<NearbySeeker>()

            allDocs.forEach { querySnapshot ->
                querySnapshot.documents.forEach { doc ->
                    val uid = doc.id

                    // Skip yourself
                    if (uid == currentUid) return@forEach

                    val isOpenToSatsang = doc.getBoolean("openToSatsang") ?: false
                    if (!isOpenToSatsang) return@forEach

                    val lat = doc.getDouble("lat") ?: return@forEach
                    val lng = doc.getDouble("lng") ?: return@forEach

                    // Double-check actual distance — geohash query gives an
                    // approximate box, not a precise circle, so filter here
                    val docLocation = GeoLocation(lat, lng)
                    val distanceKm = GeoFireUtils.getDistanceBetween(center, docLocation) / 1000.0

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

            Result.success(seekers.sortedBy { it.distanceKm })
        } catch (e: Exception) {
            Result.failure(e.toAppException())
        }
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
            Result.failure(e.toAppException())

        }
    }
}