package com.example.habittracker.data

// Model danych wysyłanych do API (username i password)
data class UserCredentials(
    val username: String,
    val password: String
)