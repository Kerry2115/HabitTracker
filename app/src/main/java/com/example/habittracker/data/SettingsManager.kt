package com.example.habittracker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Utworzenie obiektu DataStore na poziomie pliku
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(context: Context) {

    private val dataStore = context.dataStore

    // Klucze do przechowywania ustawien
    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode_enabled")
        val REMINDER_ENABLED_KEY = booleanPreferencesKey("reminder_enabled")
        val REMINDER_HOUR_KEY = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE_KEY = intPreferencesKey("reminder_minute")
    }

    // 1. Zapisywanie stanu trybu ciemnego
    suspend fun setDarkMode(isDarkMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDarkMode
        }
    }

    // 2. Odczytywanie stanu trybu ciemnego jako Flow
    val isDarkModeEnabled: Flow<Boolean> = dataStore.data
        .map { preferences ->
            // Domyslnie tryb ciemny jest wylaczony (false)
            preferences[DARK_MODE_KEY] ?: false
        }

    // 3. Zapisywanie/odczyt ustawien powiadomien
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
}
