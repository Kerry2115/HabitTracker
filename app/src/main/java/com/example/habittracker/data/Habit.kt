package com.example.habittracker.data

data class Habit(
    val id: Int = 0,       // ID z bazy danych
    val name: String,
    val progress: Float = 0f,
    val user_id: Int = 0   // ID uĹĽytkownika, do ktĂłrego naleĹĽy nawyk
)
