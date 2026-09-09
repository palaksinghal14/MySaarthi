package com.palaksinghal.mysaarthi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.palaksinghal.mysaarthi.data.local.entity.SadhanaEntryEntity
import com.palaksinghal.mysaarthi.domain.model.DailyCompletionRate
import kotlinx.coroutines.flow.Flow

@Dao
interface SadhanaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entries: List<SadhanaEntryEntity>)

    @Query("SELECT * FROM sadhana_entries WHERE date = :date")
    fun getEntriesForDate(date: String): Flow<List<SadhanaEntryEntity>>

    @Query("UPDATE sadhana_entries SET isCompleted = :isCompleted WHERE date = :date AND practice = :practice")
    suspend fun updateCompletion(date: String, practice: String, isCompleted: Boolean)

    @Query("""
    SELECT date,
           COUNT(*) as total,
           SUM(CASE WHEN isCompleted = 1 THEN 1 ELSE 0 END) as completed
    FROM sadhana_entries
    WHERE date >= :startDate
    GROUP BY date
""")
    suspend fun getCompletionRatioByDate(startDate: String): List<DailyCompletionRate>

    @Query("SELECT DISTINCT date FROM sadhana_entries WHERE isCompleted = 1 ORDER BY date ASC")
    suspend fun getAllCompletedDates(): List<String>
}