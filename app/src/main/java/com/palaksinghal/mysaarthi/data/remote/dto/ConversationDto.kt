package com.palaksinghal.mysaarthi.data.remote.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import com.palaksinghal.mysaarthi.domain.model.Conversation

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

fun ConversationDto.toDomain(myUid: String, docId: String) : Conversation?{
    if (participants.size != 2) return null
    val otherUid = participants.firstOrNull { it != myUid } ?: return null

   return Conversation(
    id = docId,
    otherUid = otherUid,
    otherUserName = participantNames[otherUid].orEmpty(),
    lastMsg = lastMessage,
    lastMsgAt =  lastMessageAt?.toDate()?.time ?: System.currentTimeMillis(),
    unreadCount = unread[myUid]?:0,
    isLastMessageMine = lastSenderId==myUid,
    isBlockedByMe = blockedBy.contains(myUid),
    isBlockedByThem = blockedBy.contains(otherUid)
)
}
