package com.example.habittracker.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.habittracker.databinding.FragmentAddHabitBinding
import com.example.habittracker.NavigationHost
import com.example.habittracker.Screen
import com.example.habittracker.R

class AddHabitFragment : Fragment() {

    private var _binding: FragmentAddHabitBinding? = null
    private val binding get() = _binding!!
    private lateinit var navigationHost: NavigationHost

    private val REQUEST_KEY = "new_habit_request"
    private val BUNDLE_KEY = "new_habit_name"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NavigationHost) {
            navigationHost = context
        } else {
            throw RuntimeException("$context must implement NavigationHost")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddHabitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarAddHabit.title = getString(R.string.add_habit_title)

        // OBSŁUGA PRZYCISKU WSTECZ NA TOOLBARZE
        binding.toolbarAddHabit.setNavigationOnClickListener {
            navigationHost.navigateTo(Screen.Dashboard)
        }

        // Logika zapisu
        binding.saveButton.setOnClickListener {
            saveHabit()
        }
    }

    private fun saveHabit() {
        val habitName = binding.habitNameEditText.text.toString().trim()

        if (habitName.isEmpty()) {
            binding.habitNameEditText.error = getString(R.string.habit_name_required)
            return
        }

        val result = Bundle().apply {
            putString(BUNDLE_KEY, habitName)
        }

        parentFragmentManager.setFragmentResult(REQUEST_KEY, result)

        // Powrót do Dashboard po zapisie
        navigationHost.navigateTo(Screen.Dashboard)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddHabitFragment()
    }
}