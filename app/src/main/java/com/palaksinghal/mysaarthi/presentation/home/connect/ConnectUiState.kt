package com.palaksinghal.mysaarthi.presentation.home.connect

import com.palaksinghal.mysaarthi.domain.model.AppException
import com.palaksinghal.mysaarthi.domain.model.Conversation

data class ConnectUiState(
    val conversations: List<Conversation> = emptyList(),
    val incomingRequestCount: Int = 0,
    val isLoading: Boolean = true,
    val error: AppException? = null
) {
    val isEmpty: Boolean get() = !isLoading && conversations.isEmpty()
}