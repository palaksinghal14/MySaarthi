package com.palaksinghal.mysaarthi.presentation.nearby

import com.palaksinghal.mysaarthi.domain.model.AppException
import com.palaksinghal.mysaarthi.domain.model.NearbySeeker
import com.palaksinghal.mysaarthi.domain.model.NearbyTemple

enum class NearbyFilter { PLACES, SEEKERS }

data class NearbyUiState(
    val isLoading: Boolean = true,
    val seekers: List<NearbySeeker> = emptyList(),
    val temples: List<NearbyTemple> = emptyList(),
    val error: AppException? = null,
    val selectedFilter: NearbyFilter = NearbyFilter.PLACES,
    val userLat: Double = 0.0,
    val userLng: Double = 0.0
)