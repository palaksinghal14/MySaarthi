package com.palaksinghal.mysaarthi.domain.repository

import com.palaksinghal.mysaarthi.domain.model.UserProfile

interface UserProfileRepo {

    suspend fun saveUserProfile(userProfile: UserProfile) :Result<Unit>
    suspend fun getUserProfile(uid:String) :Result<UserProfile?>
    suspend fun isOnboardingCompleted(uid: String): Result<Boolean>
}