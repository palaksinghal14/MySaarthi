package com.palaksinghal.mysaarthi.data.repository


import com.google.firebase.auth.FirebaseAuth
import com.palaksinghal.mysaarthi.data.local.dao.SadhanaDao
import com.palaksinghal.mysaarthi.data.local.dao.UserProfileDao
import com.palaksinghal.mysaarthi.data.local.entity.SadhanaEntryEntity
import com.palaksinghal.mysaarthi.data.local.entity.toDomain
import com.palaksinghal.mysaarthi.domain.model.SadhanaEntry
import com.palaksinghal.mysaarthi.domain.repository.SadhanaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class SadhanaRepositoryImpl @Inject constructor(
    private val sadhanaDao: SadhanaDao,
    private val auth: FirebaseAuth,
    private val userProfileDao: UserProfileDao
) : SadhanaRepository{
    override fun getTodaysSadhana(): Flow<List<SadhanaEntry>> =flow{

        val today = LocalDate.now().toString()
        val uid= auth.currentUser?.uid

        val existing= sadhanaDao.getEntriesForDate(today).first()

        if(existing.isEmpty() && uid!=null){

            val profile = userProfileDao.getUserProfile(uid)

            val practices =profile?.practices ?:emptyList()

            val entries=practices.map { practice->
                SadhanaEntryEntity(
                date = today,
                practice = practice,
                isCompleted = false
            ) }

            if(entries.isNotEmpty()){
                sadhanaDao.insertAll(entries)
            }
        }

        emitAll(
            sadhanaDao.getEntriesForDate(today).map { entities ->
                entities.map {
                    it.toDomain()
                }

            }
        )

    }


    override suspend fun togglePractice(
        date: String,
        practice: String,
        isCompleted: Boolean
    ) {
         sadhanaDao.updateCompletion(date,practice,isCompleted)
    }

}