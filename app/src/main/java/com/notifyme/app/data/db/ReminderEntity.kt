package com.notifyme.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String = "",
    val triggerAtMillis: Long,
    val isRecurring: Boolean = false,
    val recurringType: String? = null,    // DAILY, WEEKLY, CUSTOM
    val recurringDays: String? = null,    // comma-separated day numbers (1=Mon, 7=Sun) for WEEKLY
    val recurringIntervalMinutes: Long? = null, // for CUSTOM interval
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
