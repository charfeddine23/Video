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

                // Section 2: Milestone Timeline Unlocks
                item(key = "milestones_timeline_header") {
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "الإنجازات والمحطات المفتوحة بالسلسلة" else "Milestone Achievements & Unlocks",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                        )
                    }
                }

                itemsIndexed(milestones, key = { _, item -> "milestone_${item.day}" }) { index, milestone ->
                    val status = when {
                        streakDays >= milestone.day -> MilestoneStatus.UNLOCKED
                        streakDays < milestone.day && (index == 0 || streakDays >= milestones[index - 1].day) -> MilestoneStatus.CURRENT_TARGET
                        else -> MilestoneStatus.LOCKED
                    }
                    val isLast = index == milestones.lastIndex

                    TimelineItemRow(
                        milestone = milestone,
                        status = status,
                        isLast = isLast,
                        currentStreak = streakDays,
                        isArabic = isArabic
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineItemRow(
    milestone: MilestoneItem,
    status: MilestoneStatus,
    isLast: Boolean,
    currentStreak: Int,
    isArabic: Boolean
) {
    val nodeColor = when (status) {
        MilestoneStatus.UNLOCKED -> Color(0xFF10B981) // Green
        MilestoneStatus.CURRENT_TARGET -> Color(0xFF00F2FE) // Bright Cyan
        MilestoneStatus.LOCKED -> Color(0xFF475569) // Muted Slate
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Left Column: Connected Timeline Node and Line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(42.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        when (status) {
                            MilestoneStatus.UNLOCKED -> Color(0x3310B981)
                            MilestoneStatus.CURRENT_TARGET -> Color(0x3300F2FE)
                            MilestoneStatus.LOCKED -> Color(0x221E293B)
                        }
                    )
                    .border(
                        width = if (status == MilestoneStatus.CURRENT_TARGET) 2.dp else 1.5.dp,
                        color = nodeColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (status == MilestoneStatus.UNLOCKED) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Unlocked",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(18.dp)
                    )
                } else if (status == MilestoneStatus.CURRENT_TARGET) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00F2FE))
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .padding(vertical = 4.dp)
                        .background(
                            if (status == MilestoneStatus.UNLOCKED) Color(0xFF10B981).copy(alpha = 0.5f)
                            else Color(0xFF334155)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Right Column: Checkpoint Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .border(
                    width = 1.dp,
                    color = when (status) {
                        MilestoneStatus.UNLOCKED -> Color(0x4010B981)
                        MilestoneStatus.CURRENT_TARGET -> Color(0x8000F2FE)
                        MilestoneStatus.LOCKED -> Color(0x1AFFFFFF)
                    },
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (status) {
                    MilestoneStatus.UNLOCKED -> Color(0x2A121826)
                    MilestoneStatus.CURRENT_TARGET -> Color(0x40162030)
                    MilestoneStatus.LOCKED -> Color(0x180F172A)
                }
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when (status) {
                                        MilestoneStatus.UNLOCKED -> Color(0x2610B981)
                                        MilestoneStatus.CURRENT_TARGET -> Color(0x2600F2FE)
                                        MilestoneStatus.LOCKED -> Color(0x1A475569)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = null,
                                tint = when (status) {
                                    MilestoneStatus.UNLOCKED -> Color(0xFF10B981)
                                    MilestoneStatus.CURRENT_TARGET -> Color(0xFF00F2FE)
                                    MilestoneStatus.LOCKED -> Color(0xFF64748B)
                                },
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = if (isArabic) "اليوم ${milestone.day}" else "Day ${milestone.day}",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = when (status) {
                                    MilestoneStatus.UNLOCKED -> Color(0xFFFF9800)
                                    MilestoneStatus.CURRENT_TARGET -> Color(0xFF00F2FE)
                                    MilestoneStatus.LOCKED -> Color(0xFF94A3B8)
                                }
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (status) {
                            MilestoneStatus.UNLOCKED -> Color(0x3310B981)
                            MilestoneStatus.CURRENT_TARGET -> Color(0x3300F2FE)
                            MilestoneStatus.LOCKED -> Color(0x22334155)
                        }
                    ) {
                        Text(
                            text = when (status) {
                                MilestoneStatus.UNLOCKED -> if (isArabic) "مفتوحة ومفعلة ✓" else "UNLOCKED ✓"
                                MilestoneStatus.CURRENT_TARGET -> if (isArabic) "الهدف القادم 🔥" else "CURRENT TARGET 🔥"
                                MilestoneStatus.LOCKED -> if (isArabic) "مقفلة 🔒" else "LOCKED 🔒"
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
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

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isArabic) milestone.titleArabic else milestone.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (status == MilestoneStatus.LOCKED) Color(0xFF94A3B8) else Color.White,
                        fontSize = 15.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isArabic) milestone.descriptionArabic else milestone.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (status == MilestoneStatus.LOCKED) Color(0xFF64748B) else Color(0xFFCBD5E1),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                )

                if (status == MilestoneStatus.CURRENT_TARGET) {
                    val daysLeft = (milestone.day - currentStreak).coerceAtLeast(1)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isArabic) "يتبقى $daysLeft أيام من المشاهدة المستمرة لفتح هذه الميزة" else "Only $daysLeft days of genuine watching left to unlock!",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF00F2FE),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}
