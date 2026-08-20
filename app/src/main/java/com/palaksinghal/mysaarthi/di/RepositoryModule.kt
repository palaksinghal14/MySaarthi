package com.palaksinghal.mysaarthi.di

import com.palaksinghal.mysaarthi.data.repository.AuthenticationRepoImp
import com.palaksinghal.mysaarthi.data.repository.UserProfileRepoImpl
import com.palaksinghal.mysaarthi.domain.repository.AuthenticationRepo
import com.palaksinghal.mysaarthi.domain.repository.UserProfileRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepo(impl : AuthenticationRepoImp): AuthenticationRepo

    @Binds
    @Singleton
    abstract fun bindUserProfileRepo(impl : UserProfileRepoImpl): UserProfileRepo
}

