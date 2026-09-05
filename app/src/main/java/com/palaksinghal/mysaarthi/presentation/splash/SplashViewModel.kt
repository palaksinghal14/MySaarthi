package com.palaksinghal.mysaarthi.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palaksinghal.mysaarthi.domain.model.AppException
import com.palaksinghal.mysaarthi.domain.repository.AuthenticationRepo
import com.palaksinghal.mysaarthi.domain.repository.UserProfileRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepo : AuthenticationRepo,
    private val userProfileRepo: UserProfileRepo
): ViewModel() {
    private val _splashUiState =MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val splashUiState= _splashUiState.asStateFlow()

    init {
        decideNavigation()
    }


    private fun decideNavigation(){
       viewModelScope.launch {

           _splashUiState.value= SplashUiState.Loading
           delay(1500)

           if(authRepo.isUserSignedIn()){

               val uid= authRepo.getCurrentUserId()
                   ?: run {
                       _splashUiState.value= SplashUiState.OnNavToWelcome
                       return@launch
                   }


               userProfileRepo.isOnboardingCompleted(uid)
                   .onSuccess { isCompleted->
                       if(isCompleted){
                           _splashUiState.value = SplashUiState.OnNavToHome
                       }
                       else{
                           _splashUiState.value = SplashUiState.OnNavToOnboarding
                       }
                   }
                   .onFailure { throwable ->
                       android.util.Log.e("SplashVM", "getUserProfile failed: ${throwable.message}", throwable)
                       val exception = throwable as? AppException ?: AppException.UnknownException(throwable.message)
                       _splashUiState.value= SplashUiState.Error(exception)
                   }

           }else{
               _splashUiState.value= SplashUiState.OnNavToWelcome
           }
       }

    }

    fun retry(){
        decideNavigation()
    }
}