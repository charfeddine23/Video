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
        const val MAX_SHIELDS_CAPACITY = 1000
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

        // Shield Farming (60-Min Rule) & Analytics
        private const val KEY_TOTAL_ACTIVE_WATCH_SECONDS = "key_total_active_watch_seconds"
        private const val KEY_SHIELD_FARM_SECONDS = "key_shield_farm_seconds"
        private const val KEY_SHIELDS_EARNED_TODAY_DATE = "key_shields_earned_today_date"
        private const val KEY_SHIELDS_EARNED_TODAY_COUNT = "key_shields_earned_today_count"
        private const val KEY_TOTAL_TRACKS_LISTENED = "key_total_tracks_listened"

        // Hidden Vault
        private const val KEY_VAULT_PIN = "key_vault_pin"
        private const val KEY_VAULT_MEDIA_IDS = "key_vault_media_ids"
        private const val KEY_CLAIMED_VAULT_LEVELS = "key_claimed_vault_levels"

        // System Settings & Premium Gate
        private const val KEY_RGB_ACCENT = "key_rgb_accent"
        private const val KEY_OLED_BLACK = "key_oled_black"
        private const val KEY_AUTO_RESUME = "key_auto_resume"
        private const val KEY_DEFAULT_SPEED = "key_default_speed"
        private const val KEY_BLACKLISTED_FOLDERS = "key_blacklisted_folders"
        private const val KEY_PREMIUM_ACTIVATED = "key_premium_activated"
        private const val KEY_SELECTED_THEME_PRESET = "key_selected_theme_preset"
        private const val KEY_WIFI_DIRECT_SHARING = "key_wifi_direct_sharing"
        private const val KEY_SMART_NOTIFICATIONS = "key_smart_notifications"
        private const val KEY_BACKGROUND_PLAY = "key_background_play"
        private const val KEY_PIP_MODE = "key_pip_mode"
        private const val KEY_EYE_PROTECTION = "key_eye_protection"
        private const val KEY_FULL_SCREEN_MODE = "key_full_screen_mode"

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
     * Default for new sessions: 0 days streak, 0 shields (earned after 1h of usage), 0s watch time.
     */
    fun loadInitialState(): StreakUiState {
        val today = getTodayDateString()
        val isFirstRun = !prefs.getBoolean(KEY_FIRST_RUN_DONE, false)
        val currentVersion = prefs.getInt(KEY_PREFS_VERSION, 0)

        if (isFirstRun || currentVersion < CURRENT_PREFS_VERSION) {
            val defaultStreak = 0
            val defaultShields = 0
            val defaultWatchSeconds = 0

            prefs.edit()
                .putBoolean(KEY_FIRST_RUN_DONE, true)
                .putInt(KEY_PREFS_VERSION, CURRENT_PREFS_VERSION)
                .putInt(KEY_STREAK_DAYS, defaultStreak)
                .putInt(KEY_SHIELDS_COUNT, defaultShields)
                .putInt(KEY_MAX_SHIELDS, MAX_SHIELDS_CAPACITY)
                .putInt(KEY_DAILY_WATCH_SECONDS, defaultWatchSeconds)
                .putInt(KEY_DAILY_GOAL_SECONDS, 300)
                .putBoolean(KEY_IS_GOAL_COMPLETED_TODAY, false)
                .putString(KEY_LAST_ACTIVE_DATE, today)
                .putString(KEY_SHIELDS_EARNED_TODAY_DATE, today)
                .putInt(KEY_SHIELDS_EARNED_TODAY_COUNT, 0)
                .commit()

            return StreakUiState(
                streakDays = defaultStreak,
                shieldsCount = defaultShields,
                maxShields = MAX_SHIELDS_CAPACITY,
                dailyWatchSeconds = defaultWatchSeconds,
                dailyGoalSeconds = 300,
                isGoalCompletedToday = false
            )
        }

        val savedStreak = prefs.getInt(KEY_STREAK_DAYS, 0)
        val storedMaxShields = prefs.getInt(KEY_MAX_SHIELDS, MAX_SHIELDS_CAPACITY)
        val maxShields = storedMaxShields.coerceAtLeast(MAX_SHIELDS_CAPACITY)
        if (storedMaxShields < MAX_SHIELDS_CAPACITY) {
            prefs.edit().putInt(KEY_MAX_SHIELDS, MAX_SHIELDS_CAPACITY).apply()
        }
        val savedShields = prefs.getInt(KEY_SHIELDS_COUNT, 3).coerceIn(0, maxShields)
        val goalSeconds = prefs.getInt(KEY_DAILY_GOAL_SECONDS, 300)
        val lastDate = prefs.getString(KEY_LAST_ACTIVE_DATE, null)

        var watchSeconds = prefs.getInt(KEY_DAILY_WATCH_SECONDS, 0)
        var isGoalCompleted = prefs.getBoolean(KEY_IS_GOAL_COMPLETED_TODAY, false)

        // Day transition check
        if (lastDate != null && lastDate != today) {
            // New day started: reset daily watch progress and daily shield farming quota
            watchSeconds = 0
            isGoalCompleted = false
            prefs.edit()
                .putInt(KEY_DAILY_WATCH_SECONDS, 0)
                .putBoolean(KEY_IS_GOAL_COMPLETED_TODAY, false)
                .putString(KEY_LAST_ACTIVE_DATE, today)
                .putString(KEY_SHIELDS_EARNED_TODAY_DATE, today)
                .putInt(KEY_SHIELDS_EARNED_TODAY_COUNT, 0)
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

    fun tickDailyWatchTime(seconds: Int = 1): StreakUiState {
        val current = loadInitialState()
        val updatedSeconds = (current.dailyWatchSeconds + seconds).coerceAtMost(current.dailyGoalSeconds)
        val reachedGoal = updatedSeconds >= current.dailyGoalSeconds
        val newlyCompleted = reachedGoal && !current.isGoalCompletedToday
        val newStreak = if (newlyCompleted) current.streakDays + 1 else current.streakDays

        // CRITICAL: Reaching 5-minute daily watch goal feeds the streak flame ONLY, NOT awarding free shields!
        val newState = current.copy(
            dailyWatchSeconds = updatedSeconds,
            isGoalCompletedToday = reachedGoal || current.isGoalCompletedToday,
            streakDays = newStreak
        )
        saveStreakState(newState)
        return newState
    }

    fun getShieldsEarnedToday(): Int {
        val today = getTodayDateString()
        val savedDate = prefs.getString(KEY_SHIELDS_EARNED_TODAY_DATE, "") ?: ""
        return if (savedDate == today) {
            prefs.getInt(KEY_SHIELDS_EARNED_TODAY_COUNT, 0)
        } else {
            0
        }
    }

    /**
     * Ticks active foreground app usage time.
     * Strict Rules:
     * - Background usage does NOT count.
     * - 1 shield awarded per 1 full hour (3600s) of active app usage.
     * - Maximum 3 shields awarded per day.
     * - Maximum inventory capacity: 1000 shields.
     */
    fun tickActiveForegroundUsage(seconds: Int = 1): Pair<StreakUiState, Boolean> {
        val current = loadInitialState()
        val today = getTodayDateString()
        val savedDate = prefs.getString(KEY_SHIELDS_EARNED_TODAY_DATE, "") ?: ""
        var todayEarned = if (savedDate == today) {
            prefs.getInt(KEY_SHIELDS_EARNED_TODAY_COUNT, 0)
        } else {
            0
        }

        val totalActiveSecs = prefs.getLong(KEY_TOTAL_ACTIVE_WATCH_SECONDS, 0L) + seconds
        var farmSeconds = prefs.getInt(KEY_SHIELD_FARM_SECONDS, 0)
        var newShields = current.shieldsCount
        var earnedShieldNow = false

        if (todayEarned < 3 && current.shieldsCount < current.maxShields) {
            farmSeconds += seconds
            if (farmSeconds >= 3600) {
                val possibleShields = farmSeconds / 3600
                farmSeconds %= 3600
                val allowable = (3 - todayEarned).coerceAtMost(possibleShields)
                if (allowable > 0) {
                    todayEarned += allowable
                    newShields = (newShields + allowable).coerceAtMost(current.maxShields)
                    earnedShieldNow = true
                }
            }
        } else if (todayEarned >= 3) {
            farmSeconds = 3600
        }

        prefs.edit()
            .putString(KEY_SHIELDS_EARNED_TODAY_DATE, today)
            .putInt(KEY_SHIELDS_EARNED_TODAY_COUNT, todayEarned)
            .putInt(KEY_SHIELD_FARM_SECONDS, farmSeconds)
            .putLong(KEY_TOTAL_ACTIVE_WATCH_SECONDS, totalActiveSecs)
            .apply()

        if (newShields != current.shieldsCount) {
            val updatedState = current.copy(shieldsCount = newShields)
            saveStreakState(updatedState)
            return Pair(updatedState, true)
        }
        return Pair(current, false)
    }

    fun getShieldFarmAccumulatedSeconds(): Int {
        return prefs.getInt(KEY_SHIELD_FARM_SECONDS, 0)
    }

    fun getTotalWatchHours(): Float {
        val totalSecs = prefs.getLong(KEY_TOTAL_ACTIVE_WATCH_SECONDS, 7200L) // Default 2.0h offline starter
        return (totalSecs.toFloat() / 3600f)
    }

    fun getTotalTracksListened(): Int {
        return prefs.getInt(KEY_TOTAL_TRACKS_LISTENED, 14)
    }

    fun incrementTrackListened() {
        val current = prefs.getInt(KEY_TOTAL_TRACKS_LISTENED, 14)
        prefs.edit().putInt(KEY_TOTAL_TRACKS_LISTENED, current + 1).apply()
    }

    // --- Hidden Vault (PIN / Biometric) ---
    fun getVaultPin(): String? {
        return prefs.getString(KEY_VAULT_PIN, null)
    }

    fun saveVaultPin(pin: String) {
        prefs.edit().putString(KEY_VAULT_PIN, pin).apply()
    }

    fun getVaultMediaIds(): Set<String> {
        return prefs.getStringSet(KEY_VAULT_MEDIA_IDS, setOf("vault_video_1")) ?: setOf("vault_video_1")
    }

    fun toggleVaultMedia(mediaId: String): Boolean {
        val current = getVaultMediaIds().toMutableSet()
        val isNowInVault = if (current.contains(mediaId)) {
            current.remove(mediaId)
            false
        } else {
            current.add(mediaId)
            true
        }
        prefs.edit().putStringSet(KEY_VAULT_MEDIA_IDS, current).apply()
        return isNowInVault
    }

    // --- System Settings Hub ---
    fun getRgbAccentColor(): Long {
        // Defaults to Neon Cyan: 0xFF00FFCC
        return prefs.getLong(KEY_RGB_ACCENT, 0xFF00FFCCL)
    }

    fun saveRgbAccentColor(colorLong: Long) {
        prefs.edit().putLong(KEY_RGB_ACCENT, colorLong).apply()
    }

    fun isOledBlackEnabled(): Boolean {
        return prefs.getBoolean(KEY_OLED_BLACK, false)
    }

    fun saveOledBlackEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_OLED_BLACK, enabled).apply()
    }

    fun getAutoResumePlayback(): Boolean {
        return prefs.getBoolean(KEY_AUTO_RESUME, true)
    }

    fun saveAutoResumePlayback(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_RESUME, enabled).apply()
    }

    fun getDefaultPlaybackSpeed(): Float {
        return prefs.getFloat(KEY_DEFAULT_SPEED, 1.0f)
    }

    fun saveDefaultPlaybackSpeed(speed: Float) {
        prefs.edit().putFloat(KEY_DEFAULT_SPEED, speed).apply()
    }

    fun getBlacklistedFolders(): Set<String> {
        return prefs.getStringSet(KEY_BLACKLISTED_FOLDERS, emptySet()) ?: emptySet()
    }

    fun toggleFolderBlacklist(folderName: String): Boolean {
        val current = getBlacklistedFolders().toMutableSet()
        val isBlacklisted = if (current.contains(folderName)) {
            current.remove(folderName)
            false
        } else {
            current.add(folderName)
            true
        }
        prefs.edit().putStringSet(KEY_BLACKLISTED_FOLDERS, current).apply()
        return isBlacklisted
    }

    fun clearThumbnailCache(): Long {
        // Returns simulated/freed cache size in MB (e.g. 48MB)
        return 48L
    }

    // --- Aura Pulse Premium & Moved System Settings ---
    fun isPremiumActivated(): Boolean = prefs.getBoolean(KEY_PREMIUM_ACTIVATED, false)
    fun setPremiumActivated(activated: Boolean) {
        prefs.edit().putBoolean(KEY_PREMIUM_ACTIVATED, activated).apply()
    }

    fun getSelectedThemePresetIndex(): Int = prefs.getInt(KEY_SELECTED_THEME_PRESET, 0)
    fun saveSelectedThemePresetIndex(index: Int) {
        prefs.edit().putInt(KEY_SELECTED_THEME_PRESET, index).apply()
    }

    fun isWifiDirectSharingEnabled(): Boolean = prefs.getBoolean(KEY_WIFI_DIRECT_SHARING, true)
    fun setWifiDirectSharingEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_WIFI_DIRECT_SHARING, enabled).apply()
    }

    fun isSmartNotificationsEnabled(): Boolean = prefs.getBoolean(KEY_SMART_NOTIFICATIONS, true)
    fun setSmartNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SMART_NOTIFICATIONS, enabled).apply()
    }

    fun isBackgroundPlayEnabled(): Boolean = prefs.getBoolean(KEY_BACKGROUND_PLAY, true)
    fun setBackgroundPlayEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BACKGROUND_PLAY, enabled).apply()
    }

    fun isPipModeEnabled(): Boolean = prefs.getBoolean(KEY_PIP_MODE, true)
    fun setPipModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_PIP_MODE, enabled).apply()
    }

    fun isEyeProtectionEnabled(): Boolean = prefs.getBoolean(KEY_EYE_PROTECTION, false)
    fun setEyeProtectionEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_EYE_PROTECTION, enabled).apply()
    }

    fun isFullScreenModeEnabled(): Boolean = prefs.getBoolean(KEY_FULL_SCREEN_MODE, false)
    fun setFullScreenModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_FULL_SCREEN_MODE, enabled).apply()
    }

    fun exportPreferencesToJson(): String {
        val state = loadInitialState()
        return """
            {
              "app": "Aura Pulse / Video Pulse",
              "exportedAt": "${System.currentTimeMillis()}",
              "streakDays": ${state.streakDays},
              "shieldsCount": ${state.shieldsCount},
              "dailyWatchSeconds": ${state.dailyWatchSeconds},
              "userName": "${getUserName()}",
              "totalWatchHours": ${getTotalWatchHours()},
              "rgbAccent": ${getRgbAccentColor()},
              "oledBlack": ${isOledBlackEnabled()},
              "fullScreenMode": ${isFullScreenModeEnabled()},
              "autoResume": ${getAutoResumePlayback()},
              "defaultSpeed": ${getDefaultPlaybackSpeed()}
            }
        """.trimIndent()
    }

    fun importPreferencesFromJson(json: String): Boolean {
        return try {
            val jsonObject = org.json.JSONObject(json)
            val streak = jsonObject.optInt("streakDays", 0)
            val shields = jsonObject.optInt("shieldsCount", 3).coerceIn(0, MAX_SHIELDS_CAPACITY)
            val name = jsonObject.optString("userName", "Charfeddine Kahlessenane")
            val rgb = jsonObject.optLong("rgbAccent", 0xFF00FFCCL)
            val oled = jsonObject.optBoolean("oledBlack", false)
            val fullScreen = jsonObject.optBoolean("fullScreenMode", false)

            prefs.edit()
                .putInt(KEY_STREAK_DAYS, streak)
                .putInt(KEY_SHIELDS_COUNT, shields)
                .putInt(KEY_MAX_SHIELDS, MAX_SHIELDS_CAPACITY)
                .putString(KEY_USER_NAME, name)
                .putLong(KEY_RGB_ACCENT, rgb)
                .putBoolean(KEY_OLED_BLACK, oled)
                .putBoolean(KEY_FULL_SCREEN_MODE, fullScreen)
                .apply()
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun videoStorageKey(id: String, uriString: String?): String {
        val raw = if (!uriString.isNullOrBlank()) uriString else id
        return "vp_" + raw.hashCode()
    }

    fun saveVideoProgress(
        id: String,
        uri: String?,
        title: String,
        durationMs: Long,
        positionMs: Long,
        thumbnailRes: Int = 0
    ) {
        if (durationMs <= 0L) return
        val key = videoStorageKey(id, uri)
        prefs.edit()
            .putString("key_vid_id_$key", id)
            .putString("key_vid_uri_$key", uri)
            .putString("key_vid_title_$key", title)
            .putLong("key_vid_dur_$key", durationMs)
            .putLong("key_vid_pos_$key", positionMs)
            .putInt("key_vid_thumb_$key", thumbnailRes)
            .putLong("key_vid_time_$key", System.currentTimeMillis())
            .apply()

        // Also update last watched
        saveLastWatched(id, title, uri, durationMs, positionMs, thumbnailRes)
    }

    fun getVideoProgress(id: String, uriString: String? = null): VideoProgressInfo? {
        val key = videoStorageKey(id, uriString)
        val savedPos = prefs.getLong("key_vid_pos_$key", -1L)
        if (savedPos < 0L) {
            val lw = getLastWatched()
            if (lw != null && (lw.id == id || (uriString != null && lw.uriString == uriString))) {
                return VideoProgressInfo(
                    id = lw.id,
                    uriString = lw.uriString,
                    title = lw.title,
                    durationMs = lw.durationMs,
                    positionMs = lw.positionMs,
                    thumbnailRes = lw.thumbnailRes,
                    timestamp = lw.timestamp
                )
            }
            return null
        }

        val savedDur = prefs.getLong("key_vid_dur_$key", 0L)
        val title = prefs.getString("key_vid_title_$key", "") ?: ""
        val uri = prefs.getString("key_vid_uri_$key", uriString)
        val thumb = prefs.getInt("key_vid_thumb_$key", 0)
        val time = prefs.getLong("key_vid_time_$key", 0L)

        return VideoProgressInfo(
            id = id,
            uriString = uri,
            title = title,
            durationMs = savedDur,
            positionMs = savedPos,
            thumbnailRes = thumb,
            timestamp = time
        )
    }

    fun clearVideoProgress(id: String, uriString: String? = null) {
        val key = videoStorageKey(id, uriString)
        prefs.edit()
            .remove("key_vid_id_$key")
            .remove("key_vid_uri_$key")
            .remove("key_vid_title_$key")
            .remove("key_vid_dur_$key")
            .remove("key_vid_pos_$key")
            .remove("key_vid_thumb_$key")
            .remove("key_vid_time_$key")
            .apply()
    }

    fun getClaimedVaultLevels(): Set<Int> {
        val raw = prefs.getStringSet(KEY_CLAIMED_VAULT_LEVELS, emptySet()) ?: emptySet()
        return raw.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun claimVaultLevelReward(level: Int, shieldsToAward: Int): Boolean {
        val claimed = getClaimedVaultLevels().toMutableSet()
        if (claimed.contains(level)) return false
        claimed.add(level)

        val current = loadInitialState()
        val newShields = (current.shieldsCount + shieldsToAward).coerceAtMost(current.maxShields)

        prefs.edit()
            .putStringSet(KEY_CLAIMED_VAULT_LEVELS, claimed.map { it.toString() }.toSet())
            .putInt(KEY_SHIELDS_COUNT, newShields)
            .apply()

        saveStreakState(current.copy(shieldsCount = newShields))
        return true
    }

    fun claimAllUnlockedVaultRewards(levelsWithRewards: List<Pair<Int, Int>>): Int {
        val claimed = getClaimedVaultLevels().toMutableSet()
        var newlyClaimed = 0
        var totalShields = 0
        for ((level, reward) in levelsWithRewards) {
            if (!claimed.contains(level)) {
                claimed.add(level)
                newlyClaimed++
                totalShields += reward
            }
        }
        if (newlyClaimed == 0) return 0

        val current = loadInitialState()
        val newShields = (current.shieldsCount + totalShields).coerceAtMost(current.maxShields)

        prefs.edit()
            .putStringSet(KEY_CLAIMED_VAULT_LEVELS, claimed.map { it.toString() }.toSet())
            .putInt(KEY_SHIELDS_COUNT, newShields)
            .apply()

        saveStreakState(current.copy(shieldsCount = newShields))
        return newlyClaimed
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

data class VideoProgressInfo(
    val id: String,
    val uriString: String?,
    val title: String,
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
