package com.palaksinghal.mysaarthi.data.repository

import androidx.compose.runtime.snapshotFlow
import com.google.firebase.firestore.FirebaseFirestore
import com.palaksinghal.mysaarthi.core.utils.toAppException
import com.palaksinghal.mysaarthi.domain.model.AppException
import com.palaksinghal.mysaarthi.domain.model.SatsangRequest
import com.palaksinghal.mysaarthi.domain.repository.AuthenticationRepo
import com.palaksinghal.mysaarthi.domain.repository.SatsangRequestRepository
import com.palaksinghal.mysaarthi.domain.repository.UserProfileRepo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SatsangReqRepoImpl @Inject constructor(
    private val auth: AuthenticationRepo,
    private val userRepo: UserProfileRepo,
    private val firestore: FirebaseFirestore
) : SatsangRequestRepository {

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

    override fun getIncomingSatsangReq(): Flow<List<SatsangRequest>> = callbackFlow {
        val currentUid= auth.getCurrentUserId() ?: run{
            close()
            return@callbackFlow
        }

        val listener= firestore.collection("satsang_requests").
                whereEqualTo("toUid", currentUid).
                whereEqualTo("status","PENDING").
                addSnapshotListener { snapshot,error ->
                    if(error!=null){
                        close(error)
                        return@addSnapshotListener
                    }

                    val requests= snapshot?.documents?.mapNotNull { doc->
                        doc.toObject(SatsangRequest:: class.java)?.copy(requestId = doc.id)
                    } ?: emptyList()

                    trySend(requests)
                }
        awaitClose { listener.remove() }

    }

    override fun getOutgoingSatsangReq(): Flow<List<SatsangRequest>> = callbackFlow {

        val currentUid= auth.getCurrentUserId() ?: run{
            close()
            return@callbackFlow
        }

        val listener= firestore.collection("satsang_requests").
        whereEqualTo("fromUid", currentUid).
        addSnapshotListener { snapshot,error ->
            if(error!=null){
                close(error)
                return@addSnapshotListener
            }

            val requests= snapshot?.documents?.mapNotNull { doc->
                doc.toObject(SatsangRequest:: class.java)?.copy(requestId = doc.id)
            } ?: emptyList()

            trySend(requests)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun respondToRequest(requestId: String, accept: Boolean): Result<Unit> {
        return try{

            val newStatus = if(accept) "ACCEPTED" else "DECLINED"
            firestore.collection("satsang_requests")
                     .document(requestId)
                     .update("status",newStatus)
                     .await()
            Result.success(Unit)

        }catch (e: Exception){
            Result.failure(e.toAppException())
        }
    }

    override fun getConnectedUsers(): Flow<List<SatsangRequest>> = callbackFlow {
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
                    close(error)
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
                    close(error)
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