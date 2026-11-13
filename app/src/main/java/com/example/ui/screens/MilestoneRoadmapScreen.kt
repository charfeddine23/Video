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
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
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
import com.example.data.model.VaultTier
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.Charcoal950
import com.example.ui.theme.PulseCyan
import com.example.ui.theme.PulseViolet
import com.example.ui.theme.ThemeMode
import com.example.ui.viewmodel.MilestoneItem
import com.example.ui.viewmodel.MilestoneStatus
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
    VAULT_365,
    SNAKE_ROAD
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

    val milestones = streakViewModel.milestones

    // Section switcher: Shields + 365-Level Feature Vault (default) vs Winding Snake Prize Road
    var activeSectionTab by remember { mutableStateOf(RoadmapSectionTab.VAULT_365) }

    // 365-Level Progression System & Feature Vault States
    var selectedVaultTier by remember { mutableStateOf(VaultTier.FOUNDATION) }
    var claimedLevels by remember { mutableStateOf(streakViewModel.getClaimedVaultLevels()) }
    val currentProgressionLevel = streakDays.coerceAtLeast(1).coerceAtMost(365)

    // Interactive feature vault toggle states
    var mediaSharingEnabled by remember { mutableStateOf(true) }
    var selectedCustomThemeIndex by remember { mutableStateOf(0) }
    var smartNotificationsEnabled by remember { mutableStateOf(true) }
    var backgroundPlayEnabled by remember { mutableStateOf(true) }
    var pipModeEnabled by remember { mutableStateOf(true) }

    // Hidden Vault States (Level 10 Milestone)
    var showVaultAuthDialog by remember { mutableStateOf(false) }
    var isVaultUnlocked by remember { mutableStateOf(false) }
    var vaultPinInput by remember { mutableStateOf("") }
    var vaultPinError by remember { mutableStateOf<String?>(null) }
    val vaultSavedPin = remember { streakViewModel.getVaultPin() ?: "1234" }

    // Calculate individual shield inventory breakdown
    val standardShields = shieldsCount.coerceAtLeast(0)
    val goldenShields = if (streakDays >= 20) (streakDays / 20) else 0
    val diamondShields = if (streakDays >= 60) (streakDays / 60) else 0
    val flameTokens = if (streakDays >= 5) (streakDays / 5) * 2 else 0

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
                        center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.15f)
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
                            text = if (isArabic) "خزينة الدروع ومسار الـ 365 مستوى" else "Shields Vault & 365-Level Progression",
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

            // Segmented View Mode Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val isVaultSelected = activeSectionTab == RoadmapSectionTab.VAULT_365
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isVaultSelected) Color(0x3300F2FE) else Color(0x1F121826),
                    border = BorderStroke(
                        1.dp,
                        if (isVaultSelected) PulseCyan else Color(0x22FFFFFF)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            activeSectionTab = RoadmapSectionTab.VAULT_365
                        }
                        .testTag("roadmap_tab_vault_365")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = if (isVaultSelected) PulseCyan else Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArabic) "الدروع ومسار 365 مستوى" else "Shields & 365 Vault",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isVaultSelected) PulseCyan else Color.White,
                                fontWeight = if (isVaultSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                val isSnakeSelected = activeSectionTab == RoadmapSectionTab.SNAKE_ROAD
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSnakeSelected) Color(0x3310B981) else Color(0x1F121826),
                    border = BorderStroke(
                        1.dp,
                        if (isSnakeSelected) Color(0xFF10B981) else Color(0x22FFFFFF)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            activeSectionTab = RoadmapSectionTab.SNAKE_ROAD
                        }
                        .testTag("roadmap_tab_snake_road")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = null,
                            tint = if (isSnakeSelected) Color(0xFF34D399) else Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArabic) "طريق الجائزة المتعرج" else "Prize Road",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isSnakeSelected) Color(0xFF34D399) else Color.White,
                                fontWeight = if (isSnakeSelected) FontWeight.ExtraBold else FontWeight.Medium,
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
                    RoadmapSectionTab.VAULT_365 -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 36.dp)
                        ) {
                            // Section 1: Types of Shields & Quantities (خزينة الدروع المملوكة)
                            item(key = "shields_vault_header") {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = PulseCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isArabic) "خزينة الدروع المملوكة (الأنواع والكميات)" else "Owned Shields Inventory",
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

                            // Section 2: 365-Level Progression System & Feature Vault (Moved from My Space)
                            item(key = "feature_vault_header") {
                                Spacer(modifier = Modifier.height(18.dp))
                                Column {
                                    Text(
                                        text = if (isArabic) "خزانة الميزات ومسار الـ 365 مستوى" else "365-Level Feature Vault",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White,
                                            fontSize = 16.sp
                                        )
                                    )
                                    Text(
                                        text = if (isArabic) "مستويات متصاعدة ومكافآت دروع من المستوى 1 حتى 365" else "Progression milestones & shield drops from Lvl 1 to 365",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF94A3B8),
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            // 365-Level Progress Summary Card
                            item(key = "vault_progress_summary_card") {
                                val progressRatio = (currentProgressionLevel.toFloat() / 365f).coerceIn(0f, 1f)
                                val unclaimedCount = (1..currentProgressionLevel).count { !claimedLevels.contains(it) }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            1.dp,
                                            Brush.horizontalGradient(listOf(Color(0x4400F2FE), Color(0x44FFB74D), Color(0x44FFD700))),
                                            RoundedCornerShape(20.dp)
                                        ),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xD9101626))
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
                                                        .size(38.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(0x3300F2FE)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = if (streakDays >= 365) Icons.Default.EmojiEvents else Icons.Default.WorkspacePremium,
                                                        contentDescription = null,
                                                        tint = if (streakDays >= 365) Color(0xFFFFD700) else PulseCyan,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = if (isArabic) "مستوى التقدم الحالي: المستوى $currentProgressionLevel" else "Progression: Level $currentProgressionLevel of 365",
                                                        style = MaterialTheme.typography.titleSmall.copy(
                                                            color = Color.White,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    )
                                                    Text(
                                                        text = if (streakDays >= 365)
                                                            (if (isArabic) "🏆 تم تحقيق أسطورة الـ 365 يوماً بالكامل!" else "🏆 365-Day Mastery Achieved!")
                                                        else
                                                            (if (isArabic) "باقي ${365 - currentProgressionLevel} مستوى للوصول إلى وسام الأسطورة الذهبي" else "${365 - currentProgressionLevel} levels to Yearly Aura Legend"),
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            color = if (streakDays >= 365) Color(0xFFFFD700) else Color(0xFF94A3B8),
                                                            fontSize = 10.5.sp
                                                        )
                                                    )
                                                }
                                            }

                                            if (unclaimedCount > 0) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = Color(0x33FFB74D),
                                                    border = BorderStroke(1.dp, Color(0xFFFFB74D))
                                                ) {
                                                    Text(
                                                        text = if (isArabic) "$unclaimedCount مكافآت جاهزة 🎁" else "$unclaimedCount Ready 🎁",
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            color = Color(0xFFFFD700),
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 10.sp
                                                        )
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        LinearProgressIndicator(
                                            progress = { progressRatio },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = if (streakDays >= 365) Color(0xFFFFD700) else PulseCyan,
                                            trackColor = Color(0x22FFFFFF)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            // 4 Tier Navigation Tabs
                            item(key = "vault_tier_tabs") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    VaultTier.values().forEach { tier ->
                                        val isSelected = selectedVaultTier == tier
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable {
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    selectedVaultTier = tier
                                                }
                                                .testTag("vault_tier_${tier.name.lowercase()}"),
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (isSelected) Color(0x3300F2FE) else Color(0x1F121826),
                                            border = BorderStroke(
                                                1.dp,
                                                if (isSelected) PulseCyan else Color(0x22FFFFFF)
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = if (isArabic) tier.titleAr else tier.titleEn,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = if (isSelected) PulseCyan else Color.White,
                                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                                        fontSize = 10.5.sp
                                                    ),
                                                    maxLines = 1
                                                )
                                                Text(
                                                    text = tier.rangeText,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = if (isSelected) Color(0xFF67E8F9) else Color(0xFF64748B),
                                                        fontSize = 9.sp
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                            }

                            // Render Curated Milestones for Selected Tier (365-Level Feature Vault)
                            val tierMilestones = VaultProgressionRegistry.getTierMilestones(selectedVaultTier)
                            items(
                                items = tierMilestones,
                                key = { item -> "vault_milestone_${selectedVaultTier.name}_${item.level}" }
                            ) { item ->
                                val isUnlocked = currentProgressionLevel >= item.level
                                val isClaimed = claimedLevels.contains(item.level)

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            1.dp,
                                            when {
                                                isClaimed -> Color(0x4410B981)
                                                isUnlocked -> PulseCyan.copy(alpha = 0.6f)
                                                else -> Color(0x22FFFFFF)
                                            },
                                            RoundedCornerShape(20.dp)
                                        ),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isUnlocked) Color(0xD9121826) else Color(0x990D131F)
                                    )
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
                                                // Level Indicator Badge
                                                Box(
                                                    modifier = Modifier
                                                        .size(38.dp)
                                                        .clip(CircleShape)
                                                        .background(
                                                            when {
                                                                isClaimed -> Color(0x3310B981)
                                                                isUnlocked -> Color(0x3300F2FE)
                                                                else -> Color(0x22334155)
                                                            }
                                                        )
                                                        .border(
                                                            1.dp,
                                                            when {
                                                                isClaimed -> Color(0xFF10B981)
                                                                isUnlocked -> PulseCyan
                                                                else -> Color(0x3364748B)
                                                            },
                                                            CircleShape
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "${item.level}",
                                                        style = MaterialTheme.typography.labelMedium.copy(
                                                            color = when {
                                                                isClaimed -> Color(0xFF10B981)
                                                                isUnlocked -> PulseCyan
                                                                else -> Color(0xFF64748B)
                                                            },
                                                            fontWeight = FontWeight.ExtraBold,
                                                            fontSize = 12.sp
                                                        )
                                                    )
                                                }

                                                Spacer(modifier = Modifier.width(10.dp))

                                                Column {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(
                                                            text = if (isArabic) item.titleAr else item.titleEn,
                                                            style = MaterialTheme.typography.titleSmall.copy(
                                                                color = if (isUnlocked) Color.White else Color(0xFF94A3B8),
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 12.5.sp
                                                            )
                                                        )
                                                        if (item.isMajor) {
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Surface(
                                                                shape = RoundedCornerShape(6.dp),
                                                                color = Color(0x33FFD700)
                                                            ) {
                                                                Text(
                                                                    text = "★",
                                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                                        color = Color(0xFFFFD700),
                                                                        fontSize = 10.sp
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }

                                                    Text(
                                                        text = if (isArabic) item.descriptionAr else item.descriptionEn,
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            color = Color(0xFF64748B),
                                                            fontSize = 10.5.sp
                                                        )
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(8.dp))

                                            // Shields Reward Pill
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (isUnlocked) Color(0x2600F2FE) else Color(0x1F334155),
                                                border = BorderStroke(
                                                    1.dp,
                                                    if (isUnlocked) PulseCyan.copy(alpha = 0.5f) else Color(0x2264748B)
                                                )
                                            ) {
                                                Text(
                                                    text = "+${item.shieldsReward} 🛡️",
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = if (isUnlocked) PulseCyan else Color(0xFF64748B),
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 10.5.sp
                                                    )
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Action / Claim Row
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            if (isUnlocked) {
                                                if (isClaimed) {
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = Color(0x2610B981),
                                                        border = BorderStroke(1.dp, Color(0x5510B981))
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Check,
                                                                contentDescription = null,
                                                                tint = Color(0xFF10B981),
                                                                modifier = Modifier.size(13.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text(
                                                                text = if (isArabic) "تم الاستلام ✓" else "Claimed ✓",
                                                                style = MaterialTheme.typography.labelSmall.copy(
                                                                    color = Color(0xFF34D399),
                                                                    fontWeight = FontWeight.Bold,
                                                                    fontSize = 11.sp
                                                                )
                                                            )
                                                        }
                                                    }
                                                } else {
                                                    Surface(
                                                        shape = RoundedCornerShape(10.dp),
                                                        color = Color(0x3300FFCC),
                                                        border = BorderStroke(1.dp, PulseCyan),
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(10.dp))
                                                            .clickable {
                                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                                streakViewModel.claimVaultLevelReward(item.level, item.shieldsReward)
                                                                claimedLevels = streakViewModel.getClaimedVaultLevels()
                                                            }
                                                            .testTag("claim_level_reward_${item.level}")
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Shield,
                                                                contentDescription = null,
                                                                tint = PulseCyan,
                                                                modifier = Modifier.size(14.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Text(
                                                                text = if (isArabic) "استلام المكافأة (+${item.shieldsReward} دروع 🛡️)" else "Claim Reward (+${item.shieldsReward} 🛡️)",
                                                                style = MaterialTheme.typography.labelSmall.copy(
                                                                    color = PulseCyan,
                                                                    fontWeight = FontWeight.ExtraBold,
                                                                    fontSize = 11.sp
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                            } else {
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = Color(0x1F1E293B),
                                                    border = BorderStroke(1.dp, Color(0x2264748B))
                                                ) {
                                                    Text(
                                                        text = if (isArabic) "🔒 يفتح في المستوى ${item.level}" else "🔒 Unlocks at Level ${item.level}",
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            color = Color(0xFF64748B),
                                                            fontSize = 10.sp
                                                        )
                                                    )
                                                }
                                            }

                                            if (item.badgeName != null && isUnlocked) {
                                                Text(
                                                    text = item.badgeName,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = Color(0xFFFFD700),
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 10.sp
                                                    )
                                                )
                                            }
                                        }

                                        // Embedded Interactive Feature Controls for Core Levels
                                        if (isUnlocked) {
                                            when (item.level) {
                                                5 -> {
                                                    // Theme Mode Switcher
                                                    Spacer(modifier = Modifier.height(10.dp))
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        ThemeChip(
                                                            icon = Icons.Default.DarkMode,
                                                            label = if (isArabic) "ليلي" else "Dark",
                                                            isSelected = currentThemeMode == ThemeMode.DARK,
                                                            onClick = {
                                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                                onThemeModeChange(ThemeMode.DARK)
                                                            },
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                        ThemeChip(
                                                            icon = Icons.Default.LightMode,
                                                            label = if (isArabic) "نهاري" else "Light",
                                                            isSelected = currentThemeMode == ThemeMode.LIGHT,
                                                            onClick = {
                                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                                onThemeModeChange(ThemeMode.LIGHT)
                                                            },
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                        ThemeChip(
                                                            icon = Icons.Default.BrightnessAuto,
                                                            label = if (isArabic) "نظام" else "System",
                                                            isSelected = currentThemeMode == ThemeMode.SYSTEM,
                                                            onClick = {
                                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                                onThemeModeChange(ThemeMode.SYSTEM)
                                                            },
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                    }
                                                }
                                                10 -> {
                                                    // App Lock & Secret Media Vault
                                                    Spacer(modifier = Modifier.height(10.dp))
                                                    if (!isVaultUnlocked) {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.SpaceBetween
                                                        ) {
                                                            Text(
                                                                text = if (isArabic) "الخزنة مقفلة حالياً 🔒" else "Vault is Locked 🔒",
                                                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                                                            )
                                                            Surface(
                                                                shape = RoundedCornerShape(8.dp),
                                                                color = Color(0x3300FFCC),
                                                                border = BorderStroke(1.dp, PulseCyan.copy(alpha = 0.6f)),
                                                                modifier = Modifier
                                                                    .clip(RoundedCornerShape(8.dp))
                                                                    .clickable {
                                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                                        vaultPinInput = ""
                                                                        vaultPinError = null
                                                                        showVaultAuthDialog = true
                                                                    }
                                                            ) {
                                                                Text(
                                                                    text = if (isArabic) "فتح الخزنة 🔓" else "Unlock 🔓",
                                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                                        color = PulseCyan,
                                                                        fontWeight = FontWeight.Bold
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    } else {
                                                        Surface(
                                                            shape = RoundedCornerShape(10.dp),
                                                            color = Color(0x1F10B981),
                                                            border = BorderStroke(1.dp, Color(0x4410B981)),
                                                            modifier = Modifier.fillMaxWidth()
                                                        ) {
                                                            Row(
                                                                modifier = Modifier.padding(10.dp),
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.SpaceBetween
                                                            ) {
                                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                                    Icon(
                                                                        imageVector = Icons.Default.LockOpen,
                                                                        contentDescription = null,
                                                                        tint = Color(0xFF34D399),
                                                                        modifier = Modifier.size(14.dp)
                                                                    )
                                                                    Spacer(modifier = Modifier.width(6.dp))
                                                                    Text(
                                                                        text = if (isArabic) "الخزنة مفعلة ومحمية بتشفير محلي ✓" else "Vault active & protected ✓",
                                                                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF34D399), fontSize = 11.sp)
                                                                    )
                                                                }
                                                                Surface(
                                                                    shape = RoundedCornerShape(6.dp),
                                                                    color = Color(0x22FFFFFF),
                                                                    modifier = Modifier.clickable { isVaultUnlocked = false }
                                                                ) {
                                                                    Text(
                                                                        text = if (isArabic) "قفل" else "Lock",
                                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 10.sp)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                15 -> {
                                                    // Manual Language Selection
                                                    Spacer(modifier = Modifier.height(10.dp))
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        Surface(
                                                            shape = RoundedCornerShape(10.dp),
                                                            color = if (currentLanguage == AppLanguage.ARABIC) Color(0x3300F2FE) else Color(0x1F121826),
                                                            border = BorderStroke(1.dp, if (currentLanguage == AppLanguage.ARABIC) PulseCyan else Color(0x22FFFFFF)),
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .clip(RoundedCornerShape(10.dp))
                                                                .clickable {
                                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                                    onLanguageChange(AppLanguage.ARABIC)
                                                                }
                                                        ) {
                                                            Box(modifier = Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                                                                Text("العربية", color = if (currentLanguage == AppLanguage.ARABIC) PulseCyan else Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                            }
                                                        }
                                                        Surface(
                                                            shape = RoundedCornerShape(10.dp),
                                                            color = if (currentLanguage == AppLanguage.ENGLISH) Color(0x3300F2FE) else Color(0x1F121826),
                                                            border = BorderStroke(1.dp, if (currentLanguage == AppLanguage.ENGLISH) PulseCyan else Color(0x22FFFFFF)),
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .clip(RoundedCornerShape(10.dp))
                                                                .clickable {
                                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                                    onLanguageChange(AppLanguage.ENGLISH)
                                                                }
                                                        ) {
                                                            Box(modifier = Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                                                                Text("English", color = if (currentLanguage == AppLanguage.ENGLISH) PulseCyan else Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                            }
                                                        }
                                                    }
                                                }
                                                20 -> {
                                                    // Wi-Fi Direct Sharing Switch
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(
                                                            text = if (mediaSharingEnabled)
                                                                (if (isArabic) "مشاركة الوسائط اللاسلكية مفعلة" else "Offline P2P Sharing Active")
                                                            else
                                                                (if (isArabic) "المشاركة معطلة" else "Sharing Inactive"),
                                                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 11.sp)
                                                        )
                                                        Switch(
                                                            checked = mediaSharingEnabled,
                                                            onCheckedChange = {
                                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                                mediaSharingEnabled = it
                                                            },
                                                            colors = SwitchDefaults.colors(checkedTrackColor = PulseCyan, checkedThumbColor = Color.White)
                                                        )
                                                    }
                                                }
                                                25 -> {
                                                    // Exclusive 6 Themes Pack
                                                    Spacer(modifier = Modifier.height(10.dp))
                                                    val themePresets = listOf(
                                                        "Cyan Glass" to PulseCyan,
                                                        "Neon Amber" to Color(0xFFFFB74D),
                                                        "Royal Emerald" to Color(0xFF10B981),
                                                        "Pulse Violet" to PulseViolet,
                                                        "Solar Crimson" to Color(0xFFFF5722),
                                                        "Obsidian OLED" to Color(0xFF1E293B)
                                                    )
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        themePresets.forEachIndexed { i, (_, color) ->
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(28.dp)
                                                                    .clip(CircleShape)
                                                                    .background(color)
                                                                    .border(
                                                                        if (selectedCustomThemeIndex == i) 2.dp else 1.dp,
                                                                        if (selectedCustomThemeIndex == i) Color.White else Color.Transparent,
                                                                        CircleShape
                                                                    )
                                                                    .clickable {
                                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                                        selectedCustomThemeIndex = i
                                                                    }
                                                            )
                                                        }
                                                    }
                                                }
                                                30 -> {
                                                    // Smart Notifications Switch
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(
                                                            text = if (isArabic) "تنبيهات حماية السلسلة مفعلة" else "Streak Protection Alerts ON",
                                                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 11.sp)
                                                        )
                                                        Switch(
                                                            checked = smartNotificationsEnabled,
                                                            onCheckedChange = {
                                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                                smartNotificationsEnabled = it
                                                            },
                                                            colors = SwitchDefaults.colors(checkedTrackColor = PulseCyan, checkedThumbColor = Color.White)
                                                        )
                                                    }
                                                }
                                                35 -> {
                                                    // Background Play Switch
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(
                                                            text = if (backgroundPlayEnabled)
                                                                (if (isArabic) "التشغيل في الخلفية مفعل" else "Background Engine ON")
                                                            else
                                                                (if (isArabic) "التشغيل في الخلفية معطل" else "Background Engine OFF"),
                                                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 11.sp)
                                                        )
                                                        Switch(
                                                            checked = backgroundPlayEnabled,
                                                            onCheckedChange = {
                                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                                backgroundPlayEnabled = it
                                                            },
                                                            colors = SwitchDefaults.colors(checkedTrackColor = PulseCyan, checkedThumbColor = Color.White)
                                                        )
                                                    }
                                                }
                                                40 -> {
                                                    // PiP Mode Switch
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(
                                                            text = if (pipModeEnabled)
                                                                (if (isArabic) "نافذة المشغل العائم مفعلة" else "Floating PiP Player Enabled")
                                                            else
                                                                (if (isArabic) "النافذة العائمة معطلة" else "Floating PiP Player Disabled"),
                                                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 11.sp)
                                                        )
                                                        Switch(
                                                            checked = pipModeEnabled,
                                                            onCheckedChange = {
                                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                                pipModeEnabled = it
                                                            },
                                                            colors = SwitchDefaults.colors(checkedTrackColor = PulseCyan, checkedThumbColor = Color.White)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }

                    RoadmapSectionTab.SNAKE_ROAD -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 36.dp)
                        ) {
                            // Section 2: Winding Snake Prize Road (طريق الجائزة)
                            item(key = "milestones_timeline_header") {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0x3310B981),
                                    border = BorderStroke(1.dp, Color(0x5510B981).copy(alpha = ambientGlow)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        Brush.radialGradient(
                                                            listOf(Color(0xFFFFD700), Color(0xFFFF9800), Color.Transparent)
                                                        )
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.EmojiEvents,
                                                    contentDescription = null,
                                                    tint = Charcoal950,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = if (isArabic) "طريق الجائزة" else "The Prize Road",
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = Color.White,
                                                        fontSize = 16.sp
                                                    )
                                                )
                                                Text(
                                                    text = if (isArabic) "مسار المستويات المتموج • ارتقِ وافتح جوائزك" else "Winding serpentine path • Unlock prestige rewards",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = Color(0xFF94A3B8),
                                                        fontSize = 11.sp
                                                    )
                                                )
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color(0x3300F2FE),
                                            border = BorderStroke(1.dp, Color(0x6600F2FE))
                                        ) {
                                            Text(
                                                text = if (isArabic) "المستوى $streakDays" else "LVL $streakDays",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color(0xFF00F2FE),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.5.sp
                                                )
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                            }

                            itemsIndexed(milestones, key = { _, item -> "milestone_${item.day}" }) { index, milestone ->
                                val status = when {
                                    streakDays >= milestone.day -> MilestoneStatus.UNLOCKED
                                    streakDays < milestone.day && (index == 0 || streakDays >= milestones[index - 1].day) -> MilestoneStatus.CURRENT_TARGET
                                    else -> MilestoneStatus.LOCKED
                                }
                                val isLast = index == milestones.lastIndex

                                WindingSnakeMilestoneRow(
                                    index = index,
                                    milestone = milestone,
                                    status = status,
                                    isLast = isLast,
                                    currentStreak = streakDays,
                                    isArabic = isArabic,
                                    pulseFactor = ambientGlow
                                )
                            }
                        }
                    }
                }
            }
        }

        // Hidden Vault PIN Authentication Dialog (Level 10 Milestone)
        if (showVaultAuthDialog) {
            AlertDialog(
                onDismissRequest = { showVaultAuthDialog = false },
                containerColor = Color(0xF2121826),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, null, tint = PulseCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "رمز PIN لخزنة الوسائط السرية" else "Secret Vault PIN",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }
                },
                text = {
                    Column {
                        Text(
                            text = if (isArabic) "أدخل رمز PIN المكون من 4 أرقام لفتح المجلدات السرية (الافتراضي: 1234)" else "Enter your 4-digit PIN to access private files (Default: 1234)",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 12.sp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedTextField(
                            value = vaultPinInput,
                            onValueChange = {
                                if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                    vaultPinInput = it
                                    vaultPinError = null
                                }
                            },
                            singleLine = true,
                            placeholder = { Text("****") },
                            isError = vaultPinError != null,
                            supportingText = {
                                if (vaultPinError != null) {
                                    Text(vaultPinError!!, color = Color(0xFFFF5252))
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PulseCyan,
                                unfocusedBorderColor = Color(0x44FFFFFF),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (vaultPinInput == vaultSavedPin) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                isVaultUnlocked = true
                                showVaultAuthDialog = false
                            } else {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                vaultPinError = if (isArabic) "رمز PIN غير صحيح" else "Incorrect PIN"
                            }
                        }
                    ) {
                        Text(text = if (isArabic) "تأكيد 🔓" else "Unlock 🔓", color = PulseCyan, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showVaultAuthDialog = false }) {
                        Text(text = if (isArabic) "إلغاء" else "Cancel", color = Color(0xFF94A3B8))
                    }
                }
            )
        }
    }
}

@Composable
private fun ThemeChip(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) Color(0x3300F2FE) else Color(0x1F1E293B),
        border = BorderStroke(
            1.dp,
            if (isSelected) PulseCyan else Color(0x22FFFFFF)
        ),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) PulseCyan else Color(0xFF94A3B8),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) PulseCyan else Color.White,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
private fun WindingSnakeMilestoneRow(
    index: Int,
    milestone: MilestoneItem,
    status: MilestoneStatus,
    isLast: Boolean,
    currentStreak: Int,
    isArabic: Boolean,
    pulseFactor: Float
) {
    // Serpentine winding pattern: Left -> Center -> Right -> Center -> Left...
    val positionMod = index % 4
    val cardAlignment = when (positionMod) {
        0 -> Alignment.CenterStart
        1 -> Alignment.Center
        2 -> Alignment.CenterEnd
        else -> Alignment.Center
    }

    // Dynamic escalating luxury theme based on milestone day tier
    val tierGradient = when {
        milestone.day >= 60 -> listOf(Color(0xFFE0E7FF), Color(0xFF818CF8), Color(0xFFC084FC), Color(0xFF38BDF8))
        milestone.day >= 40 -> listOf(Color(0xFFFFD700), Color(0xFFFFA000), Color(0xFFFF5722))
        milestone.day >= 20 -> listOf(Color(0xFF7C3AED), Color(0xFFEC4899), Color(0xFF06B6D4))
        milestone.day >= 10 -> listOf(Color(0xFF00F2FE), Color(0xFF4FACFE), Color(0xFF10B981))
        else -> listOf(Color(0xFF10B981), Color(0xFF059669))
    }

    val primaryTierColor = tierGradient.first()
    val crownBadge = when {
        milestone.day >= 60 -> "💎 MYTHIC"
        milestone.day >= 40 -> "👑 ROYAL"
        milestone.day >= 20 -> "⚡ ELITE"
        milestone.day >= 10 -> "🔥 PRIME"
        else -> "🌱 LEVEL"
    }

    val iconVector = when (milestone.iconType) {
        "theme" -> Icons.Default.DarkMode
        "lock" -> Icons.Default.Lock
        "language" -> Icons.Default.Language
        "share" -> Icons.Default.Share
        "palette" -> Icons.Default.Palette
        "notification" -> Icons.Default.Notifications
        "background_play" -> Icons.Default.Headset
        "pip" -> Icons.Default.PictureInPicture
        else -> Icons.Default.Security
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Serpentine Connector Line from previous node
        if (index > 0) {
            val connectorBrush = if (status == MilestoneStatus.UNLOCKED) {
                Brush.verticalGradient(
                    listOf(
                        tierGradient.last().copy(alpha = 0.8f),
                        primaryTierColor.copy(alpha = 0.8f)
                    )
                )
            } else if (status == MilestoneStatus.CURRENT_TARGET) {
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF10B981).copy(alpha = 0.6f),
                        Color(0xFF00F2FE).copy(alpha = pulseFactor)
                    )
                )
            } else {
                Brush.verticalGradient(listOf(Color(0xFF1E293B), Color(0xFF334155)))
            }

            Box(
                modifier = Modifier
                    .width(if (status != MilestoneStatus.LOCKED) 4.dp else 2.5.dp)
                    .height(28.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(connectorBrush)
            )
        }

        // The Milestone Node on the winding track
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = cardAlignment
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .border(
                        width = when (status) {
                            MilestoneStatus.UNLOCKED -> 1.8.dp
                            MilestoneStatus.CURRENT_TARGET -> (2.2.dp * pulseFactor).coerceAtLeast(1.8.dp)
                            MilestoneStatus.LOCKED -> 1.dp
                        },
                        brush = when (status) {
                            MilestoneStatus.UNLOCKED -> Brush.linearGradient(tierGradient)
                            MilestoneStatus.CURRENT_TARGET -> Brush.linearGradient(
                                listOf(Color(0xFF00F2FE), Color(0xFFFFD700), Color(0xFF00FFCC))
                            )
                            MilestoneStatus.LOCKED -> Brush.linearGradient(
                                listOf(Color(0x33FFFFFF), Color(0x11FFFFFF))
                            )
                        },
                        shape = RoundedCornerShape(22.dp)
                    ),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (status) {
                        MilestoneStatus.UNLOCKED -> Color(0xD9101626)
                        MilestoneStatus.CURRENT_TARGET -> Color(0xF2131F33)
                        MilestoneStatus.LOCKED -> Color(0xCC0D121D)
                    }
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        when (status) {
                                            MilestoneStatus.UNLOCKED -> Brush.radialGradient(tierGradient)
                                            MilestoneStatus.CURRENT_TARGET -> Brush.radialGradient(
                                                listOf(Color(0xFF00F2FE), Color(0xFF0088FF))
                                            )
                                            MilestoneStatus.LOCKED -> Brush.radialGradient(
                                                listOf(Color(0x22334155), Color(0x111E293B))
                                            )
                                        }
                                    )
                                    .border(
                                        width = 1.2.dp,
                                        color = if (status != MilestoneStatus.LOCKED) Color.White.copy(alpha = 0.6f) else Color(0x33FFFFFF),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (status == MilestoneStatus.LOCKED) Icons.Default.Lock else iconVector,
                                    contentDescription = null,
                                    tint = when (status) {
                                        MilestoneStatus.UNLOCKED -> if (milestone.day >= 40) Charcoal950 else Color.White
                                        MilestoneStatus.CURRENT_TARGET -> Charcoal950
                                        MilestoneStatus.LOCKED -> Color(0xFF64748B)
                                    },
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isArabic) "المحطة ${milestone.day}" else "Day ${milestone.day}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = when (status) {
                                                MilestoneStatus.UNLOCKED -> primaryTierColor
                                                MilestoneStatus.CURRENT_TARGET -> Color(0xFF00F2FE)
                                                MilestoneStatus.LOCKED -> Color(0xFF94A3B8)
                                            },
                                            fontSize = 16.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = primaryTierColor.copy(alpha = 0.2f),
                                        border = BorderStroke(0.8.dp, primaryTierColor.copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            text = crownBadge,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = primaryTierColor,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        )
                                    }
                                }

                                Text(
                                    text = if (isArabic) milestone.titleArabic else milestone.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (status == MilestoneStatus.LOCKED) Color(0xFF94A3B8) else Color.White,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (status) {
                                MilestoneStatus.UNLOCKED -> Color(0x3310B981)
                                MilestoneStatus.CURRENT_TARGET -> Color(0x3300F2FE)
                                MilestoneStatus.LOCKED -> Color(0x22334155)
                            },
                            border = BorderStroke(
                                1.dp,
                                when (status) {
                                    MilestoneStatus.UNLOCKED -> Color(0xFF10B981).copy(alpha = 0.6f)
                                    MilestoneStatus.CURRENT_TARGET -> Color(0xFF00F2FE).copy(alpha = pulseFactor)
                                    MilestoneStatus.LOCKED -> Color(0x3364748B)
                                }
                            )
                        ) {
                            Text(
                                text = when (status) {
                                    MilestoneStatus.UNLOCKED -> if (isArabic) "مفتوحة ✓" else "UNLOCKED ✓"
                                    MilestoneStatus.CURRENT_TARGET -> if (isArabic) "هدفك الحالي 🔥" else "CURRENT 🔥"
                                    MilestoneStatus.LOCKED -> if (isArabic) "مقفلة 🔒" else "LOCKED 🔒"
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = when (status) {
                                        MilestoneStatus.UNLOCKED -> Color(0xFF34D399)
                                        MilestoneStatus.CURRENT_TARGET -> Color(0xFF00F2FE)
                                        MilestoneStatus.LOCKED -> Color(0xFF64748B)
                                    }
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isArabic) milestone.descriptionArabic else milestone.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (status == MilestoneStatus.LOCKED) Color(0xFF64748B) else Color(0xFFCBD5E1),
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    )

                    if (status == MilestoneStatus.CURRENT_TARGET) {
                        val daysLeft = (milestone.day - currentStreak).coerceAtLeast(1)
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0x2600F2FE),
                            border = BorderStroke(1.dp, Color(0x5500F2FE))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isArabic) "⚡ يتبقى $daysLeft أيام فقط في السلسلة لفتح هذه الجائزة!" else "⚡ Only $daysLeft days in streak to unlock this reward!",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF00F2FE),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp
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
