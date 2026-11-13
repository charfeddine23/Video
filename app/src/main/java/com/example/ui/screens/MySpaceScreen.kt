package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import com.example.ui.theme.stableStatusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.PulseCyan
import com.example.ui.theme.PulseViolet
import com.example.ui.theme.TextDark
import com.example.ui.theme.ThemeMode
import com.example.ui.viewmodel.StreakViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MySpaceScreen(
    streakViewModel: StreakViewModel,
    currentThemeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onOpenRoadmap: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onOpenShieldFarming: () -> Unit = {},
    onOpenStatistics: () -> Unit = {},
    onOpenSystemSettings: () -> Unit = {},
    onCheckForUpdates: () -> Unit = {},
    isCheckingForUpdates: Boolean = false,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val uiState by streakViewModel.uiState.collectAsState()
    val streakDays = uiState.streakDays
    val userName by streakViewModel.userName.collectAsState()
    val userAvatarPreset by streakViewModel.userAvatarPreset.collectAsState()
    val isPremiumActivated by streakViewModel.isPremiumActivated.collectAsState()
    val isArabic = currentLanguage == AppLanguage.ARABIC

    var showPremiumDialog by remember { mutableStateOf(false) }

    val totalWatchHours = streakViewModel.getTotalWatchHours()
    val totalTracks = streakViewModel.getTotalTracksListened()

    // Account rank & type presentation
    val currentLevel = streakDays.coerceAtLeast(1)
    val accountTypeLabel = when {
        isPremiumActivated -> if (isArabic) "عضوية Aura Premium" else "Aura Premium Member"
        streakDays >= 365 -> if (isArabic) "عضوية الأسطورة الذهبية" else "Yearly Legend Account"
        streakDays >= 100 -> if (isArabic) "عضوية Aura Pro" else "Aura Pro Account"
        streakDays >= 20 -> if (isArabic) "عضوية النخبة" else "Elite Account"
        else -> if (isArabic) "الحساب القياسي" else "Standard Account"
    }
    val accountBadgeColor = when {
        isPremiumActivated || streakDays >= 365 -> Color(0xFFFFD700)
        streakDays >= 100 -> PulseCyan
        else -> Color(0xFFA78BFA)
    }

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
                        center = Offset(size.width * 0.5f, size.height * 0.2f)
                    )
                }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .stableStatusBarsPadding(),
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

            // 1. Luxury Centered Profile Container + Streak & Clickable Shields + Aura Pulse Premium
            item(key = "user_status_header") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .border(
                            width = 1.2.dp,
                            brush = Brush.verticalGradient(
                                listOf(
                                    if (isPremiumActivated) Color(0x99FFD700) else PulseCyan.copy(alpha = 0.65f),
                                    Color(0x447928CA),
                                    Color(0x33FF5722)
                                )
                            ),
                            shape = RoundedCornerShape(28.dp)
                        ),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xE0111726))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawBehind {
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            if (isPremiumActivated) Color(0x22FFD700) else Color(0x2000F2FE),
                                            Color.Transparent
                                        ),
                                        radius = size.width * 0.55f
                                    ),
                                    center = Offset(size.width * 0.5f, 70.dp.toPx())
                                )
                            }
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val preset = defaultAvatarPresets.find { it.id == userAvatarPreset }
                            ?: defaultAvatarPresets[0]

                        // Centered Luxury Halo Avatar (No edit pencil or edit button)
                        Box(
                            modifier = Modifier
                                .size(92.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.sweepGradient(
                                        if (isPremiumActivated) {
                                            listOf(Color(0xFFFFD700), PulseCyan, Color(0xFFFF9100), Color(0xFFFFD700))
                                        } else {
                                            listOf(PulseCyan, PulseViolet, Color(0xFFFF5722), PulseCyan)
                                        }
                                    )
                                )
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onNavigateToEditProfile()
                                }
                                .padding(3.dp)
                                .testTag("profile_avatar_hero"),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(Color(0xFF0B0F19))
                                    .padding(3.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(preset.gradient)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = preset.icon,
                                    contentDescription = userName,
                                    tint = TextDark,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Centered User Name (Clean, no edit pencil or edit button)
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 20.sp
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Luxury Level & Account Type Badges Row
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Level Pill
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color(0x2200F2FE),
                                border = BorderStroke(
                                    1.dp,
                                    Brush.horizontalGradient(listOf(PulseCyan, Color(0xFF38BDF8)))
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = PulseCyan,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (isArabic) "المستوى $currentLevel" else "Level $currentLevel",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.5.sp
                                        )
                                    )
                                }
                            }

                            // Account Type Luxury Pill
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = accountBadgeColor.copy(alpha = 0.16f),
                                border = BorderStroke(
                                    1.dp,
                                    Brush.horizontalGradient(
                                        listOf(accountBadgeColor, accountBadgeColor.copy(alpha = 0.45f))
                                    )
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isPremiumActivated || streakDays >= 365) {
                                            Icons.Default.WorkspacePremium
                                        } else {
                                            Icons.Default.Verified
                                        },
                                        contentDescription = null,
                                        tint = accountBadgeColor,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = accountTypeLabel,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = accountBadgeColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.5.sp
                                        )
                                    )
                                }
                            }
                        }

                        if (streakDays >= 365) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color(0x28FFD700),
                                border = BorderStroke(1.dp, Color(0xFFFFD700))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
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
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Streak and Clickable Shields (Shield Farming Entry) Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Streak Counter (🔥)
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0x40162030),
                                border = BorderStroke(1.dp, Color(0x33FF5722))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
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

                            // Clickable Shields Tile -> Opens Shield Farming Screen
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onOpenShieldFarming()
                                    }
                                    .testTag("profile_shields_tile"),
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0x40162030),
                                border = BorderStroke(1.dp, Color(0x4400F2FE))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f, fill = false),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
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
                                                modifier = Modifier.size(20.dp)
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
                                                text = if (isArabic) "دروع الحماية" else "Shields",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Color(0xFF94A3B8),
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = if (isArabic) "زراعة الدروع" else "Shield Farming",
                                        tint = PulseCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Aura Pulse Premium Container (Moved from System Settings into Profile below User)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    showPremiumDialog = true
                                }
                                .testTag("aura_premium_activation_banner"),
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xCC161426),
                            border = BorderStroke(
                                1.2.dp,
                                Brush.horizontalGradient(
                                    if (isPremiumActivated) {
                                        listOf(Color(0xFF10B981), Color(0xFFFFD700))
                                    } else {
                                        listOf(Color(0xFFFFD700), Color(0xFFFF9100), PulseCyan)
                                    }
                                )
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
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
                                            .background(Color(0x33FFD700)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.WorkspacePremium,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier.size(21.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Aura Pulse Premium 👑",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                color = Color(0xFFFFD700),
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 14.sp
                                            )
                                        )
                                        Text(
                                            text = if (isPremiumActivated) {
                                                if (isArabic) "مفعّل • جميع الميزات المتقدمة متاحة ✓" else "Active • All premium features unlocked ✓"
                                            } else {
                                                if (isArabic) "تفعيل الإعدادات المقفلة والمزايا الحصرية" else "Unlock advanced settings & exclusive features"
                                            },
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFFE2E8F0),
                                                fontSize = 10.5.sp
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isPremiumActivated) Color(0x3310B981) else Color(0xFFFFD700),
                                    border = if (isPremiumActivated) BorderStroke(1.dp, Color(0xFF10B981)) else null
                                ) {
                                    Text(
                                        text = if (isPremiumActivated) {
                                            if (isArabic) "مفعّل ✓" else "Active ✓"
                                        } else {
                                            if (isArabic) "تفعيل" else "Activate"
                                        },
                                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isPremiumActivated) Color(0xFF34D399) else Color(0xFF0F172A),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // 2. Statistics Card (Moved OUTSIDE & BELOW the Profile Container)
            item(key = "statistics_card_below_profile") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .border(
                            1.2.dp,
                            Brush.horizontalGradient(
                                listOf(PulseCyan.copy(alpha = 0.55f), PulseViolet.copy(alpha = 0.55f))
                            ),
                            RoundedCornerShape(22.dp)
                        )
                        .clip(RoundedCornerShape(22.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onOpenStatistics()
                        }
                        .testTag("profile_statistics_tile"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xE0101626))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 16.dp),
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
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0x3300F2FE), Color(0x337928CA))
                                        )
                                    )
                                    .border(1.dp, PulseCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BarChart,
                                    contentDescription = if (isArabic) "الإحصائيات" else "Statistics",
                                    tint = PulseCyan,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = if (isArabic) "الإحصائيات" else "Statistics",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isArabic) {
                                        "${String.format("%.1f", totalWatchHours)} ساعة مشاهدة • $totalTracks مقطع صوتي"
                                    } else {
                                        "${String.format("%.1f", totalWatchHours)} hrs watched • $totalTracks audio tracks"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0x2200F2FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = PulseCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // 3. System Settings Entry Card
            item(key = "system_settings_entry_card") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .border(
                            1.2.dp,
                            Brush.horizontalGradient(listOf(PulseCyan.copy(alpha = 0.6f), PulseViolet.copy(alpha = 0.5f))),
                            RoundedCornerShape(22.dp)
                        )
                        .clip(RoundedCornerShape(22.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onOpenSystemSettings()
                        }
                        .testTag("system_settings_entry_card"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xE0101626))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0x3300F2FE), Color(0x337928CA))
                                        )
                                    )
                                    .border(1.dp, PulseCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = if (isArabic) "إعدادات النظام" else "System Settings",
                                    tint = PulseCyan,
                                    modifier = Modifier.size(23.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = if (isArabic) "إعدادات النظام" else "System Settings",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = if (isArabic) {
                                        "المظهر، اللغة، القفل السري، التشغيل والنسخ الاحتياطي"
                                    } else {
                                        "Appearance, language, PIN vault, playback & backup"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0x2200F2FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = PulseCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // 4. Developer Info & Version Card
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
                                Text(
                                    text = "Charfeddine Kahlessenane",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 15.sp
                                    ),
                                    maxLines = 1
                                )
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
                                border = BorderStroke(1.dp, PulseCyan.copy(alpha = 0.6f))
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

        // Aura Pulse Premium Activation Dialog
        if (showPremiumDialog) {
            AlertDialog(
                onDismissRequest = { showPremiumDialog = false },
                containerColor = Color(0xF2121826),
                icon = {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(34.dp)
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
                        text = if (isPremiumActivated) {
                            if (isArabic) "عضوية Aura Pulse Premium مفعّلة! جميع الإعدادات المقفلة والميزات المتقدمة متاحة الآن. ✨" else "Aura Pulse Premium is active! All locked settings and exclusive features are unlocked. ✨"
                        } else {
                            if (isArabic) {
                                "يتيح لك نظام Aura Pulse Premium فتح جميع الإعدادات المتقدمة (المظهر، اللغة، الخزنة السرية، التشغيل في الخلفية، والنافذة العائمة).\n\nاضغط تفعيل لفتح كافة الميزات الآن."
                            } else {
                                "Aura Pulse Premium unlocks Theme Mode, Language, PIN Vault, Background Play, PiP Mode, and Exclusive Themes.\n\nTap Activate to enable Premium features."
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            streakViewModel.setPremiumActivated(!isPremiumActivated)
                            showPremiumDialog = false
                        }
                    ) {
                        Text(
                            text = if (isPremiumActivated) {
                                if (isArabic) "إيقاف التفعيل" else "Deactivate"
                            } else {
                                if (isArabic) "تفعيل الآن 👑" else "Activate Now 👑"
                            },
                            color = if (isPremiumActivated) Color(0xFFFF8A65) else Color(0xFFFFD700),
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPremiumDialog = false }) {
                        Text(
                            text = if (isArabic) "إغلاق" else "Close",
                            color = PulseCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    }
}
