package com.palaksinghal.mysaarthi.data.remote.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import com.palaksinghal.mysaarthi.domain.model.ChatMessage

data class MessageDto(
    val id: String = "",
    val senderId: String = "",
    val text: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    val type: String = "text",
    val participants: List<String> = emptyList()
)

fun MessageDto.toDomain(myUid:String ,docId: String, isPending: Boolean) = ChatMessage(
    id = docId,
    msg = text,
    isMine = senderId == myUid,
    isPending = isPending,
    sentAt =createdAt?.toDate()?.time?: System.currentTimeMillis()
)