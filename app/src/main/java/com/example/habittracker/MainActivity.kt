package com.example.habittracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.habittracker.data.SettingsManager
import com.example.habittracker.databinding.ActivityMainBinding
import com.example.habittracker.ui.DashboardFragment
import com.example.habittracker.ui.SettingsFragment
import com.example.habittracker.ui.SplashFragment
import com.example.habittracker.ui.StartFragment
import com.example.habittracker.ui.AddHabitFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// ----------------------------------------------------
// KLUCZOWE INTERFEJSY I ENUMY
// ----------------------------------------------------

interface NavigationHost {
    fun navigateTo(screen: Screen)
}

sealed class Screen {
    data object Splash : Screen()
    data object Start : Screen()
    data object Dashboard : Screen()
    data object Settings : Screen()
    data object AddHabit : Screen()
}

// ----------------------------------------------------

class MainActivity : AppCompatActivity(), NavigationHost {

    private lateinit var binding: ActivityMainBinding
    private lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // Musimy zainicjować menedżer przed super.onCreate, aby zastosować motyw
        settingsManager = SettingsManager(applicationContext)

        // Zastosowanie zapisanego motywu przed wczytaniem widoku
        applyTheme()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Uruchamiamy ekran powitalny
        if (savedInstanceState == null) {
            navigateTo(Screen.Splash)
        }
    }

    private fun applyTheme() {
        // Używamy coroutine, ale tylko do odczytu stanu.
        // Jeśli ten kod jest poprawny, motyw powinien być stosowany globalnie.
        lifecycleScope.launch {
            val isDarkMode = settingsManager.isDarkModeEnabled.first()
            val mode = if (isDarkMode) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    override fun navigateTo(screen: Screen) {
        val fragment = when (screen) {
            Screen.Splash -> SplashFragment.newInstance()
            Screen.Start -> StartFragment.newInstance()
            Screen.Dashboard -> DashboardFragment.newInstance()
            Screen.Settings -> SettingsFragment.newInstance()
            Screen.AddHabit -> AddHabitFragment.newInstance()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}