package com.palaksinghal.mysaarthi.domain.model

data class NearbySeeker(
    val uid: String,
    val displayName: String,
    val practices: List<String>,
    val howLongOnPath: String,
    val spiritualIntro: String,
    val lat: Double,
    val lng: Double,
    val distanceKm: Double

)
