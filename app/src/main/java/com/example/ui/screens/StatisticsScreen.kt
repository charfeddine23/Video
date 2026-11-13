package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.ui.theme.PulseCyan
import com.example.ui.theme.PulseViolet
import com.example.ui.viewmodel.StreakViewModel

@Composable
fun StatisticsScreen(
    streakViewModel: StreakViewModel,
    onBack: () -> Unit,
    isArabic: Boolean = true,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val uiState by streakViewModel.uiState.collectAsState()

    BackHandler {
        onBack()
    }

    val totalWatchHours = streakViewModel.getTotalWatchHours()
    val totalTracks = streakViewModel.getTotalTracksListened()
    val dailyWatchMins = uiState.dailyWatchSeconds / 60
    val dailyGoalMins = uiState.dailyGoalSeconds / 60

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
            .testTag("statistics_screen")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color(0x2400F2FE), Color(0x187928CA), Color.Transparent),
                            radius = size.width * 0.95f
                        ),
                        center = Offset(size.width * 0.5f, size.height * 0.16f)
                    )
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .stableStatusBarsPadding()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            // Top Bar
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
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0x22FFFFFF))
                            .testTag("statistics_back_button")
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
                            text = if (isArabic) "الإحصائيات" else "Statistics",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = if (isArabic) "تحليل نشاط المشاهدة والاستماع المحلي" else "Offline watch & audio activity insights",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0x2200F2FE),
                    border = BorderStroke(1.dp, PulseCyan.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.BarChart, null, tint = PulseCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "OFFLINE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = PulseCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 36.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 2x2 Hero Metrics Grid
                item(key = "stats_grid") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatMetricCard(
                                title = if (isArabic) "ساعات المشاهدة" else "Total Watch Time",
                                value = "${String.format("%.1f", totalWatchHours)} " + (if (isArabic) "س" else "hrs"),
                                icon = Icons.Default.PlayCircle,
                                accentColor = PulseCyan,
                                modifier = Modifier.weight(1f)
                            )
                            StatMetricCard(
                                title = if (isArabic) "مقاطع صوتية" else "Tracks Listened",
                                value = "$totalTracks",
                                icon = Icons.Default.Headset,
                                accentColor = PulseViolet,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatMetricCard(
                                title = if (isArabic) "أيام السلسلة" else "Active Streak",
                                value = "${uiState.streakDays} " + (if (isArabic) "يوم" else "days"),
                                icon = Icons.Default.LocalFireDepartment,
                                accentColor = Color(0xFFFF5722),
                                modifier = Modifier.weight(1f)
                            )
                            StatMetricCard(
                                title = if (isArabic) "دروع الحماية" else "Shields Owned",
                                value = "${uiState.shieldsCount}/${uiState.maxShields}",
                                icon = Icons.Default.Shield,
                                accentColor = Color(0xFF10B981),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Today's Flame Goal Progress Card
                item(key = "stats_daily_goal") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0x33FF5722), RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xD9121826))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isArabic) "تقدم هدف الشعلة اليومي" else "Daily Watch Flame Progress",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = if (uiState.isGoalCompletedToday) {
                                        if (isArabic) "مكتمل 🔥" else "Completed 🔥"
                                    } else {
                                        "$dailyWatchMins / $dailyGoalMins " + (if (isArabic) "د" else "min")
                                    },
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (uiState.isGoalCompletedToday) Color(0xFF10B981) else Color(0xFFFF8A65),
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            LinearProgressIndicator(
                                progress = { uiState.dailyProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = if (uiState.isGoalCompletedToday) Color(0xFF10B981) else Color(0xFFFF5722),
                                trackColor = Color(0x22FFFFFF)
                            )
                        }
                    }
                }

                // Top Media Folders Breakdown Card
                item(key = "stats_top_folders") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                Brush.horizontalGradient(listOf(Color(0x4400F2FE), Color(0x447928CA))),
                                RoundedCornerShape(22.dp)
                            ),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xE0101626))
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Folder, null, tint = PulseCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isArabic) "أكثر المجلدات نشاطاً" else "Top Media Folders",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                            }

                            FolderStatBar("Movies", 0.62f, "62%", PulseCyan)
                            FolderStatBar("Downloads", 0.24f, "24%", Color(0xFF7C3AED))
                            FolderStatBar("Series", 0.14f, "14%", Color(0xFFFF9100))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xD9121826))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = accentColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
private fun FolderStatBar(
    folderName: String,
    ratio: Float,
    percentText: String,
    barColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = folderName,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            )
            Text(
                text = percentText,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = barColor,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        LinearProgressIndicator(
            progress = { ratio },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = barColor,
            trackColor = Color(0x22FFFFFF)
        )
    }
}
