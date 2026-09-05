package com.palaksinghal.mysaarthi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.palaksinghal.mysaarthi.data.local.entity.UserProfileEntity

@Dao
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(user: UserProfileEntity)

    @Query("SELECT * FROM userProfile WHERE uid=:uid")
    suspend fun getUserProfile(uid: String) : UserProfileEntity?

    @Query("SELECT onboardingCompleted FROM userProfile WHERE uid = :uid")
    suspend fun isOnboardingCompleted(uid: String): Boolean?

}