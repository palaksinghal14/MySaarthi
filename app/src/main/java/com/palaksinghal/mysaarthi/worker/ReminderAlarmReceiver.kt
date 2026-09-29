package com.palaksinghal.mysaarthi.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.palaksinghal.mysaarthi.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val CHANNEL_ID = "sadhana_reminders"
private const val CHANNEL_NAME = "Sadhana Reminders"

@AndroidEntryPoint
class ReminderAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val practice = intent.getStringExtra(KEY_PRACTICE) ?: return
        val hour = intent.getIntExtra(KEY_HOUR, 7)
        val minute = intent.getIntExtra(KEY_MINUTE, 0)
        val amPm = intent.getStringExtra(KEY_AM_PM) ?: "AM"

        showNotification(context, practice)

        // Re-arm tomorrow's alarm for this same practice
        reminderScheduler.scheduleReminder(practice, hour, minute, amPm)
    }

    private fun showNotification(context: Context, practice: String) {
        createChannelIfNeeded(context)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mysaarthi_logo)
            .setContentTitle("Time for $practice")
            .setContentText("A gentle reminder to keep your sadhana today.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(practice.hashCode(), notification)
    }

    private fun createChannelIfNeeded(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val KEY_PRACTICE = "practice"
        const val KEY_HOUR = "hour"
        const val KEY_MINUTE = "minute"
        const val KEY_AM_PM = "amPm"
    }
}