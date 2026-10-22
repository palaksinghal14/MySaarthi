package com.palaksinghal.mysaarthi.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.palaksinghal.mysaarthi.core.utils.toAppException
import com.palaksinghal.mysaarthi.di.ApplicationScope
import com.palaksinghal.mysaarthi.domain.model.AppException
import com.palaksinghal.mysaarthi.domain.model.SatsangRequest
import com.palaksinghal.mysaarthi.domain.model.SatsangRequestStatus
import com.palaksinghal.mysaarthi.domain.repository.AuthenticationRepo
import com.palaksinghal.mysaarthi.domain.repository.ChatRepository
import com.palaksinghal.mysaarthi.domain.repository.SatsangRequestRepository
import com.palaksinghal.mysaarthi.domain.repository.UserProfileRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SatsangReqRepoImpl @Inject constructor(
    private val auth: AuthenticationRepo,
    private val userRepo: UserProfileRepo,
    private val firestore: FirebaseFirestore,
    private val chatRepository: ChatRepository,
    @ApplicationScope private val appScope: CoroutineScope
) : SatsangRequestRepository {

    // Shared flows — ONE Firestore listener each, no matter how many
    // ViewModels collect them.

    private val incomingShared: StateFlow<List<SatsangRequest>> =
        incomingRequestsFlow().stateIn(
            scope = appScope,
            started = SharingStarted.WhileSubscribed(
                stopTimeoutMillis = 5_000,
                replayExpirationMillis = 0
            ),
            initialValue = emptyList()
        )

    private val outgoingShared: StateFlow<List<SatsangRequest>> =
        outgoingRequestsFlow().stateIn(
            scope = appScope,
            started = SharingStarted.WhileSubscribed(
                stopTimeoutMillis = 5_000,
                replayExpirationMillis = 0
            ),
            initialValue = emptyList()
        )

    private val connectedShared: StateFlow<List<SatsangRequest>> =
        connectedUsersFlow().stateIn(
            scope = appScope,
            started = SharingStarted.WhileSubscribed(
                stopTimeoutMillis = 5_000,
                replayExpirationMillis = 0
            ),
            initialValue = emptyList()
        )

    override fun getIncomingSatsangReq(): Flow<List<SatsangRequest>> = incomingShared
    override fun getOutgoingSatsangReq(): Flow<List<SatsangRequest>> = outgoingShared
    override fun getConnectedUsers(): Flow<List<SatsangRequest>> = connectedShared

    override suspend fun sendSatsangReq(toUid: String, toDisplayName: String): Result<Unit> {

        return try{
            val fromUid= auth.getCurrentUserId()?: return Result.failure(AppException.UserNotFoundException)

            val profile =userRepo.getUserProfile(fromUid).getOrNull() ?:run{
                return Result.failure(AppException.UserNotFoundException)
            }

            val displayName=profile.displayName

            val request= hashMapOf(
                "fromUid" to fromUid,
                "fromDisplayName" to displayName,
                "toUid" to toUid,
                "toDisplayName" to toDisplayName,
                "status" to "PENDING",
                "createdAt" to System.currentTimeMillis()
            )

            firestore.collection("satsang_requests")
                .add(request)
                .await()
            Result.success(Unit)
        }catch(e: Exception){
            Result.failure(e.toAppException())
        }

    }

    // The raw listeners — unchanged from your original code, just
    // made private so only the shared flows above can start them.
    private fun incomingRequestsFlow(): Flow<List<SatsangRequest>> = callbackFlow {
        val currentUid= auth.getCurrentUserId() ?: run{
            close()
            return@callbackFlow
        }

        val listener= firestore.collection("satsang_requests").
                whereEqualTo("toUid", currentUid).
                whereEqualTo("status","PENDING").
                addSnapshotListener { snapshot,error ->
                    if(error!=null){
                        trySend(emptyList())
                        return@addSnapshotListener
                    }

                    val requests= snapshot?.documents?.mapNotNull { doc->
                        doc.toObject(SatsangRequest:: class.java)?.copy(requestId = doc.id)
                    } ?: emptyList()

                    trySend(requests)
                }
        awaitClose { listener.remove() }

    }

    private fun outgoingRequestsFlow(): Flow<List<SatsangRequest>> = callbackFlow {

        val currentUid= auth.getCurrentUserId() ?: run{
            close()
            return@callbackFlow
        }

        val listener= firestore.collection("satsang_requests").
        whereEqualTo("fromUid", currentUid).
        addSnapshotListener { snapshot,error ->
            if(error!=null){
               trySend(emptyList())
                return@addSnapshotListener
            }

            val requests= snapshot?.documents?.mapNotNull { doc->
                doc.toObject(SatsangRequest:: class.java)?.copy(requestId = doc.id)
            } ?: emptyList()

            trySend(requests)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun respondToRequest(request: SatsangRequest, accept: Boolean): Result<Unit> {
        return try{

            val newStatus = if(accept) "ACCEPTED" else "DECLINED"
            firestore.collection("satsang_requests")
                     .document(request.requestId)
                     .update("status",newStatus)
                     .await()

            if (accept) {
                chatRepository.ensureConversation(request.copy(status = SatsangRequestStatus.ACCEPTED))
            }

            Result.success(Unit)

        }catch (e: Exception){
            Result.failure(e.toAppException())
        }
    }

    private fun connectedUsersFlow(): Flow<List<SatsangRequest>> = callbackFlow {
        val currentUid = auth.getCurrentUserId() ?: run {
            close()
            return@callbackFlow
        }

        // Track both listeners' latest results separately, merge on every update
        var fromResults = listOf<SatsangRequest>()
        var toResults = listOf<SatsangRequest>()

        fun emitMerged() {
            trySend(fromResults + toResults)
        }

        val fromListener = firestore.collection("satsang_requests")
            .whereEqualTo("fromUid", currentUid)
            .whereEqualTo("status", "ACCEPTED")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    emitMerged()
                    return@addSnapshotListener
                }
                fromResults = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(SatsangRequest::class.java)?.copy(requestId = doc.id)
                } ?: emptyList()
                emitMerged()
            }

        val toListener = firestore.collection("satsang_requests")
            .whereEqualTo("toUid", currentUid)
            .whereEqualTo("status", "ACCEPTED")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    emitMerged()
                    return@addSnapshotListener
                }
                toResults = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(SatsangRequest::class.java)?.copy(requestId = doc.id)
                } ?: emptyList()
                emitMerged()
            }

        awaitClose {
            fromListener.remove()
            toListener.remove()
        }
    }

}