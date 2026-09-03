package com.palaksinghal.mysaarthi.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.palaksinghal.mysaarthi.data.local.dao.ShlokaDao
import com.palaksinghal.mysaarthi.data.local.entity.ShlokaEntity

@Database(entities = [ShlokaEntity::class] , version = 1 , exportSchema =false)
abstract class GitaDatabase: RoomDatabase() {
    abstract fun shlokaDao() : ShlokaDao
}