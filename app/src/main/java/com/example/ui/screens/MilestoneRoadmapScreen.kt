package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
            .testTag("milestone_roadmap_screen")
    ) {
        // Subtle ambient liquid background glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(
                                Color(0x22FF5722),
                                Color(0x1500F2FE),
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
                            text = if (isArabic) "خارطة طريق الميزات والمكافآت" else "Milestone Feature Roadmap",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 17.sp
                            )
                        )
                        Text(
                            text = if (isArabic) "تقدم تدريجي من اليوم 5 إلى اليوم 40" else "Progressive Rewards: Day 5 to Day 40",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Streak & Shields Quick Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0x33121826),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FF5722))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = Color(0xFFFF5722),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${streakDays}d",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🛡️ $shieldsCount",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = PulseCyan,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Road Description Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0x40121826),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x22FFFFFF))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0x3300F2FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = PulseCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isArabic)
                            "حافظ على سلسلة مشاهدتك لمدة 5 دقائق يومياً لفتح ميزات إضافية. كل يوم ضائع يستهلك درعاً واحداً."
                        else
                            "Watch 5 min daily to unlock elite player features. Shields automatically protect your streak if a day is missed.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )
                    )
                }
            }

            // Vertical Timeline List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 48.dp)
            ) {
                itemsIndexed(milestones) { index, milestone ->
                    val status = streakViewModel.getMilestoneStatus(milestone.day, streakDays)
                    val isLast = index == milestones.lastIndex

                    TimelineMilestoneRow(
                        milestone = milestone,
                        status = status,
                        currentStreak = streakDays,
                        isLast = isLast,
                        isArabic = isArabic
                    )
                }
            }
        }
    }
}

@Composable
fun TimelineMilestoneRow(
    milestone: MilestoneItem,
    status: MilestoneStatus,
    currentStreak: Int,
    isLast: Boolean,
    isArabic: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_target")
    val targetPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "target_pulse"
    )

    val nodeColor = when (status) {
        MilestoneStatus.UNLOCKED -> Color(0xFF10B981) // Emerald Green
        MilestoneStatus.CURRENT_TARGET -> Color(0xFF00F2FE) // Bright Pulse Cyan
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
            // Timeline Node Circle
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
                        color = if (status == MilestoneStatus.CURRENT_TARGET) nodeColor.copy(alpha = targetPulseAlpha) else nodeColor,
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

            // Connecting Vertical Line
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

                    // Status Badge
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
                    val daysLeft = milestone.day - currentStreak
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
