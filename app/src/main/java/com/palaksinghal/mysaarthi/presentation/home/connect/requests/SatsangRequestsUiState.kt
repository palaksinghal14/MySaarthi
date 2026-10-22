package com.palaksinghal.mysaarthi.presentation.home.connect.requests
import com.palaksinghal.mysaarthi.domain.model.SatsangRequest

data class SatsangRequestsUiState(
    val incomingRequests: List<SatsangRequest> = emptyList(),
    val outgoingRequests: List<SatsangRequest> = emptyList(),
)