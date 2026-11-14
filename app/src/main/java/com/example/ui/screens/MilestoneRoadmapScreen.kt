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
import com.example.ui.theme.stableStatusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VaultProgressionRegistry
import com.example.ui.components.ForgeSectionView
import com.example.ui.components.ForgeTabDestination
import com.example.ui.components.SerpentineRoadmapView
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.ThemeMode
import com.example.ui.theme.aura
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
    INVENTORY,
    SHOP
}

@Composable
fun MilestoneRoadmapScreen(
    streakViewModel: StreakViewModel,
    streakDays: Int,
    shieldsCount: Int,
    maxShields: Int = 1000,
    currentThemeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onBack: () -> Unit,
    isArabic: Boolean = false,
    modifier: Modifier = Modifier
) {
    val aura = MaterialTheme.aura
    val haptic = LocalHapticFeedback.current
    var selectedShieldInfo by remember { mutableStateOf<ShieldTypeItem?>(null) }

    BackHandler {
        onBack()
    }

    // Section switcher: "Roadmap" (365-Level Serpentine Game Road) vs "Inventory" vs "The Forge"
    var activeSectionTab by remember { mutableStateOf(RoadmapSectionTab.ROADMAP) }
    var forgeDestination by remember { mutableStateOf(ForgeTabDestination.HUB) }

    BackHandler(enabled = activeSectionTab == RoadmapSectionTab.SHOP && forgeDestination != ForgeTabDestination.HUB) {
        forgeDestination = ForgeTabDestination.HUB
    }

    var claimedLevels by remember { mutableStateOf(streakViewModel.getClaimedVaultLevels()) }
    val currentProgressionLevel = streakDays.coerceAtLeast(1).coerceAtMost(365)

    // Shield inventory breakdown
    val standardShields = shieldsCount.coerceAtLeast(0)
    val goldenShields = if (streakDays >= 20) (streakDays / 20) else 0
    val diamondShields = if (streakDays >= 60) (streakDays / 60) else 0
    val flameTokens = if (streakDays >= 5) (streakDays / 5) * 2 else 0
    val dailyShieldsToday = streakViewModel.getShieldsEarnedToday().coerceAtLeast(0)
    val farmSecs by streakViewModel.shieldFarmSeconds.collectAsState()
    val forgeState by streakViewModel.forgeState.collectAsState()
    val farmMins = (farmSecs / 60).coerceIn(0, 60)
    val remainingMins = (60 - farmMins).coerceAtLeast(0)
    val isShieldVaultFull = shieldsCount >= maxShields
    val farmProgress = if (isShieldVaultFull) 1f else (farmSecs.toFloat() / 3600f).coerceIn(0f, 1f)

    val shieldTypes = listOf(
        ShieldTypeItem(
            id = "standard_shield",
            nameAr = "درع الحماية الأساسي",
            nameEn = "Standard Streak Shield",
            descriptionAr = "يحمي سلسلتك تلقائياً عند تفويت مشاهدة يوم كامل لمنع إعادة العداد إلى الصفر.",
            descriptionEn = "Automatically protects streak when missing a full day of watch time.",
            count = standardShields,
            icon = Icons.Default.Shield,
            iconColor = aura.accentCyan,
            bgGlow = aura.accentCyanSoft,
            tagAr = "نشط ومتاح",
            tagEn = "ACTIVE"
        ),
        ShieldTypeItem(
            id = "golden_shield",
            nameAr = "الدرع الذهبي الملكي",
            nameEn = "Golden Royal Shield",
            descriptionAr = "درع نادر يُمنح كل 20 يوماً من السلسلة. يحمي السلسلة ليومين متتاليين دون استهلاك دروع إضافية.",
            descriptionEn = "Rare shield awarded every 20 days. Protects streak for 2 consecutive days.",
            count = goldenShields,
            icon = Icons.Default.Security,
            iconColor = aura.accentGold,
            bgGlow = aura.accentGoldBg,
            tagAr = if (goldenShields > 0) "مكتسب 👑" else "يفتح في اليوم 20",
            tagEn = if (goldenShields > 0) "EARNED 👑" else "DAY 20+"
        ),
        ShieldTypeItem(
            id = "diamond_shield",
            nameAr = "الدرع الماسي الأسطوري",
            nameEn = "Legendary Diamond Shield",
            descriptionAr = "درع أسطوري للمشاهدين الـ VIP. يحمي السلسلة لأسبوع كامل (7 أيام) مع تجميد السلسلة دون فقدانها.",
            descriptionEn = "Legendary VIP shield that freezes and protects streak for an entire week.",
            count = diamondShields,
            icon = Icons.Default.Diamond,
            iconColor = if (aura.isDark) Color(0xFF67E8F9) else Color(0xFF0284C7),
            bgGlow = if (aura.isDark) Color(0x2838BDF8) else Color(0x180284C7),
            tagAr = if (diamondShields > 0) "أسطوري 💎" else "يفتح في اليوم 60",
            tagEn = if (diamondShields > 0) "LEGENDARY 💎" else "DAY 60+"
        ),
        ShieldTypeItem(
            id = "flame_token",
            nameAr = "شعلة الحصانة والمكافأة",
            nameEn = "Flame Boost Token",
            descriptionAr = "شعلة مضاعفة النقاط ومكافأة التقدم السريع نحو المستوى التالي في المنصة.",
            descriptionEn = "Flame multiplier token boosting tier progression and unlocking elite perks.",
            count = flameTokens,
            icon = Icons.Default.LocalFireDepartment,
            iconColor = aura.accentFlame,
            bgGlow = aura.accentFlameSoft,
            tagAr = "$flameTokens شعلة 🔥",
            tagEn = "$flameTokens Flames 🔥"
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
            .background(aura.background)
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
                                aura.accentCyan.copy(alpha = if (aura.isDark) 0.16f else 0.10f),
                                aura.accentFlame.copy(alpha = if (aura.isDark) 0.09f else 0.06f),
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
                .stableStatusBarsPadding()
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
                            .background(aura.iconButtonBg)
                            .testTag("roadmap_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isArabic) "رجوع" else "Back",
                            tint = aura.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (isArabic) "الإنجازات, الدروع والمكافآت" else "Achievements, Shields & Rewards",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = aura.textPrimary,
                                fontSize = 16.5.sp
                            )
                        )
                        Text(
                            text = if (isArabic) "خريطة Roadmap (365)، المخزون و The Forge" else "365-Level Roadmap, Inventory & The Forge",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = aura.textSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Total Shields Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = aura.surfaceCard,
                    border = BorderStroke(1.dp, aura.accentCyan.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = aura.accentCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$shieldsCount 🛡️",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = aura.textPrimary
                            )
                        )
                    }
                }
            }

            // Segmented View Mode Pills: Roadmap vs Inventory vs The Forge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val isRoadmapSelected = activeSectionTab == RoadmapSectionTab.ROADMAP
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isRoadmapSelected) aura.accentCyanSoft else aura.unselectedChipBg,
                    border = BorderStroke(
                        1.dp,
                        if (isRoadmapSelected) aura.accentCyan else aura.borderSubtle
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
                            tint = if (isRoadmapSelected) aura.accentCyan else aura.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Roadmap",
                            maxLines = 1,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isRoadmapSelected) aura.accentCyan else aura.textPrimary,
                                fontWeight = if (isRoadmapSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                val isInventorySelected = activeSectionTab == RoadmapSectionTab.INVENTORY
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isInventorySelected) aura.accentEmeraldSoft else aura.unselectedChipBg,
                    border = BorderStroke(
                        1.dp,
                        if (isInventorySelected) aura.accentEmerald else aura.borderSubtle
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
                            tint = if (isInventorySelected) aura.accentEmerald else aura.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isArabic) "المخزون" else "Inventory",
                            maxLines = 1,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isInventorySelected) aura.accentEmerald else aura.textPrimary,
                                fontWeight = if (isInventorySelected) FontWeight.ExtraBold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                val isShopSelected = activeSectionTab == RoadmapSectionTab.SHOP
                val shopAccent = aura.accentGold
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isShopSelected) aura.accentGoldBg else aura.unselectedChipBg,
                    border = BorderStroke(
                        1.dp,
                        if (isShopSelected) shopAccent else aura.borderSubtle
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            forgeDestination = ForgeTabDestination.HUB
                            activeSectionTab = RoadmapSectionTab.SHOP
                        }
                        .testTag("roadmap_tab_shop")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = if (isShopSelected) shopAccent else aura.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "The Forge",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isShopSelected) shopAccent else aura.textPrimary,
                                fontWeight = if (isShopSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                fontSize = 11.5.sp
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
                                    color = aura.accentEmeraldSoft,
                                    border = BorderStroke(1.dp, aura.accentEmerald.copy(alpha = 0.55f * ambientGlow)),
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
                                                        .background(aura.accentEmeraldSoft)
                                                        .border(1.dp, aura.accentEmerald, CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Inventory2,
                                                        contentDescription = null,
                                                        tint = aura.accentEmerald,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text(
                                                        text = if (isArabic) "المخزون (Inventory)" else "Player Inventory",
                                                        style = MaterialTheme.typography.titleMedium.copy(
                                                            fontWeight = FontWeight.ExtraBold,
                                                            color = aura.textPrimary,
                                                            fontSize = 16.sp
                                                        )
                                                    )
                                                    Text(
                                                        text = if (isArabic) "خزينة الدروع المملوكة والشارات المكتسبة" else "Owned shields vault, quantities & earned badges",
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            color = aura.textSecondary,
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
                                                value = "$shieldsCount / $maxShields",
                                                accent = aura.accentCyan,
                                                modifier = Modifier.weight(1f)
                                            )
                                            InventoryStatChip(
                                                label = if (isArabic) "مكتسب اليوم" else "Farmed Today",
                                                value = "$dailyShieldsToday ♾️",
                                                accent = aura.accentEmerald,
                                                modifier = Modifier.weight(1f)
                                            )
                                            InventoryStatChip(
                                                label = if (isArabic) "الأوسمة المفتوحة" else "Badges Owned",
                                                value = "$unlockedBadgesCount / ${inventoryBadges.size}",
                                                accent = aura.accentGold,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                            }

                            // Shield Farming Progress Bar Card (above Owned Shields Vault)
                            item(key = "inventory_shield_farming_progress") {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            1.dp,
                                            Brush.horizontalGradient(
                                                listOf(
                                                    aura.accentCyan.copy(alpha = 0.65f),
                                                    aura.accentEmerald.copy(alpha = 0.45f)
                                                )
                                            ),
                                            RoundedCornerShape(18.dp)
                                        )
                                        .testTag("inventory_shield_farming_card"),
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = aura.surfaceCard)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(34.dp)
                                                        .clip(CircleShape)
                                                        .background(aura.accentCyanSoft),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.AccessTime,
                                                        contentDescription = null,
                                                        tint = if (isShieldVaultFull) aura.accentEmerald else aura.accentCyan,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = if (isArabic) "شريط تقدم مزرعة الدروع (غير محدود ∞)" else "Shield Farming Progress (Unlimited ∞)",
                                                        style = MaterialTheme.typography.titleSmall.copy(
                                                            color = aura.textPrimary,
                                                            fontWeight = FontWeight.ExtraBold,
                                                            fontSize = 13.5.sp
                                                        )
                                                    )
                                                    Text(
                                                        text = when {
                                                            isShieldVaultFull -> if (isArabic) {
                                                                "خزينة الدروع ممتلئة ($shieldsCount / $maxShields)"
                                                            } else {
                                                                "Shield vault is full ($shieldsCount / $maxShields)"
                                                            }
                                                            else -> if (isArabic) {
                                                                "$farmMins / 60 دقيقة للدرع القادم • متبقي $remainingMins د"
                                                            } else {
                                                                "$farmMins / 60 mins to next shield • $remainingMins min left"
                                                            }
                                                        },
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            color = aura.textSecondary,
                                                            fontSize = 11.sp
                                                        )
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(8.dp))

                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = if (isShieldVaultFull) aura.accentEmeraldSoft else aura.accentCyanSoft,
                                                border = BorderStroke(
                                                    1.dp,
                                                    if (isShieldVaultFull) aura.accentEmerald.copy(alpha = 0.55f) else aura.accentCyan.copy(alpha = 0.55f)
                                                )
                                            ) {
                                                Text(
                                                    text = "${(farmProgress * 100).toInt()}%",
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = if (isShieldVaultFull) aura.accentEmerald else aura.accentCyan,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        fontSize = 11.sp
                                                    )
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        LinearProgressIndicator(
                                            progress = { farmProgress },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(9.dp)
                                                .clip(RoundedCornerShape(5.dp))
                                                .testTag("inventory_shield_farming_progress_bar"),
                                            color = if (isShieldVaultFull) aura.accentEmerald else aura.accentCyan,
                                            trackColor = aura.progressTrack
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // Section 1: Owned Shields Vault (Types & Quantities) — 2 Shields Per Row Grid
                            item(key = "shields_vault_header") {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Shield,
                                            contentDescription = null,
                                            tint = aura.accentCyan,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isArabic) "خزينة الدروع المملوكة (الأنواع والكميات)" else "Owned Shields Vault (Types & Quantities)",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = aura.textPrimary,
                                                fontSize = 14.5.sp
                                            )
                                        )
                                    }
                                }
                            }

                            items(
                                items = shieldTypes.chunked(2),
                                key = { row -> row.joinToString("_") { it.id } }
                            ) { pair ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    pair.forEach { shield ->
                                        Card(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(18.dp))
                                                .border(
                                                    1.1.dp,
                                                    Brush.verticalGradient(
                                                        listOf(
                                                            shield.iconColor.copy(alpha = 0.65f),
                                                            aura.borderSubtle
                                                        )
                                                    ),
                                                    RoundedCornerShape(18.dp)
                                                )
                                                .clickable {
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    selectedShieldInfo = shield
                                                }
                                                .testTag("shield_vault_card_${shield.id}"),
                                            shape = RoundedCornerShape(18.dp),
                                            colors = CardDefaults.cardColors(containerColor = aura.surfaceCard)
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(14.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Surface(
                                                        shape = RoundedCornerShape(10.dp),
                                                        color = shield.bgGlow,
                                                        border = BorderStroke(1.dp, shield.iconColor.copy(alpha = 0.65f))
                                                    ) {
                                                        Text(
                                                            text = "× ${shield.count}",
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                            style = MaterialTheme.typography.labelLarge.copy(
                                                                fontWeight = FontWeight.ExtraBold,
                                                                color = shield.iconColor,
                                                                fontSize = 12.5.sp
                                                            )
                                                        )
                                                    }

                                                    Icon(
                                                        imageVector = Icons.Default.Info,
                                                        contentDescription = if (isArabic) "تفاصيل الدرع" else "Shield details",
                                                        tint = aura.textTertiary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }

                                                Spacer(modifier = Modifier.height(10.dp))

                                                Box(
                                                    modifier = Modifier
                                                        .size(50.dp)
                                                        .clip(CircleShape)
                                                        .background(shield.bgGlow)
                                                        .border(1.2.dp, shield.iconColor.copy(alpha = 0.45f), CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = shield.icon,
                                                        contentDescription = null,
                                                        tint = shield.iconColor,
                                                        modifier = Modifier.size(26.dp)
                                                    )
                                                }

                                                Spacer(modifier = Modifier.height(10.dp))

                                                Text(
                                                    text = if (isArabic) shield.nameAr else shield.nameEn,
                                                    style = MaterialTheme.typography.titleSmall.copy(
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = aura.textPrimary,
                                                        fontSize = 12.5.sp
                                                    ),
                                                    textAlign = TextAlign.Center,
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis
                                                )

                                                Spacer(modifier = Modifier.height(6.dp))

                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = if (shield.count > 0) aura.accentEmeraldSoft else aura.surfaceSubtle,
                                                    border = BorderStroke(
                                                        0.8.dp,
                                                        if (shield.count > 0) aura.accentEmerald.copy(alpha = 0.45f) else aura.borderSubtle
                                                    )
                                                ) {
                                                    Text(
                                                        text = if (isArabic) shield.tagAr else shield.tagEn,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            color = if (shield.count > 0) aura.accentEmerald else aura.textSecondary,
                                                            fontSize = 9.5.sp,
                                                            fontWeight = FontWeight.Bold
                                                        ),
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    if (pair.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }

                            // Forged Keys Vault Summary in Inventory
                            item(key = "inventory_forged_keys_section") {
                                Spacer(modifier = Modifier.height(16.dp))
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            1.dp,
                                            Brush.horizontalGradient(
                                                listOf(
                                                    aura.accentGold.copy(alpha = 0.6f),
                                                    aura.accentCyan.copy(alpha = 0.55f)
                                                )
                                            ),
                                            RoundedCornerShape(18.dp)
                                        )
                                        .testTag("inventory_forged_keys_card"),
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = aura.surfaceCard)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.VpnKey,
                                                    contentDescription = null,
                                                    tint = aura.accentGold,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = if (isArabic) "خزينة المفاتيح المصهورة (The Forge)" else "Forged Keys Vault",
                                                    style = MaterialTheme.typography.titleSmall.copy(
                                                        color = aura.textPrimary,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        fontSize = 14.sp
                                                    )
                                                )
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = aura.accentGoldBg,
                                                border = BorderStroke(1.dp, aura.accentGold.copy(alpha = 0.55f)),
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .clickable {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        activeSectionTab = RoadmapSectionTab.SHOP
                                                        forgeDestination = ForgeTabDestination.INSIDE_FORGE
                                                    }
                                            ) {
                                                Text(
                                                    text = if (isArabic) "فتح Forge 🔥" else "Open Forge 🔥",
                                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = aura.accentGold,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        fontSize = 10.5.sp
                                                    )
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            InventoryStatChip(
                                                label = if (isArabic) "برونزي" else "Bronze",
                                                value = "${forgeState.bronzeKeys}",
                                                accent = if (aura.isDark) Color(0xFFE59A54) else Color(0xFFB45309),
                                                modifier = Modifier.weight(1f)
                                            )
                                            InventoryStatChip(
                                                label = if (isArabic) "فضي" else "Silver",
                                                value = "${forgeState.silverKeys}",
                                                accent = if (aura.isDark) Color(0xFFE2E8F0) else Color(0xFF475569),
                                                modifier = Modifier.weight(1f)
                                            )
                                            InventoryStatChip(
                                                label = if (isArabic) "ذهبي" else "Gold",
                                                value = "${forgeState.goldenKeys}",
                                                accent = aura.accentGold,
                                                modifier = Modifier.weight(1f)
                                            )
                                            InventoryStatChip(
                                                label = if (isArabic) "ماسي" else "Diamond",
                                                value = "${forgeState.diamondKeys}",
                                                accent = if (aura.isDark) Color(0xFF38BDF8) else Color(0xFF0284C7),
                                                modifier = Modifier.weight(1f)
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
                                        tint = aura.accentGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isArabic) "الأوسمة والشارات المملوكة" else "Collected Badges & Titles",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = aura.textPrimary,
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
                                            if (isOwned) aura.accentGold.copy(alpha = 0.55f) else aura.borderSubtle,
                                            RoundedCornerShape(16.dp)
                                        ),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isOwned) aura.surfaceCard else aura.surfaceSubtle
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
                                                    .background(if (isOwned) aura.accentGoldBg else aura.surfaceGlass),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = if (isOwned) Icons.Default.Verified else Icons.Default.Lock,
                                                    contentDescription = null,
                                                    tint = if (isOwned) aura.accentGold else aura.textTertiary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(
                                                    text = badgeItem.badgeName ?: (if (isArabic) badgeItem.titleAr else badgeItem.titleEn),
                                                    style = MaterialTheme.typography.titleSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isOwned) aura.textPrimary else aura.textSecondary,
                                                        fontSize = 13.sp
                                                    )
                                                )
                                                Text(
                                                    text = if (isArabic) badgeItem.titleAr else badgeItem.titleEn,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = aura.textTertiary,
                                                        fontSize = 11.sp
                                                    )
                                                )
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isOwned) aura.accentEmeraldSoft else aura.unselectedChipBg,
                                            border = BorderStroke(
                                                1.dp,
                                                if (isOwned) aura.accentEmerald.copy(alpha = 0.5f) else aura.borderSubtle
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
                                                    color = if (isOwned) aura.accentEmerald else aura.textTertiary,
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

                    RoadmapSectionTab.SHOP -> {
                        ForgeSectionView(
                            shieldsCount = shieldsCount,
                            forgeState = forgeState,
                            isArabic = isArabic,
                            activeView = forgeDestination,
                            onNavigateView = { forgeDestination = it },
                            onStartForge = { tier ->
                                streakViewModel.startForge(tier, isArabic = isArabic)
                            },
                            onClaimForgeKey = { tier ->
                                streakViewModel.claimForgeKey(tier, isArabic = isArabic)
                            },
                            onCancelForge = { tier ->
                                streakViewModel.cancelForge(tier, isArabic = isArabic)
                            }
                        )
                    }
                }
            }
        }

        // Shield Role Explanation Popup Dialog
        val activeShield = selectedShieldInfo
        if (activeShield != null) {
            AlertDialog(
                onDismissRequest = { selectedShieldInfo = null },
                containerColor = aura.dialogBg,
                modifier = Modifier.testTag("shield_role_dialog"),
                icon = {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(activeShield.bgGlow)
                            .border(1.4.dp, activeShield.iconColor.copy(alpha = 0.7f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = activeShield.icon,
                            contentDescription = null,
                            tint = activeShield.iconColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isArabic) activeShield.nameAr else activeShield.nameEn,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = aura.textPrimary,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = activeShield.bgGlow,
                            border = BorderStroke(1.dp, activeShield.iconColor.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = if (isArabic) "الكمية المملوكة: × ${activeShield.count}" else "Owned: × ${activeShield.count}",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = activeShield.iconColor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isArabic) "دور هذا الدرع:" else "Shield Role:",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = aura.accentCyan,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isArabic) activeShield.descriptionAr else activeShield.descriptionEn,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = aura.textSecondary,
                                lineHeight = 21.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { selectedShieldInfo = null }) {
                        Text(
                            text = if (isArabic) "فهمت" else "Got it",
                            color = aura.accentCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
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
    val aura = MaterialTheme.aura
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (aura.isDark) Color(0x330B0F19) else aura.surfaceElevated,
        border = BorderStroke(0.8.dp, accent.copy(alpha = 0.45f))
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
                    color = aura.textSecondary,
                    fontSize = 9.5.sp
                ),
                maxLines = 1
            )
        }
    }
}
