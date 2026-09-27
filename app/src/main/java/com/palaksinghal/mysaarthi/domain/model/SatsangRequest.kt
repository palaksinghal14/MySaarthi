package com.palaksinghal.mysaarthi.domain.model

enum class SatsangRequestStatus { PENDING, ACCEPTED, DECLINED }

data class SatsangRequest(
    val requestId: String = "",
    val fromUid: String = "",
    val fromDisplayName: String = "",
    val toUid: String = "",
    val toDisplayName: String = "",
    val status: SatsangRequestStatus = SatsangRequestStatus.PENDING,
    val createdAt: Long = 0L
)

// Given my own uid, returns the OTHER person's uid and display name
// — works regardless of whether I was the sender or recipient
fun SatsangRequest.otherPerson(myUid: String): Pair<String, String> {
    return if (fromUid == myUid) {
        toUid to toDisplayName
    } else {
        fromUid to fromDisplayName
    }
}
