package com.palaksinghal.mysaarthi.presentation.home.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palaksinghal.mysaarthi.domain.model.AppException
import com.palaksinghal.mysaarthi.domain.repository.AuthenticationRepo
import com.palaksinghal.mysaarthi.domain.repository.UserProfileRepo
import com.palaksinghal.mysaarthi.worker.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class SettingsUiState(
    val isDeleting: Boolean = false,
    val deleteSuccess: Boolean = false,
    val error: AppException? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepo: AuthenticationRepo,
    private val userProfileRepo: UserProfileRepo,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    fun signOut() {
        authRepo.logout()
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, error = null) }

            val uid = authRepo.getCurrentUserId()
            if (uid == null) {
                _uiState.update {
                    it.copy(isDeleting = false, error = AppException.UserNotFoundException)
                }
                return@launch
            }

            // Delete Firestore + Room data first
            userProfileRepo.deleteAllUserData(uid)
                .onFailure { throwable ->
                    val exception = throwable as? AppException
                        ?: AppException.UnknownException(throwable.message)
                    _uiState.update { it.copy(isDeleting = false, error = exception) }
                    return@launch
                }

            // Then delete the Firebase Auth account itself
            authRepo.deleteAccount()
                .onSuccess {
                    _uiState.update { it.copy(isDeleting = false, deleteSuccess = true) }
                }
                .onFailure { throwable ->
                    val exception = throwable as? AppException
                        ?: AppException.UnknownException(throwable.message)
                    _uiState.update { it.copy(isDeleting = false, error = exception) }
                }
        }
    }

    fun resetError() {
        _uiState.update { it.copy(error = null) }
    }
}