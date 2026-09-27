package com.palaksinghal.mysaarthi.domain.repository

import com.palaksinghal.mysaarthi.domain.model.SatsangRequest
import kotlinx.coroutines.flow.Flow

interface SatsangRequestRepository {

    suspend fun sendSatsangReq(toUid:String , toDisplayName:String) :Result<Unit>
    fun getIncomingSatsangReq(): Flow<List<SatsangRequest>>
    fun getOutgoingSatsangReq(): Flow<List<SatsangRequest>>
    suspend fun respondToRequest(requestId: String, accept: Boolean): Result<Unit>
    fun getConnectedUsers(): Flow<List<SatsangRequest>>
}