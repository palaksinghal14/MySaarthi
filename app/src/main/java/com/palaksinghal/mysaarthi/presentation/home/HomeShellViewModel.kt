package com.palaksinghal.mysaarthi.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palaksinghal.mysaarthi.domain.repository.SatsangRequestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeShellViewModel @Inject constructor(
    satsangRequestRepository: SatsangRequestRepository
) : ViewModel() {

    // Requests are the ONLY badge source for now.
    // When chat lands, this becomes:
    //   combine(incomingCount, unreadChatCount) { r, c -> r + c }
    val pendingRequestCount: StateFlow<Int> =
        satsangRequestRepository.getIncomingSatsangReq()
            .map { it.size }          // query already filters status == PENDING
            .catch { emit(0) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0
            )
}