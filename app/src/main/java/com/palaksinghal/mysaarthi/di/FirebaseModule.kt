package com.palaksinghal.mysaarthi.di

import dagger.Binds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.palaksinghal.mysaarthi.data.repository.AuthenticationRepoImp
import com.palaksinghal.mysaarthi.domain.repository.AuthenticationRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth() : FirebaseAuth= FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore() : FirebaseFirestore= FirebaseFirestore.getInstance()

}

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule{

    @Binds
    @Singleton
    abstract fun bindAuthRepo(impl : AuthenticationRepoImp): AuthenticationRepo
}
