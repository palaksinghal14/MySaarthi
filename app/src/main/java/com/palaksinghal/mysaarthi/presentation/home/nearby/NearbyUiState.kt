package com.palaksinghal.mysaarthi.presentation.nearby

import com.palaksinghal.mysaarthi.domain.model.AppException
import com.palaksinghal.mysaarthi.domain.model.NearbySeeker
import com.palaksinghal.mysaarthi.domain.model.NearbyTemple
import com.palaksinghal.mysaarthi.domain.model.SatsangRequestStatus

enum class NearbyFilter { PLACES, SEEKERS }
enum class SeekerConnectionState { NONE, PENDING, CONNECTED, DECLINED }

data class NearbyUiState(
    val isLoading: Boolean = true,
    val seekers: List<NearbySeeker> = emptyList(),
    val temples: List<NearbyTemple> = emptyList(),
    val error: AppException? = null,
    val selectedFilter: NearbyFilter = NearbyFilter.PLACES,
    val userLat: Double = 0.0,
    val userLng: Double = 0.0,
    val seekerConnectionStates: Map<String, SeekerConnectionState> = emptyMap()
)