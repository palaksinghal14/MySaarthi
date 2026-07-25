package com.palaksinghal.mysaarthi.core.utils

import com.palaksinghal.mysaarthi.domain.model.AppException

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val exception: AppException) : UiState<Nothing>()
}