package com.example.habittracker.data

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val success: Boolean,
    val message: String,
    @SerializedName("user_id")
    val userId: Int? = null
)