package com.palaksinghal.mysaarthi.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.palaksinghal.mysaarthi.core.constants.ChatConstants
import com.palaksinghal.mysaarthi.core.constants.ChatConstants.PAGE_SIZE
import com.palaksinghal.mysaarthi.core.utils.conversationId
import com.palaksinghal.mysaarthi.core.utils.conversationParticipants
import com.palaksinghal.mysaarthi.core.utils.toAppException
import com.palaksinghal.mysaarthi.data.remote.dto.ConversationDto
import com.palaksinghal.mysaarthi.data.remote.dto.MessageDto
import com.palaksinghal.mysaarthi.data.remote.dto.toDomain
import com.palaksinghal.mysaarthi.di.ApplicationScope
import com.palaksinghal.mysaarthi.domain.model.AppException
import com.palaksinghal.mysaarthi.domain.model.ChatMessage
import com.palaksinghal.mysaarthi.domain.model.Conversation
import com.palaksinghal.mysaarthi.domain.model.SatsangRequest
import com.palaksinghal.mysaarthi.domain.model.otherPerson
import com.palaksinghal.mysaarthi.domain.repository.AuthenticationRepo
import com.palaksinghal.mysaarthi.domain.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class ChatRepoImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AuthenticationRepo,
    @ApplicationScope appScope: CoroutineScope
) : ChatRepository{


    private val conversationsSharedFlow: StateFlow<List<Conversation>> = callbackFlow{
        val myUid= auth.getCurrentUserId()?: run{
            close()
            return@callbackFlow
        }

        val listener= firestore.collection(ChatConstants.CONVERSATIONS)
            .whereArrayContains(ChatConstants.PARTICIPANTS,myUid)
            .orderBy(ChatConstants.LAST_MESSAGE_AT, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot ,error ->
                if(error!=null){
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val conversations= snapshot?.documents?.mapNotNull { doc->
                    doc.toObject(ConversationDto::class.java)?.toDomain(myUid,doc.id)
                }?:emptyList()

                android.util.Log.d("ChatRepo", "conversations=${conversations.map { it.otherUserName to it.id }}")
                trySend(conversations)


            }
        awaitClose { listener.remove() }
    }.stateIn(
        scope = appScope,
        started = SharingStarted.WhileSubscribed( stopTimeoutMillis = 5_000, replayExpirationMillis = 0),
        initialValue = emptyList()
    )

    override fun observeConversations(): Flow<List<Conversation>> = conversationsSharedFlow



    override fun observeMessages(convId: String): Flow<List<ChatMessage>> =callbackFlow{

        val myUid= auth.getCurrentUserId()?: run{
            close()
            return@callbackFlow
        }

        val listener= firestore.collection(ChatConstants.CONVERSATIONS)
            .document(convId)
            .collection(ChatConstants.MESSAGES)
            .whereArrayContains(ChatConstants.PARTICIPANTS ,myUid)
            .orderBy(ChatConstants.CREATED_AT,Query.Direction.DESCENDING)
            .limit(PAGE_SIZE.toLong())
            .addSnapshotListener { snapshot ,error ->
                if(error!=null){
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val messages= snapshot?.documents?.mapNotNull { doc->
                    doc.toObject(MessageDto::class.java)?.toDomain(myUid,doc.id ,doc.metadata.hasPendingWrites())
                }?:emptyList()

                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun sendMessage(
        convId: String,
        otherUid: String,
        text: String
    ): Result<Unit> {
        return try {
            val myUid= auth.getCurrentUserId()?: return Result.failure(AppException.UserNotFoundException)

            val trimmed = text.trim()
            if (trimmed.isEmpty() || trimmed.length > ChatConstants.MAX_MESSAGE_LENGTH) {
                return Result.failure(AppException.UnknownException("Message is not valid "))   // use whatever yours is
            }

            val messageId = UUID.randomUUID().toString()
            val convRef= firestore.collection(ChatConstants.CONVERSATIONS).document(convId)
            val msgRef= convRef.collection(ChatConstants.MESSAGES).document(messageId)

            val messageDto= MessageDto(
                id=messageId,
                senderId=myUid,
                text=trimmed,
                participants=conversationParticipants(myUid,otherUid)

            )
            val batch=firestore.batch()
            batch.set(msgRef,messageDto)
            batch.update(
                convRef,
                mapOf(
                           ChatConstants.LAST_MESSAGE to trimmed,
                           ChatConstants.LAST_MESSAGE_AT to FieldValue.serverTimestamp(),
                           ChatConstants.LAST_SENDER_ID to myUid,
                           "${ChatConstants.UNREAD}.$otherUid" to FieldValue.increment(1)

                )
            )
            batch.commit().await()

           Result.success(Unit)
        }catch (e: Exception){
            Result.failure(e.toAppException())
        }
    }


    override suspend fun ensureConversation(request: SatsangRequest): Result<String>  // returning converation id
    {
       return try{

           android.util.Log.d("ChatRepo", "ensureConversation called, requestId=${request.requestId}")
           val myUid= auth.getCurrentUserId()?: return Result.failure(AppException.UserNotFoundException)
           val (otherUid , otherName) = request.otherPerson(myUid)
           val myName= if(myUid== request.fromUid) request.fromDisplayName else request.toDisplayName


           val participants = conversationParticipants(myUid, otherUid)
           val convId = conversationId(myUid, otherUid)

           val dto = ConversationDto(
               participants = participants,
               participantNames = mapOf(myUid to myName, otherUid to otherName),
               requestId = request.requestId,
               unread = mapOf(participants[0] to 0, participants[1] to 0)
           )
           android.util.Log.d("ChatRepo", "about to write convId=$convId")
           firestore.collection(ChatConstants.CONVERSATIONS)
               .document(convId)
               .set(dto)
               .await()
           android.util.Log.d("ChatRepo", "write succeeded")
           Result.success(convId)

       }catch(e: Exception){
           android.util.Log.e("ChatRepo", "ensureConversation failed", e)
           Result.failure(e.toAppException())
       }
    }

    override suspend fun markRead(convId: String): Result<Unit> {
        return try {
            val myUid = auth.getCurrentUserId()
                ?: return Result.failure(AppException.UserNotFoundException)

            firestore.collection(ChatConstants.CONVERSATIONS)
                .document(convId)
                .update("${ChatConstants.UNREAD}.$myUid", 0)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e.toAppException())
        }
    }

    override suspend fun loadOlderMessages(
        convId: String,
        beforeSentAt: Long
    ): Result<List<ChatMessage>> {
        return try {
            val myUid = auth.getCurrentUserId()
                ?: return Result.failure(AppException.UserNotFoundException)

            val snapshot = firestore.collection(ChatConstants.CONVERSATIONS)
                .document(convId)
                .collection(ChatConstants.MESSAGES)
                .whereArrayContains(ChatConstants.PARTICIPANTS ,myUid)
                .orderBy(ChatConstants.CREATED_AT, Query.Direction.DESCENDING)
                .startAfter(Timestamp(Date(beforeSentAt)))
                .limit(ChatConstants.PAGE_SIZE.toLong())
                .get()
                .await()

            val messages = snapshot.documents.mapNotNull { doc ->
                doc.toObject(MessageDto::class.java)?.toDomain(myUid, doc.id, isPending = false)
            }

            Result.success(messages)
        } catch (e: Exception) {
            Result.failure(e.toAppException())
        }
    }

    override suspend fun setBlocked(
        convId: String,
        otherUid: String,
        blocked: Boolean
    ): Result<Unit> {
        return try {
            val myUid = auth.getCurrentUserId()
                ?: return Result.failure(AppException.UserNotFoundException)

            val op = if (blocked) FieldValue.arrayUnion(myUid) else FieldValue.arrayRemove(myUid)

            firestore.collection(ChatConstants.CONVERSATIONS)
                .document(convId)
                .update(ChatConstants.BLOCKED_BY, op)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e.toAppException())
        }
    }

    override suspend fun reportUser(
        otherUid: String,
        convId: String,
        reason: String
    ): Result<Unit> {
        return try {
            val myUid = auth.getCurrentUserId()
                ?: return Result.failure(AppException.UserNotFoundException)

            firestore.collection(ChatConstants.REPORTS)
                .add(
                    mapOf(
                        "reporterUid" to myUid,
                        "reportedUid" to otherUid,
                        "convId" to convId,
                        "reason" to reason,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e.toAppException())
        }
    }
}