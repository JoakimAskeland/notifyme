package com.notifyme.app.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.notifyme.app.data.db.ReminderEntity

object AlarmScheduler {

    fun schedule(context: Context, reminder: ReminderEntity) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val intent = createIntent(context, reminder)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminder.triggerAtMillis,
            intent
        )
    }

    fun cancel(context: Context, reminder: ReminderEntity) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val intent = createIntent(context, reminder)
        alarmManager.cancel(intent)
    }

    fun scheduleNext(context: Context, reminder: ReminderEntity): Long? {
        if (!reminder.isRecurring) return null

        val nextTrigger = calculateNextTrigger(reminder) ?: return null
        val updated = reminder.copy(triggerAtMillis = nextTrigger)
        schedule(context, updated)
        return nextTrigger
    }

    private fun calculateNextTrigger(reminder: ReminderEntity): Long? {
        val now = System.currentTimeMillis()

        return when (reminder.recurringType) {
            "DAILY" -> {
                val interval = 24 * 60 * 60 * 1000L
                var next = reminder.triggerAtMillis
                while (next <= now) {
                    next += interval
                }
                next
            }
            "WEEKLY" -> {
                val interval = 7 * 24 * 60 * 60 * 1000L
                var next = reminder.triggerAtMillis
                while (next <= now) {
                    next += interval
                }
                next
            }
            "CUSTOM" -> {
                val intervalMs = (reminder.recurringIntervalMinutes ?: return null) * 60 * 1000
                var next = reminder.triggerAtMillis
                while (next <= now) {
                    next += intervalMs
                }
                next
            }
            else -> null
        }
    }

    private fun createIntent(context: Context, reminder: ReminderEntity): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_REMINDER_ID, reminder.id)
            putExtra(AlarmReceiver.EXTRA_TITLE, reminder.title)
            putExtra(AlarmReceiver.EXTRA_MESSAGE, reminder.message)
            putExtra(AlarmReceiver.EXTRA_IS_RECURRING, reminder.isRecurring)
        }
        return PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
