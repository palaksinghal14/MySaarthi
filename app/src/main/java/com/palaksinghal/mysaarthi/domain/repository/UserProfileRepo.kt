package com.palaksinghal.mysaarthi.domain.repository

import com.palaksinghal.mysaarthi.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepo {

    suspend fun saveUserProfile(userProfile: UserProfile) :Result<Unit>
    suspend fun getUserProfile(uid:String) :Result<UserProfile?>
    fun observeUserProfile(uid:String): Flow<UserProfile?>
    suspend fun isOnboardingCompleted(uid: String): Result<Boolean>
}