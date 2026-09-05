package com.palaksinghal.mysaarthi.presentation.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palaksinghal.mysaarthi.domain.model.AppException
import com.palaksinghal.mysaarthi.domain.model.SadhanaEntry
import com.palaksinghal.mysaarthi.domain.repository.AuthenticationRepo
import com.palaksinghal.mysaarthi.domain.repository.SadhanaRepository
import com.palaksinghal.mysaarthi.domain.repository.ShlokaRepo
import com.palaksinghal.mysaarthi.domain.repository.UserProfileRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepo: AuthenticationRepo,
    private val userProfileRepo: UserProfileRepo,
    private val shlokaRepo: ShlokaRepo,
    private val sadhanaRepo: SadhanaRepository
): ViewModel(){

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState= _uiState.asStateFlow()

    init {
        loadData()
        getSadhanaEntries()
    }

    fun loadData(){

        viewModelScope.launch {

            val uid= authRepo.getCurrentUserId()
            val isEvening= LocalTime.now().hour >= 18

            if(uid==null){
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            val profileDeferred = async { userProfileRepo.getUserProfile(uid) }
            val shlokaDeferred = async { shlokaRepo.getTodayShloka() }

            val profileResult= profileDeferred.await()
            val shlokaResult= shlokaDeferred.await()

            profileResult
                .onSuccess { profile ->
                     val name= profile?.displayName
                     _uiState.update { it.copy(
                           displayName = name?:""
                         )
                     }
                }.onFailure { throwable ->
                        val exception = throwable as? AppException
                            ?: AppException.UnknownException(throwable.message)

                        _uiState.update { it.copy(
                            error = exception
                            )
                        }
                }

            shlokaResult
                .onSuccess { shloka ->
                        _uiState.update { it.copy(
                            shloka=shloka,
                            isLoading = false
                           )
                        }
                }.onFailure { throwable ->
                          val exception = throwable as? AppException
                            ?: AppException.UnknownException(throwable.message)

                          _uiState.update { it.copy(
                                error = exception
                             )
                          }
                }

            _uiState.update { it.copy(isLoading = false, isEvening=isEvening) }
        }
    }
    fun getSadhanaEntries(){
         viewModelScope.launch {
             sadhanaRepo.getTodaysSadhana().collect { entries ->
                 _uiState.update { it.copy(
                     sadhanaEntries =entries,
                     isLoading = false
                 ) }
             }
         }
    }

    fun onToggle(  practice :String , isCompleted:Boolean){
        viewModelScope.launch {
            val date = LocalDate.now().toString()
            sadhanaRepo.togglePractice(date,practice,isCompleted)
        }
    }

    fun advanceNextShloka(){
        viewModelScope.launch {
            shlokaRepo.advanceToNextShloka()
            shlokaRepo.getTodayShloka()
                .onSuccess { shloka ->
                    _uiState.update { it.copy(shloka=shloka) }
                }
        }
    }
}