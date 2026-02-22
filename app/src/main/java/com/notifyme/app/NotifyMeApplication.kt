package com.notifyme.app

import android.app.Application
import com.notifyme.app.data.db.AppDatabase
import com.notifyme.app.data.repository.ReminderRepository
import com.notifyme.app.notification.NotificationHelper

class NotifyMeApplication : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val repository: ReminderRepository by lazy { ReminderRepository(database.reminderDao()) }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
    }
}
