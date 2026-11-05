package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    val streakDays: Int = 14,
    val shieldsCount: Int = 18,
    val maxShields: Int = 30,
    val dailyWatchSeconds: Int = 180, // 3 min / 5 min
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

class StreakViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(StreakUiState())
    val uiState: StateFlow<StreakUiState> = _uiState.asStateFlow()

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
     */
    fun tickGenuineWatchTime(seconds: Int = 1, isSeeking: Boolean = false) {
        if (isSeeking) return

        _uiState.update { current ->
            val updatedSeconds = (current.dailyWatchSeconds + seconds).coerceAtMost(current.dailyGoalSeconds)
            val reachedGoal = updatedSeconds >= current.dailyGoalSeconds
            val newlyCompleted = reachedGoal && !current.isGoalCompletedToday
            val newStreak = if (newlyCompleted) current.streakDays + 1 else current.streakDays

            current.copy(
                dailyWatchSeconds = updatedSeconds,
                isGoalCompletedToday = reachedGoal || current.isGoalCompletedToday,
                streakDays = newStreak,
                feedbackMessage = if (newlyCompleted) "🎉 5-Minute Daily Goal Completed! Streak: ${newStreak} Days!" else current.feedbackMessage
            )
        }
    }

    /**
     * Consumes 1 shield to protect streak if user missed a day.
     * Strict Penalty: If 0 shields, streak resets to 0 and all features lock!
     */
    fun simulateMissedDay() {
        _uiState.update { current ->
            if (current.shieldsCount > 0) {
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
        }
    }

    /**
     * Add a shield up to max 30
     */
    fun addShield() {
        _uiState.update { current ->
            if (current.shieldsCount < current.maxShields) {
                current.copy(
                    shieldsCount = current.shieldsCount + 1,
                    feedbackMessage = "🛡️ Shield added! Inventory: ${current.shieldsCount + 1}/${current.maxShields}"
                )
            } else {
                current.copy(
                    feedbackMessage = "🛡️ Shield inventory is full (max 30 shields)."
                )
            }
        }
    }

    fun setStreak(days: Int) {
        _uiState.update { current ->
            current.copy(
                streakDays = days.coerceAtLeast(0),
                feedbackMessage = if (days == 0) "❄️ Streak reset to 0! Visuals frozen with snowfall." else "🔥 Streak set to $days days! Living liquid aura restored."
            )
        }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }
}
