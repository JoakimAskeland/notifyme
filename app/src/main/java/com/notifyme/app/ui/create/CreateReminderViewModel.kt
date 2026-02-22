package com.notifyme.app.ui.create

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.notifyme.app.NotifyMeApplication
import com.notifyme.app.data.db.ReminderEntity
import com.notifyme.app.notification.AlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class CreateReminderState(
    val title: String = "",
    val message: String = "",
    val year: Int = Calendar.getInstance().get(Calendar.YEAR),
    val month: Int = Calendar.getInstance().get(Calendar.MONTH),
    val day: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
    val hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
    val minute: Int = Calendar.getInstance().get(Calendar.MINUTE),
    val isRecurring: Boolean = false,
    val recurringType: String = "DAILY",
    val customIntervalMinutes: Long = 60,
    val isSaving: Boolean = false,
    val isValid: Boolean = false
)

class CreateReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as NotifyMeApplication
    private val repository = app.repository

    private val _state = MutableStateFlow(CreateReminderState())
    val state: StateFlow<CreateReminderState> = _state

    fun updateTitle(title: String) {
        _state.value = _state.value.copy(title = title, isValid = title.isNotBlank())
    }

    fun updateMessage(message: String) {
        _state.value = _state.value.copy(message = message)
    }

    fun updateDate(year: Int, month: Int, day: Int) {
        _state.value = _state.value.copy(year = year, month = month, day = day)
    }

    fun updateTime(hour: Int, minute: Int) {
        _state.value = _state.value.copy(hour = hour, minute = minute)
    }

    fun updateRecurring(isRecurring: Boolean) {
        _state.value = _state.value.copy(isRecurring = isRecurring)
    }

    fun updateRecurringType(type: String) {
        _state.value = _state.value.copy(recurringType = type)
    }

    fun updateCustomInterval(minutes: Long) {
        _state.value = _state.value.copy(customIntervalMinutes = minutes)
    }

    fun save(onComplete: () -> Unit) {
        val s = _state.value
        if (s.title.isBlank()) return

        _state.value = s.copy(isSaving = true)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, s.year)
            set(Calendar.MONTH, s.month)
            set(Calendar.DAY_OF_MONTH, s.day)
            set(Calendar.HOUR_OF_DAY, s.hour)
            set(Calendar.MINUTE, s.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val reminder = ReminderEntity(
            title = s.title.trim(),
            message = s.message.trim(),
            triggerAtMillis = calendar.timeInMillis,
            isRecurring = s.isRecurring,
            recurringType = if (s.isRecurring) s.recurringType else null,
            recurringIntervalMinutes = if (s.isRecurring && s.recurringType == "CUSTOM") s.customIntervalMinutes else null
        )

        viewModelScope.launch {
            val id = repository.insert(reminder)
            val saved = reminder.copy(id = id)
            AlarmScheduler.schedule(getApplication(), saved)
            onComplete()
        }
    }
}
