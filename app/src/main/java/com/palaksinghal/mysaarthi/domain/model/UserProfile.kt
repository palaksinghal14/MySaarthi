package com.palaksinghal.mysaarthi.domain.model

import com.google.firebase.firestore.PropertyName

data class UserProfile(
    val uid: String = "",
    val displayName: String = "",
    val practices: List<String> = emptyList(),
    val howLongOnPath: String = "",
    @get:PropertyName("openToSatsang")
    @set:PropertyName("openToSatsang")
    var isOpenToSatsang: Boolean = false,
    val spiritualIntro: String = "",
    val onboardingCompleted: Boolean = false,
    val practiceReminders :List<Map<String, Any>> = emptyList(),
    val geohash: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val lastLocationUpdate: Long = 0L
)
