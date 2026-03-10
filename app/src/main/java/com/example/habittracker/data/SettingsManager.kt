package com.example.habittracker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode_enabled")
        val REMINDER_ENABLED_KEY = booleanPreferencesKey("reminder_enabled")
        val REMINDER_HOUR_KEY = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE_KEY = intPreferencesKey("reminder_minute")
    }

    private fun completedDatesKey(userId: Int) = stringSetPreferencesKey("completed_dates_$userId")

    suspend fun setDarkMode(isDarkMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDarkMode
        }
    }

    val isDarkModeEnabled: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[DARK_MODE_KEY] ?: false
        }

    suspend fun setReminderEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[REMINDER_ENABLED_KEY] = enabled
        }
    }

    val isReminderEnabled: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[REMINDER_ENABLED_KEY] ?: false
        }

    suspend fun setReminderTime(hour: Int, minute: Int) {
        dataStore.edit { preferences ->
            preferences[REMINDER_HOUR_KEY] = hour
            preferences[REMINDER_MINUTE_KEY] = minute
        }
    }

    val reminderTime: Flow<Pair<Int, Int>> = dataStore.data
        .map { preferences ->
            val hour = preferences[REMINDER_HOUR_KEY] ?: 20
            val minute = preferences[REMINDER_MINUTE_KEY] ?: 0
            Pair(hour, minute)
        }

    fun completedDates(userId: Int): Flow<Set<LocalDate>> = dataStore.data
        .map { preferences ->
            preferences[completedDatesKey(userId)]
                ?.mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }
                ?.toSet()
                ?: emptySet()
        }

    suspend fun setDayCompleted(userId: Int, date: LocalDate, completed: Boolean) {
        val key = completedDatesKey(userId)
        val dateString = date.toString()
        dataStore.edit { preferences ->
            val current = preferences[key] ?: emptySet()
            preferences[key] = if (completed) {
                current + dateString
            } else {
                current - dateString
            }
        }
    }
}
