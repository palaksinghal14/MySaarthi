package com.palaksinghal.mysaarthi.presentation.home.connect.chat

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palaksinghal.mysaarthi.core.constants.ChatConstants
import com.palaksinghal.mysaarthi.core.utils.toAppException
import com.palaksinghal.mysaarthi.domain.repository.ChatRepository
import com.palaksinghal.mysaarthi.presentation.util.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val convId: String = checkNotNull(savedStateHandle["convId"])
    private val otherUid: String = checkNotNull(savedStateHandle["otherUid"])

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        observeMessages()
        observeConversation()
    }

    private fun observeMessages() {
        viewModelScope.launch {
            chatRepository.observeMessages(convId)
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.toAppException())
                    }
                }
                .collect { messages ->
                    _uiState.update {
                        it.copy(liveMessages = messages, isLoading = false)
                    }
                }
        }
    }

    private fun observeConversation() {
        viewModelScope.launch {
            chatRepository.observeConversations()
                .mapNotNull { list -> list.firstOrNull { it.id == convId } }
                .distinctUntilChanged()
                .catch { e -> _uiState.update { it.copy(errorMessage = e.toAppException()) } }
                .collect { conv ->
                    _uiState.update {
                        it.copy(
                            otherUserName = conv.otherUserName,
                            isBlockedByMe = conv.isBlockedByMe,
                            isBlockedByThem = conv.isBlockedByThem
                        )
                    }
                    // Re-runs whenever the conversation doc changes, so a message
                    // arriving while this screen is open is marked read too.
                    if (conv.unreadCount > 0) {
                        chatRepository.markRead(convId)
                    }
                }
        }
    }

    fun onDraftChange(text: String) {
        _uiState.update { it.copy(draft = text) }
    }

    fun onSendClick() {
        val state = _uiState.value
        val text = state.draft.trim()
        if (text.isEmpty() || state.isSending || !state.canSend) return

        _uiState.update { it.copy(draft = "", isSending = true) }

        viewModelScope.launch {
            chatRepository.sendMessage(convId, otherUid, text)
                .onFailure { e ->
                    _uiState.update {
                        it.copy(draft = text, errorMessage = e.toAppException())
                    }
                }
            _uiState.update { it.copy(isSending = false) }
        }
    }

    fun onLoadOlder() {
        val state = _uiState.value
        Log.d("Pagination", "onLoadOlder: isLoadingOlder=${state.isLoadingOlder} hasMoreOlder=${state.hasMoreOlder} oldest=${state.messages.lastOrNull()?.msg}")
        if (state.isLoadingOlder || !state.hasMoreOlder) return
        val oldest = state.messages.lastOrNull() ?: return

        _uiState.update { it.copy(isLoadingOlder = true) }

        viewModelScope.launch {
            chatRepository.loadOlderMessages(convId, oldest.sentAt)
                .onSuccess { older ->
                    Log.d("Pagination", "loaded ${older.size} older")
                    _uiState.update {
                        it.copy(
                            olderMessages = it.olderMessages + older,
                            hasMoreOlder = older.size >= ChatConstants.PAGE_SIZE,
                            isLoadingOlder = false
                        )
                    }
                }
                .onFailure { e ->
                    Log.e("Pagination", "load failed", e)
                    _uiState.update {
                        it.copy(isLoadingOlder = false, errorMessage = e.toAppException())
                    }
                }
        }
    }

    fun onToggleBlock() {
        val blocked = _uiState.value.isBlockedByMe
        viewModelScope.launch {
            chatRepository.setBlocked(convId, otherUid, blocked = !blocked)
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.toAppException()) }
                }
        }
    }

    fun onReport(reason: String) {
        viewModelScope.launch {
            chatRepository.reportUser(otherUid, convId, reason)
                .onSuccess {
                    _uiState.update { it.copy(infoMessage = "Report submitted") }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.toAppException()) }
                }
        }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onInfoShown() {
        _uiState.update { it.copy(infoMessage = null) }
    }
}