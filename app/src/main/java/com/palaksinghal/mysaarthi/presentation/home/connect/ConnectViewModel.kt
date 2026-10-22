package com.palaksinghal.mysaarthi.presentation.home.connect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palaksinghal.mysaarthi.core.utils.conversationId
import com.palaksinghal.mysaarthi.core.utils.toAppException
import com.palaksinghal.mysaarthi.domain.model.SatsangRequest
import com.palaksinghal.mysaarthi.domain.repository.AuthenticationRepo
import com.palaksinghal.mysaarthi.domain.repository.ChatRepository
import com.palaksinghal.mysaarthi.domain.repository.SatsangRequestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val satsangRepository: SatsangRequestRepository,
    private val auth: AuthenticationRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConnectUiState())
    val uiState: StateFlow<ConnectUiState> = _uiState.asStateFlow()

    /** convIds we've already tried to repair, so we don't retry every emission. */
    private val reconcileAttempted = mutableSetOf<String>()

    init {
        observeIncomingRequests()
        observeConversationsAndReconcile()
    }

    private fun observeIncomingRequests() {
        viewModelScope.launch {
            satsangRepository.getIncomingSatsangReq()
                .catch { e -> _uiState.update { it.copy(error = e.toAppException()) } }
                .collect { requests ->
                    _uiState.update { it.copy(incomingRequestCount = requests.size) }
                }
        }
    }

    private fun observeConversationsAndReconcile() {
        viewModelScope.launch {
            combine(
                chatRepository.observeConversations(),
                satsangRepository.getConnectedUsers()
            ) { conversations, connected -> conversations to connected }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.toAppException()) }
                }
                .collect { (conversations, connected) ->
                    _uiState.update {
                        it.copy(conversations = conversations, isLoading = false)
                    }
                    reconcile(conversations.map { c -> c.id }.toSet(), connected)
                }
        }
    }

    /**
     * Creates conversation docs for connections that don't have one — testers who
     * connected before chat existed, or an acceptance whose chat write failed.
     */
    private suspend fun reconcile(existingIds: Set<String>, connected: List<SatsangRequest>) {
        val myUid = auth.getCurrentUserId() ?: return

        connected.forEach { request ->
            val otherUid = if (request.fromUid == myUid) request.toUid else request.fromUid
            val convId = conversationId(myUid, otherUid)

            if (convId !in existingIds && reconcileAttempted.add(convId)) {
                chatRepository.ensureConversation(request)
            }
        }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(error = null) }
    }
}