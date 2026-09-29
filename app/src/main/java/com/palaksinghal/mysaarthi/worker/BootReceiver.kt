package com.palaksinghal.mysaarthi.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.palaksinghal.mysaarthi.domain.repository.AuthenticationRepo
import com.palaksinghal.mysaarthi.domain.repository.UserProfileRepo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    @Inject
    lateinit var authRepo: AuthenticationRepo

    @Inject
    lateinit var userProfileRepo: UserProfileRepo

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        CoroutineScope(Dispatchers.IO).launch {
            val uid = authRepo.getCurrentUserId() ?: return@launch
            val profile = userProfileRepo.getUserProfile(uid).getOrNull() ?: return@launch

            val typedReminders = profile.practiceReminders.map { map ->
                com.palaksinghal.mysaarthi.domain.model.PracticeReminder(
                    practice = map["practice"] as? String ?: "",
                    hour = (map["hour"] as? Long)?.toInt() ?: 7,
                    minute = (map["minute"] as? Long)?.toInt() ?: 0,
                    amPm = map["amPm"] as? String ?: "AM",
                    isEnabled = map["isEnabled"] as? Boolean ?: true
                )
            }

            reminderScheduler.scheduleAllReminders(typedReminders)
        }
    }
}