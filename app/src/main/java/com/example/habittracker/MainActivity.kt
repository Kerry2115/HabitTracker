package com.example.habittracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.habittracker.data.SettingsManager
import com.example.habittracker.databinding.ActivityMainBinding
import com.example.habittracker.ui.AddHabitFragment
import com.example.habittracker.ui.DashboardFragment
import com.example.habittracker.ui.ScannerFragment
import com.example.habittracker.ui.SettingsFragment
import com.example.habittracker.ui.SplashFragment
import com.example.habittracker.ui.StartFragment
import com.example.habittracker.work.HabitReminderScheduler
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

interface NavigationHost {
    fun navigateTo(screen: Screen)
}

sealed class Screen {
    data object Splash : Screen()
    data object Start : Screen()
    data object Dashboard : Screen()
    data object Settings : Screen()
    data object AddHabit : Screen()
    data object Scanner : Screen()
}


class MainActivity : AppCompatActivity(), NavigationHost {

    private lateinit var binding: ActivityMainBinding
    private lateinit var settingsManager: SettingsManager
    private var isBottomNavProgrammatic = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingsManager = SettingsManager(applicationContext)
        applyTheme()
        scheduleReminderIfEnabled()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNav()

        if (savedInstanceState == null) {
            navigateTo(Screen.Splash)
        }
    }

    private fun applyTheme() {
        lifecycleScope.launch {
            val isDarkMode = settingsManager.isDarkModeEnabled.first()
            val mode = if (isDarkMode) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            if (AppCompatDelegate.getDefaultNightMode() != mode) {
                AppCompatDelegate.setDefaultNightMode(mode)
            }
        }
    }

    private fun scheduleReminderIfEnabled() {
        lifecycleScope.launch {
            val enabled = settingsManager.isReminderEnabled.first()
            if (enabled) {
                val (hour, minute) = settingsManager.reminderTime.first()
                HabitReminderScheduler.scheduleDailyReminder(this@MainActivity, hour, minute)
            }
        }
    }

    override fun navigateTo(screen: Screen) {
        val fragment = when (screen) {
            Screen.Splash -> SplashFragment.newInstance()
            Screen.Start -> StartFragment.newInstance()
            Screen.Dashboard -> DashboardFragment.newInstance()
            Screen.Settings -> SettingsFragment.newInstance()
            Screen.AddHabit -> AddHabitFragment.newInstance()
            Screen.Scanner -> ScannerFragment.newInstance()
        }

        binding.bottomNav.visibility = when (screen) {
            Screen.Dashboard, Screen.Scanner -> android.view.View.VISIBLE
            else -> android.view.View.GONE
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        when (screen) {
            Screen.Dashboard -> setBottomNavSelection(R.id.nav_habits)
            Screen.Scanner -> setBottomNavSelection(R.id.nav_scanner)
            else -> Unit
        }
    }

    private fun setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            if (isBottomNavProgrammatic) return@setOnItemSelectedListener true
            when (item.itemId) {
                R.id.nav_habits -> {
                    navigateTo(Screen.Dashboard)
                    true
                }
                R.id.nav_scanner -> {
                    navigateTo(Screen.Scanner)
                    true
                }
                else -> false
            }
        }
    }

    private fun setBottomNavSelection(itemId: Int) {
        if (binding.bottomNav.selectedItemId == itemId) return
        isBottomNavProgrammatic = true
        binding.bottomNav.selectedItemId = itemId
        isBottomNavProgrammatic = false
    }
}
