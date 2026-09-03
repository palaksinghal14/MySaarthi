package com.palaksinghal.mysaarthi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.palaksinghal.mysaarthi.data.local.entity.ShlokaEntity
import com.palaksinghal.mysaarthi.domain.model.Shloka

@Dao
interface ShlokaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllShloka(shlokas :List<ShlokaEntity>)

    @Query("SELECT COUNT(*) FROM shlokas")
    suspend fun getShlokaCount() :Int

    @Query( "SELECT * FROM shlokas WHERE shlokaNumber = :number")
    suspend fun getShlokaByNumber(number:Int) : ShlokaEntity?

}