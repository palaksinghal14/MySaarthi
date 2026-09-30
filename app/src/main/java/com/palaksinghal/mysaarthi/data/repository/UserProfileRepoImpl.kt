package com.palaksinghal.mysaarthi.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.palaksinghal.mysaarthi.core.utils.toAppException
import com.palaksinghal.mysaarthi.data.local.dao.SadhanaDao
import com.palaksinghal.mysaarthi.data.local.dao.UserProfileDao
import com.palaksinghal.mysaarthi.data.local.entity.toDomain
import com.palaksinghal.mysaarthi.data.local.entity.toEntity
import com.palaksinghal.mysaarthi.domain.model.AppException
import com.palaksinghal.mysaarthi.domain.model.UserProfile
import com.palaksinghal.mysaarthi.domain.repository.UserProfileRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class UserProfileRepoImpl @Inject constructor(
    private val auth : FirebaseAuth,
    private val firestore : FirebaseFirestore,
    private val userProfileDao: UserProfileDao,
    private val sadhanaDao: SadhanaDao
) : UserProfileRepo {

    override suspend fun saveUserProfile(userProfile: UserProfile): Result<Unit> {
        return try {
            val uid =
                auth.currentUser?.uid ?: return Result.failure(AppException.UserNotFoundException)

            userProfileDao.insertUserProfile(userProfile.copy(uid=uid).toEntity())

            withTimeoutOrNull(2000L){
                firestore.collection("users")
                    .document(uid)
                    .set(userProfile.copy(uid = uid))
                    .await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e.toAppException())
        }
    }

    override suspend fun getUserProfile(uid: String): Result<UserProfile?> {
        return try {

            val localProfile= userProfileDao.getUserProfile(uid)

            if(localProfile!=null){
                Result.success(localProfile)
            }

            val snapshot = firestore.collection("users")
                .document(uid)
                .get()
                .await()

            val userProfile = snapshot.toObject(UserProfile::class.java)
            if(userProfile!=null){
                userProfileDao.insertUserProfile(userProfile.toEntity())
            }
            Result.success(userProfile)
        } catch (e: Exception) {
            Result.failure(e.toAppException())
        }
    }

    override suspend fun isOnboardingCompleted(uid: String): Result<Boolean> {
        return try {
            // Check Room first — fast, offline-safe for the normal case
            val localResult = userProfileDao.isOnboardingCompleted(uid)
            if (localResult != null) {
                return Result.success(localResult)
            }

            // Room has nothing (fresh install, cache wiped) — fall back to Firestore,
            // and cache the result locally so subsequent launches are fast again
            val snapshot = firestore.collection("users").document(uid).get().await()
            val userProfile = snapshot.toObject(UserProfile::class.java)

            if (userProfile != null) {
                userProfileDao.insertUserProfile(userProfile.toEntity())
                Result.success(userProfile.onboardingCompleted)
            } else {
                Result.success(false)
            }
        } catch (e: Exception) {
            Result.failure(e.toAppException())
        }
    }

    override fun observeUserProfile(uid: String): Flow<UserProfile?> {
         return userProfileDao.observeUserProfile(uid).map { entity ->
             entity?.toDomain()
         }
    }

    override suspend fun deleteAllUserData(uid: String): Result<Unit> {
        return try {
            // Delete satsang_requests where this user is either party
            val asFromUid = firestore.collection("satsang_requests")
                .whereEqualTo("fromUid", uid)
                .get()
                .await()
            val asToUid = firestore.collection("satsang_requests")
                .whereEqualTo("toUid", uid)
                .get()
                .await()

            (asFromUid.documents + asToUid.documents).forEach { doc ->
                doc.reference.delete().await()
            }

            // Delete the user's Firestore profile document
            firestore.collection("users").document(uid).delete().await()

            // Delete local Room cache
            userProfileDao.deleteUserProfile(uid)

            //delete sadhna entries
            sadhanaDao.deleteAll()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e.toAppException())
        }
    }
}

