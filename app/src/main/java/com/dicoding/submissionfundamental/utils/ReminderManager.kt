package com.dicoding.submissionfundamental.utils

import ReminderWorker
import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class ReminderManager(private val context: Context) {
    private val workManager = WorkManager.getInstance(context)
    private val sharedPreferences = context.getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)

    fun scheduleReminder() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val reminderWork = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            reminderWork
        )
        saveReminderState(true)
    }

    fun cancelReminder() {
        workManager.cancelUniqueWork(WORK_NAME)
        saveReminderState(false)
    }

    fun saveReminderState(isActive: Boolean) {
        sharedPreferences.edit().putBoolean(REMINDER_STATE_KEY, isActive).apply()
    }

    fun getReminderState(): Boolean {
        return sharedPreferences.getBoolean(REMINDER_STATE_KEY, false)
    }

    companion object {
        private const val WORK_NAME = "EventReminderWork"
        private const val REMINDER_STATE_KEY = "reminder_state"
    }
}