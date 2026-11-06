package com.example.ui.screens

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AutoAwesome
import com.example.ui.components.AuraPulseBrandHeader
import com.example.ui.components.FlameParticleComponent
import com.example.ui.theme.PulseCyan
import com.example.ui.theme.PulseViolet
import com.example.ui.viewmodel.StreakViewModel

@Composable
fun HomeScreen(
    streakViewModel: StreakViewModel,
    onNavigateToRoadmap: () -> Unit,
    isArabic: Boolean = false,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val uiState by streakViewModel.uiState.collectAsState()

    // Pure minimalist dark liquid glass aesthetic (#0B0F19 / #121826)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
            .testTag("home_screen_minimal")
    ) {
        // Ambient liquid neon glow behind the central flame
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    // Central warm flame glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(
                                Color(0x30FF5722),
                                Color(0x18FF2D73),
                                Color.Transparent
                            ),
                            radius = size.width * 0.95f
                        ),
                        center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.35f)
                    )
                    // Lower cyan ambient glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(
                                Color(0x1500F2FE),
                                Color.Transparent
                            ),
                            radius = size.width * 0.8f
                        ),
                        center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.85f)
                    )
                }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Requirement 1, 2, 3: Official App Branding "Aura Pulse" with Dynamic Liquid Glow (Streak > 0)
            // or Streak Freeze & Falling Snow Effect (Streak == 0)
            item(key = "aura_pulse_brand_header") {
                AuraPulseBrandHeader(
                    streakDays = uiState.streakDays,
                    isArabic = isArabic,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Central Glowing Animated Flame Component
            // Triggers Milestone Roadmap Screen on 1-second Long Press
            item(key = "flame_center") {
                FlameParticleComponent(
                    streakDays = uiState.streakDays,
                    onLongPressToRoadmap = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onNavigateToRoadmap()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Streak Status & Shield Status Card
            item(key = "streak_status_card") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .border(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(
                                listOf(Color(0x80FF5722), Color(0x33FF9800), Color(0x6600F2FE))
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xD9121826))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Streak info
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x26FF5722)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = Color(0xFFFF5722),
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = if (isArabic) "${uiState.streakDays} يوماً متواصلاً" else "${uiState.streakDays} Days Streak",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = if (isArabic) "سلسلة المشاهدة النشطة" else "Current Active Streak",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }

                        // Shield Inventory Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x2600F2FE),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x5500F2FE))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = PulseCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${uiState.shieldsCount}/${uiState.maxShields} 🛡️",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = PulseCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Daily 5-Minute Watch Progress Card (Circular Progress Bar)
            item(key = "daily_watch_progress_card") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .border(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(
                                listOf(Color(0x5500F2FE), Color(0x3310B981))
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xD9121826))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Circular Progress Indicator
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(76.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { 1f },
                                modifier = Modifier.fillMaxSize(),
                                color = Color(0x1FFFFFFF),
                                strokeWidth = 7.dp,
                                strokeCap = StrokeCap.Round
                            )
                            CircularProgressIndicator(
                                progress = { uiState.dailyProgress },
                                modifier = Modifier.fillMaxSize(),
                                color = if (uiState.isGoalCompletedToday) Color(0xFF10B981) else Color(0xFFFF9800),
                                strokeWidth = 7.dp,
                                strokeCap = StrokeCap.Round
                            )
                            if (uiState.isGoalCompletedToday) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Goal Completed",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(32.dp)
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${(uiState.dailyProgress * 100).toInt()}%",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(18.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (isArabic) "هدف الـ 5 دقائق اليومي" else "Daily 5-Min Goal",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (uiState.isGoalCompletedToday) Color(0x3310B981) else Color(0x2E00F2FE)
                                ) {
                                    Text(
                                        text = if (uiState.isGoalCompletedToday)
                                            (if (isArabic) "مكتمل ✓" else "DONE ✓")
                                        else
                                            (if (isArabic) "قيد التشغيل" else "ACTIVE"),
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (uiState.isGoalCompletedToday) Color(0xFF34D399) else PulseCyan,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.5.sp
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
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = if (isArabic)
                                    "تشغيل محلي فعلي (التخطي لا يُحتسب لضمان المشاهدة)"
                                else
                                    "Actual playback required (seeking disabled during tracking)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.5.sp,
                                    lineHeight = 14.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Quick Roadmap Shortcut Banner
            item(key = "roadmap_shortcut") {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onNavigateToRoadmap()
                        },
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0x59162030),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x3300F2FE))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x2600F2FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Map,
                                    contentDescription = null,
                                    tint = PulseCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = if (isArabic) "خارطة طريق المكافآت والمحطات" else "Milestone Feature Roadmap",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = if (isArabic) "اضغط مطولاً (1ث) على الشعلة أو انقر هنا" else "Hold flame 1s or tap here to view full path",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open Roadmap",
                            tint = PulseCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

        }
    }
}
