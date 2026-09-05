package com.palaksinghal.mysaarthi.di

import com.palaksinghal.mysaarthi.data.repository.AuthenticationRepoImp
import com.palaksinghal.mysaarthi.data.repository.SadhanaRepositoryImpl
import com.palaksinghal.mysaarthi.data.repository.ShlokaRepoImpl
import com.palaksinghal.mysaarthi.data.repository.UserProfileRepoImpl
import com.palaksinghal.mysaarthi.domain.repository.AuthenticationRepo
import com.palaksinghal.mysaarthi.domain.repository.SadhanaRepository
import com.palaksinghal.mysaarthi.domain.repository.ShlokaRepo
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

    @Binds
    @Singleton
    abstract fun bindShlokaRepo( impl : ShlokaRepoImpl): ShlokaRepo

    @Binds
    @Singleton
    abstract fun bindSadhanaRepository(impl: SadhanaRepositoryImpl): SadhanaRepository
}

