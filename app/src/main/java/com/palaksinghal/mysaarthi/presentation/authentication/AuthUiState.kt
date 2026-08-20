package com.palaksinghal.mysaarthi.presentation.authentication

import com.palaksinghal.mysaarthi.domain.model.AppException

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error( val exception: AppException): AuthUiState()
}