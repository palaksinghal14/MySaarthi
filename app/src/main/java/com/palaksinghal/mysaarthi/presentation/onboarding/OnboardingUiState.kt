package com.palaksinghal.mysaarthi.presentation.onboarding

import com.palaksinghal.mysaarthi.domain.model.AppException

sealed class OnboardingUiState {
    object Idle : OnboardingUiState()
    object Loading : OnboardingUiState()
    object Success : OnboardingUiState()
    data class Error(val exception: AppException) : OnboardingUiState()
}