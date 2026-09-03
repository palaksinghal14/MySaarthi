package com.palaksinghal.mysaarthi.data.repository

import com.palaksinghal.mysaarthi.core.utils.toAppException
import com.palaksinghal.mysaarthi.data.local.dao.ShlokaDao
import com.palaksinghal.mysaarthi.data.local.datasources.GitaLocalDataSource
import com.palaksinghal.mysaarthi.data.local.datasources.UserPreferencesDataSource
import com.palaksinghal.mysaarthi.data.local.entity.ShlokaEntity
import com.palaksinghal.mysaarthi.data.local.entity.toShloka
import com.palaksinghal.mysaarthi.domain.model.AppException
import com.palaksinghal.mysaarthi.domain.model.Shloka
import com.palaksinghal.mysaarthi.domain.repository.ShlokaRepo
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ShlokaRepoImpl @Inject constructor(
    private val gitaLocalDataSource: GitaLocalDataSource,
    private val userPreferencesDataSource: UserPreferencesDataSource,
    private val shlokaDao: ShlokaDao

) : ShlokaRepo{
    override suspend fun getTodayShloka():Result<Shloka>{
        // only the first time -> fetch shlokas from json file and save them in room and then return shloka
        // then if room is empty do the above this else fetch shloka from room
        return try {
            // First launch — Room is empty, load from assets
             if(shlokaDao.getShlokaCount()==0){
                val gitaString= gitaLocalDataSource.readGitaJson()
                 val shlokasDto= gitaLocalDataSource.toGitaDto(gitaString)
                 val shlokasEntity =shlokasDto.mapIndexed { index, dto ->
                     ShlokaEntity(
                         shlokaNumber = index+1,
                         chapter =dto.chapter,
                         verse = dto.verse,
                         sanskrit = dto.sanskrit,
                         hindi = dto.hindi,
                         english = dto.english,
                         transliteration = dto.transliteration
                     )
                 }
                 shlokaDao.addAllShloka(shlokasEntity)
             }

            val currentNumber =userPreferencesDataSource.currentShlokaIndex.first()
            val entity= shlokaDao.getShlokaByNumber(currentNumber)
            val shloka = entity?.toShloka() ?: return Result.failure(AppException.DatabaseException)
            Result.success(shloka)

        }catch (e: Exception){
            Result.failure(e.toAppException())
        }
    }

    override suspend fun advanceToNextShloka() {
      userPreferencesDataSource.advanceShloka()
    }
}