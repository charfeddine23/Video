package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.AuraAtmosphericLayer
import com.example.ui.theme.PulseCyan
import com.example.ui.theme.PulseViolet
import com.example.ui.theme.ThemeMode
import com.example.ui.theme.aura
import com.example.ui.viewmodel.StreakViewModel

@Composable
fun SystemSettingsScreen(
    streakViewModel: StreakViewModel,
    currentThemeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onBack: () -> Unit,
    isArabic: Boolean = true,
    modifier: Modifier = Modifier
) {
    val aura = MaterialTheme.aura
    val haptic = LocalHapticFeedback.current
    val isPremiumActivated by streakViewModel.isPremiumActivated.collectAsState()
    var isMultiThemeEngineOpen by remember { mutableStateOf(false) }

    BackHandler {
        if (isMultiThemeEngineOpen) {
            isMultiThemeEngineOpen = false
        } else {
            onBack()
        }
    }

    // Core System Settings States
    var currentRgbAccent by remember { mutableStateOf(streakViewModel.getRgbAccentColor()) }
    var isOledBlackMode by remember { mutableStateOf(streakViewModel.isOledBlackEnabled()) }
    val isFullScreenMode by streakViewModel.isFullScreenMode.collectAsState()
    var isAutoResumeEnabled by remember { mutableStateOf(streakViewModel.getAutoResume()) }
    var defaultPlaybackSpeed by remember { mutableStateOf(streakViewModel.getDefaultSpeed()) }

    // Moved Locked Settings (Activated via Aura Pulse Premium)
    var selectedCustomThemeIndex by remember { mutableStateOf(streakViewModel.getSelectedThemePresetIndex()) }
    var eyeProtectionEnabled by remember { mutableStateOf(streakViewModel.isEyeProtectionEnabled()) }
    var mediaSharingEnabled by remember { mutableStateOf(streakViewModel.isWifiDirectSharingEnabled()) }
    var smartNotificationsEnabled by remember { mutableStateOf(streakViewModel.isSmartNotificationsEnabled()) }
    var backgroundPlayEnabled by remember { mutableStateOf(streakViewModel.isBackgroundPlayEnabled()) }
    var pipModeEnabled by remember { mutableStateOf(streakViewModel.isPipModeEnabled()) }

    // Secret Vault PIN States
    var showVaultAuthDialog by remember { mutableStateOf(false) }
    var isVaultUnlocked by remember { mutableStateOf(false) }
    var vaultPinInput by remember { mutableStateOf("") }
    var vaultPinError by remember { mutableStateOf<String?>(null) }
    val vaultSavedPin = remember { streakViewModel.getVaultPin() ?: "1234" }

    // Aura Pulse Premium Activation Dialog
    var showPremiumDialog by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(aura.background)
            .testTag("system_settings_screen")
    ) {
        AuraAtmosphericLayer()

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onBack()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(aura.iconButtonBg)
                        .testTag("system_settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = if (isArabic) "رجوع" else "Back",
                        tint = aura.textPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (isArabic) "إعدادات النظام" else "System Settings",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = aura.textPrimary
                        )
                    )
                    Text(
                        text = if (isArabic) "مركز التحكم الكامل والنسخ الاحتياطي و Premium" else "Appearance, playback, backup & Aura Pulse Premium",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = aura.textSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 0. Multi-Theme Engine Card (Interactive -> Soon Sub-page)
                item(key = "multi_theme_engine_item") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                Brush.horizontalGradient(
                                    listOf(aura.accentViolet.copy(alpha = 0.55f), aura.accentCyan.copy(alpha = 0.55f))
                                ),
                                RoundedCornerShape(22.dp)
                            )
                            .clip(RoundedCornerShape(22.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                isMultiThemeEngineOpen = true
                            }
                            .testTag("multi_theme_engine_entry"),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = aura.surfaceCard)
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
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(aura.accentViolet.copy(alpha = 0.22f), aura.accentCyanSoft)
                                            )
                                        )
                                        .border(1.dp, aura.accentViolet.copy(alpha = 0.45f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = "Multi-Theme Engine",
                                        tint = aura.accentViolet,
                                        modifier = Modifier.size(21.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Multi-Theme Engine",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = aura.textPrimary,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp
                                        )
                                    )
                                    Text(
                                        text = if (isArabic) "محرك الثيمات المتعددة وتخصيص الواجهة" else "Dynamic visual themes & engine presets",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = aura.textSecondary,
                                            fontSize = 11.5.sp
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = aura.accentViolet.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, aura.accentViolet.copy(alpha = 0.45f))
                            ) {
                                Text(
                                    text = if (isArabic) "قريباً" else "Soon",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = aura.accentViolet,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // 1. Standard Appearance, Media, Playback & Backup Card
                item(key = "core_system_settings") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                Brush.horizontalGradient(listOf(aura.accentCyan.copy(alpha = 0.5f), aura.accentViolet.copy(alpha = 0.3f))),
                                RoundedCornerShape(22.dp)
                            ),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = aura.surfaceCard)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(aura.accentCyanSoft),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Settings, null, tint = aura.accentCyan, modifier = Modifier.size(19.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isArabic) "الإعدادات الأساسية" else "Core System Settings",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = aura.textPrimary,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // RGB Accent Picker
                            Text(
                                text = if (isArabic) "لون النيون الرئيسي (RGB Accent)" else "RGB ACCENT COLOR",
                                style = MaterialTheme.typography.labelSmall.copy(color = aura.accentCyan, fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val rgbOptions = listOf(
                                    Pair(0xFF00FFCCL, Color(0xFF00FFCC)),
                                    Pair(0xFFFF007FL, Color(0xFFFF007F)),
                                    Pair(0xFFFF9900L, Color(0xFFFF9900)),
                                    Pair(0xFF00E676L, Color(0xFF00E676))
                                )
                                rgbOptions.forEach { (colorVal, color) ->
                                    val isSelected = currentRgbAccent == colorVal
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = color.copy(alpha = if (isSelected) 0.35f else 0.12f),
                                        border = BorderStroke(
                                            if (isSelected) 2.dp else 1.dp,
                                            if (isSelected) color else color.copy(alpha = 0.3f)
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp)
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

                            // OLED Black Mode
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isArabic) "وضع السواد المطلق (OLED Black)" else "OLED Deep Black Mode",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = aura.textPrimary, fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = if (isArabic) "خلفيات سوداء نقية لتوفير طاقة شاشات OLED" else "True #000000 pixel shutoff for battery savings",
                                        style = MaterialTheme.typography.bodySmall.copy(color = aura.textSecondary, fontSize = 11.sp)
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
                                        checkedTrackColor = aura.accentCyan,
                                        checkedThumbColor = Color.White,
                                        uncheckedTrackColor = aura.progressTrack
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Full Screen Mode (Hide Top Status Bar)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        val next = !isFullScreenMode
                                        streakViewModel.setFullScreenModeEnabled(next)
                                        feedbackMessage = if (next) {
                                            if (isArabic) "تم تفعيل وضع ملء الشاشة وإخفاء الشريط العلوي 📱" else "Full Screen enabled — top status bar hidden 📱"
                                        } else {
                                            if (isArabic) "تم إظهار الشريط العلوي للهاتف" else "Top status bar restored"
                                        }
                                    }
                                    .padding(vertical = 2.dp)
                                    .testTag("full_screen_mode_row"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fullscreen,
                                        contentDescription = null,
                                        tint = if (isFullScreenMode) aura.accentCyan else aura.textSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = if (isArabic) "وضع ملء الشاشة (Full Screen)" else "Full Screen Mode",
                                            style = MaterialTheme.typography.bodyMedium.copy(color = aura.textPrimary, fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = if (isArabic) "إخفاء الشريط العلوي للهاتف (الساعة والبطارية)" else "Hide the phone's top status bar",
                                            style = MaterialTheme.typography.bodySmall.copy(color = aura.textSecondary, fontSize = 11.sp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Switch(
                                    checked = isFullScreenMode,
                                    onCheckedChange = { enabled ->
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        streakViewModel.setFullScreenModeEnabled(enabled)
                                        feedbackMessage = if (enabled) {
                                            if (isArabic) "تم تفعيل وضع ملء الشاشة وإخفاء الشريط العلوي 📱" else "Full Screen enabled — top status bar hidden 📱"
                                        } else {
                                            if (isArabic) "تم إظهار الشريط العلوي للهاتف" else "Top status bar restored"
                                        }
                                    },
                                    modifier = Modifier.testTag("full_screen_mode_switch"),
                                    colors = SwitchDefaults.colors(
                                        checkedTrackColor = aura.accentCyan,
                                        checkedThumbColor = Color.White,
                                        uncheckedTrackColor = aura.progressTrack
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Cache Cleaner
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isArabic) "تنظيف الذاكرة المؤقتة (Cache Cleaner)" else "Thumbnail Cache Cleaner",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = aura.textPrimary, fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = if (isArabic) "حذف الصور المصغرة المؤقتة واسترجاع المساحة" else "Clear cached preview thumbnails (free 48MB)",
                                        style = MaterialTheme.typography.bodySmall.copy(color = aura.textSecondary, fontSize = 11.sp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = aura.accentCyanSoft,
                                    border = BorderStroke(1.dp, aura.accentCyan.copy(alpha = 0.5f)),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            val freed = streakViewModel.clearThumbnailCache()
                                            feedbackMessage = if (isArabic) "تم تنظيف الذاكرة المؤقتة وحذف ${freed}MB بنجاح 🧹" else "Cleared ${freed}MB of thumbnail cache 🧹"
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.CleaningServices, null, tint = aura.accentCyan, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isArabic) "تنظيف" else "Clean",
                                            style = MaterialTheme.typography.labelSmall.copy(color = aura.accentCyan, fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Auto-Resume
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isArabic) "الاستئناف التلقائي (Auto-Resume)" else "Auto-Resume Playback",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = aura.textPrimary, fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = if (isArabic) "استكمال الفيديو من آخر نقطة توقف تلقائياً" else "Pick up right where you left off",
                                        style = MaterialTheme.typography.bodySmall.copy(color = aura.textSecondary, fontSize = 11.sp)
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
                                        checkedTrackColor = aura.accentCyan,
                                        checkedThumbColor = Color.White,
                                        uncheckedTrackColor = aura.progressTrack
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Default Speed
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isArabic) "سرعة التشغيل الافتراضية" else "Default Playback Speed",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = aura.textPrimary, fontWeight = FontWeight.Medium)
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf(1.0f, 1.25f, 1.5f).forEach { spd ->
                                        val isSpdSelected = defaultPlaybackSpeed == spd
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSpdSelected) aura.accentCyan else aura.unselectedChipBg,
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
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (isSpdSelected) Color.White else aura.textPrimary,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Offline JSON Backup & Restore
                            Text(
                                text = if (isArabic) "النسخ الاحتياطي المحلي (JSON Backup & Restore)" else "OFFLINE LOCAL BACKUP & RESTORE",
                                style = MaterialTheme.typography.labelSmall.copy(color = aura.accentCyan, fontWeight = FontWeight.Bold)
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
                                            streakViewModel.exportPreferencesToJson()
                                            feedbackMessage = if (isArabic) "تم إنشاء نسخة JSON الاحتياطية بنجاح 💾" else "Exported local JSON backup 💾"
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    color = aura.accentCyanSoft,
                                    border = BorderStroke(1.dp, aura.accentCyan.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = if (isArabic) "تصدير JSON 📤" else "Export JSON 📤",
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = aura.accentCyan,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
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
                                            feedbackMessage = if (restored) {
                                                if (isArabic) "تمت استعادة البيانات بنجاح 📥" else "Preferences restored successfully 📥"
                                            } else {
                                                if (isArabic) "فشلت استعادة البيانات" else "Restore failed"
                                            }
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    color = aura.accentFlameSoft,
                                    border = BorderStroke(1.dp, aura.accentFlame.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = if (isArabic) "استعادة JSON 📥" else "Restore JSON 📥",
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = aura.accentFlame,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Moved Locked Settings (Activated by Aura Pulse Premium)
                item(key = "premium_gated_settings_card") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                if (isPremiumActivated) aura.accentGold.copy(alpha = 0.45f) else aura.borderSubtle,
                                RoundedCornerShape(22.dp)
                            ),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = aura.surfaceCard)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isArabic) "إعدادات متقدمة (Aura Pulse Premium)" else "Premium System Controls",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = aura.accentGold,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isPremiumActivated) aura.accentEmeraldSoft else aura.accentGoldBg
                                ) {
                                    Text(
                                        text = if (isPremiumActivated) {
                                            if (isArabic) "مفتوح 🔓" else "UNLOCKED 🔓"
                                        } else {
                                            if (isArabic) "مقفل 🔒" else "LOCKED 🔒"
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isPremiumActivated) aura.accentEmerald else aura.accentGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }

                            // 3A. Theme Mode Switcher
                            PremiumLockedSettingBlock(
                                title = if (isArabic) "مبدل المظهر (ليلي / نهاري / تلقائي)" else "Theme Mode Switcher",
                                icon = Icons.Default.DarkMode,
                                isUnlocked = isPremiumActivated,
                                isArabic = isArabic,
                                onLockedClick = { showPremiumDialog = true }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    ThemeModeChip(
                                        icon = Icons.Default.DarkMode,
                                        label = if (isArabic) "ليلي" else "Dark",
                                        isSelected = currentThemeMode == ThemeMode.DARK,
                                        enabled = isPremiumActivated,
                                        onClick = {
                                            if (isPremiumActivated) {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                onThemeModeChange(ThemeMode.DARK)
                                            } else {
                                                showPremiumDialog = true
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                    ThemeModeChip(
                                        icon = Icons.Default.LightMode,
                                        label = if (isArabic) "نهاري" else "Light",
                                        isSelected = currentThemeMode == ThemeMode.LIGHT,
                                        enabled = isPremiumActivated,
                                        onClick = {
                                            if (isPremiumActivated) {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                onThemeModeChange(ThemeMode.LIGHT)
                                            } else {
                                                showPremiumDialog = true
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                    ThemeModeChip(
                                        icon = Icons.Default.BrightnessAuto,
                                        label = if (isArabic) "تلقائي" else "System",
                                        isSelected = currentThemeMode == ThemeMode.SYSTEM,
                                        enabled = isPremiumActivated,
                                        onClick = {
                                            if (isPremiumActivated) {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                onThemeModeChange(ThemeMode.SYSTEM)
                                            } else {
                                                showPremiumDialog = true
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            // 3B. Manual Language Switcher
                            PremiumLockedSettingBlock(
                                title = if (isArabic) "لغة التطبيق (العربية / English)" else "App Language (Arabic / English)",
                                icon = Icons.Default.Language,
                                isUnlocked = isPremiumActivated,
                                isArabic = isArabic,
                                onLockedClick = { showPremiumDialog = true }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (currentLanguage == AppLanguage.ARABIC) aura.accentCyanSoft else aura.unselectedChipBg,
                                        border = BorderStroke(1.dp, if (currentLanguage == AppLanguage.ARABIC) aura.accentCyan else aura.borderSubtle),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(42.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable {
                                                if (isPremiumActivated) {
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    onLanguageChange(AppLanguage.ARABIC)
                                                } else {
                                                    showPremiumDialog = true
                                                }
                                            }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("العربية", color = if (currentLanguage == AppLanguage.ARABIC) aura.accentCyan else aura.textPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (currentLanguage == AppLanguage.ENGLISH) aura.accentCyanSoft else aura.unselectedChipBg,
                                        border = BorderStroke(1.dp, if (currentLanguage == AppLanguage.ENGLISH) aura.accentCyan else aura.borderSubtle),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(42.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable {
                                                if (isPremiumActivated) {
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    onLanguageChange(AppLanguage.ENGLISH)
                                                } else {
                                                    showPremiumDialog = true
                                                }
                                            }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("English", color = if (currentLanguage == AppLanguage.ENGLISH) aura.accentCyan else aura.textPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }

                            // 3C. App Lock & Hidden Secret Media Vault
                            PremiumLockedSettingBlock(
                                title = if (isArabic) "قفل التطبيق وخزنة الوسائط السرية (PIN)" else "App Lock & Secret Media Vault",
                                icon = if (isVaultUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                isUnlocked = isPremiumActivated,
                                isArabic = isArabic,
                                onLockedClick = { showPremiumDialog = true }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (isVaultUnlocked) {
                                            if (isArabic) "الخزنة مفتوحة ومحمية محلياً ✓" else "Vault active & unlocked ✓"
                                        } else {
                                            if (isArabic) "محمية برمز PIN (الافتراضي: 1234)" else "Protected by PIN (Default: 1234)"
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isVaultUnlocked) aura.accentEmerald else aura.textSecondary,
                                            fontSize = 11.5.sp
                                        )
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = aura.accentCyanSoft,
                                        border = BorderStroke(1.dp, aura.accentCyan.copy(alpha = 0.6f)),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable {
                                                if (!isPremiumActivated) {
                                                    showPremiumDialog = true
                                                } else if (isVaultUnlocked) {
                                                    isVaultUnlocked = false
                                                } else {
                                                    vaultPinInput = ""
                                                    vaultPinError = null
                                                    showVaultAuthDialog = true
                                                }
                                            }
                                    ) {
                                        Text(
                                            text = if (isVaultUnlocked) {
                                                if (isArabic) "قفل 🔒" else "Lock 🔒"
                                            } else {
                                                if (isArabic) "فتح الخزنة 🔓" else "Unlock 🔓"
                                            },
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = aura.accentCyan,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }

                            // 3D. Exclusive 6 Glass Themes Pack
                            PremiumLockedSettingBlock(
                                title = if (isArabic) "حزمة الثيمات الزجاجية الحصرية (6 ألوان)" else "Exclusive Glass Themes Pack",
                                icon = Icons.Default.Palette,
                                isUnlocked = isPremiumActivated,
                                isArabic = isArabic,
                                onLockedClick = { showPremiumDialog = true }
                            ) {
                                val themePresets = listOf(
                                    PulseCyan,
                                    Color(0xFFFFB74D),
                                    Color(0xFF10B981),
                                    PulseViolet,
                                    Color(0xFFFF5722),
                                    Color(0xFF1E293B)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    themePresets.forEachIndexed { i, color ->
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                                .border(
                                                    if (selectedCustomThemeIndex == i) 2.dp else 1.dp,
                                                    if (selectedCustomThemeIndex == i) Color.White else Color(0x33FFFFFF),
                                                    CircleShape
                                                )
                                                .clickable {
                                                    if (isPremiumActivated) {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        selectedCustomThemeIndex = i
                                                        streakViewModel.saveSelectedThemePresetIndex(i)
                                                    } else {
                                                        showPremiumDialog = true
                                                    }
                                                }
                                        )
                                    }
                                }
                            }

                            // 3E..3I Premium Toggle Switches
                            PremiumLockedToggleRow(
                                title = if (isArabic) "مرشح حماية العين الليلي" else "Visual Eye Protection Scrim",
                                subtitle = if (isArabic) "تعتيم مريح للعين أثناء المشاهدة في الظلام" else "Softer ambient scrim for dark room viewing",
                                icon = Icons.Default.RemoveRedEye,
                                checked = eyeProtectionEnabled,
                                isUnlocked = isPremiumActivated,
                                isArabic = isArabic,
                                onCheckedChange = {
                                    if (isPremiumActivated) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        eyeProtectionEnabled = it
                                        streakViewModel.setEyeProtectionEnabled(it)
                                    } else {
                                        showPremiumDialog = true
                                    }
                                }
                            )

                            PremiumLockedToggleRow(
                                title = if (isArabic) "مشاركة الوسائط السريعة (Wi-Fi Direct)" else "Offline Media Sharing (Wi-Fi Direct)",
                                subtitle = if (isArabic) "مشاركة ونقل الفيديوهات محلياً بدون إنترنت" else "High-speed local P2P video transfer",
                                icon = Icons.Default.Share,
                                checked = mediaSharingEnabled,
                                isUnlocked = isPremiumActivated,
                                isArabic = isArabic,
                                onCheckedChange = {
                                    if (isPremiumActivated) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        mediaSharingEnabled = it
                                        streakViewModel.setWifiDirectSharingEnabled(it)
                                    } else {
                                        showPremiumDialog = true
                                    }
                                }
                            )

                            PremiumLockedToggleRow(
                                title = if (isArabic) "تنبيهات حماية السلسلة الذكية" else "Smart Streak Protection Reminders",
                                subtitle = if (isArabic) "إشعارات ذكية قبل انتهاء اليوم لحفظ الدروع" else "Daily offline reminders before streak expires",
                                icon = Icons.Default.Notifications,
                                checked = smartNotificationsEnabled,
                                isUnlocked = isPremiumActivated,
                                isArabic = isArabic,
                                onCheckedChange = {
                                    if (isPremiumActivated) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        smartNotificationsEnabled = it
                                        streakViewModel.setSmartNotificationsEnabled(it)
                                    } else {
                                        showPremiumDialog = true
                                    }
                                }
                            )

                            PremiumLockedToggleRow(
                                title = if (isArabic) "التشغيل في الخلفية (Background Play)" else "Background Play Engine",
                                subtitle = if (isArabic) "استمرار الصوت عند إغلاق الشاشة" else "Keep audio running when screen locks",
                                icon = Icons.Default.Headset,
                                checked = backgroundPlayEnabled,
                                isUnlocked = isPremiumActivated,
                                isArabic = isArabic,
                                onCheckedChange = {
                                    if (isPremiumActivated) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        backgroundPlayEnabled = it
                                        streakViewModel.setBackgroundPlayEnabled(it)
                                    } else {
                                        showPremiumDialog = true
                                    }
                                }
                            )

                            PremiumLockedToggleRow(
                                title = if (isArabic) "نافذة المشغل العائم (PiP Mode)" else "Floating Pop-up Player (PiP)",
                                subtitle = if (isArabic) "تشغيل الفيديو في نافذة مصغرة فوق التطبيقات" else "Watch in a resizable floating window",
                                icon = Icons.Default.PictureInPicture,
                                checked = pipModeEnabled,
                                isUnlocked = isPremiumActivated,
                                isArabic = isArabic,
                                onCheckedChange = {
                                    if (isPremiumActivated) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        pipModeEnabled = it
                                        streakViewModel.setPipModeEnabled(it)
                                    } else {
                                        showPremiumDialog = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Aura Pulse Premium Activation Dialog
        if (showPremiumDialog) {
            AlertDialog(
                onDismissRequest = { showPremiumDialog = false },
                containerColor = aura.dialogBg,
                icon = {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = aura.accentGold,
                        modifier = Modifier.size(34.dp)
                    )
                },
                title = {
                    Text(
                        text = "Aura Pulse Premium",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = aura.accentGold
                        )
                    )
                },
                text = {
                    Text(
                        text = if (isPremiumActivated) {
                            if (isArabic) "جميع ميزات Aura Pulse Premium وإعدادات النظام المتقدمة مفعّلة بنجاح! ✨" else "Aura Pulse Premium is active! All advanced controls are unlocked. ✨"
                        } else {
                            if (isArabic) {
                                "يتيح لك نظام Aura Pulse Premium فتح جميع الإعدادات المتقدمة (المظهر، اللغة، الخزنة السرية، التشغيل في الخلفية، والنافذة العائمة).\n\nاضغط تفعيل لفتح كافة الميزات الآن."
                            } else {
                                "Aura Pulse Premium unlocks Theme Mode, Language, PIN Vault, Background Play, PiP Mode, and Exclusive Themes.\n\nTap Activate to enable Premium features."
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = aura.textPrimary,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            val target = !isPremiumActivated
                            streakViewModel.setPremiumActivated(target)
                            showPremiumDialog = false
                            feedbackMessage = if (target) {
                                if (isArabic) "تم تفعيل Aura Pulse Premium وفتح جميع الإعدادات 👑" else "Aura Pulse Premium Activated 👑"
                            } else {
                                if (isArabic) "تم إيقاف وضع Aura Pulse Premium" else "Aura Pulse Premium Deactivated"
                            }
                        }
                    ) {
                        Text(
                            text = if (isPremiumActivated) {
                                if (isArabic) "إيقاف التفعيل" else "Deactivate"
                            } else {
                                if (isArabic) "تفعيل الآن 👑" else "Activate Now 👑"
                            },
                            color = if (isPremiumActivated) aura.accentFlame else aura.accentGold,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPremiumDialog = false }) {
                        Text(
                            text = if (isArabic) "إغلاق" else "Close",
                            color = aura.accentCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }

        // Hidden Vault PIN Authentication Dialog
        if (showVaultAuthDialog) {
            AlertDialog(
                onDismissRequest = { showVaultAuthDialog = false },
                containerColor = aura.dialogBg,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, null, tint = aura.accentCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "رمز PIN لخزنة الوسائط السرية" else "Secret Vault PIN",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = aura.textPrimary)
                        )
                    }
                },
                text = {
                    Column {
                        Text(
                            text = if (isArabic) "أدخل رمز PIN المكون من 4 أرقام لفتح الخزنة السرية (الافتراضي: 1234)" else "Enter your 4-digit PIN (Default: 1234)",
                            style = MaterialTheme.typography.bodySmall.copy(color = aura.textSecondary, fontSize = 12.sp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedTextField(
                            value = vaultPinInput,
                            onValueChange = {
                                if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                    vaultPinInput = it
                                    vaultPinError = null
                                }
                            },
                            singleLine = true,
                            placeholder = { Text("****") },
                            isError = vaultPinError != null,
                            supportingText = {
                                if (vaultPinError != null) {
                                    Text(vaultPinError!!, color = aura.accentDanger)
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = aura.accentCyan,
                                unfocusedBorderColor = aura.borderSubtle,
                                focusedTextColor = aura.textPrimary,
                                unfocusedTextColor = aura.textPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (vaultPinInput == vaultSavedPin) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                isVaultUnlocked = true
                                showVaultAuthDialog = false
                            } else {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                vaultPinError = if (isArabic) "رمز PIN غير صحيح" else "Incorrect PIN"
                            }
                        }
                    ) {
                        Text(text = if (isArabic) "تأكيد 🔓" else "Unlock 🔓", color = aura.accentCyan, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showVaultAuthDialog = false }) {
                        Text(text = if (isArabic) "إلغاء" else "Cancel", color = aura.textSecondary)
                    }
                }
            )
        }

        // Bottom Feedback Pill
        AnimatedVisibility(
            visible = feedbackMessage != null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = aura.dialogBg,
                border = BorderStroke(1.dp, aura.accentCyan),
                shadowElevation = 12.dp
            ) {
                Text(
                    text = feedbackMessage ?: "",
                    modifier = Modifier
                        .clickable { feedbackMessage = null }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = aura.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    )
                )
            }
        }

        // Multi-Theme Engine Soon Page
        if (isMultiThemeEngineOpen) {
            MultiThemeEngineSoonScreen(
                isArabic = isArabic,
                onBack = { isMultiThemeEngineOpen = false }
            )
        }
    }
}

@Composable
private fun MultiThemeEngineSoonScreen(
    isArabic: Boolean,
    onBack: () -> Unit
) {
    val aura = MaterialTheme.aura
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(aura.background)
            .clickable(enabled = false) {}
            .testTag("multi_theme_engine_screen")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                aura.accentViolet.copy(alpha = if (aura.isDark) 0.18f else 0.11f),
                                aura.accentCyan.copy(alpha = if (aura.isDark) 0.12f else 0.07f),
                                Color.Transparent
                            ),
                            radius = size.width * 0.85f
                        ),
                        center = Offset(size.width * 0.5f, size.height * 0.35f)
                    )
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .stableStatusBarsPadding()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onBack()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(aura.iconButtonBg)
                        .testTag("multi_theme_engine_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = if (isArabic) "رجوع" else "Back",
                        tint = aura.textPrimary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Multi-Theme Engine",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = aura.textPrimary,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        aura.accentViolet.copy(alpha = 0.22f),
                                        aura.accentCyanSoft
                                    )
                                )
                            )
                            .border(
                                1.5.dp,
                                Brush.linearGradient(listOf(aura.accentViolet, aura.accentCyan)),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Multi-Theme Engine",
                            tint = aura.accentViolet,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Text(
                        text = "Multi-Theme Engine",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = aura.textPrimary,
                            fontWeight = FontWeight.Black
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = aura.accentViolet.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, aura.accentViolet.copy(alpha = 0.45f))
                    ) {
                        Text(
                            text = if (isArabic) "قريباً" else "Soon",
                            modifier = Modifier
                                .padding(horizontal = 24.dp, vertical = 10.dp)
                                .testTag("multi_theme_soon_label"),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = aura.accentViolet,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumLockedSettingBlock(
    title: String,
    icon: ImageVector,
    isUnlocked: Boolean,
    isArabic: Boolean,
    onLockedClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val aura = MaterialTheme.aura
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(aura.surfaceSubtle)
            .clickable(enabled = !isUnlocked) { onLockedClick() }
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
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
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isUnlocked) aura.accentCyan else aura.textSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isUnlocked) aura.textPrimary else aura.textSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                )
            }
            if (!isUnlocked) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = aura.accentGoldBg
                ) {
                    Text(
                        text = if (isArabic) "👑 مقفل" else "👑 Premium",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = aura.accentGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp
                        )
                    )
                }
            }
        }
        content()
    }
}

@Composable
private fun PremiumLockedToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    isUnlocked: Boolean,
    isArabic: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val aura = MaterialTheme.aura
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(aura.surfaceSubtle)
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isUnlocked) aura.accentCyan else aura.textSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isUnlocked) aura.textPrimary else aura.textSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    )
                    if (!isUnlocked) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "👑",
                            fontSize = 11.sp
                        )
                    }
                }
                Text(
                    text = if (isUnlocked) subtitle else (if (isArabic) "يتطلب تفعيل Aura Pulse Premium" else "Requires Aura Pulse Premium"),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = aura.textSecondary,
                        fontSize = 10.5.sp
                    )
                )
            }
        }
        Switch(
            checked = checked && isUnlocked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = aura.accentCyan,
                checkedThumbColor = Color.White,
                uncheckedTrackColor = aura.progressTrack
            )
        )
    }
}

@Composable
private fun ThemeModeChip(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val aura = MaterialTheme.aura
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected && enabled) aura.accentCyanSoft else aura.unselectedChipBg,
        border = BorderStroke(
            1.dp,
            if (isSelected && enabled) aura.accentCyan else aura.borderSubtle
        ),
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected && enabled) aura.accentCyan else aura.textSecondary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected && enabled) aura.accentCyan else aura.textPrimary,
                    fontWeight = if (isSelected && enabled) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 11.sp
                )
            )
        }
    }
}
