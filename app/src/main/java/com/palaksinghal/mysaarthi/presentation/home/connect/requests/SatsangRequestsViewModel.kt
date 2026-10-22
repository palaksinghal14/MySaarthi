package com.palaksinghal.mysaarthi.presentation.home.connect


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palaksinghal.mysaarthi.domain.model.SatsangRequestStatus
import com.palaksinghal.mysaarthi.domain.repository.AuthenticationRepo
import com.palaksinghal.mysaarthi.domain.repository.SatsangRequestRepository
import com.palaksinghal.mysaarthi.domain.repository.UserProfileRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SatsangRequestsViewModel @Inject constructor(
    private val authRepo: AuthenticationRepo,
    private val userProfileRepo: UserProfileRepo,
    private val satsangRequestRepository: SatsangRequestRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SatsangRequestsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadMyUid()
        observeIncomingRequests()
        observeOutgoingRequests()
        observeConnections()
    }

    private fun loadMyUid() {
        _uiState.update { it.copy(myUid = authRepo.getCurrentUserId() ?: "") }
    }

    private fun observeIncomingRequests() {
        viewModelScope.launch {
            satsangRequestRepository.getIncomingSatsangReq()
                .catch { }
                .collect { requests ->
                    // Query already filters status == PENDING
                    _uiState.update { it.copy(incomingRequests = requests) }
                }
        }
    }

    private fun observeOutgoingRequests() {
        viewModelScope.launch {
            satsangRequestRepository.getOutgoingSatsangReq()
                .catch { }
                .collect { requests ->
                    _uiState.update {
                        it.copy(
                            outgoingRequests = requests.filter { req ->
                                req.status != SatsangRequestStatus.ACCEPTED
                            }
                        )
                    }
                }
        }
    }

    private fun observeConnections() {
        viewModelScope.launch {
            satsangRequestRepository.getConnectedUsers()
                .catch { }
                .collect { connections ->
                    _uiState.update { it.copy(connections = connections) }
                }
        }
    }

    fun respondToRequest(requestId: String, accept: Boolean) {
        viewModelScope.launch {
            satsangRequestRepository.respondToRequest(requestId, accept)
        }
    }

    suspend fun getContactEmail(uid: String): String {
        return userProfileRepo.getUserProfile(uid).getOrNull()?.email ?: ""
    }
}