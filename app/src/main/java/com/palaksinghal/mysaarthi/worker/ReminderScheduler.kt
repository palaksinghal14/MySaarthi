package com.palaksinghal.mysaarthi.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.palaksinghal.mysaarthi.domain.model.PracticeReminder
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun scheduleReminder(practice: String, hour: Int, minute: Int, amPm: String) {
        android.util.Log.d("AlarmDebug", "Scheduling '$practice' (requestCode=${practice.hashCode()}) for $hour:$minute $amPm")
        val delayMillis = calculateDelayMillis(hour, minute, amPm)
        val triggerAtMillis = System.currentTimeMillis() + delayMillis

        val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            putExtra(ReminderAlarmReceiver.KEY_PRACTICE, practice)
            putExtra(ReminderAlarmReceiver.KEY_HOUR, hour)
            putExtra(ReminderAlarmReceiver.KEY_MINUTE, minute)
            putExtra(ReminderAlarmReceiver.KEY_AM_PM, amPm)
        }

        // Unique request code per practice — same purpose as WorkManager's
        // unique work name: re-scheduling replaces this exact alarm, not a duplicate
        val requestCode = practice.hashCode()

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

    fun scheduleAllReminders(reminders: List<PracticeReminder>) {
        reminders
            .filter { it.isEnabled }
            .forEach { reminder ->
                scheduleReminder(reminder.practice, reminder.hour, reminder.minute, reminder.amPm)
            }
    }

    fun cancelReminder(practice: String) {
        android.util.Log.d("AlarmDebug", "Cancelling '$practice' (requestCode=${practice.hashCode()})")
        val intent = Intent(context, ReminderAlarmReceiver::class.java)
        val requestCode = practice.hashCode()

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    fun cancelAllReminders(practices: List<String>) {
        practices.forEach { cancelReminder(it) }
    }

    private fun calculateDelayMillis(hour: Int, minute: Int, amPm: String): Long {
        val hour24 = to24Hour(hour, amPm)
        val now = LocalDateTime.now()
        var target = now.toLocalDate().atTime(LocalTime.of(hour24, minute))

        if (target.isBefore(now) || target.isEqual(now)) {
            target = target.plusDays(1)
        }

        return Duration.between(now, target).toMillis()
    }

    private fun to24Hour(hour: Int, amPm: String): Int {
        return when {
            amPm.equals("AM", ignoreCase = true) && hour == 12 -> 0
            amPm.equals("PM", ignoreCase = true) && hour != 12 -> hour + 12
            else -> hour
        }
    }
}