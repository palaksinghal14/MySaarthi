package com.palaksinghal.mysaarthi.domain.repository

import com.palaksinghal.mysaarthi.domain.model.User

interface AuthenticationRepo {

    fun getCurrentUserId():String?
    fun isUserSignedIn(): Boolean
    suspend fun loginWithEmail( email:String , password:String): Result<User>
    suspend fun loginWithGoogle( idToken : String): Result<User>
    suspend fun registerWithEmail(email:String , password:String):Result<User>
    fun logout()
}