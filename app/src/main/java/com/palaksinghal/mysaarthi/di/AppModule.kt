package com.palaksinghal.mysaarthi.di

import android.content.Context
import androidx.room.Room
import com.palaksinghal.mysaarthi.data.local.dao.SadhanaDao
import com.palaksinghal.mysaarthi.data.local.dao.ShlokaDao
import com.palaksinghal.mysaarthi.data.local.dao.UserProfileDao
import com.palaksinghal.mysaarthi.data.local.database.GitaDatabase
import com.palaksinghal.mysaarthi.data.local.datasources.UserPreferencesDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserPreferencesDataSource(
        @ApplicationContext context: Context
    ): UserPreferencesDataSource = UserPreferencesDataSource(context)

    @Provides
    @Singleton
    fun provideGitaDatabase(
        @ApplicationContext context: Context
    ): GitaDatabase = Room.databaseBuilder(
        context,
        GitaDatabase::class.java,
        "gita_database"
    )
        .fallbackToDestructiveMigration(true)
        .build()



    @Provides
    @Singleton
    fun provideShlokaDao(gitaDatabase: GitaDatabase): ShlokaDao =gitaDatabase.shlokaDao()

    @Provides
    @Singleton
    fun provideUserProfileDao(gitaDatabase: GitaDatabase): UserProfileDao=gitaDatabase.userProfileDao()

    @Provides
    @Singleton
    fun provideSadhanaDao(database: GitaDatabase): SadhanaDao = database.sadhanaDao()
}