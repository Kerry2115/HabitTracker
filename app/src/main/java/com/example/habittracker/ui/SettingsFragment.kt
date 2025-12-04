package com.example.habittracker.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.habittracker.NavigationHost
import com.example.habittracker.R
import com.example.habittracker.Screen
import com.example.habittracker.data.SettingsManager
import com.example.habittracker.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch
import com.example.habittracker.data.SessionManager


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var navigationHost: NavigationHost
    private lateinit var settingsManager: SettingsManager

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

        // Obserwacja stanu trybu ciemnego i ustawienie stanu przełącznika
        viewLifecycleOwner.lifecycleScope.launch {
            settingsManager.isDarkModeEnabled.collect { isDarkMode ->
                // Ustawienie przełącznika bez wywoływania listenera
                binding.switchDarkMode.setOnCheckedChangeListener(null)
                binding.switchDarkMode.isChecked = isDarkMode
                binding.switchDarkMode.setOnCheckedChangeListener { _, checked ->
                    saveThemePreference(checked)
                }
            }
        }

        binding.logoutButton.setOnClickListener {
            // 1. Wyczyść sesję
            val sessionManager = SessionManager(requireContext())
            sessionManager.logout()

            // 2. Przejdź do ekranu startowego
            navigationHost.navigateTo(Screen.Start)
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

        AppCompatDelegate.setDefaultNightMode(mode)
        requireActivity().recreate()
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