package com.example.data.model

enum class ForgeKeyTier(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val rarityAr: String,
    val rarityEn: String,
    val requiredShields: Int,
    val requiredKeys: Int,
    val durationHours: Int
) {
    BRONZE(
        id = "bronze",
        nameAr = "المفتاح البرونزي",
        nameEn = "Bronze Key",
        rarityAr = "برونزي • شائع",
        rarityEn = "BRONZE • COMMON",
        requiredShields = 10,
        requiredKeys = 0,
        durationHours = 3
    ),
    SILVER(
        id = "silver",
        nameAr = "المفتاح الفضي",
        nameEn = "Silver Key",
        rarityAr = "فضي • نادر",
        rarityEn = "SILVER • RARE",
        requiredShields = 20,
        requiredKeys = 0,
        durationHours = 6
    ),
    GOLD(
        id = "gold",
        nameAr = "المفتاح الذهبي",
        nameEn = "Golden Key",
        rarityAr = "ذهبي • ملحمي",
        rarityEn = "GOLD • EPIC",
        requiredShields = 0,
        requiredKeys = 30,
        durationHours = 9
    ),
    DIAMOND(
        id = "diamond",
        nameAr = "المفتاح الماسي",
        nameEn = "Diamond Key",
        rarityAr = "ماسي • أسطوري",
        rarityEn = "DIAMOND • MYTHIC",
        requiredShields = 0,
        requiredKeys = 100,
        durationHours = 100
    );

    val durationSeconds: Int
        get() = durationHours * 3600

    val usesShields: Boolean
        get() = requiredShields > 0
}

data class ForgeSlotState(
    val tier: ForgeKeyTier,
    val isActive: Boolean = false,
    val elapsedSeconds: Int = 0,
    val refundShields: Int = 0,
    val refundBronzeKeys: Int = 0,
    val refundSilverKeys: Int = 0,
    val refundGoldKeys: Int = 0
) {
    val totalDurationSeconds: Int
        get() = tier.durationSeconds

    val clampedElapsedSeconds: Int
        get() = elapsedSeconds.coerceIn(0, totalDurationSeconds)

    val remainingSeconds: Int
        get() = (totalDurationSeconds - clampedElapsedSeconds).coerceAtLeast(0)

    val progress: Float
        get() = if (totalDurationSeconds > 0) {
            (clampedElapsedSeconds.toFloat() / totalDurationSeconds.toFloat()).coerceIn(0f, 1f)
        } else 0f

    val isReadyToClaim: Boolean
        get() = isActive && clampedElapsedSeconds >= totalDurationSeconds
}

data class ForgeUiState(
    val bronzeKeys: Int = 0,
    val silverKeys: Int = 0,
    val goldenKeys: Int = 0,
    val diamondKeys: Int = 0,
    val slots: Map<ForgeKeyTier, ForgeSlotState> = ForgeKeyTier.entries.associateWith { ForgeSlotState(it) }
) {
    val totalKeys: Int
        get() = bronzeKeys + silverKeys + goldenKeys + diamondKeys

    fun getSlot(tier: ForgeKeyTier): ForgeSlotState = slots[tier] ?: ForgeSlotState(tier)

    fun getKeyCount(tier: ForgeKeyTier): Int = when (tier) {
        ForgeKeyTier.BRONZE -> bronzeKeys
        ForgeKeyTier.SILVER -> silverKeys
        ForgeKeyTier.GOLD -> goldenKeys
        ForgeKeyTier.DIAMOND -> diamondKeys
    }

    fun availableKeysFor(tier: ForgeKeyTier): Int = when (tier) {
        ForgeKeyTier.BRONZE, ForgeKeyTier.SILVER -> 0
        ForgeKeyTier.GOLD -> bronzeKeys + silverKeys
        ForgeKeyTier.DIAMOND -> bronzeKeys + silverKeys + goldenKeys
    }

    fun canStartForging(tier: ForgeKeyTier, currentShields: Int): Boolean {
        if (getSlot(tier).isActive) return false
        return if (tier.usesShields) {
            currentShields >= tier.requiredShields
        } else {
            availableKeysFor(tier) >= tier.requiredKeys
        }
    }

    val activeSlotsCount: Int
        get() = slots.values.count { it.isActive && !it.isReadyToClaim }

    val readySlotsCount: Int
        get() = slots.values.count { it.isReadyToClaim }
}
