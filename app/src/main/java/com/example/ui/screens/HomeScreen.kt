package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaItem
import com.example.ui.components.FlameParticleComponent
import com.example.ui.components.LocalVideosSheet
import com.example.ui.components.VideoPulseHeader
import com.example.ui.theme.Charcoal950
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PulseAmber
import com.example.ui.theme.PulseCyan
import com.example.ui.theme.PulseViolet
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MilestoneStatus
import com.example.ui.viewmodel.StreakViewModel

@Composable
fun HomeScreen(
    streakViewModel: StreakViewModel,
    onMediaClick: (MediaItem) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenNotifications: () -> Unit,
    onNavigateToRoadmap: () -> Unit,
    onOpenVideosScreen: () -> Unit,
    isArabic: Boolean = false,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val uiState by streakViewModel.uiState.collectAsState()

    var isLocalVideosSheetOpen by remember { mutableStateOf(false) }

    // Liquid Glass Deep Dark Theme
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
            .testTag("home_screen_gamified")
    ) {
        // Subtle ambient liquid neon lighting
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    // Top ambient orange/pink glow for flame
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(
                                Color(0x28FF5722),
                                Color(0x18FF2D73),
                                Color.Transparent
                            ),
                            radius = size.width * 0.9f
                        ),
                        center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.28f)
                    )
                    // Bottom ambient cyan glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(
                                Color(0x1800F2FE),
                                Color.Transparent
                            ),
                            radius = size.width * 0.7f
                        ),
                        center = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.8f)
                    )
                }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Header: Settings (left) and Search (right)
            item(key = "header") {
                VideoPulseHeader(
                    onSettingsClick = onOpenSettings,
                    onSearchClick = onOpenSearch,
                    onNotificationsClick = onOpenNotifications,
                    isArabic = isArabic
                )
            }

            // Streak Feedback Toast Banner if any
            if (uiState.feedbackMessage != null) {
                item(key = "streak_feedback") {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xE61E293B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00F2FE)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clickable {
                                streakViewModel.clearFeedback()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.feedbackMessage ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Central Glowing Animated Flame Component
            item(key = "animated_flame_center") {
                Spacer(modifier = Modifier.height(8.dp))
                FlameParticleComponent(
                    streakDays = uiState.streakDays,
                    onLongPressToRoadmap = {
                        onNavigateToRoadmap()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Streak & Shields Info Cards
            item(key = "streak_shields_cards") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Row with Streak Card & Shield Inventory Card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Card 1: Streak Count Card
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    width = 1.dp,
                                    brush = Brush.verticalGradient(
                                        listOf(Color(0x80FF5722), Color(0x22FF5722))
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xB3121826))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(Color(0x33FF5722)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocalFireDepartment,
                                            contentDescription = null,
                                            tint = Color(0xFFFF5722),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0x33FF5722)
                                    ) {
                                        Text(
                                            text = if (isArabic) "نشطة" else "ACTIVE",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color(0xFFFF9800),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = if (isArabic) "${uiState.streakDays} يوماً" else "${uiState.streakDays} Days",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                )

                                Text(
                                    text = if (isArabic) "سلسلة المشاهدة الحالية" else "Current Active Streak",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // Card 2: Shield Inventory Card (Max 30)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    width = 1.dp,
                                    brush = Brush.verticalGradient(
                                        listOf(Color(0x8000F2FE), Color(0x2200F2FE))
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xB3121826))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
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

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0x3300F2FE)
                                    ) {
                                        Text(
                                            text = "${uiState.shieldsCount}/${uiState.maxShields}",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = PulseCyan,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "🛡️ ${uiState.shieldsCount}",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                )

                                Text(
                                    text = if (isArabic) "دروع الحماية المتوفرة" else "Shields Inventory",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }

                    // Card 3: Daily Watch Progress Card (5-Minute Goal with Circular Progress Bar)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                brush = Brush.horizontalGradient(
                                    listOf(Color(0x66FF5722), Color(0x6600F2FE))
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xCC121826))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Circular Progress Indicator for 5-minute genuine watch goal
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(68.dp)
                            ) {
                                CircularProgressIndicator(
                                    progress = { 1f },
                                    modifier = Modifier.fillMaxSize(),
                                    color = Color(0x22FFFFFF),
                                    strokeWidth = 6.dp,
                                    strokeCap = StrokeCap.Round
                                )
                                CircularProgressIndicator(
                                    progress = { uiState.dailyProgress },
                                    modifier = Modifier.fillMaxSize(),
                                    color = if (uiState.isGoalCompletedToday) Color(0xFF10B981) else Color(0xFFFF9800),
                                    strokeWidth = 6.dp,
                                    strokeCap = StrokeCap.Round
                                )
                                if (uiState.isGoalCompletedToday) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Completed",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(28.dp)
                                    )
                                } else {
                                    Text(
                                        text = "${(uiState.dailyProgress * 100).toInt()}%",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.sp
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (isArabic) "هدف المشاهدة اليومي" else "Daily Watch Goal",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (uiState.isGoalCompletedToday) Color(0x3310B981) else Color(0x33FF9800)
                                    ) {
                                        Text(
                                            text = if (uiState.isGoalCompletedToday)
                                                (if (isArabic) "مكتمل اليوم ✓" else "COMPLETED ✓")
                                            else
                                                (if (isArabic) "قيد التقدم" else "IN PROGRESS"),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (uiState.isGoalCompletedToday) Color(0xFF34D399) else Color(0xFFFFB74D),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                val curMin = uiState.dailyWatchSeconds / 60
                                val curSec = uiState.dailyWatchSeconds % 60
                                Text(
                                    text = String.format("%02d:%02d / 05:00", curMin, curSec),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = PulseCyan,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )

                                Text(
                                    text = if (isArabic)
                                        "يتطلب 5 دقائق من المشاهدة المحلية الحقيقية (التخطي والتقديم السريع لا يُحتسب)"
                                    else
                                        "Requires 5 minutes of actual local playback (seeking disabled during tracking)",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF94A3B8),
                                        fontSize = 10.5.sp,
                                        lineHeight = 14.sp
                                    )
                                )
                            }
                        }
                    }

                    // Milestone Roadmap Teaser Card
                    val nextMilestone = streakViewModel.milestones.firstOrNull { it.day > uiState.streakDays }
                    if (nextMilestone != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onNavigateToRoadmap()
                                },
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0x801A2234),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4000F2FE))
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
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0x3300F2FE)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Map,
                                            contentDescription = null,
                                            tint = PulseCyan,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = if (isArabic) "الهدف القادم: اليوم ${nextMilestone.day}" else "Next Target: Day ${nextMilestone.day}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = PulseCyan,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        )
                                        Text(
                                            text = if (isArabic) nextMilestone.titleArabic else nextMilestone.title,
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Open Roadmap",
                                    tint = PulseCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Interactive Action Controls / Testing Affordances
                    Text(
                        text = if (isArabic) "إجراءات واختبارات القواعد" else "Quick Actions & Gamification Testing",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    // Action 1: Open Device Videos to watch and earn genuine progress
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onOpenVideosScreen()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VideoLibrary, null, tint = PulseCyan, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isArabic) "تشغيل فيديو محلي من ذاكرة الهاتف" else "Play Local Video from Device",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = if (isArabic) "احصل على تقدم حقيقي نحو هدف الـ 5 دقائق" else "Advances 5-minute genuine watch goal",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF94A3B8),
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                            Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }

                    // Action 2 & 3: Simulate Missed Day & Refill Shield
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Simulate Missed Day (Strict reset penalty test)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x22EF4444),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x66EF4444)),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    streakViewModel.simulateMissedDay()
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.Warning, null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isArabic) "محاكاة يوم ضائع" else "Simulate Missed Day",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFFEF4444),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp
                                    )
                                )
                            }
                        }

                        // Refill Shield (+1 🛡️)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x2200F2FE),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x6600F2FE)),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    streakViewModel.addShield()
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.Add, null, tint = PulseCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isArabic) "إضافة درع (+1 🛡️)" else "Add Shield (+1 🛡️)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = PulseCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp
                                    )
                                )
                            }
                        }
                    }

                    // Fast 1-minute genuine test advancement
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x1A10B981),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x3310B981)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                streakViewModel.tickGenuineWatchTime(60, isSeeking = false)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.HourglassTop, null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isArabic) "محاكاة مشاهدة (+1 دقيقة)" else "Simulate Genuine Watch (+1 min)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
