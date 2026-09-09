package com.palaksinghal.mysaarthi.presentation.home.profile

import com.palaksinghal.mysaarthi.domain.model.AppException

data class YouUiState(
    val isLoading: Boolean = true,
    val error: AppException? = null,
    val displayName: String = "",
    val spiritualIntro: String = "",
    val practices: List<String> = emptyList(),
    val howLongOnPath: String = "",
    val isOpenToSatsang: Boolean = false,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val daysPracticed: Int = 0,
    val last30Days: List<Float> = emptyList()
)
