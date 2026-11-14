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
import com.example.ui.theme.stableStatusBarsPadding
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
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.ui.theme.aura
import com.example.ui.viewmodel.StreakViewModel

@Composable
fun HomeScreen(
    streakViewModel: StreakViewModel,
    onNavigateToRoadmap: () -> Unit,
    onNavigateToVault: () -> Unit = {},
    isArabic: Boolean = false,
    modifier: Modifier = Modifier
) {
    val aura = MaterialTheme.aura
    val haptic = LocalHapticFeedback.current
    val uiState by streakViewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(aura.background)
            .testTag("home_screen_minimal")
    ) {
        // Ambient liquid neon glow behind the central flame and extending cleanly to top edge
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0x28FF5722),
                                Color(0x1400F2FE),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = size.height * 0.24f
                        )
                    )
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
                .stableStatusBarsPadding(),
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
            // Triggers Vault Screen on 1-second Long Press
            item(key = "flame_center") {
                FlameParticleComponent(
                    streakDays = uiState.streakDays,
                    onLongPressToRoadmap = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onNavigateToVault()
                    },
                    isArabic = isArabic,
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
                                listOf(aura.accentFlame.copy(alpha = 0.55f), Color(0x44FF9800), aura.accentCyan.copy(alpha = 0.45f))
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = aura.surfaceCard)
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
                                val streakDaysCount = uiState.streakDays
                                val formattedStreakDays = when {
                                    streakDaysCount == 0 -> "0 يوم"
                                    streakDaysCount == 1 -> "يوم واحد"
                                    streakDaysCount == 2 -> "يومان"
                                    streakDaysCount in 3..10 -> "$streakDaysCount أيام"
                                    else -> "$streakDaysCount يوماً"
                                }
                                Text(
                                    text = if (isArabic) formattedStreakDays else "$streakDaysCount Days Streak",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = aura.textPrimary
                                    )
                                )
                                Text(
                                    text = if (isArabic) "سلسلة الحماسة" else "Current Active Streak",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = aura.textSecondary,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }

                        // Shield Inventory Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = aura.accentCyanSoft,
                            border = androidx.compose.foundation.BorderStroke(1.dp, aura.accentCyan.copy(alpha = 0.45f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = aura.accentCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${uiState.shieldsCount}/${uiState.maxShields} 🛡️",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = aura.accentCyan,
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

            // Daily 5-Minute Watch Progress Card (Compact & Harmonious Design)
            item(key = "daily_watch_progress_card") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .border(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(
                                listOf(
                                    Color(0x3300F2FE),
                                    Color(0x40FF9800),
                                    Color(0x2210B981)
                                )
                            ),
                            shape = RoundedCornerShape(18.dp)
                        ),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = aura.surfaceCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
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
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    CircularProgressIndicator(
                                        progress = { 1f },
                                        modifier = Modifier.fillMaxSize(),
                                        color = aura.progressTrack,
                                        strokeWidth = 3.dp,
                                        strokeCap = StrokeCap.Round
                                    )
                                    CircularProgressIndicator(
                                        progress = { uiState.dailyProgress },
                                        modifier = Modifier.fillMaxSize(),
                                        color = if (uiState.isGoalCompletedToday) aura.accentEmerald else Color(0xFFFF9800),
                                        strokeWidth = 3.dp,
                                        strokeCap = StrokeCap.Round
                                    )
                                    Icon(
                                        imageVector = if (uiState.isGoalCompletedToday) Icons.Default.CheckCircle else Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = if (uiState.isGoalCompletedToday) aura.accentEmerald else Color(0xFFFF9800),
                                        modifier = Modifier.size(17.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = if (isArabic) "شاهد 5 دقائق يوميا لتغذية الشعلة" else "Watch 5 mins daily to feed flame",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = aura.textPrimary,
                                            fontSize = 12.5.sp
                                        )
                                    )
                                    val curMin = uiState.dailyWatchSeconds / 60
                                    val curSec = uiState.dailyWatchSeconds % 60
                                    Text(
                                        text = String.format("%02d:%02d / 05:00", curMin, curSec),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = aura.accentCyan,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            // Compact status badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (uiState.isGoalCompletedToday) aura.accentEmeraldSoft else aura.accentCyanSoft,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (uiState.isGoalCompletedToday) aura.accentEmerald.copy(alpha = 0.45f) else aura.accentCyan.copy(alpha = 0.35f)
                                )
                            ) {
                                Text(
                                    text = if (uiState.isGoalCompletedToday) {
                                        if (isArabic) "مكتمل ✓" else "DONE ✓"
                                    } else {
                                        "${(uiState.dailyProgress * 100).toInt()}%"
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (uiState.isGoalCompletedToday) aura.accentEmerald else aura.accentCyan,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Thin subtle progress line
                        LinearProgressIndicator(
                            progress = { uiState.dailyProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = if (uiState.isGoalCompletedToday) aura.accentEmerald else Color(0xFFFF9800),
                            trackColor = aura.progressTrack
                        )
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
                    color = aura.surfaceCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, aura.accentCyan.copy(alpha = 0.35f))
                ) {
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
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(aura.accentCyanSoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = aura.accentCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = if (isArabic) "الإنجازات, الدروع والمكافآت" else "Achievements, Shields & Rewards",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = aura.textPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = if (isArabic) "عرض أنواع الدروع وكمياتها وسجل المكافآت المفتوحة" else "View all shield types, counts & unlocked perks",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = aura.textSecondary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open Roadmap",
                            tint = aura.accentCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

        }
    }
}
