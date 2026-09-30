package com.palaksinghal.mysaarthi.presentation.home.profile

import androidx.lifecycle.ViewModel
import com.palaksinghal.mysaarthi.domain.repository.AuthenticationRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepo: AuthenticationRepo
) : ViewModel() {

    fun signOut() {
        authRepo.logout()
    }
}