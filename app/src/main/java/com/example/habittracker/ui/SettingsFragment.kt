package com.example.habittracker.ui

import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.habittracker.NavigationHost
import com.example.habittracker.R
import com.example.habittracker.Screen
import com.example.habittracker.data.SessionManager
import com.example.habittracker.data.SettingsManager
import com.example.habittracker.databinding.FragmentSettingsBinding
import com.example.habittracker.work.HabitReminderScheduler
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var navigationHost: NavigationHost
    private lateinit var settingsManager: SettingsManager

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                saveReminderEnabled(true)
            } else {
                binding.switchReminders.isChecked = false
                saveReminderEnabled(false)
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NavigationHost) {
            navigationHost = context
        } else {
            throw RuntimeException("$context must implement NavigationHost")
        }
        settingsManager = SettingsManager(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarSettings.title = getString(R.string.settings_title)

        binding.toolbarSettings.setNavigationOnClickListener {
            navigationHost.navigateTo(Screen.Dashboard)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            settingsManager.isDarkModeEnabled.collect { isDarkMode ->
                binding.switchDarkMode.setOnCheckedChangeListener(null)
                binding.switchDarkMode.isChecked = isDarkMode
                binding.switchDarkMode.setOnCheckedChangeListener { _, checked ->
                    saveThemePreference(checked)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            settingsManager.isReminderEnabled.collect { enabled ->
                binding.switchReminders.setOnCheckedChangeListener(null)
                binding.switchReminders.isChecked = enabled
                binding.switchReminders.setOnCheckedChangeListener { _, checked ->
                    if (checked) {
                        ensureNotificationPermissionThenEnable()
                    } else {
                        saveReminderEnabled(false)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            settingsManager.reminderTime.collect { (hour, minute) ->
                binding.textReminderTime.text = String.format("%02d:%02d", hour, minute)
            }
        }

        binding.buttonSetReminderTime.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val (hour, minute) = settingsManager.reminderTime.first()
                showTimePicker(hour, minute)
            }
        }

        binding.logoutButton.setOnClickListener {
            val sessionManager = SessionManager(requireContext())
            sessionManager.logout()
            navigationHost.navigateTo(Screen.Start)
        }
    }

    private fun ensureNotificationPermissionThenEnable() {
        if (Build.VERSION.SDK_INT >= 33) {
            val granted = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }
        saveReminderEnabled(true)
    }

    private fun showTimePicker(initialHour: Int, initialMinute: Int) {
        val dialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                viewLifecycleOwner.lifecycleScope.launch {
                    settingsManager.setReminderTime(hourOfDay, minute)
                    val enabled = settingsManager.isReminderEnabled.first()
                    if (enabled) {
                        HabitReminderScheduler.scheduleDailyReminder(
                            requireContext(),
                            hourOfDay,
                            minute
                        )
                    }
                }
            },
            initialHour,
            initialMinute,
            true
        )
        dialog.show()
    }

    private fun saveReminderEnabled(enabled: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {
            settingsManager.setReminderEnabled(enabled)
            if (enabled) {
                val (hour, minute) = settingsManager.reminderTime.first()
                HabitReminderScheduler.scheduleDailyReminder(
                    requireContext(),
                    hour,
                    minute
                )
            } else {
                HabitReminderScheduler.cancelDailyReminder(requireContext())
            }
        }
    }

    private fun saveThemePreference(isDarkMode: Boolean) {
        val mode = if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }

        viewLifecycleOwner.lifecycleScope.launch {
            settingsManager.setDarkMode(isDarkMode)
        }

        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}
