package com.example.habittracker.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.habittracker.api.RetrofitClient
import com.example.habittracker.data.ResetHabitsRequest
import com.example.habittracker.data.SessionManager

class ResetHabitsWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val sessionManager = SessionManager(applicationContext)
        val userId = sessionManager.getUserId()
        if (userId == -1) {
            return Result.success()
        }

        return try {
            val response = RetrofitClient.service.resetHabits(ResetHabitsRequest(userId))
            if (response.success) {
                Result.success()
            } else {
                Log.e("ResetHabitsWorker", "Reset failed: ${response.message}")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e("ResetHabitsWorker", "Reset failed", e)
            Result.retry()
        }
    }
}
