package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.PulseCyan
import com.example.ui.theme.PulseViolet
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
    onOpenRoadmap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val uiState by streakViewModel.uiState.collectAsState()
    val isArabic = currentLanguage == AppLanguage.ARABIC

    // Interactive feature vault toggle states
    var appLockEnabled by remember { mutableStateOf(false) }
    var mediaSharingEnabled by remember { mutableStateOf(true) }
    var selectedCustomThemeIndex by remember { mutableStateOf(0) }
    var smartNotificationsEnabled by remember { mutableStateOf(true) }
    var backgroundPlayEnabled by remember { mutableStateOf(true) }
    var pipModeEnabled by remember { mutableStateOf(true) }

    val streakDays = uiState.streakDays

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
                    Text(
                        text = if (isArabic) "مركز المكافآت والإعدادات المتقدمة" else "Rewards Hub & Feature Vault",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
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
                        // User Profile Summary Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(listOf(PulseCyan, PulseViolet))
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = TextDark,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "Charfeddine Ka",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Text(
                                        text = if (isArabic) "مستخدم Video Pulse المميز" else "Video Pulse Elite Member",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = PulseCyan,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }

                            // View Roadmap path button
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0x3300F2FE),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PulseCyan.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onOpenRoadmap()
                                    }
                            ) {
                                Text(
                                    text = if (isArabic) "خارطة المكافآت 🗺️" else "Roadmap 🗺️",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
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

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick Shield Actions: Add Shield & Simulate Missed Day
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0x2200F2FE),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4400F2FE)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        streakViewModel.addShield()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.Add, null, tint = PulseCyan, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isArabic) "إضافة درع (+1 🛡️)" else "+1 Shield (Max 30)",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = PulseCyan,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0x22EF4444),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x44EF4444)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        streakViewModel.simulateMissedDay()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.Warning, null, tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isArabic) "محاكاة تفويت يوم" else "Simulate Missed Day",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFFEF4444),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // 2. Milestone Feature Vault (Control Panel) Header
            item(key = "feature_vault_header") {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = if (isArabic) "لوحة تحكم ميزات الإنجازات (Feature Vault)" else "Milestone Feature Vault",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = if (isArabic) "الميزات المقفولة والمفعلة حسب أيام السلسلة" else "Progressive controls tied to your watch streak",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Feature 1: Manual Dark/Light Mode toggle (Day 5)
            item(key = "vault_day_5") {
                val isUnlocked = streakDays >= 5
                VaultFeatureCard(
                    dayRequired = 5,
                    isUnlocked = isUnlocked,
                    icon = Icons.Default.DarkMode,
                    title = if (isArabic) "الوضع الليلي والنهاري (Theme Mode)" else "Manual Dark / Light Mode Toggle",
                    subtitle = if (isArabic) "تبديل المظهر فوراً بين الوضع الليلي والنهاري وحسب النظام" else "Switch appearances instantly anytime",
                    isArabic = isArabic
                ) {
                    if (isUnlocked) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ThemeChip(
                                icon = Icons.Default.DarkMode,
                                label = if (isArabic) "ليلي" else "Dark",
                                isSelected = currentThemeMode == ThemeMode.DARK,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onThemeModeChange(ThemeMode.DARK)
                                },
                                modifier = Modifier.weight(1f)
                            )
                            ThemeChip(
                                icon = Icons.Default.LightMode,
                                label = if (isArabic) "نهاري" else "Light",
                                isSelected = currentThemeMode == ThemeMode.LIGHT,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onThemeModeChange(ThemeMode.LIGHT)
                                },
                                modifier = Modifier.weight(1f)
                            )
                            ThemeChip(
                                icon = Icons.Default.BrightnessAuto,
                                label = if (isArabic) "نظام" else "System",
                                isSelected = currentThemeMode == ThemeMode.SYSTEM,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onThemeModeChange(ThemeMode.SYSTEM)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Feature 2: App Lock PIN/Pattern setup (Day 10)
            item(key = "vault_day_10") {
                val isUnlocked = streakDays >= 10
                VaultFeatureCard(
                    dayRequired = 10,
                    isUnlocked = isUnlocked,
                    icon = Icons.Default.Lock,
                    title = if (isArabic) "قفل التطبيق (App Lock PIN)" else "App Lock PIN / Pattern Setup",
                    subtitle = if (isArabic) "حماية مجلدات الفيديوهات برمز سري وبصمة الإصبع" else "Secure offline video library with biometric/PIN code",
                    isArabic = isArabic
                ) {
                    if (isUnlocked) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (appLockEnabled)
                                    (if (isArabic) "القفل مفعل (PIN: ****)" else "PIN Lock Enabled (****)")
                                else
                                    (if (isArabic) "تفعيل قفل التطبيق" else "Enable App Lock"),
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 12.sp)
                            )
                            Switch(
                                checked = appLockEnabled,
                                onCheckedChange = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    appLockEnabled = it
                                },
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = PulseCyan,
                                    checkedThumbColor = Color.White
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Feature 3: Manual Language Selection (Day 15)
            item(key = "vault_day_15") {
                val isUnlocked = streakDays >= 15
                VaultFeatureCard(
                    dayRequired = 15,
                    isUnlocked = isUnlocked,
                    icon = Icons.Default.Language,
                    title = if (isArabic) "اختيار لغة التطبيق يدوياً" else "Manual Language Selection",
                    subtitle = if (isArabic) "التبديل الفوري بين اللغة العربية واللغة الإنجليزية" else "Instant toggle between Arabic and English localization",
                    isArabic = isArabic
                ) {
                    if (isUnlocked) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (currentLanguage == AppLanguage.ARABIC) Color(0x3300F2FE) else Color(0x1F1E293B),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (currentLanguage == AppLanguage.ARABIC) PulseCyan else Color(0x22FFFFFF)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onLanguageChange(AppLanguage.ARABIC)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "العربية (Arabic)",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = if (currentLanguage == AppLanguage.ARABIC) PulseCyan else Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (currentLanguage == AppLanguage.ENGLISH) Color(0x3300F2FE) else Color(0x1F1E293B),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (currentLanguage == AppLanguage.ENGLISH) PulseCyan else Color(0x22FFFFFF)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onLanguageChange(AppLanguage.ENGLISH)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "English (الإنجليزية)",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = if (currentLanguage == AppLanguage.ENGLISH) PulseCyan else Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Feature 4: Media Sharing capabilities (Day 20)
            item(key = "vault_day_20") {
                val isUnlocked = streakDays >= 20
                VaultFeatureCard(
                    dayRequired = 20,
                    isUnlocked = isUnlocked,
                    icon = Icons.Default.Share,
                    title = if (isArabic) "مشاركة الوسائط محلياً" else "Media Sharing Capabilities",
                    subtitle = if (isArabic) "إرسال المقاطع للأجهزة القريبة عبر Wi-Fi Direct فائق السرعة" else "High-speed Wi-Fi Direct offline transfer to nearby devices",
                    isArabic = isArabic
                ) {
                    if (isUnlocked) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (mediaSharingEnabled)
                                    (if (isArabic) "المشاركة اللاسلكية مفعلة" else "Local Offline P2P Sharing Active")
                                else
                                    (if (isArabic) "المشاركة اللاسلكية معطلة" else "Offline Sharing Paused"),
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 12.sp)
                            )
                            Switch(
                                checked = mediaSharingEnabled,
                                onCheckedChange = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    mediaSharingEnabled = it
                                },
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = PulseCyan,
                                    checkedThumbColor = Color.White
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Feature 5: Custom Themes selection - 3 Dark / 3 Light (Day 25)
            item(key = "vault_day_25") {
                val isUnlocked = streakDays >= 25
                VaultFeatureCard(
                    dayRequired = 25,
                    isUnlocked = isUnlocked,
                    icon = Icons.Default.Palette,
                    title = if (isArabic) "ثيمات حصرية (3 ليلي / 3 نهاري)" else "Custom Themes (3 Dark / 3 Light)",
                    subtitle = if (isArabic) "ألوان النيون والتدرجات الزجاجية الحصرية للمشغل" else "Cyber Cyan, Obsidian Dark, Sunset Violet, Pure Pearl, Solar Light",
                    isArabic = isArabic
                ) {
                    if (isUnlocked) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            val themes = listOf(
                                "Obsidian Matrix (Dark)",
                                "Cyan Pulse (Dark)",
                                "Neon Sunset (Dark)",
                                "Pure Glass (Light)",
                                "Aurora Glow (Light)",
                                "Solar Pearl (Light)"
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                themes.take(3).forEachIndexed { idx, thName ->
                                    ThemePill(
                                        title = thName.substringBefore(" ("),
                                        isSelected = selectedCustomThemeIndex == idx,
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            selectedCustomThemeIndex = idx
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                themes.drop(3).forEachIndexed { idx, thName ->
                                    val realIdx = idx + 3
                                    ThemePill(
                                        title = thName.substringBefore(" ("),
                                        isSelected = selectedCustomThemeIndex == realIdx,
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            selectedCustomThemeIndex = realIdx
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Feature 6: Smart Notifications toggle (Day 30)
            item(key = "vault_day_30") {
                val isUnlocked = streakDays >= 30
                VaultFeatureCard(
                    dayRequired = 30,
                    isUnlocked = isUnlocked,
                    icon = Icons.Default.Notifications,
                    title = if (isArabic) "إشعارات ذكية مخصصة" else "Smart Notifications Toggle",
                    subtitle = if (isArabic) "تنبيهات استكمال المشاهدة وتذكير حماية السلسلة قبل منتصف الليل" else "Continue watching prompts & streak preservation alerts",
                    isArabic = isArabic
                ) {
                    if (isUnlocked) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (smartNotificationsEnabled)
                                    (if (isArabic) "التنبيهات الذكية مفعلة" else "Smart Alerts Active")
                                else
                                    (if (isArabic) "التنبيهات الذكية معطلة" else "Smart Alerts Muted"),
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 12.sp)
                            )
                            Switch(
                                checked = smartNotificationsEnabled,
                                onCheckedChange = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    smartNotificationsEnabled = it
                                },
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = PulseCyan,
                                    checkedThumbColor = Color.White
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Feature 7: Background Play settings (Day 35)
            item(key = "vault_day_35") {
                val isUnlocked = streakDays >= 35
                VaultFeatureCard(
                    dayRequired = 35,
                    isUnlocked = isUnlocked,
                    icon = Icons.Default.Headset,
                    title = if (isArabic) "التشغيل في الخلفية (Background Play)" else "Background Audio & Video Play",
                    subtitle = if (isArabic) "الاستمرار في سماع الصوت عند إغلاق الشاشة أو تصفح تطبيقات أخرى" else "Keep audio playing when screen is locked or switching apps",
                    isArabic = isArabic
                ) {
                    if (isUnlocked) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (backgroundPlayEnabled)
                                    (if (isArabic) "التشغيل في الخلفية مفعل" else "Background Engine ON")
                                else
                                    (if (isArabic) "التشغيل في الخلفية معطل" else "Background Engine OFF"),
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 12.sp)
                            )
                            Switch(
                                checked = backgroundPlayEnabled,
                                onCheckedChange = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    backgroundPlayEnabled = it
                                },
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = PulseCyan,
                                    checkedThumbColor = Color.White
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Feature 8: Pop-up Video / PiP Mode settings (Day 40)
            item(key = "vault_day_40") {
                val isUnlocked = streakDays >= 40
                VaultFeatureCard(
                    dayRequired = 40,
                    isUnlocked = isUnlocked,
                    icon = Icons.Default.PictureInPicture,
                    title = if (isArabic) "فيديو عائم في نافذة منبثقة (PiP Mode)" else "Pop-up Video / PiP Mode Settings",
                    subtitle = if (isArabic) "مشغل فيديو عائم قابل لتغيير الحجم فوق كافة التطبيقات" else "Floating resizable picture-in-picture video player",
                    isArabic = isArabic
                ) {
                    if (isUnlocked) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (pipModeEnabled)
                                    (if (isArabic) "نافذة المشغل العائم مفعلة" else "Floating PiP Player Enabled")
                                else
                                    (if (isArabic) "النافذة العائمة معطلة" else "Floating PiP Player Disabled"),
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 12.sp)
                            )
                            Switch(
                                checked = pipModeEnabled,
                                onCheckedChange = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    pipModeEnabled = it
                                },
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = PulseCyan,
                                    checkedThumbColor = Color.White
                                )
                            )
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
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x2600F2FE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Code,
                                        contentDescription = null,
                                        tint = PulseCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Developed by: Charfeddine Ka",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                    )
                                    Text(
                                        text = if (isArabic) "مهندس ومصمم التطبيق" else "Lead Android Engineer & Designer",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF94A3B8),
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0x3300F2FE),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PulseCyan.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "v${BuildConfig.VERSION_NAME}",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
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
                    }
                }
            }
        }
    }
}

@Composable
fun VaultFeatureCard(
    dayRequired: Int,
    isUnlocked: Boolean,
    icon: ImageVector,
    title: String,
    subtitle: String,
    isArabic: Boolean,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .border(
                width = 1.dp,
                color = if (isUnlocked) Color(0x4010B981) else Color(0x1FFFFFFF),
                shape = RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) Color(0xD9121826) else Color(0x660F172A)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isUnlocked) Color(0x3310B981) else Color(0x1A475569)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isUnlocked) Color(0xFF10B981) else Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlocked) Color.White else Color(0xFF94A3B8),
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isUnlocked) Color(0xFF94A3B8) else Color(0xFF64748B),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isUnlocked) Color(0x3310B981) else Color(0x33334155),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isUnlocked) Color(0x5510B981) else Color(0x33FFFFFF)
                    )
                ) {
                    Text(
                        text = if (isUnlocked)
                            (if (isArabic) "مفعلة ✓" else "UNLOCKED ✓")
                        else
                            (if (isArabic) "🔒 اليوم $dayRequired" else "🔒 Day $dayRequired"),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = if (isUnlocked) Color(0xFF34D399) else Color(0xFF94A3B8)
                        )
                    )
                }
            }

            content()
        }
    }
}

@Composable
fun ThemeChip(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) Color(0x3300F2FE) else Color(0x1F1E293B),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) PulseCyan else Color(0x22FFFFFF)
        ),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) PulseCyan else Color(0xFF94A3B8),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) PulseCyan else Color.White,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
fun ThemePill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) Color(0x3300F2FE) else Color(0x1A1E293B),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) PulseCyan else Color(0x1FFFFFFF)
        ),
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (isSelected) PulseCyan else Color(0xFF94A3B8),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 9.sp
            ),
            maxLines = 1
        )
    }
}
