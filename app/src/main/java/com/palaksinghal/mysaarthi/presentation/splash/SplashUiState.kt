package com.palaksinghal.mysaarthi.presentation.splash

import com.palaksinghal.mysaarthi.domain.model.AppException

sealed class SplashUiState {
    object Loading : SplashUiState()
    object OnNavToWelcome: SplashUiState()
    object OnNavToOnboarding: SplashUiState()
    object OnNavToHome: SplashUiState()
    data class Error(val exception: AppException) : SplashUiState()

}