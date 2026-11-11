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
            streakDays <= 20 -> if (isArabic) "مستخدم عادي" else "Standard User"
            streakDays in 21..60 -> if (isArabic) "مستخدم مميز" else "Special User"
            streakDays in 61..180 -> if (isArabic) "مستخدم مميز" else "VIP User"
            else -> "Vip II Member"
        }
    }

    val milestones = listOf(
        MilestoneItem(
            day = 5,
            title = "Manual Dark/Light Mode",
            titleArabic = "تبديل الوضع الليلي والنهاري",
            description = "Customizable light and dark appearances anytime.",
            descriptionArabic = "التحكم اليدوي الكامل بين الوضع الليلي والنهاري في أي وقت.",
            iconType = "theme"
        ),
        MilestoneItem(
            day = 10,
            title = "App Lock (PIN/Pattern)",
            titleArabic = "قفل التطبيق (PIN / نمط)",
            description = "Biometric & PIN security for local video vaults.",
            descriptionArabic = "حماية الخصوصية وقفل الفيديوهات برمز سري وبصمة الإصبع.",
            iconType = "lock"
        ),
        MilestoneItem(
            day = 15,
            title = "Manual Language Selection",
            titleArabic = "اختيار اللغة يدوياً",
            description = "Switch between Arabic & English on demand.",
            descriptionArabic = "التبديل الفوري بين اللغة العربية والإنجليزية وتغيير اتجاه الواجهة.",
            iconType = "language"
        ),
        MilestoneItem(
            day = 20,
            title = "Media Sharing Capabilities",
            titleArabic = "مشاركة الوسائط محلياً",
            description = "High-speed offline transfer to nearby devices.",
            descriptionArabic = "إرسال ومشاركة ملفات الفيديو محلياً بسرعة فائقة بدون إنترنت.",
            iconType = "share"
        ),
        MilestoneItem(
            day = 25,
            title = "Custom Themes (3 Dark / 3 Light)",
            titleArabic = "ثيمات مخصصة (3 ليلي / 3 نهاري)",
            description = "Exclusive neon gradients and glass colors.",
            descriptionArabic = "6 ثيمات لونية حصرية تشمل النيون الفاقع والزجاج المصقول.",
            iconType = "palette"
        ),
        MilestoneItem(
            day = 30,
            title = "Smart Notifications",
            titleArabic = "إشعارات ذكية مخصصة",
            description = "Continue watching & streak preservation reminders.",
            descriptionArabic = "تنبيهات ذكية لاستكمال المشاهدة وحماية الدرع وتجنب فقدان السلسلة.",
            iconType = "notification"
        ),
        MilestoneItem(
            day = 35,
            title = "Background Play",
            titleArabic = "التشغيل في الخلفية",
            description = "Audio & video playback with screen locked.",
            descriptionArabic = "الاستمرار في سماع الصوت وتشغيل الفيديو عند إغلاق الشاشة أو التبديل.",
            iconType = "background_play"
        ),
        MilestoneItem(
            day = 40,
            title = "Pop-up Videos / PiP Mode",
            titleArabic = "فيديو منبثق (PiP Mode)",
            description = "Floating resizable player over any Android app.",
            descriptionArabic = "مشغل عائم متعدد المهام فوق التطبيقات بحرية تامة وسلاسة.",
            iconType = "pip"
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

    // System Settings Hub
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
}
