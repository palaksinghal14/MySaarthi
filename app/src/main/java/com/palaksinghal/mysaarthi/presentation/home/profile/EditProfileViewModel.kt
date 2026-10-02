package com.palaksinghal.mysaarthi.presentation.home.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palaksinghal.mysaarthi.domain.model.AppException
import com.palaksinghal.mysaarthi.domain.model.PracticeReminder
import com.palaksinghal.mysaarthi.domain.model.UserProfile
import com.palaksinghal.mysaarthi.domain.repository.AuthenticationRepo
import com.palaksinghal.mysaarthi.domain.repository.LocationRepository
import com.palaksinghal.mysaarthi.domain.repository.UserProfileRepo
import com.palaksinghal.mysaarthi.worker.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authRepo: AuthenticationRepo,
    private val userProfileRepo: UserProfileRepo,
    private val reminderScheduler: ReminderScheduler,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState = _uiState.asStateFlow()

    // Hold the full profile so unchanged fields are preserved on save
    private var currentProfile: UserProfile? = null

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val uid = authRepo.getCurrentUserId() ?: return@launch
            userProfileRepo.getUserProfile(uid)
                .onSuccess { profile ->
                    currentProfile = profile
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            spiritualIntro = profile?.spiritualIntro ?: "",
                            isOpenToSatsang = profile?.isOpenToSatsang ?: false,
                            practices = profile?.practices ?: emptyList(),
                            practiceReminders = profile?.practiceReminders?:emptyList()
                        )
                    }
                }
                .onFailure {
                    _uiState.update { state ->
                        state.copy(isLoading = false)
                    }
                }
        }
    }

    fun updateSpiritualIntro(value: String) {
        _uiState.update { it.copy(spiritualIntro = value) }
    }

    fun updateIsOpenToSatsang(value: Boolean) {
        _uiState.update { it.copy(isOpenToSatsang = value) }
    }

    fun togglePractice(practice: String) {
        _uiState.update { current ->

            val updatedPractices: List<String>
            val updatedReminders: List<Map<String, Any>>

            if (practice in current.practices) {
                // Removing a practice — also remove its reminder
                updatedPractices = current.practices - practice
                updatedReminders = current.practiceReminders.filter {
                    it["practice"] != practice
                }
            } else {
                // Adding a practice — create a default reminder for it
                updatedPractices = current.practices + practice
                updatedReminders = current.practiceReminders + defaultReminderFor(practice)
            }

            current.copy(
                practices = updatedPractices,
                practiceReminders = updatedReminders
            )
        }
    }

    fun updateCustomPracticeInput(value: String) {
        _uiState.update { it.copy(customPracticeInput = value) }
    }

    fun addCustomPractice() {
        val practice = _uiState.value.customPracticeInput.trim()
        if (practice.isBlank() || practice in _uiState.value.practices) return
        _uiState.update {
            it.copy(
                practices = it.practices + practice,
                practiceReminders = it.practiceReminders + defaultReminderFor(practice),
                customPracticeInput = ""
            )
        }
    }

    fun removeCustomPractice(practice: String) {
        _uiState.update { it.copy(
            practices = it.practices - practice,
            practiceReminders = it.practiceReminders.filter { r -> r["practice"] != practice }
        ) }
    }

    // Update reminder time for a specific practice
    fun updateReminderTime(practice: String, hour: Int, minute: Int, amPm: String) {
        _uiState.update { current ->
            val updated = current.practiceReminders.map { reminder ->
                if (reminder["practice"] == practice) {
                    reminder.toMutableMap().apply {
                        this["hour"] = hour.toLong()
                        this["minute"] = minute.toLong()
                        this["amPm"] = amPm
                    }
                } else reminder
            }
            current.copy(practiceReminders = updated)
        }
    }

    // Toggle a specific reminder on/off
    fun toggleReminderEnabled(practice: String, enabled: Boolean) {
        _uiState.update { current ->
            val updated = current.practiceReminders.map { reminder ->
                if (reminder["practice"] == practice) {
                    reminder.toMutableMap().apply {
                        this["isEnabled"] = enabled
                    }
                } else reminder
            }
            current.copy(practiceReminders = updated)
        }
    }

    fun saveProfile() {
        val profile = currentProfile ?: return
        val state = _uiState.value

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val updatedProfile = profile.copy(
                spiritualIntro = state.spiritualIntro.trim(),
                isOpenToSatsang = state.isOpenToSatsang,
                practices = state.practices,
                practiceReminders = state.practiceReminders
            )

            userProfileRepo.saveUserProfile(updatedProfile)
                .onSuccess {

                    // Convert List<Map<String, Any>> → List<PracticeReminder>
                    val oldPractices = profile.practices

                    val typedReminders = state.practiceReminders.map { map ->
                        PracticeReminder(
                            practice = map["practice"] as? String ?: "",
                            hour = (map["hour"] as? Long)?.toInt() ?: 7,
                            minute = (map["minute"] as? Long)?.toInt() ?: 0,
                            amPm = map["amPm"] as? String ?: "AM",
                            isEnabled = map["isEnabled"] as? Boolean ?: true
                        )
                    }
                    android.util.Log.d("AlarmDebug", "oldPractices = $oldPractices")
                    android.util.Log.d("AlarmDebug", "new typedReminders = $typedReminders")
                    // Cancel old ones first, then set fresh ones —
                    // handles practices being removed, added, or times changed
                    reminderScheduler.cancelAllReminders(oldPractices)
                    reminderScheduler.scheduleAllReminders(typedReminders)

                    if (updatedProfile.isOpenToSatsang) {
                        locationRepository.updateUserLocation()
                            .onFailure { throwable ->
                                android.util.Log.e("LocationUpdate", "Edit profile location write failed: ${throwable.message}", throwable)
                            }
                    }
                    _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
                }
                .onFailure { throwable ->
                    val exception = throwable as? AppException
                        ?: AppException.UnknownException(throwable.message)
                    _uiState.update { it.copy(isSaving = false, error = exception) }
                }
        }
    }

    fun resetSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
}

private fun defaultReminderFor(practice: String): Map<String, Any> {
    val (hour, minute, amPm) = when (practice.lowercase()) {
        "brahma muhurta" -> Triple(4, 30, "AM")
        "satwik diet" -> Triple(12, 0, "PM")
        "meditation" -> Triple(6, 0, "PM")
        "kirtan / chanting" -> Triple(7, 0, "PM")
        else -> Triple(7, 0, "AM")
    }
    return mapOf(
        "practice" to practice,
        "hour" to hour.toLong(),
        "minute" to minute.toLong(),
        "amPm" to amPm,
        "isEnabled" to true
    )
}