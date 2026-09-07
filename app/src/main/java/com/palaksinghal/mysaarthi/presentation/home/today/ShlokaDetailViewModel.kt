package com.palaksinghal.mysaarthi.presentation.home.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palaksinghal.mysaarthi.domain.model.Shloka
import com.palaksinghal.mysaarthi.domain.repository.ShlokaRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShlokaDetailViewModel @Inject constructor(
    private val shlokaRepo: ShlokaRepo
) : ViewModel() {

    private val _shloka = MutableStateFlow<Shloka?>(null)
    val shloka = _shloka.asStateFlow()

    init {
        loadShloka()
    }

    private fun loadShloka() {
        viewModelScope.launch {
            shlokaRepo.getTodayShloka()
                .onSuccess { shloka ->
                    _shloka.value = shloka
                }
        }
    }
}