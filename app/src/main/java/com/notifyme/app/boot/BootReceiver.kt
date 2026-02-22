package com.notifyme.app.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.notifyme.app.NotifyMeApplication
import com.notifyme.app.notification.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val app = context.applicationContext as NotifyMeApplication
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reminders = app.repository.getEnabledReminders()
                for (reminder in reminders) {
                    AlarmScheduler.schedule(context, reminder)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
