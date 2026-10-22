package com.palaksinghal.mysaarthi.data.remote.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class ConversationDto(
    val participants: List<String> = emptyList(),
    val participantNames: Map<String, String> = emptyMap(),
    val requestId: String = "",
    val lastMessage: String = "",
    val lastSenderId: String = "",
    @ServerTimestamp val lastMessageAt: Timestamp? = null, //When Firestore serialises an object, a @ServerTimestamp-annotated field that’s null gets replaced with the server sentinel on write
    @ServerTimestamp val createdAt: Timestamp? = null,
    val unread: Map<String, Int> = emptyMap(),
    val blockedBy: List<String> = emptyList()
)
