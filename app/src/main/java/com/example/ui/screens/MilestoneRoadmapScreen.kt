package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VaultProgressionRegistry
import com.example.ui.components.SerpentineRoadmapView
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.PulseCyan
import com.example.ui.theme.ThemeMode
import com.example.ui.viewmodel.StreakViewModel

data class ShieldTypeItem(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val count: Int,
    val icon: ImageVector,
    val iconColor: Color,
    val bgGlow: Color,
    val tagAr: String,
    val tagEn: String
)

private enum class RoadmapSectionTab {
    ROADMAP,
    INVENTORY
}

@Composable
fun MilestoneRoadmapScreen(
    streakViewModel: StreakViewModel,
    streakDays: Int,
    shieldsCount: Int,
    currentThemeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onBack: () -> Unit,
    isArabic: Boolean = false,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    BackHandler {
        onBack()
    }

    // Section switcher: "Roadmap" (365-Level Serpentine Game Road) vs "Inventory" (Owned Shields Vault & Badges)
    var activeSectionTab by remember { mutableStateOf(RoadmapSectionTab.ROADMAP) }

    var claimedLevels by remember { mutableStateOf(streakViewModel.getClaimedVaultLevels()) }
    val currentProgressionLevel = streakDays.coerceAtLeast(1).coerceAtMost(365)

    // Shield inventory breakdown
    val standardShields = shieldsCount.coerceAtLeast(0)
    val goldenShields = if (streakDays >= 20) (streakDays / 20) else 0
    val diamondShields = if (streakDays >= 60) (streakDays / 60) else 0
    val flameTokens = if (streakDays >= 5) (streakDays / 5) * 2 else 0
    val dailyShieldsToday = streakViewModel.getShieldsEarnedToday().coerceIn(0, 3)

    val shieldTypes = listOf(
        ShieldTypeItem(
            id = "standard_shield",
            nameAr = "درع الحماية الأساسي (Standard Shield)",
            nameEn = "Standard Streak Shield",
            descriptionAr = "يحمي سلسلتك تلقائياً عند تفويت مشاهدة يوم كامل لمنع إعادة العداد إلى الصفر.",
            descriptionEn = "Automatically protects streak when missing a full day of watch time.",
            count = standardShields,
            icon = Icons.Default.Shield,
            iconColor = PulseCyan,
            bgGlow = Color(0x3300F2FE),
            tagAr = "نشط ومتاح",
            tagEn = "ACTIVE"
        ),
        ShieldTypeItem(
            id = "golden_shield",
            nameAr = "الدرع الذهبي الملكي (Golden Shield)",
            nameEn = "Golden Royal Shield",
            descriptionAr = "درع نادر يُمنح كل 20 يوماً من السلسلة. يحمي السلسلة ليومين متتاليين دون استهلاك دروع إضافية.",
            descriptionEn = "Rare shield awarded every 20 days. Protects streak for 2 consecutive days.",
            count = goldenShields,
            icon = Icons.Default.Security,
            iconColor = Color(0xFFFFD700),
            bgGlow = Color(0x33FFD700),
            tagAr = if (goldenShields > 0) "مكتسب 👑" else "يفتح في اليوم 20",
            tagEn = if (goldenShields > 0) "EARNED 👑" else "DAY 20+"
        ),
        ShieldTypeItem(
            id = "diamond_shield",
            nameAr = "الدرع الماسي الأسطوري (Diamond Shield)",
            nameEn = "Legendary Diamond Shield",
            descriptionAr = "درع أسطوري للمشاهدين الـ VIP. يحمي السلسلة لأسبوع كامل (7 أيام) مع تجميد السلسلة دون فقدانها.",
            descriptionEn = "Legendary VIP shield that freezes and protects streak for an entire week.",
            count = diamondShields,
            icon = Icons.Default.Diamond,
            iconColor = Color(0xFF67E8F9),
            bgGlow = Color(0x3338BDF8),
            tagAr = if (diamondShields > 0) "أسطوري 💎" else "يفتح في اليوم 60",
            tagEn = if (diamondShields > 0) "LEGENDARY 💎" else "DAY 60+"
        ),
        ShieldTypeItem(
            id = "flame_token",
            nameAr = "شعلة الحصانة والمكافأة (Flame Token)",
            nameEn = "Flame Boost Token",
            descriptionAr = "شعلة مضاعفة النقاط ومكافأة التقدم السريع نحو المستوى التالي في المنصة.",
            descriptionEn = "Flame multiplier token boosting tier progression and unlocking elite perks.",
            count = flameTokens,
            icon = Icons.Default.LocalFireDepartment,
            iconColor = Color(0xFFFF5722),
            bgGlow = Color(0x33FF5722),
            tagAr = "${flameTokens} شعلة 🔥",
            tagEn = "${flameTokens} Flames 🔥"
        )
    )

    // Curated collectible badges in Inventory
    val badgeLevels = remember { listOf(5, 10, 15, 20, 25, 30, 50, 75, 100, 150, 200, 365) }
    val inventoryBadges = remember(streakDays) {
        badgeLevels.map { lvl -> VaultProgressionRegistry.getMilestone(lvl) }
    }
    val unlockedBadgesCount = inventoryBadges.count { streakDays >= it.level }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_rewards")
    val ambientGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambient_glow_anim"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
            .testTag("achievements_shields_rewards_screen")
    ) {
        // Ambient background glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(
                                Color(0x2800F2FE),
                                Color(0x18FF5722),
                                Color.Transparent
                            ),
                            radius = size.width * 1.1f
                        ),
                        center = Offset(size.width * 0.5f, size.height * 0.15f)
                    )
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onBack()
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0x22FFFFFF))
                            .testTag("roadmap_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isArabic) "رجوع" else "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (isArabic) "الإنجازات, الدروع والمكافآت" else "Achievements, Shields & Rewards",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 16.5.sp
                            )
                        )
                        Text(
                            text = if (isArabic) "خريطة Roadmap (365) والمخزون" else "365-Level Roadmap & Inventory",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Total Shields Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0x33121826),
                    border = BorderStroke(1.dp, PulseCyan.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = PulseCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$shieldsCount 🛡️",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }

            // Segmented View Mode Pills: Roadmap vs Inventory
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val isRoadmapSelected = activeSectionTab == RoadmapSectionTab.ROADMAP
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isRoadmapSelected) Color(0x3300F2FE) else Color(0x1F121826),
                    border = BorderStroke(
                        1.dp,
                        if (isRoadmapSelected) PulseCyan else Color(0x22FFFFFF)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            activeSectionTab = RoadmapSectionTab.ROADMAP
                        }
                        .testTag("roadmap_tab_roadmap")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Route,
                            contentDescription = null,
                            tint = if (isRoadmapSelected) PulseCyan else Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Roadmap",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isRoadmapSelected) PulseCyan else Color.White,
                                fontWeight = if (isRoadmapSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                fontSize = 12.5.sp
                            )
                        )
                    }
                }

                val isInventorySelected = activeSectionTab == RoadmapSectionTab.INVENTORY
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isInventorySelected) Color(0x3310B981) else Color(0x1F121826),
                    border = BorderStroke(
                        1.dp,
                        if (isInventorySelected) Color(0xFF10B981) else Color(0x22FFFFFF)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            activeSectionTab = RoadmapSectionTab.INVENTORY
                        }
                        .testTag("roadmap_tab_inventory")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = if (isInventorySelected) Color(0xFF34D399) else Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArabic) "المخزون Inventory" else "Inventory",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isInventorySelected) Color(0xFF34D399) else Color.White,
                                fontWeight = if (isInventorySelected) FontWeight.ExtraBold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            AnimatedContent(
                targetState = activeSectionTab,
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(150)) },
                label = "roadmap_section_transition"
            ) { tab ->
                when (tab) {
                    RoadmapSectionTab.ROADMAP -> {
                        SerpentineRoadmapView(
                            currentProgressionLevel = currentProgressionLevel,
                            streakDays = streakDays,
                            claimedLevels = claimedLevels,
                            isArabic = isArabic,
                            onClaimLevel = { level, reward ->
                                streakViewModel.claimVaultLevelReward(level, reward)
                                claimedLevels = streakViewModel.getClaimedVaultLevels()
                            },
                            onClaimAllReady = { readyList ->
                                streakViewModel.claimAllUnlockedVaultRewards(readyList)
                                claimedLevels = streakViewModel.getClaimedVaultLevels()
                            }
                        )
                    }

                    RoadmapSectionTab.INVENTORY -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                                .testTag("inventory_tab_list"),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 36.dp)
                        ) {
                            // Inventory Hero Summary Banner
                            item(key = "inventory_summary_banner") {
                                Surface(
                                    shape = RoundedCornerShape(18.dp),
                                    color = Color(0x2A10B981),
                                    border = BorderStroke(1.dp, Color(0x6610B981).copy(alpha = ambientGlow)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(0x3310B981))
                                                        .border(1.dp, Color(0xFF34D399), CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Inventory2,
                                                        contentDescription = null,
                                                        tint = Color(0xFF34D399),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text(
                                                        text = if (isArabic) "المخزون (Inventory)" else "Player Inventory",
                                                        style = MaterialTheme.typography.titleMedium.copy(
                                                            fontWeight = FontWeight.ExtraBold,
                                                            color = Color.White,
                                                            fontSize = 16.sp
                                                        )
                                                    )
                                                    Text(
                                                        text = if (isArabic) "خزينة الدروع المملوكة والشارات المكتسبة" else "Owned shields vault, quantities & earned badges",
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            color = Color(0xFFCBD5E1),
                                                            fontSize = 11.sp
                                                        )
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            InventoryStatChip(
                                                label = if (isArabic) "إجمالي الدروع" else "Total Shields",
                                                value = "$shieldsCount / 30",
                                                accent = PulseCyan,
                                                modifier = Modifier.weight(1f)
                                            )
                                            InventoryStatChip(
                                                label = if (isArabic) "مكتسب اليوم" else "Farmed Today",
                                                value = "$dailyShieldsToday / 3",
                                                accent = Color(0xFF34D399),
                                                modifier = Modifier.weight(1f)
                                            )
                                            InventoryStatChip(
                                                label = if (isArabic) "الأوسمة المفتوحة" else "Badges Owned",
                                                value = "$unlockedBadgesCount / ${inventoryBadges.size}",
                                                accent = Color(0xFFFFD700),
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // Section 1: Owned Shields Vault (Types & Quantities)
                            item(key = "shields_vault_header") {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = PulseCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isArabic) "خزينة الدروع المملوكة (الأنواع والكميات)" else "Owned Shields Vault (Types & Quantities)",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 15.sp
                                        )
                                    )
                                }
                            }

                            itemsIndexed(shieldTypes, key = { _, s -> s.id }) { _, shield ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                        .border(
                                            1.dp,
                                            Brush.horizontalGradient(listOf(shield.iconColor.copy(alpha = 0.6f), Color(0x22FFFFFF))),
                                            RoundedCornerShape(18.dp)
                                        ),
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xDD121826))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .clip(CircleShape)
                                                    .background(shield.bgGlow),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = shield.icon,
                                                    contentDescription = null,
                                                    tint = shield.iconColor,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column {
                                                Text(
                                                    text = if (isArabic) shield.nameAr else shield.nameEn,
                                                    style = MaterialTheme.typography.titleSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White,
                                                        fontSize = 13.sp
                                                    )
                                                )
                                                Spacer(modifier = Modifier.height(3.dp))
                                                Text(
                                                    text = if (isArabic) shield.descriptionAr else shield.descriptionEn,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = Color(0xFF94A3B8),
                                                        fontSize = 11.sp,
                                                        lineHeight = 15.sp
                                                    )
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        // Quantity Badge
                                        Column(horizontalAlignment = Alignment.End) {
                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = shield.bgGlow,
                                                border = BorderStroke(1.dp, shield.iconColor.copy(alpha = 0.7f))
                                            ) {
                                                Text(
                                                    text = "× ${shield.count}",
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = shield.iconColor,
                                                        fontSize = 14.sp
                                                    )
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = if (isArabic) shield.tagAr else shield.tagEn,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (shield.count > 0) Color(0xFF10B981) else Color(0xFF64748B),
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            // Section 2: Collectible Badges & Titles in Inventory
                            item(key = "inventory_badges_header") {
                                Spacer(modifier = Modifier.height(18.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = Color(0xFFFFD700),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isArabic) "الأوسمة والشارات المملوكة" else "Collected Badges & Titles",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 15.sp
                                        )
                                    )
                                }
                            }

                            items(inventoryBadges, key = { "inv_badge_${it.level}" }) { badgeItem ->
                                val isOwned = streakDays >= badgeItem.level
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .border(
                                            1.dp,
                                            if (isOwned) Color(0x55FFD700) else Color(0x1EFFFFFF),
                                            RoundedCornerShape(16.dp)
                                        ),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isOwned) Color(0xDD121826) else Color(0x880D131F)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(38.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isOwned) Color(0x33FFD700) else Color(0x1F334155)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = if (isOwned) Icons.Default.Verified else Icons.Default.Lock,
                                                    contentDescription = null,
                                                    tint = if (isOwned) Color(0xFFFFD700) else Color(0xFF64748B),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(
                                                    text = badgeItem.badgeName ?: (if (isArabic) badgeItem.titleAr else badgeItem.titleEn),
                                                    style = MaterialTheme.typography.titleSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isOwned) Color.White else Color(0xFF94A3B8),
                                                        fontSize = 13.sp
                                                    )
                                                )
                                                Text(
                                                    text = if (isArabic) badgeItem.titleAr else badgeItem.titleEn,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = Color(0xFF64748B),
                                                        fontSize = 11.sp
                                                    )
                                                )
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isOwned) Color(0x2810B981) else Color(0x1F1E293B),
                                            border = BorderStroke(
                                                1.dp,
                                                if (isOwned) Color(0x5510B981) else Color(0x2264748B)
                                            )
                                        ) {
                                            Text(
                                                text = if (isOwned) {
                                                    if (isArabic) "مملوك ✓" else "Owned ✓"
                                                } else {
                                                    if (isArabic) "المستوى ${badgeItem.level}" else "Lvl ${badgeItem.level}"
                                                },
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (isOwned) Color(0xFF34D399) else Color(0xFF64748B),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InventoryStatChip(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0x330B0F19),
        border = BorderStroke(0.8.dp, accent.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = accent,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.5.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF94A3B8),
                    fontSize = 9.5.sp
                ),
                maxLines = 1
            )
        }
    }
}
