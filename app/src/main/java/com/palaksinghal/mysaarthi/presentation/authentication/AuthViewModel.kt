package com.palaksinghal.mysaarthi.presentation.authentication

import android.util.Patterns.EMAIL_ADDRESS
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palaksinghal.mysaarthi.core.utils.toAppException
import com.palaksinghal.mysaarthi.domain.model.AppException
import com.palaksinghal.mysaarthi.domain.model.User
import com.palaksinghal.mysaarthi.domain.repository.AuthenticationRepo
import com.palaksinghal.mysaarthi.domain.repository.UserProfileRepo
import com.palaksinghal.mysaarthi.presentation.util.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo : AuthenticationRepo,
    private val userProfileRepo: UserProfileRepo
): ViewModel(){

    private val _authState= MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authState =_authState.asStateFlow()

     fun loginWithEmail(email: String, password: String) {
         if (email.isBlank() || password.isBlank()) {
             _authState.value = AuthUiState.Error(
                 AppException.EmptyFieldsException
             )
             return
         }
         if (!EMAIL_ADDRESS.matcher(email).matches()) {
             _authState.value = AuthUiState.Error(
                 AppException.InvalidEmailFormatException
             )
             return
         }
         viewModelScope.launch {
             _authState.value= AuthUiState.Loading
             authRepo.loginWithEmail(email,password)
                 .onSuccess {
                     decidePostLoginNavigation()
                 }
                 .onFailure { throwable ->

                     val exception= throwable as? AppException?: AppException.UnknownException(throwable.message)
                     _authState.value= AuthUiState.Error(exception)
                 }
         }
     }
    fun registerWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthUiState.Error(
                AppException.EmptyFieldsException
            )
            return
        }

        if (!EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = AuthUiState.Error(
                AppException.InvalidEmailFormatException
            )
            return
        }
        viewModelScope.launch {
            _authState.value= AuthUiState.Loading
            authRepo.registerWithEmail(email,password)
                .onSuccess {
                    _authState.value= AuthUiState.Success
                }
                .onFailure { throwable ->

                    val exception= throwable as? AppException?: AppException.UnknownException(throwable.message)
                    _authState.value= AuthUiState.Error(exception)
                }
        }
    }
    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value= AuthUiState.Idle

        }
    }

    fun resetState() {
        _authState.value = AuthUiState.Idle
    }


    private fun decidePostLoginNavigation(){
        val uid = authRepo.getCurrentUserId() ?: run {
            _authState.value = AuthUiState.Error(
                AppException.UnknownException("No user found after login")
            )
            return
        }
        viewModelScope.launch {
            userProfileRepo.isOnboardingCompleted(uid)
                .onSuccess { isCompleted ->
                    if (isCompleted) {
                        _authState.value = AuthUiState.onNavigateToHome
                    } else {
                        _authState.value = AuthUiState.onNavigateToOnboarding
                    }
                }
                .onFailure {
                    // If check fails, default to onboarding — safer than skipping it
                    _authState.value = AuthUiState.onNavigateToOnboarding
                }
        }

    }

}