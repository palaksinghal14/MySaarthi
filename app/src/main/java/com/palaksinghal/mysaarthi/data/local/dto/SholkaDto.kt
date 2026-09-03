package com.palaksinghal.mysaarthi.data.local.dto

import com.palaksinghal.mysaarthi.data.local.entity.ShlokaEntity

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
