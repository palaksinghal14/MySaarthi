package com.palaksinghal.mysaarthi.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palaksinghal.mysaarthi.domain.repository.ChatRepository
import com.palaksinghal.mysaarthi.domain.repository.SatsangRequestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeShellViewModel @Inject constructor(
    satsangRequestRepository: SatsangRequestRepository,
    chatRepository : ChatRepository
) : ViewModel() {


    //   requests and unread chat count is the badge source
    val connectBadgeCount: StateFlow<Int> =
        combine(

            satsangRequestRepository.getIncomingSatsangReq().map { it.size },
            chatRepository.observeConversations().map{
                convs-> convs.count{it.unreadCount>0}
            }
        ){
                requests, unreadChats -> requests + unreadChats
        }
               // query already filters status == PENDING
            .catch { emit(0) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0
            )

}
