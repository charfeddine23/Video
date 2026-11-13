package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.PulseCyan
import com.example.ui.theme.TextDark
import com.example.ui.theme.ThemeMode
import com.example.ui.viewmodel.StreakViewModel

@Composable
fun MySpaceScreen(
    streakViewModel: StreakViewModel,
    currentThemeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onOpenRoadmap: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onCheckForUpdates: () -> Unit = {},
    isCheckingForUpdates: Boolean = false,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val uiState by streakViewModel.uiState.collectAsState()
    val streakDays = uiState.streakDays
    val userName by streakViewModel.userName.collectAsState()
    val userAvatarPreset by streakViewModel.userAvatarPreset.collectAsState()
    val isArabic = currentLanguage == AppLanguage.ARABIC

    // Premium Activation Dialog State
    var showPremiumPromoDialog by remember { mutableStateOf(false) }

    // System Settings Hub States
    var currentRgbAccent by remember { mutableStateOf(streakViewModel.getRgbAccentColor()) }
    var isOledBlackMode by remember { mutableStateOf(streakViewModel.isOledBlackEnabled()) }
    var isAutoResumeEnabled by remember { mutableStateOf(streakViewModel.getAutoResume()) }
    var defaultPlaybackSpeed by remember { mutableStateOf(streakViewModel.getDefaultSpeed()) }
    var systemFeedbackMessage by remember { mutableStateOf<String?>(null) }

    val userTierBadge = streakViewModel.getUserTierBadge(streakDays, isArabic)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
            .testTag("my_space_screen")
    ) {
        // Ambient background glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(
                                Color(0x1800F2FE),
                                Color(0x10FF5722),
                                Color.Transparent
                            ),
                            radius = size.width * 0.95f
                        ),
                        center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.2f)
                    )
                }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
        ) {
            // Screen Title
            item(key = "my_space_title") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isArabic) "مساحتي الخاصة" else "My Space",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // 1. User Status Header: Current Streak counter & Active Shield inventory tracker (Max 30)
            item(key = "user_status_header") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .border(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(
                                listOf(PulseCyan.copy(alpha = 0.6f), Color(0xFFFF5722).copy(alpha = 0.6f))
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xD9121826))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        // User Profile Summary Row (Clickable to Edit Profile)
                        val preset = defaultAvatarPresets.find { it.id == userAvatarPreset }
                            ?: defaultAvatarPresets[0]

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onNavigateToEditProfile()
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(preset.gradient))
                                        .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = preset.icon,
                                        contentDescription = null,
                                        tint = TextDark,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = userName,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Profile",
                                            tint = PulseCyan.copy(alpha = 0.8f),
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = userTierBadge,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (streakDays >= 365) Color(0xFFFFD700) else if (streakDays >= 100) PulseCyan else Color(0xFF67E8F9),
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )

                                    if (streakDays >= 365) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0x33FFD700),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.EmojiEvents,
                                                    contentDescription = null,
                                                    tint = Color(0xFFFFD700),
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Yearly Aura Legend 👑",
                                                    color = Color(0xFFFFD700),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Edit Profile Pill Button
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0x2600F2FE),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PulseCyan.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = if (isArabic) "تعديل ✏️" else "Edit ✏️",
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = PulseCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Streak and Shield Inventory Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Streak Counter (🔥)
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0x40162030),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FF5722))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
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
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "${uiState.streakDays}d",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White
                                            )
                                        )
                                        Text(
                                            text = if (isArabic) "سلسلة المشاهدة" else "Active Streak",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFF94A3B8),
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }
                            }

                            // Active Shield Inventory (🛡️ max 30)
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0x40162030),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x3300F2FE))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(Color(0x3300F2FE)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Shield,
                                                contentDescription = null,
                                                tint = PulseCyan,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "${uiState.shieldsCount}/${uiState.maxShields}",
                                                style = MaterialTheme.typography.titleLarge.copy(
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = Color.White
                                                )
                                            )
                                            Text(
                                                text = if (isArabic) "دروع الحماية" else "Shields Tracker",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Color(0xFF94A3B8),
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

                Spacer(modifier = Modifier.height(14.dp))
            }

            // 1b. Shield Farming (60-Min Rule Tracker)
            item(key = "shield_farming_tracker_card") {
                val farmSecs = streakViewModel.getShieldFarmAccumulatedSeconds()
                val farmMins = (farmSecs / 60).coerceIn(0, 60)
                val dailyShieldsToday = streakViewModel.getShieldsEarnedToday()
                val isDailyCapReached = dailyShieldsToday >= 3
                val farmProgress = if (isDailyCapReached) 1f else (farmSecs.toFloat() / 3600f).coerceIn(0f, 1f)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .border(
                            1.dp,
                            Brush.horizontalGradient(listOf(Color(0x3300F2FE), Color(0x33FFB74D))),
                            RoundedCornerShape(20.dp)
                        ),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xD9121826))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x3300F2FE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = PulseCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isArabic) "زراعة الدروع (ساعة لكل درع)" else "Shield Farming (1h Per Shield)",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = if (isArabic)
                                            "درع واحد لكل 1 ساعة استخدام للتطبيق (حد 3 يومياً • بدون خلفية)"
                                        else
                                            "1 Shield per 1h app usage (3 max/day • foreground only)",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF94A3B8),
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                            Text(
                                text = if (isDailyCapReached) {
                                    if (isArabic) "3/3 اليوم ✓" else "3/3 Max"
                                } else {
                                    "$farmMins / 60 دقيقة"
                                },
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (isDailyCapReached) Color(0xFF10B981) else PulseCyan,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { farmProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (isDailyCapReached) Color(0xFF10B981) else PulseCyan,
                            trackColor = Color(0x22FFFFFF)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isArabic) "المكتسب اليوم: $dailyShieldsToday من 3 دروع" else "Earned today: $dailyShieldsToday of 3 shields",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = if (isArabic) "الاستخدام في الخلفية لا يُحتسب" else "Foreground usage only",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF64748B),
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // 1c. Aura Wrapped Analytics Card
            item(key = "aura_wrapped_analytics_card") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .border(
                            1.dp,
                            Brush.horizontalGradient(listOf(Color(0x447928CA), Color(0x4400F2FE), Color(0x44FF5722))),
                            RoundedCornerShape(20.dp)
                        ),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xE6101626))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = PulseCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isArabic) "إحصائيات Aura Wrapped 🎧🎬" else "Aura Wrapped Analytics 🎧🎬",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0x2200F2FE),
                                border = BorderStroke(1.dp, PulseCyan.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "OFFLINE STATS",
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = PulseCyan,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0x22FFFFFF)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "${String.format("%.1f", streakViewModel.getTotalWatchHours())} hrs",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    )
                                    Text(
                                        text = if (isArabic) "ساعات المشاهدة" else "Total Watch Time",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF94A3B8),
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0x22FFFFFF)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "${streakViewModel.getTotalTracksListened()}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = PulseCyan,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    )
                                    Text(
                                        text = if (isArabic) "مقاطع صوتية" else "Tracks Listened",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF94A3B8),
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Top Media Folders Breakdown
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x1A00F2FE)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (isArabic) "أكثر المجلدات نشاطاً: Movies (62%) • Downloads (24%) • Series (14%)" else "Top Folders: Movies (62%) • Downloads (24%) • Series (14%)",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // 1d. Premium Activation Promotional Card
            item(key = "aura_premium_activation_card") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .border(
                            1.5.dp,
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFFD700), Color(0xFFFF9100), PulseCyan)
                            ),
                            RoundedCornerShape(22.dp)
                        )
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            showPremiumPromoDialog = true
                        }
                        .testTag("aura_premium_activation_card"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xE6141324))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Aura Pulse Premium",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFFFFD700)
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "👑", fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isArabic) "إلغاء قفل كافة الميزات الحصرية ومحسنات الصوت والصور السينمائية" else "Unlock all exclusive cinematic shaders, spatial audio & unlimited vaults",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFFE2E8F0),
                                        fontSize = 11.5.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFFD700),
                                modifier = Modifier.clip(RoundedCornerShape(12.dp))
                            ) {
                                Text(
                                    text = if (isArabic) "ترقية 🚀" else "Upgrade 🚀",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF0F172A),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Feature Vault & 365-Level Progression moved to MilestoneRoadmapScreen (Achievements & Shields)

            // 4. System Settings Hub (Appearance, Media Management, Playback, and Backup)
            item(key = "system_settings_hub_card") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .border(
                            1.dp,
                            Brush.horizontalGradient(listOf(PulseCyan.copy(alpha = 0.5f), Color(0x337928CA))),
                            RoundedCornerShape(22.dp)
                        ),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xD9101626))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x3300F2FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storage,
                                    contentDescription = null,
                                    tint = PulseCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isArabic) "مركز إعدادات النظام (System Settings)" else "System Settings Hub",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                                Text(
                                    text = if (isArabic) "المظهر، إدارة الوسائط، النسخ الاحتياطي غير المتصل" else "Offline appearance, storage, playback & backup",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8), fontSize = 11.sp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // A. Appearance: RGB Accent Picker
                        Text(
                            text = if (isArabic) "لون النيون الرئيسي (RGB Accent)" else "RGB ACCENT COLOR",
                            style = MaterialTheme.typography.labelSmall.copy(color = PulseCyan, fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val rgbOptions = listOf(
                                Pair(0xFF00FFCCL, Color(0xFF00FFCC)), // Neon Cyan
                                Pair(0xFFFF007FL, Color(0xFFFF007F)), // Neon Pink
                                Pair(0xFFFF9900L, Color(0xFFFF9900)), // Solar Amber
                                Pair(0xFF00E676L, Color(0xFF00E676))  // Emerald
                            )
                            rgbOptions.forEach { (colorVal, color) ->
                                val isSelected = currentRgbAccent == colorVal
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = color.copy(alpha = if (isSelected) 0.35f else 0.12f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        if (isSelected) 2.dp else 1.dp,
                                        if (isSelected) color else color.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            currentRgbAccent = colorVal
                                            streakViewModel.saveRgbAccentColor(colorVal)
                                        }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, null, tint = color, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // OLED Black Mode Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isArabic) "وضع السواد المطلق (OLED Black)" else "OLED Deep Black Mode",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = if (isArabic) "خلفيات سوداء نقية لتوفير طاقة شاشات OLED" else "True #000000 pixel shutoff for battery savings",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8), fontSize = 11.sp)
                                )
                            }
                            Switch(
                                checked = isOledBlackMode,
                                onCheckedChange = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    isOledBlackMode = it
                                    streakViewModel.saveOledBlackEnabled(it)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = PulseCyan,
                                    checkedThumbColor = Color.White
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // B. Media Management: Thumbnail Cache Cleaner
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isArabic) "تنظيف الذاكرة المؤقتة (Cache Cleaner)" else "Thumbnail Cache Cleaner",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = if (isArabic) "حذف الصور المصغرة المؤقتة واسترجاع المساحة" else "Clear cached video preview thumbs (free 48MB)",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8), fontSize = 11.sp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0x3300FFCC),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PulseCyan.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        val freed = streakViewModel.clearThumbnailCache()
                                        systemFeedbackMessage = if (isArabic) "تم تنظيف الذاكرة المؤقتة وحذف ${freed}MB بنجاح 🧹" else "Cleared ${freed}MB of thumbnail cache 🧹"
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CleaningServices, null, tint = PulseCyan, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isArabic) "تنظيف 🧹" else "Clean",
                                        style = MaterialTheme.typography.labelSmall.copy(color = PulseCyan, fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // C. Playback Settings: Auto-Resume & Default Speed
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isArabic) "الاستئناف التلقائي (Auto-Resume)" else "Auto-Resume Playback",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = if (isArabic) "استكمال الفيديو من آخر نقطة توقف تلقائياً" else "Pick up right where you left off",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8), fontSize = 11.sp)
                                )
                            }
                            Switch(
                                checked = isAutoResumeEnabled,
                                onCheckedChange = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    isAutoResumeEnabled = it
                                    streakViewModel.saveAutoResume(it)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = PulseCyan,
                                    checkedThumbColor = Color.White
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Default Speed Selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isArabic) "سرعة التشغيل الافتراضية" else "Default Playback Speed",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Medium)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(1.0f, 1.25f, 1.5f).forEach { spd ->
                                    val isSpdSelected = defaultPlaybackSpeed == spd
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSpdSelected) PulseCyan else Color(0x22FFFFFF),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                defaultPlaybackSpeed = spd
                                                streakViewModel.saveDefaultSpeed(spd)
                                            }
                                    ) {
                                        Text(
                                            text = "${spd}x",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isSpdSelected) Color(0xFF0F172A) else Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // D. Offline Local JSON Backup & Restore
                        Text(
                            text = if (isArabic) "النسخ الاحتياطي المحلي (JSON Backup & Restore)" else "OFFLINE LOCAL BACKUP & RESTORE",
                            style = MaterialTheme.typography.labelSmall.copy(color = PulseCyan, fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        val exportedJson = streakViewModel.exportPreferencesToJson()
                                        systemFeedbackMessage = if (isArabic) "تم إنشاء نسخة JSON الاحتياطية بنجاح 💾" else "Exported local JSON backup 💾"
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0x2600F2FE),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PulseCyan.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = if (isArabic) "تصدير JSON 📤" else "Export JSON 📤",
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = PulseCyan,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                )
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        val backupJson = streakViewModel.exportPreferencesToJson()
                                        val restored = streakViewModel.importPreferencesFromJson(backupJson)
                                        systemFeedbackMessage = if (restored) {
                                            if (isArabic) "تمت استعادة البيانات بنجاح 📥" else "Preferences restored successfully 📥"
                                        } else {
                                            if (isArabic) "فشلت استعادة البيانات" else "Restore failed"
                                        }
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0x26FF9100),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF9100).copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = if (isArabic) "استعادة JSON 📥" else "Restore JSON 📥",
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFFFF9100),
                                        fontWeight = FontWeight.Bold,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Developer Info & Version Card
            item(key = "developer_info_card") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .border(
                            1.dp,
                            Brush.horizontalGradient(listOf(Color(0x3300F2FE), Color(0x33FF5722))),
                            RoundedCornerShape(20.dp)
                        ),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xD9121826))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0x3300F2FE), Color(0x337928CA))
                                        )
                                    )
                                    .border(1.dp, PulseCyan.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = null,
                                    tint = PulseCyan,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Charfeddine Kahlessenane",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 15.sp
                                        ),
                                        maxLines = 1
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isArabic) "المطور الرئيسي ومصمم التطبيق" else "Lead Android Engineer & Designer",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = PulseCyan,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0x3300F2FE),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PulseCyan.copy(alpha = 0.6f))
                            ) {
                                Text(
                                    text = "v${BuildConfig.VERSION_NAME}",
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = PulseCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0x1FFFFFFF))
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isArabic) "تاريخ آخر تحديث" else "Last Updated",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B), fontSize = 11.sp)
                            )
                            Text(
                                text = "November 5, 2026",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isArabic) "رقم البناء (Build Code)" else "Build Code",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B), fontSize = 11.sp)
                            )
                            Text(
                                text = "${BuildConfig.VERSION_CODE}",
                                style = MaterialTheme.typography.bodySmall.copy(color = PulseCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0x22E50914),
                            border = BorderStroke(1.dp, Color(0x66FF2438)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable(enabled = !isCheckingForUpdates) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onCheckForUpdates()
                                }
                                .testTag("check_for_updates_button")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                if (isCheckingForUpdates) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = Color(0xFFFF2438),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (isArabic) "جاري التحقق من التحديثات..." else "Checking for updates...",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.SystemUpdate,
                                        contentDescription = null,
                                        tint = Color(0xFFFF4D5A),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isArabic) "التحقق من التحديثات" else "Check for Updates",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }



        // =========================================================================
        // AURA PULSE PREMIUM PROMOTIONAL DIALOG
        // =========================================================================
        if (showPremiumPromoDialog) {
            AlertDialog(
                onDismissRequest = { showPremiumPromoDialog = false },
                containerColor = Color(0xF2121826),
                icon = {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(32.dp)
                    )
                },
                title = {
                    Text(
                        text = "Aura Pulse Premium",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFFD700)
                        )
                    )
                },
                text = {
                    Text(
                        text = "Coming Soon 🚀 Stay tuned for exclusive features!\n\nقريباً 🚀 ترقبوا ميزات النسخة الاحترافية الفاخرة!",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showPremiumPromoDialog = false }) {
                        Text(
                            text = if (isArabic) "حسناً (OK)" else "Got it",
                            color = PulseCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }

        // System Settings Feedback Pill
        AnimatedVisibility(
            visible = systemFeedbackMessage != null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xF2101626),
                border = androidx.compose.foundation.BorderStroke(1.dp, PulseCyan),
                shadowElevation = 12.dp
            ) {
                Text(
                    text = systemFeedbackMessage ?: "",
                    modifier = Modifier
                        .clickable { systemFeedbackMessage = null }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    )
                )
            }
        }
    }
}

