package com.palaksinghal.mysaarthi.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object Keys {
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
        val REMINDER_AM_PM = stringPreferencesKey("reminder_am_pm")
    }

    val reminderTime: Flow<ReminderTime> = context.dataStore.data.map { preferences ->
        ReminderTime(
            hour = preferences[Keys.REMINDER_HOUR] ?: 5,
            minute = preferences[Keys.REMINDER_MINUTE] ?: 0,
            amPm = preferences[Keys.REMINDER_AM_PM] ?: "AM"
        )
    }

    suspend fun saveReminderTime(hour: Int, minute: Int, amPm: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.REMINDER_HOUR] = hour
            preferences[Keys.REMINDER_MINUTE] = minute
            preferences[Keys.REMINDER_AM_PM] = amPm
        }
    }
}

data class ReminderTime(
    val hour: Int,
    val minute: Int,
    val amPm: String
)