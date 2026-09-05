package com.palaksinghal.mysaarthi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.palaksinghal.mysaarthi.data.local.entity.SadhanaEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SadhanaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entries: List<SadhanaEntryEntity>)

    @Query("SELECT * FROM sadhana_entries WHERE date = :date")
    fun getEntriesForDate(date: String): Flow<List<SadhanaEntryEntity>>

    @Query("UPDATE sadhana_entries SET isCompleted = :isCompleted WHERE date = :date AND practice = :practice")
    suspend fun updateCompletion(date: String, practice: String, isCompleted: Boolean)
}