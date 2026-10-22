package com.palaksinghal.mysaarthi.domain.model

data class ChatMessage(
    val id:String,
    val msg:String ,
    val isMine:Boolean,
    val sentAt:Long,
    val isPending: Boolean = false // isPending is what drives the clock-icon → tick.
)
