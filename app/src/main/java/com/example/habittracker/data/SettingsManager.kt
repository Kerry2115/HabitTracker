package com.example.habittracker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Utworzenie obiektu DataStore na poziomie pliku
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(context: Context) {

    private val dataStore = context.dataStore

    // Klucz do przechowywania stanu trybu ciemnego
    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode_enabled")
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
            // Domyślnie tryb ciemny jest wyłączony (false)
            preferences[DARK_MODE_KEY] ?: false
        }
}