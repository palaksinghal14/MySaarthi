package com.palaksinghal.mysaarthi.domain.repository

import com.palaksinghal.mysaarthi.domain.model.SadhanaEntry
import kotlinx.coroutines.flow.Flow

interface SadhanaRepository {
    fun getTodaysSadhana(): Flow<List<SadhanaEntry>>
    suspend fun togglePractice(date: String, practice: String, isCompleted: Boolean)
}