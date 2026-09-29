package com.palaksinghal.mysaarthi.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.palaksinghal.mysaarthi.domain.model.PracticeReminder
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

private const val REMINDER_TAG = "sadhana_reminder"

@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // Schedules a single practice's reminder — used both for the very first
    // scheduling (onboarding/edit profile) and for the daily self-reschedule
    // that ReminderWorker triggers after each run
    fun scheduleReminder(practice: String, hour: Int, minute: Int, amPm: String) {
        val delayMillis = calculateDelayMillis(hour, minute, amPm)

        val inputData = Data.Builder()
            .putString(ReminderWorker.KEY_PRACTICE, practice)
            .putInt(ReminderWorker.KEY_HOUR, hour)
            .putInt(ReminderWorker.KEY_MINUTE, minute)
            .putString(ReminderWorker.KEY_AM_PM, amPm)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag(REMINDER_TAG)
            .build()

        // Unique name per practice — re-scheduling the same practice
        // replaces its previous pending request instead of stacking a duplicate
        val uniqueName = "reminder_$practice"

        WorkManager.getInstance(context)
            .enqueueUniqueWork(uniqueName, ExistingWorkPolicy.REPLACE, workRequest)
    }

    // Schedules every enabled reminder from a user's full list at once —
    // called right after onboarding completes, and after Edit Profile saves
    fun scheduleAllReminders(reminders: List<PracticeReminder>) {
        reminders
            .filter { it.isEnabled }
            .forEach { reminder ->
                scheduleReminder(reminder.practice, reminder.hour, reminder.minute, reminder.amPm)
            }
    }

    // Cancels every scheduled reminder — useful if reminders are disabled
    // entirely, or before re-scheduling a fresh full set from Edit Profile
    fun cancelAllReminders() {
        WorkManager.getInstance(context).cancelAllWorkByTag(REMINDER_TAG)
    }

    // Converts 12-hour + AM/PM into the exact delay (in milliseconds) until
    // that clock time next occurs — today if it hasn't passed yet, else tomorrow
    private fun calculateDelayMillis(hour: Int, minute: Int, amPm: String): Long {
        val hour24 = to24Hour(hour, amPm)

        val now = LocalDateTime.now()
        var target = now.toLocalDate().atTime(LocalTime.of(hour24, minute))

        if (target.isBefore(now) || target.isEqual(now)) {
            target = target.plusDays(1)
        }

        val delay = Duration.between(now, target).toMillis()
        android.util.Log.d("ReminderDebug", "now=$now, target=$target, delayMillis=$delay (~${delay / 60000} minutes)")
        return delay

    }

    // Handles the classic 12-hour clock edge cases: 12 AM = hour 0,
    // 12 PM = hour 12, everything else just shifts by 12 for PM
    private fun to24Hour(hour: Int, amPm: String): Int {
        return when {
            amPm.equals("AM", ignoreCase = true) && hour == 12 -> 0
            amPm.equals("PM", ignoreCase = true) && hour != 12 -> hour + 12
            else -> hour
        }
    }
}