package com.notifyme.app.data.repository

import com.notifyme.app.data.db.ReminderDao
import com.notifyme.app.data.db.ReminderEntity
import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val dao: ReminderDao) {

    val allReminders: Flow<List<ReminderEntity>> = dao.getAllReminders()

    suspend fun getEnabledReminders(): List<ReminderEntity> = dao.getEnabledReminders()

    suspend fun getReminderById(id: Long): ReminderEntity? = dao.getReminderById(id)

    suspend fun insert(reminder: ReminderEntity): Long = dao.insert(reminder)

    suspend fun update(reminder: ReminderEntity) = dao.update(reminder)

    suspend fun delete(reminder: ReminderEntity) = dao.delete(reminder)

    suspend fun deleteById(id: Long) = dao.deleteById(id)
}
