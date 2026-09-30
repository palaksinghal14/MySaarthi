package com.palaksinghal.mysaarthi.presentation.nearby

import com.palaksinghal.mysaarthi.domain.model.AppException
import com.palaksinghal.mysaarthi.domain.model.NearbySeeker
import com.palaksinghal.mysaarthi.domain.model.NearbyTemple

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
    val seekerConnectionStates: Map<String, SeekerConnectionState> = emptyMap(),
    // Whether the CURRENT user has Open to Satsang on — controls whether the
    // seekers tab shows real data or an explanatory empty state. Defaults to
    // true so we don't briefly flash the "closed" message before the first
    // load resolves (isLoading gates the whole screen until then anyway).
    val isOpenToSatsang: Boolean = true
)