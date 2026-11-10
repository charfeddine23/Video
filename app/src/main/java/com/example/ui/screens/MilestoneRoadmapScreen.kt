package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.Charcoal950
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PulseCyan
import com.example.ui.theme.PulseViolet
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

@Composable
fun MilestoneRoadmapScreen(
    streakViewModel: StreakViewModel,
    streakDays: Int,
    shieldsCount: Int,
    onBack: () -> Unit,
    isArabic: Boolean = false,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    BackHandler {
        onBack()
    }

    val milestones = streakViewModel.milestones

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
        ) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onBack()
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0x22FFFFFF))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
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
                                fontSize = 17.sp
                            )
                        )
                        Text(
                            text = if (isArabic) "خزينة الدروع الكاملة وسجل المحطات المكتسبة" else "Full Shields Vault & Milestone Unlocks",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Total Shields Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0x33121826),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PulseCyan.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
            ) {
                // Section 1: Types of Shields & Quantities (انواع الدروع والكمية تاع كل درع)
                item(key = "shields_vault_header") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 10.dp)
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
                            .padding(vertical = 6.dp)
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
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(shield.bgGlow),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = shield.icon,
                                        contentDescription = null,
                                        tint = shield.iconColor,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    Text(
                                        text = if (isArabic) shield.nameAr else shield.nameEn,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 13.5.sp
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

                            Spacer(modifier = Modifier.width(12.dp))

                            // Quantity Badge
                            Column(horizontalAlignment = Alignment.End) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = shield.bgGlow,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, shield.iconColor.copy(alpha = 0.7f))
                                ) {
                                    Text(
                                        text = "× ${shield.count}",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = shield.iconColor,
                                            fontSize = 15.sp
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

                // Section 2: Winding Snake Prize Road (طريق الجائزة)
                item(key = "milestones_timeline_header") {
                    Spacer(modifier = Modifier.height(20.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0x3310B981),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x5510B981).copy(alpha = ambientGlow)),
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
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x6600F2FE))
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
        milestone.day >= 60 -> listOf(Color(0xFFE0E7FF), Color(0xFF818CF8), Color(0xFFC084FC), Color(0xFF38BDF8)) // Mythic Celestial Diamond
        milestone.day >= 40 -> listOf(Color(0xFFFFD700), Color(0xFFFFA000), Color(0xFFFF5722)) // Sovereign Imperial Gold
        milestone.day >= 20 -> listOf(Color(0xFF7C3AED), Color(0xFFEC4899), Color(0xFF06B6D4)) // Royal Cosmic Purple & Cyan
        milestone.day >= 10 -> listOf(Color(0xFF00F2FE), Color(0xFF4FACFE), Color(0xFF10B981)) // Radiant Cyber Glow
        else -> listOf(Color(0xFF10B981), Color(0xFF059669)) // Emerald Bronze
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
        // Serpentine Connector Line from previous node with curving snake vibe
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

            // Curved connector line mimicking a snake trail
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
                    // Header Row: Day number, Winding Position Chip & Unlock Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Glowing Node Avatar
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
                                    // Tier Badge
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = primaryTierColor.copy(alpha = 0.2f),
                                        border = androidx.compose.foundation.BorderStroke(0.8.dp, primaryTierColor.copy(alpha = 0.5f))
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

                        // Status Tag
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (status) {
                                MilestoneStatus.UNLOCKED -> Color(0x3310B981)
                                MilestoneStatus.CURRENT_TARGET -> Color(0x3300F2FE)
                                MilestoneStatus.LOCKED -> Color(0x22334155)
                            },
                            border = androidx.compose.foundation.BorderStroke(
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

                    // Target Countdown
                    if (status == MilestoneStatus.CURRENT_TARGET) {
                        val daysLeft = (milestone.day - currentStreak).coerceAtLeast(1)
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0x2600F2FE),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x5500F2FE))
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
