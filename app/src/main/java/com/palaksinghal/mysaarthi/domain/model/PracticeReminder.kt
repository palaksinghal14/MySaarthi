package com.palaksinghal.mysaarthi.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PracticeReminder(
    val practice: String = "",
    val hour: Int = 7,
    val minute: Int = 0,
    val amPm: String = "AM",
    val isEnabled: Boolean = true
)
