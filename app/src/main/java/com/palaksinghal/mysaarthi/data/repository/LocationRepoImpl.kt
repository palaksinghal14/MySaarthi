package com.palaksinghal.mysaarthi.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.palaksinghal.mysaarthi.core.utils.toAppException
import com.palaksinghal.mysaarthi.data.local.dao.UserProfileDao
import com.palaksinghal.mysaarthi.domain.model.AppException
import com.palaksinghal.mysaarthi.domain.model.Location
import com.palaksinghal.mysaarthi.domain.repository.AuthenticationRepo
import com.palaksinghal.mysaarthi.domain.repository.LocationRepository
import com.palaksinghal.mysaarthi.domain.repository.UserProfileRepo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LocationRepoImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fusedLocationProviderClient : FusedLocationProviderClient,
    private val auth: AuthenticationRepo,
    private val userProfileRepo: UserProfileRepo
): LocationRepository {

    override suspend fun getUserLocation(): Result<Location> {
           return try {
               val hasPermission = ContextCompat.checkSelfPermission(
                   context,Manifest.permission.ACCESS_COARSE_LOCATION
               )== PackageManager.PERMISSION_GRANTED

               if(!hasPermission){
                   return Result.failure(AppException.LocationPermissionDeniedException)
               }

               val request= CurrentLocationRequest.Builder()
                   .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
                   .build()

               val androidLocation=fusedLocationProviderClient.getCurrentLocation(request,null).await()

               if(androidLocation==null){
                  return Result.failure(AppException.UnknownException("Could not determine location"))
               }

               Result.success( Location(androidLocation.latitude,androidLocation.longitude))

           }catch (e: Exception){
               Result.failure(e.toAppException())
           }
    }

    override suspend fun updateUserLocation(): Result<Unit> {

        return try{
            Log.d("LocationUpdate", "Step 1: getting uid")
            val uid=auth.getCurrentUserId() ?:  run {
                Log.d("LocationUpdate", "FAILED: uid is null")
                return Result.failure(AppException.UserNotFoundException)
            }
            Log.d("LocationUpdate", "Step 1 OK: uid = $uid")

            Log.d("LocationUpdate", "Step 2: fetching profile")
            val profile =userProfileRepo.getUserProfile(uid).getOrNull() ?: run {
                Log.d("LocationUpdate", "FAILED: profile is null")
                return Result.failure(AppException.UserNotFoundException)
            }

            Log.d("LocationUpdate", "Step 2 OK: isOpenToSatsang = ${profile.isOpenToSatsang}")
            //skip entirely if user hasn't opted into satsang
            if (!profile.isOpenToSatsang) {
                android.util.Log.d("LocationUpdate", "STOPPED: isOpenToSatsang is false")
                return Result.failure(AppException.UnknownException("Satsang request not enabled"))
            }
            Log.d("LocationUpdate", "Step 3: fetching device location")
            val location=getUserLocation().getOrElse { throwable ->
                Log.e("LocationUpdate", "FAILED: getUserLocation error: ${throwable.message}", throwable)
                return Result.failure(throwable)
            }
            Log.d("LocationUpdate", "Step 3 OK: lat=${location.lat}, lng=${location.lng}")
            val geoLocation = GeoLocation(location.lat,location.lng)

            val fullGeoHash = GeoFireUtils.getGeoHashForLocation(geoLocation)
            val truncatedGeoHash = fullGeoHash.take(6)  // ~1.2km precision — good privacy balance

            Log.d("LocationUpdate", "Step 4: computed geohash = $fullGeoHash")
            val updatedProfile= profile.copy(
                geohash = truncatedGeoHash,
                lat= location.lat,
                lng = location.lng,
                lastLocationUpdate = System.currentTimeMillis()
            )
            Log.d("LocationUpdate", "Step 5: saving to Firestore + Room")
            val saveResult = userProfileRepo.saveUserProfile(updatedProfile)
            saveResult
                .onSuccess { Log.d("LocationUpdate", "Step 5 OK: save succeeded") }
                .onFailure { Log.e("LocationUpdate", "Step 5 FAILED: ${it.message}", it) }

            saveResult

        }catch (e: Exception){
            Log.e("LocationUpdate", "EXCEPTION: ${e.message}", e)
            Result.failure(e.toAppException())
        }

    }
}