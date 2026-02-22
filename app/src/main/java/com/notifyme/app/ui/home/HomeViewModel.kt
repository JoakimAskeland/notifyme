package com.notifyme.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.notifyme.app.NotifyMeApplication
import com.notifyme.app.data.db.ReminderEntity
import com.notifyme.app.notification.AlarmScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as NotifyMeApplication
    private val repository = app.repository

    val reminders: StateFlow<List<ReminderEntity>> = repository.allReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            AlarmScheduler.cancel(getApplication(), reminder)
            repository.delete(reminder)
        }
    }

    fun toggleReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            val updated = reminder.copy(isEnabled = !reminder.isEnabled)
            repository.update(updated)
            if (updated.isEnabled) {
                AlarmScheduler.schedule(getApplication(), updated)
            } else {
                AlarmScheduler.cancel(getApplication(), updated)
            }
        }
    }
}
