package com.example.zero.data

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

object Const {
    const val PATH_USERS = "users"
    const val PATH_UID = "uid"
    const val PATH_USERNAME = "username"
    const val PATH_AVATAR_ID = "avatarId"
    const val PATH_USERPOINTS = "userpoints"


    private const val STREAK_PREFS_NAME = "streak_prefs"
    private const val STREAK_LAST_USED_DATE_KEY = "last_used_date"
    private const val STREAK_COUNT_KEY = "streak_count"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(STREAK_PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun updateStreak(context: Context) {
        val sharedPreferences = getPreferences(context)
        val lastUsedDate = sharedPreferences.getString(STREAK_LAST_USED_DATE_KEY, null)
        val currentDate = getCurrentDate()

        val streakCount = if (lastUsedDate == null) {
            1 // First time the app is used
        } else {
            val lastDate = LocalDate.parse(lastUsedDate)
            val currentDateParsed = LocalDate.parse(currentDate)

            when (ChronoUnit.DAYS.between(lastDate, currentDateParsed)) {
                1L -> sharedPreferences.getInt(STREAK_COUNT_KEY, 0) + 1 // Increment streak
                0L -> sharedPreferences.getInt(STREAK_COUNT_KEY, 0) // Same day, no change
                else -> 1 // More than 1 day gap, reset streak
            }
        }

        // Save the current date and updated streak count
        sharedPreferences.edit().apply {
            putString(STREAK_LAST_USED_DATE_KEY, currentDate)
            putInt(STREAK_COUNT_KEY, streakCount)
            apply()
        }

        // Optionally, show a message with the updated streak count
        Toast.makeText(context, "Current streak: $streakCount days", Toast.LENGTH_SHORT).show()
    }

    fun getCurrentStreak(context: Context): Int {
        val sharedPreferences = getPreferences(context)
        return sharedPreferences.getInt(STREAK_COUNT_KEY, 1)
    }

}