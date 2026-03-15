package com.marwadiuniversity.fitlife

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*

class DailyTaskManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("daily_tasks", Context.MODE_PRIVATE)

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Save completion of a task
    fun markTaskCompleted(taskKey: String) {
        prefs.edit().putBoolean(taskKey, true).apply()
        prefs.edit().putString("last_completed_date", today()).apply()
    }

    // Check if a task is completed
    fun isTaskCompleted(taskKey: String): Boolean {
        return prefs.getBoolean(taskKey, false)
    }

    // Reset all tasks if a new day has started
    fun resetIfNewDay() {
        val lastDate = prefs.getString("last_completed_date", "")
        val todayDate = today()

        // If day changed → reset all tasks
        if (lastDate != todayDate) {
            prefs.edit().clear().apply()
            prefs.edit().putString("last_completed_date", todayDate).apply()
        }
    }

    // Get today's date formatted as yyyy-MM-dd
    private fun today(): String {
        return dateFormat.format(Date())
    }
}
