package com.palaksinghal.mysaarthi.domain.model

data class Conversation(
    val id: String ,
    val otherUid:String,
    val otherUserName:String,
    val lastMsg:String,
    val lastMsgAt:Long,
    val unreadCount:Int,
    val isLastMessageMine: Boolean,
    val isBlockedByMe: Boolean,
    val isBlockedByThem: Boolean
)
