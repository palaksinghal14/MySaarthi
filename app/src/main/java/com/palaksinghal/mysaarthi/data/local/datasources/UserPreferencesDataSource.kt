package com.palaksinghal.mysaarthi.data.local.datasources

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.palaksinghal.mysaarthi.domain.model.PracticeReminder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object Keys {
        val PRACTICE_REMINDERS = stringPreferencesKey("practice_reminders")
        val CURRENT_SHLOKA_INDEX = intPreferencesKey("current_shloka_index")
    }

    // Read reminders as a Flow — emits whenever reminders change
    val practiceReminders: Flow<List<PracticeReminder>> = context.dataStore.data.map { preferences ->
        val json = preferences[Keys.PRACTICE_REMINDERS] ?: "[]"
        try {
            Json.decodeFromString<List<PracticeReminder>>(json)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Save the full list of practice reminders
    suspend fun savePracticeReminders(reminders: List<PracticeReminder>) {
        context.dataStore.edit { preferences ->
            preferences[Keys.PRACTICE_REMINDERS] = Json.encodeToString(reminders)
        }
    }


    // for storing user's current shloka progress
    val currentShlokaIndex: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[Keys.CURRENT_SHLOKA_INDEX] ?: 1  // default: start at verse 1
    }

    suspend fun advanceShloka() {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.CURRENT_SHLOKA_INDEX] ?: 1
            prefs[Keys.CURRENT_SHLOKA_INDEX] = if (current >= 700) 1 else current + 1
        }
    }
}