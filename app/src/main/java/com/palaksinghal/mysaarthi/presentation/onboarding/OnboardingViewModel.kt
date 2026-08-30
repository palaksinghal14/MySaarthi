package com.palaksinghal.mysaarthi.presentation.onboarding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palaksinghal.mysaarthi.data.local.UserPreferencesDataSource
import com.palaksinghal.mysaarthi.domain.model.AppException
import com.palaksinghal.mysaarthi.domain.model.PracticeReminder
import com.palaksinghal.mysaarthi.domain.model.UserProfile
import com.palaksinghal.mysaarthi.domain.repository.UserProfileRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Default reminder times per practice
private fun defaultReminderFor(practice: String): PracticeReminder {
    val (hour, minute, amPm) = when (practice.lowercase()) {
        "brahma muhurta" -> Triple(4, 30, "AM")
        "satwik diet" -> Triple(12, 0, "PM")
        "meditation" -> Triple(6, 0, "PM")
        "kirtan / chanting" -> Triple(7, 0, "PM")
        else -> Triple(7, 0, "AM") // daily paath + custom practices
    }
    return PracticeReminder(practice = practice, hour = hour, minute = minute, amPm = amPm)
}

data class OnboardingFormState(
    val displayName: String = "",
    val selectedPractices: List<String> = emptyList(),
    val practiceReminders: List<PracticeReminder> = emptyList(),
    val customPracticeInput: String = "",
    val howLongOnPath: String = "",
    val isOpenToSatsang: Boolean = false,
    val spiritualIntro: String = "",
    val useLocation: Boolean = false,

    )

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userProfileRepo: UserProfileRepo,
    private val userPreferences: UserPreferencesDataSource
) : ViewModel() {

    // One StateFlow for the entire form
    private val _formState = MutableStateFlow(OnboardingFormState())
    val formState = _formState.asStateFlow()

    // Separate StateFlow for the save operation
    private val _saveState = MutableStateFlow<OnboardingUiState>(OnboardingUiState.Idle)
    val saveState = _saveState.asStateFlow()

    // Step 1 — name
    fun updateDisplayName(name: String) {
        _formState.update { it.copy(displayName = name) }
    }

    // Step 2 — toggle practice + auto-manage reminder
    fun togglePractice(practice: String) {
        _formState.update { current ->
            if (practice in current.selectedPractices) {
                // Remove practice and its reminder
                current.copy(
                    selectedPractices = current.selectedPractices - practice,
                    practiceReminders = current.practiceReminders.filter { it.practice != practice }
                )
            } else {
                // Add practice and create default reminder for it
                current.copy(
                    selectedPractices = current.selectedPractices + practice,
                    practiceReminders = current.practiceReminders + defaultReminderFor(practice)
                )
            }
        }
    }

    // Step 2 — custom practice input field value
    fun updateCustomPracticeInput(value: String) {
        _formState.update { it.copy(customPracticeInput = value) }
    }

    // Step 2 — add custom practice on button tap
    fun addCustomPractice() {
        val practice = _formState.value.customPracticeInput.trim()
        if (practice.isBlank()) return
        if (practice in _formState.value.selectedPractices) return // already added
        _formState.update { current ->
            current.copy(
                selectedPractices = current.selectedPractices + practice,
                practiceReminders = current.practiceReminders + defaultReminderFor(practice),
                customPracticeInput = "" // clear input after adding
            )
        }
    }
    // Step 3 — how long on path
    fun updateHowLongOnPath(value: String) {
        _formState.update { it.copy(howLongOnPath = value) }
    }

    // Step 4 — update reminder time for a specific practice
    fun updatePracticeReminderTime(practice: String, hour: Int, minute: Int, amPm: String) {
        _formState.update { current ->
            current.copy(
                practiceReminders = current.practiceReminders.map { reminder ->
                    if (reminder.practice == practice)
                        reminder.copy(hour = hour, minute = minute, amPm = amPm)
                    else reminder
                }
            )
        }
    }

    // Step 4 — toggle individual reminder on/off
    fun togglePracticeReminder(practice: String, enabled: Boolean) {
        _formState.update { current ->
            current.copy(
                practiceReminders = current.practiceReminders.map { reminder ->
                    if (reminder.practice == practice) reminder.copy(isEnabled = enabled)
                    else reminder
                }
            )
        }
    }

    // Step 5 — satsang openness and optional spiritual intro
    fun updateIsOpenToSatsang(value: Boolean) {
        _formState.update { it.copy(isOpenToSatsang = value) }
    }

    fun updateSpiritualIntro(value: String) {
        _formState.update { it.copy(spiritualIntro = value) }
    }
    fun updateUseLocation(value: Boolean) {
        _formState.update { it.copy(useLocation = value) }
    }

    // // Final step — save everything
    fun completeOnboarding() {
        val form = _formState.value

        // Validate
        if (form.displayName.isBlank() ||form.selectedPractices.isEmpty() ||form.howLongOnPath.isBlank() ) {
            _saveState.value = OnboardingUiState.Error(AppException.EmptyFieldsException)
            return
        }

        viewModelScope.launch {
            _saveState.value = OnboardingUiState.Loading

            // Convert PracticeReminder list to List<Map<String, Any>> for Firestore
            val remindersForFirestore = form.practiceReminders.map { reminder ->
                mapOf(
                    "practice" to reminder.practice,
                    "hour" to reminder.hour,
                    "minute" to reminder.minute,
                    "amPm" to reminder.amPm,
                    "isEnabled" to reminder.isEnabled
                )
            }
            val userProfile = UserProfile(
                displayName = form.displayName.trim(),
                practices = form.selectedPractices,
                howLongOnPath = form.howLongOnPath,
                isOpenToSatsang = form.isOpenToSatsang,
                spiritualIntro = form.spiritualIntro.trim(),
                onboardingCompleted = true,
                practiceReminders = remindersForFirestore
            )

            userProfileRepo.saveUserProfile(userProfile)
                .onSuccess {
                    // Save reminder time to DataStore after Firestore succeeds
                    userPreferences.savePracticeReminders(form.practiceReminders)
                    _saveState.value = OnboardingUiState.Success
                }
                .onFailure { throwable ->
                    val exception = throwable as? AppException
                        ?: AppException.UnknownException(throwable.message)
                    _saveState.value = OnboardingUiState.Error(exception)
                }
        }
    }

    fun resetSaveState() {
        _saveState.value = OnboardingUiState.Idle
    }

    fun removeCustomPractice(practice: String) {
        _formState.update { current ->
            current.copy(
                selectedPractices = current.selectedPractices - practice,
                practiceReminders = current.practiceReminders.filter { it.practice != practice }
            )
        }
    }
}