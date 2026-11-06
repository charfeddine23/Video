package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.ThemeMode
import com.example.ui.viewmodel.StreakUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StreakPreferencesRepository private constructor(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "video_pulse_persistent_store"

        private const val KEY_FIRST_RUN_DONE = "key_first_run_done"
        private const val KEY_STREAK_DAYS = "key_streak_days"
        private const val KEY_SHIELDS_COUNT = "key_shields_count"
        private const val KEY_MAX_SHIELDS = "key_max_shields"
        private const val KEY_DAILY_WATCH_SECONDS = "key_daily_watch_seconds"
        private const val KEY_DAILY_GOAL_SECONDS = "key_daily_goal_seconds"
        private const val KEY_IS_GOAL_COMPLETED_TODAY = "key_is_goal_completed_today"
        private const val KEY_LAST_ACTIVE_DATE = "key_last_active_date"
        private const val KEY_THEME_MODE = "key_theme_mode"
        private const val KEY_APP_LANGUAGE = "key_app_language"

        @Volatile
        private var instance: StreakPreferencesRepository? = null

        fun getInstance(context: Context): StreakPreferencesRepository {
            return instance ?: synchronized(this) {
                instance ?: StreakPreferencesRepository(context.applicationContext).also { instance = it }
            }
        }
    }

    private fun getTodayDateString(): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            sdf.format(Date())
        } catch (_: Exception) {
            "today"
        }
    }

    /**
     * Loads the persistent Streak, Shield, and Watch state from disk.
     * Checks calendar day transitions to ensure streak continuity or daily timer refresh.
     */
    fun loadInitialState(): StreakUiState {
        val today = getTodayDateString()
        val isFirstRun = !prefs.getBoolean(KEY_FIRST_RUN_DONE, false)

        if (isFirstRun) {
            val defaultStreak = 14
            val defaultShields = 18
            val defaultWatchSeconds = 180

            prefs.edit()
                .putBoolean(KEY_FIRST_RUN_DONE, true)
                .putInt(KEY_STREAK_DAYS, defaultStreak)
                .putInt(KEY_SHIELDS_COUNT, defaultShields)
                .putInt(KEY_MAX_SHIELDS, 30)
                .putInt(KEY_DAILY_WATCH_SECONDS, defaultWatchSeconds)
                .putInt(KEY_DAILY_GOAL_SECONDS, 300)
                .putBoolean(KEY_IS_GOAL_COMPLETED_TODAY, false)
                .putString(KEY_LAST_ACTIVE_DATE, today)
                .apply()

            return StreakUiState(
                streakDays = defaultStreak,
                shieldsCount = defaultShields,
                maxShields = 30,
                dailyWatchSeconds = defaultWatchSeconds,
                dailyGoalSeconds = 300,
                isGoalCompletedToday = false
            )
        }

        val savedStreak = prefs.getInt(KEY_STREAK_DAYS, 14)
        val savedShields = prefs.getInt(KEY_SHIELDS_COUNT, 18).coerceIn(0, 30)
        val maxShields = prefs.getInt(KEY_MAX_SHIELDS, 30)
        val goalSeconds = prefs.getInt(KEY_DAILY_GOAL_SECONDS, 300)
        val lastDate = prefs.getString(KEY_LAST_ACTIVE_DATE, null)

        var watchSeconds = prefs.getInt(KEY_DAILY_WATCH_SECONDS, 0)
        var isGoalCompleted = prefs.getBoolean(KEY_IS_GOAL_COMPLETED_TODAY, false)

        // Day transition check
        if (lastDate != null && lastDate != today) {
            // New day started: reset daily watch progress for the new day
            watchSeconds = 0
            isGoalCompleted = false
            prefs.edit()
                .putInt(KEY_DAILY_WATCH_SECONDS, 0)
                .putBoolean(KEY_IS_GOAL_COMPLETED_TODAY, false)
                .putString(KEY_LAST_ACTIVE_DATE, today)
                .apply()
        }

        return StreakUiState(
            streakDays = savedStreak,
            shieldsCount = savedShields,
            maxShields = maxShields,
            dailyWatchSeconds = watchSeconds,
            dailyGoalSeconds = goalSeconds,
            isGoalCompletedToday = isGoalCompleted
        )
    }

    /**
     * Immediately commits streak, shield count, watch progress, and goal status to disk.
     */
    fun saveStreakState(state: StreakUiState) {
        val today = getTodayDateString()
        prefs.edit()
            .putBoolean(KEY_FIRST_RUN_DONE, true)
            .putInt(KEY_STREAK_DAYS, state.streakDays)
            .putInt(KEY_SHIELDS_COUNT, state.shieldsCount)
            .putInt(KEY_MAX_SHIELDS, state.maxShields)
            .putInt(KEY_DAILY_WATCH_SECONDS, state.dailyWatchSeconds)
            .putInt(KEY_DAILY_GOAL_SECONDS, state.dailyGoalSeconds)
            .putBoolean(KEY_IS_GOAL_COMPLETED_TODAY, state.isGoalCompletedToday)
            .putString(KEY_LAST_ACTIVE_DATE, today)
            .commit()
    }

    fun saveThemeMode(themeMode: ThemeMode) {
        prefs.edit()
            .putString(KEY_THEME_MODE, themeMode.name)
            .apply()
    }

    fun loadThemeMode(): ThemeMode {
        val saved = prefs.getString(KEY_THEME_MODE, null)
        return try {
            if (saved != null) ThemeMode.valueOf(saved) else ThemeMode.DARK
        } catch (_: Exception) {
            ThemeMode.DARK
        }
    }

    fun saveLanguage(language: AppLanguage) {
        prefs.edit()
            .putString(KEY_APP_LANGUAGE, language.name)
            .apply()
    }

    fun loadLanguage(): AppLanguage {
        val saved = prefs.getString(KEY_APP_LANGUAGE, null)
        return try {
            if (saved != null) AppLanguage.valueOf(saved) else AppLanguage.ARABIC
        } catch (_: Exception) {
            AppLanguage.ARABIC
        }
    }
}
