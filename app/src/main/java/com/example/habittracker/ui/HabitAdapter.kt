package com.example.habittracker.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.data.Habit
import com.example.habittracker.databinding.ItemHabitBinding

// Interfejs do komunikacji z Fragmentem (DashboardFragment)
interface HabitUpdateListener {
    fun onHabitProgressChanged(habit: Habit, isCompleted: Boolean)
    fun onHabitDeleted(habit: Habit)
}

class HabitAdapter(
    private val habits: MutableList<Habit>,
    private val updateListener: HabitUpdateListener
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    inner class HabitViewHolder(private val binding: ItemHabitBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(habit: Habit) {
            binding.habitName.text = habit.name

            // Ustawienie paska postepu
            binding.progressBar.progress = (habit.progress * 100).toInt()

            // Ustawienie checkboxa
            binding.habitCheckbox.setOnCheckedChangeListener(null)
            binding.habitCheckbox.isChecked = habit.progress >= 1.0f

            // LOGIKA ZAZNACZANIA POSTEPU
            binding.habitCheckbox.setOnCheckedChangeListener { _, isChecked ->
                val newProgress = if (isChecked) 1.0f else 0.0f
                val updatedHabit = habit.copy(progress = newProgress)

                val index = habits.indexOf(habit)
                if (index != -1) {
                    habits[index] = updatedHabit
                }

                updateListener.onHabitProgressChanged(updatedHabit, isChecked)

                binding.progressBar.progress = (newProgress * 100).toInt()
            }

            // Usuwanie z przycisku
            binding.habitDeleteButton.setOnClickListener {
                updateListener.onHabitDeleted(habit)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habits[position])
    }

    override fun getItemCount() = habits.size

    // Funkcja do usuwania elementu (uzywana przez DashboardFragment)
    fun removeHabit(habit: Habit) {
        val index = habits.indexOf(habit)
        if (index != -1) {
            habits.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun updateData(newHabits: List<Habit>) {
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged()
    }
}
