package com.palaksinghal.mysaarthi.presentation.home.profile

import com.palaksinghal.mysaarthi.domain.model.AppException
import com.palaksinghal.mysaarthi.domain.model.PracticeReminder

data class EditProfileUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: AppException? = null,
    val spiritualIntro: String = "",
    val isOpenToSatsang: Boolean = false,
    val practices: List<String> = emptyList(),
    val practiceReminders: List<Map<String, Any>> = emptyList(),
    val customPracticeInput: String = ""
)
