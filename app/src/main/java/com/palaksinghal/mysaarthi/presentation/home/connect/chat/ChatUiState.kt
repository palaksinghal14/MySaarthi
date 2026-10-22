package com.palaksinghal.mysaarthi.presentation.home.connect.chat

import com.palaksinghal.mysaarthi.domain.model.AppException
import com.palaksinghal.mysaarthi.domain.model.ChatMessage

data class ChatUiState(
    val liveMessages: List<ChatMessage> = emptyList(),
    val olderMessages: List<ChatMessage> = emptyList(),
    val otherUserName: String = "",
    val draft: String = "",
    val isLoading: Boolean = true,
    val isSending: Boolean = false,
    val isLoadingOlder: Boolean = false,
    val hasMoreOlder: Boolean = true,
    val isBlockedByMe: Boolean = false,
    val isBlockedByThem: Boolean = false,
    val errorMessage: AppException? = null,
    val infoMessage: String? = null
) {
    // Newest first — matches the DESCENDING query and reverseLayout.
    val messages: List<ChatMessage> get() = liveMessages + olderMessages

    val canSend: Boolean get() = !isBlockedByMe && !isBlockedByThem

    val isEmpty: Boolean get() = !isLoading && messages.isEmpty()
}