package com.example.habittracker.data

data class Habit(
    val id: Int = 0,
    val name: String,
    val progress: Float = 0f,
    val user_id: Int = 0
)
