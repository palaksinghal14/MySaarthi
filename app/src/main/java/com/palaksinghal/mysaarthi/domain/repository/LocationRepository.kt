package com.palaksinghal.mysaarthi.domain.repository

import com.palaksinghal.mysaarthi.domain.model.Location

interface LocationRepository {
    suspend fun getUserLocation() :Result<Location>
    suspend fun updateUserLocation() :Result<Unit>
}