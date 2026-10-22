package com.palaksinghal.mysaarthi.domain.repository

import com.palaksinghal.mysaarthi.domain.model.ChatMessage
import com.palaksinghal.mysaarthi.domain.model.Conversation
import com.palaksinghal.mysaarthi.domain.model.SatsangRequest
import kotlinx.coroutines.flow.Flow

interface ChatRepository{

    fun observeConversations() :Flow<List<Conversation>>
    fun observeMessages(convId:String): Flow<List<ChatMessage>>
    suspend fun sendMessage(convId: String, otherUid: String, text: String) : Result<Unit>
    suspend fun markRead(convId: String): Result<Unit>
    suspend fun ensureConversation(request: SatsangRequest): Result<String>  //It creates the conversations/{convId} document if it doesn’t already exist, and returns the ID.

    suspend fun loadOlderMessages(convId: String, beforeSentAt: Long): Result<List<ChatMessage>>

    suspend fun setBlocked(convId: String, otherUid: String, blocked: Boolean): Result<Unit>

    suspend fun reportUser(otherUid: String, convId: String, reason: String): Result<Unit>

}