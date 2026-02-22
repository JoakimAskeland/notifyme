package com.notifyme.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.notifyme.app.NotifyMeApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_REMINDER_ID = "reminder_id"
        const val EXTRA_TITLE = "title"
        const val EXTRA_MESSAGE = "message"
        const val EXTRA_IS_RECURRING = "is_recurring"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(EXTRA_REMINDER_ID, -1)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Reminder"
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: ""
        val isRecurring = intent.getBooleanExtra(EXTRA_IS_RECURRING, false)

        NotificationHelper.showNotification(context, reminderId, title, message)

        if (isRecurring && reminderId != -1L) {
            val app = context.applicationContext as NotifyMeApplication
            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val reminder = app.repository.getReminderById(reminderId)
                    if (reminder != null && reminder.isEnabled) {
                        val nextTrigger = AlarmScheduler.scheduleNext(context, reminder)
                        if (nextTrigger != null) {
                            app.repository.update(reminder.copy(triggerAtMillis = nextTrigger))
                        }
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
