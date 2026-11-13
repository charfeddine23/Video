package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.data.preferences.StreakPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class MilestoneStatus {
    UNLOCKED,
    CURRENT_TARGET,
    LOCKED
}

data class MilestoneItem(
    val day: Int,
    val title: String,
    val titleArabic: String,
    val description: String,
    val descriptionArabic: String,
    val iconType: String
)

data class StreakUiState(
    val streakDays: Int = 0,
    val shieldsCount: Int = 3,
    val maxShields: Int = 30,
    val dailyWatchSeconds: Int = 0, // 0 sec / 5 min
    val dailyGoalSeconds: Int = 300, // 5 minutes actual playback
    val isGoalCompletedToday: Boolean = false,
    val isGenuinePlaybackActive: Boolean = false,
    val feedbackMessage: String? = null
) {
    val dailyProgress: Float
        get() = (dailyWatchSeconds.toFloat() / dailyGoalSeconds.toFloat()).coerceIn(0f, 1f)

    val remainingMinutes: Int
        get() = ((dailyGoalSeconds - dailyWatchSeconds).coerceAtLeast(0)) / 60

    val remainingSeconds: Int
        get() = ((dailyGoalSeconds - dailyWatchSeconds).coerceAtLeast(0)) % 60
}

class StreakViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = StreakPreferencesRepository.getInstance(application)

    // Load initial state directly from persistent local storage on startup
    private val _uiState = MutableStateFlow(repository.loadInitialState())
    val uiState: StateFlow<StreakUiState> = _uiState.asStateFlow()

    private val _userName = MutableStateFlow(repository.getUserName())
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userAvatarUri = MutableStateFlow(repository.getUserAvatarUri())
    val userAvatarUri: StateFlow<String?> = _userAvatarUri.asStateFlow()

    private val _userAvatarPreset = MutableStateFlow(repository.getUserAvatarPreset())
    val userAvatarPreset: StateFlow<Int> = _userAvatarPreset.asStateFlow()

    fun updateProfile(name: String, avatarUri: String?, presetId: Int) {
        val safeName = name.trim().ifBlank { "Charfeddine Kahlessenane" }
        repository.saveUserName(safeName)
        repository.saveUserAvatarUri(avatarUri)
        repository.saveUserAvatarPreset(presetId)
        _userName.value = safeName
        _userAvatarUri.value = avatarUri
        _userAvatarPreset.value = presetId
    }

    fun getUserTierBadge(streakDays: Int, isArabic: Boolean): String {
        return when {
            streakDays >= 365 -> if (isArabic) "🏆 Yearly Aura Legend (Lvl 365)" else "🏆 Yearly Aura Legend (Lvl 365)"
            streakDays >= 200 -> if (isArabic) "🎖️ Local System Master (Lvl 200+)" else "🎖️ Local System Master (Lvl 200+)"
            streakDays >= 100 -> if (isArabic) "✨ Aura Pro Title (Lvl 100+)" else "✨ Aura Pro Title (Lvl 100+)"
            streakDays >= 50 -> if (isArabic) "💎 Acoustic Master (Lvl 50+)" else "💎 Acoustic Master (Lvl 50+)"
            streakDays >= 20 -> if (isArabic) "🛡️ مستخدم مميز (المستوى $streakDays)" else "🛡️ Special User (Lvl $streakDays)"
            else -> if (isArabic) "المستوى $streakDays • التأسيسي" else "Level $streakDays • Foundation"
        }
    }

    val milestones = listOf(
        MilestoneItem(
            day = 5,
            title = "Glowing Starter Shield",
            titleArabic = "درع البداية المتوهج",
            description = "Bonus streak shield drop & neon border aura badge.",
            descriptionArabic = "مكافأة دروع إضافية وشارة توهج الزجاج السائل للملف الشخصي.",
            iconType = "shield"
        ),
        MilestoneItem(
            day = 10,
            title = "Silver Flame Guardian",
            titleArabic = "حارس الشعلة الفضي",
            description = "Awarded 3 extra Streak Shields & silver prestige badge.",
            descriptionArabic = "منح 3 دروع حماية إضافية مع وسام الحارس الفضي في مسار الإنجاز.",
            iconType = "trophy"
        ),
        MilestoneItem(
            day = 15,
            title = "Perseverance Shield Pack",
            titleArabic = "حزمة دروع المثابرة",
            description = "Half-month milestone drop with +3 Streak Shields.",
            descriptionArabic = "إنجاز منتصف الشهر مع مكافأة فورية +3 دروع حماية للسلسلة.",
            iconType = "shield"
        ),
        MilestoneItem(
            day = 20,
            title = "Golden Royal Shield",
            titleArabic = "الدرع الذهبي الملكي",
            description = "Rare Golden Shield protecting streaks across 2 days.",
            descriptionArabic = "درع ذهبي نادر يحمي سلسلتك ليومين متتاليين مع لقب مميز.",
            iconType = "gold"
        ),
        MilestoneItem(
            day = 25,
            title = "Radiant Aura Emblem",
            titleArabic = "وسام الهالة المشعّة",
            description = "Exclusive +5 Shields bonus & luminous glass crest.",
            descriptionArabic = "شعار هالة نيوني لامع مع مكافأة +5 دروع حماية فورية.",
            iconType = "sparkle"
        ),
        MilestoneItem(
            day = 30,
            title = "Month-1 Grand Drop",
            titleArabic = "مكافأة الشهر الأول الكبرى",
            description = "Complete 30 days to earn +10 Shields & Month-1 Guardian title.",
            descriptionArabic = "اكتمال 30 يوماً متواصلة مع مكافأة كبرى +10 دروع ووسام حارس الشهر.",
            iconType = "crown"
        ),
        MilestoneItem(
            day = 45,
            title = "Elite Streak Champion",
            titleArabic = "وسام بطل السلسلة المتواصلة",
            description = "45-day dedication trophy & +12 Shields inventory boost.",
            descriptionArabic = "إنجاز 45 يوماً من الاستمرارية مع تعزيز +12 درعاً في الخزينة.",
            iconType = "trophy"
        ),
        MilestoneItem(
            day = 60,
            title = "Legendary Diamond Shield",
            titleArabic = "الدرع الماسي الأسطوري",
            description = "Mythic VIP Diamond Shield freezing streaks for 7 full days.",
            descriptionArabic = "الدرع الماسي الأسطوري لتجميد وحماية السلسلة لأسبوع كامل.",
            iconType = "diamond"
        )
    )

    fun getMilestoneStatus(day: Int, currentStreak: Int): MilestoneStatus {
        return when {
            currentStreak >= day -> MilestoneStatus.UNLOCKED
            milestones.firstOrNull { it.day > currentStreak }?.day == day -> MilestoneStatus.CURRENT_TARGET
            else -> MilestoneStatus.LOCKED
        }
    }

    /**
     * Ticks actual playback time towards the 5-minute daily watch goal.
     * Core Rule: Seeking or fast-forwarding MUST NOT count.
     * Automatically persists state to disk immediately.
     */
    fun tickGenuineWatchTime(seconds: Int = 1, isSeeking: Boolean = false) {
        if (isSeeking) return

        _uiState.update { current ->
            val updatedSeconds = (current.dailyWatchSeconds + seconds).coerceAtMost(current.dailyGoalSeconds)
            val reachedGoal = updatedSeconds >= current.dailyGoalSeconds
            val newlyCompleted = reachedGoal && !current.isGoalCompletedToday
            val newStreak = if (newlyCompleted) current.streakDays + 1 else current.streakDays

            val newState = current.copy(
                dailyWatchSeconds = updatedSeconds,
                isGoalCompletedToday = reachedGoal || current.isGoalCompletedToday,
                streakDays = newStreak,
                feedbackMessage = if (newlyCompleted) {
                    "🔥 أتممت هدف الـ 5 دقائق لتغذية الشعلة! السترايك: ${newStreak} يوماً"
                } else current.feedbackMessage
            )
            repository.saveStreakState(newState)
            newState
        }
    }

    /**
     * Ticks active foreground app usage time.
     * Core Rule: Background usage does NOT count.
     * 1 shield awarded per 1 full hour of active app usage (max 3 shields/day, max 30 total).
     */
    fun tickActiveForegroundUsage(seconds: Int = 1) {
        val (updatedState, earnedShield) = repository.tickActiveForegroundUsage(seconds)
        if (earnedShield) {
            val todayEarned = repository.getShieldsEarnedToday()
            _uiState.update { current ->
                current.copy(
                    shieldsCount = updatedState.shieldsCount,
                    feedbackMessage = "🛡️ كسبت درع حماية جديد بعد ساعة كاملة من استخدام التطبيق! ($todayEarned/3 اليوم)"
                )
            }
        }
    }

    /**
     * Consumes 1 shield to protect streak if user missed a day.
     * Strict Penalty: If 0 shields, streak resets to 0 and all features lock!
     * Persists immediately to local storage.
     */
    fun simulateMissedDay() {
        _uiState.update { current ->
            val newState = if (current.shieldsCount > 0) {
                val newShields = current.shieldsCount - 1
                current.copy(
                    shieldsCount = newShields,
                    dailyWatchSeconds = 0,
                    isGoalCompletedToday = false,
                    feedbackMessage = "🛡️ Day missed! 1 shield consumed. Streak saved at ${current.streakDays} days! ($newShields shields left)"
                )
            } else {
                current.copy(
                    streakDays = 0,
                    dailyWatchSeconds = 0,
                    isGoalCompletedToday = false,
                    feedbackMessage = "⚠️ Day missed with 0 shields! Streak reset to 0. All milestones locked!"
                )
            }
            repository.saveStreakState(newState)
            newState
        }
    }

    /**
     * Add a shield up to max 30 and save immediately.
     */
    fun addShield() {
        _uiState.update { current ->
            val newState = if (current.shieldsCount < current.maxShields) {
                current.copy(
                    shieldsCount = current.shieldsCount + 1,
                    feedbackMessage = "🛡️ Shield added! Inventory: ${current.shieldsCount + 1}/${current.maxShields}"
                )
            } else {
                current.copy(
                    feedbackMessage = "🛡️ Shield inventory is full (max 30 shields)."
                )
            }
            repository.saveStreakState(newState)
            newState
        }
    }

    /**
     * Set streak count and persist to local storage.
     */
    fun setStreak(days: Int) {
        _uiState.update { current ->
            val newState = current.copy(
                streakDays = days.coerceAtLeast(0),
                feedbackMessage = if (days == 0) "❄️ Streak reset to 0! Visuals frozen with snowfall." else "🔥 Streak set to $days days! Living liquid aura restored."
            )
            repository.saveStreakState(newState)
            newState
        }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }

    // Shield Farming & Analytics
    fun getShieldFarmAccumulatedSeconds(): Int = repository.getShieldFarmAccumulatedSeconds()
    fun getShieldsEarnedToday(): Int = repository.getShieldsEarnedToday()
    fun getTotalWatchHours(): Float = repository.getTotalWatchHours()
    fun getTotalTracksListened(): Int = repository.getTotalTracksListened()
    fun incrementTrackListened() = repository.incrementTrackListened()

    // Hidden Vault
    fun getVaultPin(): String? = repository.getVaultPin()
    fun saveVaultPin(pin: String) = repository.saveVaultPin(pin)
    fun getVaultMediaIds(): Set<String> = repository.getVaultMediaIds()
    fun toggleVaultMedia(id: String): Boolean = repository.toggleVaultMedia(id)

    // System Settings Hub & Aura Pulse Premium
    fun getRgbAccentColor(): Long = repository.getRgbAccentColor()
    fun saveRgbAccentColor(color: Long) = repository.saveRgbAccentColor(color)
    fun isOledBlackEnabled(): Boolean = repository.isOledBlackEnabled()
    fun saveOledBlackEnabled(enabled: Boolean) = repository.saveOledBlackEnabled(enabled)
    fun getAutoResume(): Boolean = repository.getAutoResumePlayback()
    fun saveAutoResume(enabled: Boolean) = repository.saveAutoResumePlayback(enabled)
    fun getDefaultSpeed(): Float = repository.getDefaultPlaybackSpeed()
    fun saveDefaultSpeed(speed: Float) = repository.saveDefaultPlaybackSpeed(speed)
    fun clearThumbnailCache(): Long = repository.clearThumbnailCache()
    fun exportPreferencesToJson(): String = repository.exportPreferencesToJson()
    fun importPreferencesFromJson(json: String): Boolean = repository.importPreferencesFromJson(json)

    private val _isPremiumActivated = MutableStateFlow(repository.isPremiumActivated())
    val isPremiumActivated: StateFlow<Boolean> = _isPremiumActivated.asStateFlow()

    fun setPremiumActivated(activated: Boolean) {
        repository.setPremiumActivated(activated)
        _isPremiumActivated.value = activated
    }

    fun getSelectedThemePresetIndex(): Int = repository.getSelectedThemePresetIndex()
    fun saveSelectedThemePresetIndex(index: Int) = repository.saveSelectedThemePresetIndex(index)
    fun isWifiDirectSharingEnabled(): Boolean = repository.isWifiDirectSharingEnabled()
    fun setWifiDirectSharingEnabled(enabled: Boolean) = repository.setWifiDirectSharingEnabled(enabled)
    fun isSmartNotificationsEnabled(): Boolean = repository.isSmartNotificationsEnabled()
    fun setSmartNotificationsEnabled(enabled: Boolean) = repository.setSmartNotificationsEnabled(enabled)
    fun isBackgroundPlayEnabled(): Boolean = repository.isBackgroundPlayEnabled()
    fun setBackgroundPlayEnabled(enabled: Boolean) = repository.setBackgroundPlayEnabled(enabled)
    fun isPipModeEnabled(): Boolean = repository.isPipModeEnabled()
    fun setPipModeEnabled(enabled: Boolean) = repository.setPipModeEnabled(enabled)
    fun isEyeProtectionEnabled(): Boolean = repository.isEyeProtectionEnabled()
    fun setEyeProtectionEnabled(enabled: Boolean) = repository.setEyeProtectionEnabled(enabled)

    // 365-Level Progression & Feature Vault
    fun getClaimedVaultLevels(): Set<Int> = repository.getClaimedVaultLevels()
    fun isVaultLevelClaimed(level: Int): Boolean = repository.getClaimedVaultLevels().contains(level)

    fun claimVaultLevelReward(level: Int, shieldsToAward: Int): Boolean {
        val success = repository.claimVaultLevelReward(level, shieldsToAward)
        if (success) {
            val updated = repository.loadInitialState()
            _uiState.update {
                it.copy(
                    shieldsCount = updated.shieldsCount,
                    feedbackMessage = "🎉 تم استلام مكافأة المستوى $level بنجاح (+ $shieldsToAward دروع 🛡️)"
                )
            }
        }
        return success
    }
}
