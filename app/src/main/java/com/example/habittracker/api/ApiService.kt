package com.example.habittracker.api

import com.example.habittracker.data.AuthResponse
import com.example.habittracker.data.Habit
import com.example.habittracker.data.ResetHabitsRequest
import com.example.habittracker.data.UserCredentials
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    companion object {
        const val BASE_URL = "http://10.0.2.2/habit_api/"
    }

    @POST("register.php")
    suspend fun registerUser(@Body credentials: UserCredentials): AuthResponse

    @POST("login.php")
    suspend fun loginUser(@Body credentials: UserCredentials): AuthResponse

    @GET("get_habits.php")
    suspend fun getHabits(@Query("user_id") userId: Int): List<Habit>

    @POST("add_habit.php")
    suspend fun addHabit(@Body habit: Habit): AuthResponse

    @POST("delete_habit.php")
    suspend fun deleteHabit(@Body habit: Habit): AuthResponse

    // TEJ LINII PRAWDOPODOBNIE CI BRAKOWAŁO:
    @POST("update_habit.php")
    suspend fun updateHabit(@Body habit: Habit): AuthResponse

    @POST("reset_habits.php")
    suspend fun resetHabits(@Body request: ResetHabitsRequest): AuthResponse
}
