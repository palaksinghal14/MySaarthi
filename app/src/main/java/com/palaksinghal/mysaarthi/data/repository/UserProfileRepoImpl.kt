package com.palaksinghal.mysaarthi.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.palaksinghal.mysaarthi.core.utils.toAppException
import com.palaksinghal.mysaarthi.data.local.dao.UserProfileDao
import com.palaksinghal.mysaarthi.data.local.entity.toDomain
import com.palaksinghal.mysaarthi.data.local.entity.toEntity
import com.palaksinghal.mysaarthi.domain.model.AppException
import com.palaksinghal.mysaarthi.domain.model.UserProfile
import com.palaksinghal.mysaarthi.domain.repository.UserProfileRepo
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserProfileRepoImpl @Inject constructor(
    private val auth : FirebaseAuth,
    private val firestore : FirebaseFirestore,
    private val userProfileDao: UserProfileDao
) : UserProfileRepo {

    override suspend fun saveUserProfile(userProfile: UserProfile): Result<Unit> {
        return try {
            val uid =
                auth.currentUser?.uid ?: return Result.failure(AppException.UserNotFoundException)

            userProfileDao.insertUserProfile(userProfile.copy(uid=uid).toEntity())

            firestore.collection("users")
                .document(uid)
                .set(userProfile.copy(uid = uid))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e.toAppException())
        }
    }

    override suspend fun getUserProfile(uid: String): Result<UserProfile?> {
        return try {

            val snapshot = firestore.collection("users")
                .document(uid)
                .get()
                .await()

            val userProfile = snapshot.toObject(UserProfile::class.java)
            Result.success(userProfile)
        } catch (e: Exception) {
            Result.failure(e.toAppException())
        }
    }

    override suspend fun isOnboardingCompleted(uid: String): Result<Boolean> {
        return try {
            val ans=userProfileDao.isOnboardingCompleted(uid)?:false
            Result.success(ans)
        } catch (e: Exception) {
            Result.failure(e.toAppException())
        }
    }
}

