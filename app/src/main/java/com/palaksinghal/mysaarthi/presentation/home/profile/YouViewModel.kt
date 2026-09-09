package com.palaksinghal.mysaarthi.presentation.home.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palaksinghal.mysaarthi.data.local.dao.SadhanaDao
import com.palaksinghal.mysaarthi.domain.model.AppException
import com.palaksinghal.mysaarthi.domain.model.DailyCompletionRate
import com.palaksinghal.mysaarthi.domain.repository.AuthenticationRepo
import com.palaksinghal.mysaarthi.domain.repository.UserProfileRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class YouViewModel @Inject constructor(
    private val authRepo: AuthenticationRepo,
    private val userProfileRepo: UserProfileRepo,
    private val sadhanaDao: SadhanaDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(YouUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeProfile()
        loadStreakData()
    }

    fun observeProfile(){
        viewModelScope.launch {
            val uid = authRepo.getCurrentUserId()?: return@launch
            userProfileRepo.observeUserProfile(uid).collect { profile ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        displayName = profile?.displayName?:"",
                        spiritualIntro = profile?.spiritualIntro ?: "",
                        practices = profile?.practices ?: emptyList(),
                        howLongOnPath = profile?.howLongOnPath ?: "",
                        isOpenToSatsang = profile?.isOpenToSatsang ?: false
                    )
                }
            }
        }
    }
    fun loadStreakData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }


            val completedDates =  sadhanaDao.getAllCompletedDates()
            val startDate = LocalDate.now().minusDays(29).toString()
            val last30Data = sadhanaDao.getCompletionRatioByDate(startDate)

            val stats = calculateStats(completedDates)
            val grid = buildLast30DaysGrid(last30Data)

                    _uiState.update {
                        it.copy(
                            currentStreak = stats.first,
                            longestStreak = stats.second,
                            daysPracticed = stats.third,
                            last30Days = grid
                        )
                    }
        }
    }

    fun signOut() {
        authRepo.logout()
    }

    private fun calculateStats(completedDates: List<String>): Triple<Int, Int, Int> {
        if (completedDates.isEmpty()) return Triple(0, 0, 0)

        val dates = completedDates.map { LocalDate.parse(it) }.sorted()
        val daysPracticed = dates.size

        // Calculate longest streak
        var longestStreak = 1
        var tempStreak = 1
        for (i in 1 until dates.size) {
            if (dates[i] == dates[i - 1].plusDays(1)) {
                tempStreak++
                longestStreak = maxOf(longestStreak, tempStreak)
            } else {
                tempStreak = 1
            }
        }

        // Calculate current streak — count backwards from today
        var currentStreak = 0
        var checkDate = LocalDate.now()
        while (dates.contains(checkDate)) {
            currentStreak++
            checkDate = checkDate.minusDays(1)
        }

        return Triple(currentStreak, longestStreak, daysPracticed)
    }

    private fun buildLast30DaysGrid(
        data: List<DailyCompletionRate>
    ): List<Float> {
        val today = LocalDate.now()
        val ratioByDate = data.associate { it.date to it.completed.toFloat() / it.total }
        return (29 downTo 0).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong()).toString()
            ratioByDate[date] ?: 0f
        }
    }
}