package com.palaksinghal.mysaarthi.domain.repository

import com.palaksinghal.mysaarthi.domain.model.Shloka

interface ShlokaRepo {
    suspend fun getTodayShloka() : Result<Shloka>
    suspend fun advanceToNextShloka()
}