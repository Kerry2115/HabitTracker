package com.example.habittracker.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittracker.NavigationHost
import com.example.habittracker.Screen
import com.example.habittracker.R
import com.example.habittracker.data.Habit
import com.example.habittracker.data.SessionManager
import com.example.habittracker.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment(), HabitUpdateListener {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var navigationHost: NavigationHost
    private lateinit var sessionManager: SessionManager
    private val viewModel: HabitsViewModel by viewModels()

    private val REQUEST_KEY = "new_habit_request"
    private val BUNDLE_KEY = "new_habit_name"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NavigationHost) {
            navigationHost = context
        } else {
            throw RuntimeException("$context must implement NavigationHost")
        }
        sessionManager = SessionManager(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = sessionManager.getUserId()

        binding.recyclerViewHabits.layoutManager = LinearLayoutManager(context)
        val adapter = HabitAdapter(mutableListOf(), this)
        binding.recyclerViewHabits.adapter = adapter

        viewModel.habits.observe(viewLifecycleOwner) { habits ->
            adapter.updateData(habits)
        }

        if (userId != -1) {
            viewModel.loadHabits(userId)
        }

        parentFragmentManager.setFragmentResultListener(REQUEST_KEY, viewLifecycleOwner) { _, bundle ->
            val newHabitName = bundle.getString(BUNDLE_KEY)
            if (newHabitName != null && userId != -1) {
                viewModel.addHabit(userId, newHabitName)
            }
        }

        binding.toolbarDashboard.title = getString(R.string.dashboard_title)
        binding.toolbarDashboard.inflateMenu(R.menu.dashboard_menu)

        binding.toolbarDashboard.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_settings -> {
                    navigationHost.navigateTo(Screen.Settings)
                    true
                }
                else -> false
            }
        }

        binding.fabAddHabit.setOnClickListener {
            navigationHost.navigateTo(Screen.AddHabit)
        }
    }


    override fun onHabitDeleted(habit: Habit) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_dialog_title))
            .setMessage(getString(R.string.delete_dialog_message, habit.name))
            .setPositiveButton(getString(R.string.delete_confirm)) { _, _ ->
                viewModel.deleteHabit(sessionManager.getUserId(), habit)
            }
            .setNegativeButton(getString(R.string.delete_cancel), null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object { @JvmStatic fun newInstance() = DashboardFragment() }

    override fun onHabitProgressChanged(habit: Habit, isChecked: Boolean) {
        viewModel.updateHabitProgress(habit, isChecked)
    }
}