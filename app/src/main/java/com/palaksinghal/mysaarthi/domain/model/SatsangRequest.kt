package com.palaksinghal.mysaarthi.domain.model

enum class SatsangRequestStatus { PENDING, ACCEPTED, DECLINED }

data class SatsangRequest(
    val requestId: String = "",
    val fromUid: String = "",
    val fromDisplayName: String = "",
    val toUid: String = "",
    val status: SatsangRequestStatus = SatsangRequestStatus.PENDING,
    val createdAt: Long = 0L
)
