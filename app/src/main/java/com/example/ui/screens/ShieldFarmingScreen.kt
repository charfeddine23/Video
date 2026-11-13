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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PulseCyan
import com.example.ui.viewmodel.StreakViewModel

@Composable
fun ShieldFarmingScreen(
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

    val farmSecs = streakViewModel.getShieldFarmAccumulatedSeconds()
    val farmMins = (farmSecs / 60).coerceIn(0, 60)
    val remainingMins = (60 - farmMins).coerceAtLeast(0)
    val dailyShieldsToday = streakViewModel.getShieldsEarnedToday().coerceIn(0, 3)
    val isDailyCapReached = dailyShieldsToday >= 3
    val farmProgress = if (isDailyCapReached) 1f else (farmSecs.toFloat() / 3600f).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
            .testTag("shield_farming_screen")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color(0x2400F2FE), Color(0x14FFB74D), Color.Transparent),
                            radius = size.width * 0.95f
                        ),
                        center = Offset(size.width * 0.5f, size.height * 0.16f)
                    )
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
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
                            .testTag("shield_farming_back_button")
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
                            text = if (isArabic) "زراعة الدروع" else "Shield Farming",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = if (isArabic) "اكسب دروع الحماية من استخدام التطبيق" else "Earn protection shields via active usage",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0x2800F2FE),
                    border = BorderStroke(1.dp, PulseCyan.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Shield, null, tint = PulseCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "${uiState.shieldsCount}/${uiState.maxShields}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
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
                // Hero Live Progress Card
                item(key = "shield_farm_hero") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.2.dp,
                                Brush.horizontalGradient(listOf(PulseCyan.copy(alpha = 0.7f), Color(0x88FFB74D))),
                                RoundedCornerShape(24.dp)
                            ),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xE0121826))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(Color(0x4400F2FE), Color(0x1100F2FE))
                                        )
                                    )
                                    .border(1.5.dp, PulseCyan.copy(alpha = 0.8f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = if (isDailyCapReached) Color(0xFF10B981) else PulseCyan,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = if (isDailyCapReached) {
                                    if (isArabic) "تم بلوغ الحد اليومي (3/3 دروع) ✓" else "Daily Limit Reached (3/3) ✓"
                                } else {
                                    if (isArabic) "$farmMins / 60 دقيقة للدرع القادم" else "$farmMins / 60 mins to next shield"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = if (isDailyCapReached) {
                                    if (isArabic) "أحسنت! يمكنك كسب المزيد من الدروع غداً" else "Great job! Daily farming resets tomorrow"
                                } else {
                                    if (isArabic) "متبقي $remainingMins دقيقة من الاستخدام النشط" else "$remainingMins minutes of foreground activity remaining"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            LinearProgressIndicator(
                                progress = { farmProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = if (isDailyCapReached) Color(0xFF10B981) else PulseCyan,
                                trackColor = Color(0x22FFFFFF)
                            )
                        }
                    }
                }

                // Daily 3-Shield Quota Card
                item(key = "shield_farm_daily_slots") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0x28FFFFFF), RoundedCornerShape(22.dp)),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xD9101626))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isArabic) "حصيلة اليوم (3 دروع كحد أقصى)" else "Today's Shield Quota (Max 3)",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "$dailyShieldsToday / 3",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = if (isDailyCapReached) Color(0xFF10B981) else PulseCyan,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                (1..3).forEach { slot ->
                                    val earned = dailyShieldsToday >= slot
                                    Surface(
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (earned) Color(0x2210B981) else Color(0x18FFFFFF),
                                        border = BorderStroke(
                                            1.dp,
                                            if (earned) Color(0xFF10B981) else Color(0x22FFFFFF)
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 14.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = if (earned) Icons.Default.CheckCircle else Icons.Default.Shield,
                                                contentDescription = null,
                                                tint = if (earned) Color(0xFF34D399) else Color(0xFF64748B),
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = if (isArabic) "الدرع $slot" else "Shield $slot",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (earned) Color.White else Color(0xFF94A3B8),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            Text(
                                                text = if (earned) (if (isArabic) "مكتسب ✓" else "Earned ✓") else "${slot}h",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (earned) Color(0xFF34D399) else Color(0xFF64748B),
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

                // Rules Card
                item(key = "shield_farm_rules") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0x2200F2FE), RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xCC0F172A))
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, null, tint = PulseCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isArabic) "قواعد زراعة الدروع" else "Shield Farming Rules",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            RuleLine(
                                icon = Icons.Default.AccessTime,
                                text = if (isArabic) "تُمنح 1 درع حماية مقابل كل ساعة كاملة (60 دقيقة) من استخدام التطبيق." else "Earn 1 Shield for every full hour (60 min) of active app usage.",
                                tint = PulseCyan
                            )
                            RuleLine(
                                icon = Icons.Default.Shield,
                                text = if (isArabic) "الحد الأقصى اليومي هو 3 دروع، وبسعة خزينة تصل إلى ${uiState.maxShields} درع." else "Maximum 3 shields earned per day, up to ${uiState.maxShields} shields total inventory.",
                                tint = Color(0xFFFFB74D)
                            )
                            RuleLine(
                                icon = Icons.Default.Info,
                                text = if (isArabic) "الاستخدام في الخلفية لا يُحتسب ضمن عداد زراعة الدروع." else "Background usage does NOT count toward shield farming.",
                                tint = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RuleLine(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    tint: Color
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(16.dp).padding(top = 2.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFFCBD5E1),
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        )
    }
}
