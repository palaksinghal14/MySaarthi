package com.palaksinghal.mysaarthi.data.local.dto

import kotlinx.serialization.Serializable

@Serializable
data class SholkaDto(
    val chapter: Int,
    val verse: Int,
    val sanskrit: String,
    val hindi: String,
    val english: String,
    val transliteration: String,
)
