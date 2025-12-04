package com.example.habittracker.data

import com.google.gson.annotations.SerializedName

// Model danych otrzymywanych z PHP po logowaniu/rejestracji
data class AuthResponse(
    val success: Boolean,
    val message: String,
    @SerializedName("user_id")
    val userId: Int? = null
)