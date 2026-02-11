package com.example.habittracker.work

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.habittracker.R
import com.example.habittracker.api.RetrofitClient
import com.example.habittracker.data.SessionManager

class HabitReminderWorker(
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
            val habits = RetrofitClient.service.getHabits(userId)
            val incompleteCount = habits.count { it.progress < 1.0f }
            if (incompleteCount > 0) {
                showNotification(incompleteCount)
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("HabitReminderWorker", "Reminder failed", e)
            Result.retry()
        }
    }

    private fun showNotification(incompleteCount: Int) {
        if (Build.VERSION.SDK_INT >= 33) {
            val granted = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        NotificationChannels.ensureReminderChannel(applicationContext)

        val body = applicationContext.getString(
            R.string.reminder_body_count,
            incompleteCount
        )

        val notification = NotificationCompat.Builder(
            applicationContext,
            NotificationChannels.REMINDER_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(applicationContext.getString(R.string.reminder_title))
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(1001, notification)
    }
}
