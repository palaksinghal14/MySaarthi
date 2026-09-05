package com.palaksinghal.mysaarthi.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.palaksinghal.mysaarthi.data.local.converters.Converters
import com.palaksinghal.mysaarthi.data.local.dao.SadhanaDao
import com.palaksinghal.mysaarthi.data.local.dao.ShlokaDao
import com.palaksinghal.mysaarthi.data.local.dao.UserProfileDao
import com.palaksinghal.mysaarthi.data.local.entity.SadhanaEntryEntity
import com.palaksinghal.mysaarthi.data.local.entity.ShlokaEntity
import com.palaksinghal.mysaarthi.data.local.entity.UserProfileEntity

@Database(entities = [ShlokaEntity::class , UserProfileEntity :: class , SadhanaEntryEntity:: class] , version = 2, exportSchema =false)
@TypeConverters(Converters::class)
abstract class GitaDatabase: RoomDatabase() {

    abstract fun shlokaDao() : ShlokaDao

    abstract fun userProfileDao() : UserProfileDao

    abstract fun sadhanaDao() : SadhanaDao
}