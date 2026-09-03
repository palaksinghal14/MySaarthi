package com.palaksinghal.mysaarthi.domain.model

data class Shloka(
    val shlokaNumber:Int,
    val chapter: Int,
    val verse: Int,
    val shlokaSanskrit: String,
    val hindiTranslation: String,
    val englishTranslation: String,
)
