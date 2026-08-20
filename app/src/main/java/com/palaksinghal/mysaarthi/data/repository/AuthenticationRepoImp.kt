package com.palaksinghal.mysaarthi.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.palaksinghal.mysaarthi.core.utils.toAppException
import com.palaksinghal.mysaarthi.domain.model.AppException
import com.palaksinghal.mysaarthi.domain.model.User
import com.palaksinghal.mysaarthi.domain.repository.AuthenticationRepo
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthenticationRepoImp @Inject constructor(
    private val auth: FirebaseAuth
) : AuthenticationRepo{

    override fun isUserSignedIn(): Boolean {
        return auth.currentUser!=null
    }

    override fun logout() {
         auth.signOut()
    }

    override suspend fun loginWithEmail(email: String, password: String): Result<User> {
        return try {
            val result= auth.signInWithEmailAndPassword(email,password).await()
            val user= result.user?:return Result.failure(AppException.UserNotFoundException)
            Result.success(user.toDomainUser())
        }catch (e:Exception){
            Result.failure(e.toAppException())
        }
    }

    override suspend fun loginWithGoogle( idToken: String): Result<User> {
        TODO("Not yet implemented")
    }

    override suspend fun registerWithEmail(email: String, password: String): Result<User>{
        return try {
            val result= auth.createUserWithEmailAndPassword(email,password).await()
            val user= result.user?:return Result.failure(AppException.UnknownException("No user returned"))
            Result.success(user.toDomainUser())
        }catch (e:Exception){
            Result.failure(e.toAppException())
        }
    }

}

private fun FirebaseUser.toDomainUser() =User(
    uid = uid,
    email = email,
    displayName = displayName,
    imageUrl = photoUrl?.toString()
)
