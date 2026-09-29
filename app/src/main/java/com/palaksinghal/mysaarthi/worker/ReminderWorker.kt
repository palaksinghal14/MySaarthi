package com.palaksinghal.mysaarthi.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.palaksinghal.mysaarthi.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import org.jetbrains.annotations.ApiStatus
import javax.inject.Inject


private const val CHANNEL_ID = "sadhana_reminders"
private const val CHANNEL_NAME = "Sadhana Reminders"

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val reminderScheduler: ReminderScheduler
   ):CoroutineWorker(context,workerParams) {

    override suspend fun doWork(): Result {

        val practice = inputData.getString(KEY_PRACTICE)?: return Result.failure()
        val hour= inputData.getInt(KEY_HOUR,7)
        val minute = inputData.getInt(KEY_MINUTE, 0)
        val amPm = inputData.getString(KEY_AM_PM) ?: "AM"

        showNotification(practice)

        reminderScheduler.scheduleReminder(practice,hour,minute,amPm)

       return Result.success()

    }

    fun showNotification( practice : String){

        createChannelIfNeeded()
        val hasPermission = ContextCompat.checkSelfPermission(
            applicationContext, Manifest.permission.POST_NOTIFICATIONS
        )== PackageManager.PERMISSION_GRANTED

        if(!hasPermission) return

        val notification= NotificationCompat.Builder(applicationContext,CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mysaarthi_logo)
            .setContentTitle("Time for $practice")
            .setContentText("A gentle reminder to keep your sadhana today.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(practice.hashCode(), notification)

    }

    fun createChannelIfNeeded(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel= NotificationChannel(
                CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }


    companion object {
        const val KEY_PRACTICE ="practice"
        const val KEY_HOUR = "hour"
        const val KEY_MINUTE = "minute"
        const val KEY_AM_PM = "amPm"
    }
}