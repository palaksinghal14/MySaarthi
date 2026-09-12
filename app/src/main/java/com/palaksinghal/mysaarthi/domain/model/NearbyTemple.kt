package com.palaksinghal.mysaarthi.domain.model

data class NearbyTemple(
    val placeId: String,
    val name: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val distanceKm: Double
)
