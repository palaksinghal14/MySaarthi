package com.palaksinghal.mysaarthi.domain.repository

import com.palaksinghal.mysaarthi.domain.model.NearbySeeker
import com.palaksinghal.mysaarthi.domain.model.NearbyTemple

interface NearbyRepository {
    suspend fun getNearbySeekers(radiusKm: Double): Result<List<NearbySeeker>>
    suspend fun getNearbyTemples(radiusKm: Double): Result<List<NearbyTemple>>
}