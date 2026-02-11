package com.example.habittracker.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.habittracker.R

object NotificationChannels {
    const val REMINDER_CHANNEL_ID = "habit_reminders"

    fun ensureReminderChannel(context: Context) {
        if (Build.VERSION.SDK_INT < 26) return

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val existing = manager.getNotificationChannel(REMINDER_CHANNEL_ID)
        if (existing != null) return

        val name = context.getString(R.string.reminder_channel_name)
        val description = context.getString(R.string.reminder_channel_desc)
        val channel = NotificationChannel(
            REMINDER_CHANNEL_ID,
            name,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = description
        manager.createNotificationChannel(channel)
    }
}
