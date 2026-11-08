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
        private const val KEY_PREFS_VERSION = "key_prefs_version"
        private const val CURRENT_PREFS_VERSION = 2

        private const val KEY_STREAK_DAYS = "key_streak_days"
        private const val KEY_SHIELDS_COUNT = "key_shields_count"
        private const val KEY_MAX_SHIELDS = "key_max_shields"
        private const val KEY_DAILY_WATCH_SECONDS = "key_daily_watch_seconds"
        private const val KEY_DAILY_GOAL_SECONDS = "key_daily_goal_seconds"
        private const val KEY_IS_GOAL_COMPLETED_TODAY = "key_is_goal_completed_today"
        private const val KEY_LAST_ACTIVE_DATE = "key_last_active_date"
        private const val KEY_THEME_MODE = "key_theme_mode"
        private const val KEY_APP_LANGUAGE = "key_app_language"
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_USER_AVATAR_URI = "key_user_avatar_uri"
        private const val KEY_USER_AVATAR_PRESET = "key_user_avatar_preset"
        private const val KEY_WATCH_VIEW_MODE = "key_watch_view_mode"
        private const val KEY_HIDE_FILE_EXTENSIONS = "key_hide_file_extensions"
        private const val KEY_LAST_WATCHED_ID = "key_last_watched_id"
        private const val KEY_LAST_WATCHED_TITLE = "key_last_watched_title"
        private const val KEY_LAST_WATCHED_URI = "key_last_watched_uri"
        private const val KEY_LAST_WATCHED_DURATION_MS = "key_last_watched_duration_ms"
        private const val KEY_LAST_WATCHED_POSITION_MS = "key_last_watched_position_ms"
        private const val KEY_LAST_WATCHED_THUMBNAIL_RES = "key_last_watched_thumbnail_res"
        private const val KEY_LAST_WATCHED_TIMESTAMP = "key_last_watched_timestamp"

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
     * Default for new or upgraded sessions: 0 days streak, 3 shields, 0s watch time.
     */
    fun loadInitialState(): StreakUiState {
        val today = getTodayDateString()
        val isFirstRun = !prefs.getBoolean(KEY_FIRST_RUN_DONE, false)
        val currentVersion = prefs.getInt(KEY_PREFS_VERSION, 0)

        if (isFirstRun || currentVersion < CURRENT_PREFS_VERSION) {
            val defaultStreak = 0
            val defaultShields = 3
            val defaultWatchSeconds = 0

            prefs.edit()
                .putBoolean(KEY_FIRST_RUN_DONE, true)
                .putInt(KEY_PREFS_VERSION, CURRENT_PREFS_VERSION)
                .putInt(KEY_STREAK_DAYS, defaultStreak)
                .putInt(KEY_SHIELDS_COUNT, defaultShields)
                .putInt(KEY_MAX_SHIELDS, 30)
                .putInt(KEY_DAILY_WATCH_SECONDS, defaultWatchSeconds)
                .putInt(KEY_DAILY_GOAL_SECONDS, 300)
                .putBoolean(KEY_IS_GOAL_COMPLETED_TODAY, false)
                .putString(KEY_LAST_ACTIVE_DATE, today)
                .commit()

            return StreakUiState(
                streakDays = defaultStreak,
                shieldsCount = defaultShields,
                maxShields = 30,
                dailyWatchSeconds = defaultWatchSeconds,
                dailyGoalSeconds = 300,
                isGoalCompletedToday = false
            )
        }

        val savedStreak = prefs.getInt(KEY_STREAK_DAYS, 0)
        val savedShields = prefs.getInt(KEY_SHIELDS_COUNT, 3).coerceIn(0, 30)
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
            .putInt(KEY_PREFS_VERSION, CURRENT_PREFS_VERSION)
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

    fun getUserName(): String {
        return prefs.getString(KEY_USER_NAME, "Charfeddine Kahlessenane") ?: "Charfeddine Kahlessenane"
    }

    fun saveUserName(name: String) {
        prefs.edit().putString(KEY_USER_NAME, name.trim().ifBlank { "Charfeddine Kahlessenane" }).commit()
    }

    fun getUserAvatarUri(): String? {
        return prefs.getString(KEY_USER_AVATAR_URI, null)
    }

    fun saveUserAvatarUri(uriString: String?) {
        prefs.edit().putString(KEY_USER_AVATAR_URI, uriString).commit()
    }

    fun getUserAvatarPreset(): Int {
        return prefs.getInt(KEY_USER_AVATAR_PRESET, 0)
    }

    fun saveUserAvatarPreset(presetId: Int) {
        prefs.edit().putInt(KEY_USER_AVATAR_PRESET, presetId).commit()
    }

    fun getWatchViewMode(): String {
        return prefs.getString(KEY_WATCH_VIEW_MODE, "LIST") ?: "LIST"
    }

    fun saveWatchViewMode(modeName: String) {
        prefs.edit().putString(KEY_WATCH_VIEW_MODE, modeName).apply()
    }

    fun getHideFileExtensions(): Boolean {
        return prefs.getBoolean(KEY_HIDE_FILE_EXTENSIONS, true)
    }

    fun saveHideFileExtensions(hide: Boolean) {
        prefs.edit().putBoolean(KEY_HIDE_FILE_EXTENSIONS, hide).apply()
    }

    fun saveLastWatched(
        id: String,
        title: String,
        uri: String?,
        durationMs: Long,
        positionMs: Long,
        thumbnailRes: Int = 0
    ) {
        prefs.edit()
            .putString(KEY_LAST_WATCHED_ID, id)
            .putString(KEY_LAST_WATCHED_TITLE, title)
            .putString(KEY_LAST_WATCHED_URI, uri)
            .putLong(KEY_LAST_WATCHED_DURATION_MS, durationMs)
            .putLong(KEY_LAST_WATCHED_POSITION_MS, positionMs)
            .putInt(KEY_LAST_WATCHED_THUMBNAIL_RES, thumbnailRes)
            .putLong(KEY_LAST_WATCHED_TIMESTAMP, System.currentTimeMillis())
            .apply()
    }

    fun getLastWatched(): LastWatchedVideoInfo? {
        val id = prefs.getString(KEY_LAST_WATCHED_ID, null) ?: return null
        val title = prefs.getString(KEY_LAST_WATCHED_TITLE, "") ?: ""
        val uri = prefs.getString(KEY_LAST_WATCHED_URI, null)
        val durationMs = prefs.getLong(KEY_LAST_WATCHED_DURATION_MS, 0L)
        val positionMs = prefs.getLong(KEY_LAST_WATCHED_POSITION_MS, 0L)
        val thumbRes = prefs.getInt(KEY_LAST_WATCHED_THUMBNAIL_RES, 0)
        val timestamp = prefs.getLong(KEY_LAST_WATCHED_TIMESTAMP, 0L)

        return LastWatchedVideoInfo(
            id = id,
            title = title,
            uriString = uri,
            durationMs = durationMs,
            positionMs = positionMs,
            thumbnailRes = thumbRes,
            timestamp = timestamp
        )
    }

    fun clearLastWatched() {
        prefs.edit()
            .remove(KEY_LAST_WATCHED_ID)
            .remove(KEY_LAST_WATCHED_TITLE)
            .remove(KEY_LAST_WATCHED_URI)
            .remove(KEY_LAST_WATCHED_DURATION_MS)
            .remove(KEY_LAST_WATCHED_POSITION_MS)
            .remove(KEY_LAST_WATCHED_THUMBNAIL_RES)
            .remove(KEY_LAST_WATCHED_TIMESTAMP)
            .apply()
    }
}

data class LastWatchedVideoInfo(
    val id: String,
    val title: String,
    val uriString: String?,
    val durationMs: Long,
    val positionMs: Long,
    val thumbnailRes: Int = 0,
    val timestamp: Long = 0L
) {
    val progress: Float
        get() = if (durationMs > 0L) (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

    val progressPercent: Int
        get() = (progress * 100).toInt().coerceIn(0, 100)

    val remainingMs: Long
        get() = (durationMs - positionMs).coerceAtLeast(0L)
}
