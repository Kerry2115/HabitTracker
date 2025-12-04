package com.example.habittracker.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.api.RetrofitClient
import com.example.habittracker.data.Habit
import kotlinx.coroutines.launch

class HabitsViewModel : ViewModel() {

    private val _habits = MutableLiveData<List<Habit>>()
    val habits: LiveData<List<Habit>> = _habits

    // Pobiera nawyki z bazy
    fun loadHabits(userId: Int) {
        viewModelScope.launch {
            try {
                val list = RetrofitClient.service.getHabits(userId)
                _habits.postValue(list)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Dodaje nawyk do bazy
    fun addHabit(userId: Int, name: String) {
        viewModelScope.launch {
            try {
                val newHabit = Habit(name = name, user_id = userId)
                val response = RetrofitClient.service.addHabit(newHabit)
                if (response.success) {
                    loadHabits(userId) // Odśwież listę po dodaniu
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Usuwa nawyk z bazy
    fun deleteHabit(userId: Int, habit: Habit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.service.deleteHabit(habit)
                if (response.success) {
                    loadHabits(userId) // Odśwież listę po usunięciu
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}