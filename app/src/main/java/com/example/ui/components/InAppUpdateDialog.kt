package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.BuildConfig
import com.example.ui.theme.Charcoal800
import com.example.ui.theme.Charcoal850
import com.example.ui.theme.Charcoal950
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PulseAmber
import com.example.ui.theme.PulseCyan
import com.example.ui.theme.PulseViolet
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

data class AppUpdateInfo(
    val latestVersionName: String = "1.0.3",
    val latestVersionCode: Int = 4,
    val releaseTitle: String = "Video Pulse Spring Update",
    val releaseTitleAr: String = "تحديث ربيع Video Pulse الجديد",
    val releaseNotesEn: List<String> = listOf(
        "120 FPS Ultra Motion video stability enhancements",
        "Full Arabic & English native RTL/LTR interface support",
        "Dynamic Dark/Light/System theme switcher",
        "Enhanced offline downloads and memory management"
    ),
    val releaseNotesAr: List<String> = listOf(
        "تحسينات فائقة في سلاسة تشغيل الفيديو بمعدل 120 إطاراً في الثانية",
        "دعم كامل لتخطيط الواجهة باللغتين العربية والإنجليزية (RTL/LTR)",
        "إمكانية التبديل بين الوضع الليلي، النهاري، وحسب إعدادات النظام",
        "تسريع تحميل الحلقات والمشاهدة دون اتصال بالإنترنت"
    ),
    val apkSize: String = "18.4 MB"
)

enum class UpdateDialogStep {
    PROMPT,
    DOWNLOADING,
    READY_TO_INSTALL
}

@Composable
fun InAppUpdateDialog(
    isOpen: Boolean,
    updateInfo: AppUpdateInfo = remember { AppUpdateInfo() },
    isArabic: Boolean = false,
    onDismiss: () -> Unit,
    onInstallComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    val haptic = LocalHapticFeedback.current
    var step by remember { mutableStateOf(UpdateDialogStep.PROMPT) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }

    // Simulated progress when downloading
    LaunchedEffect(step) {
        if (step == UpdateDialogStep.DOWNLOADING) {
            downloadProgress = 0f
            while (downloadProgress < 1f) {
                delay(60)
                downloadProgress = (downloadProgress + 0.04f).coerceAtMost(1f)
            }
            delay(300)
            step = UpdateDialogStep.READY_TO_INSTALL
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Dialog(onDismissRequest = {
        if (step != UpdateDialogStep.DOWNLOADING) {
            onDismiss()
        }
    }) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(
                    1.dp,
                    Brush.verticalGradient(listOf(PulseCyan, PulseViolet.copy(alpha = 0.5f))),
                    RoundedCornerShape(24.dp)
                )
                .testTag("in_app_update_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (step) {
                    UpdateDialogStep.PROMPT -> {
                        // Header with Icon & Close
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0x3300F2FE),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PulseCyan.copy(alpha = 0.6f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NewReleases,
                                        contentDescription = null,
                                        tint = PulseCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isArabic) "تحديث متاح" else "New Update Available",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = PulseCyan,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onDismiss()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Rocket Icon with Glow
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .drawBehind {
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                PulseCyan.copy(alpha = 0.4f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                }
                                .clip(CircleShape)
                                .background(Color(0xFF16253A))
                                .border(1.dp, PulseCyan.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.RocketLaunch,
                                contentDescription = null,
                                tint = PulseCyan,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isArabic) updateInfo.releaseTitleAr else updateInfo.releaseTitle,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 18.sp
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Version badge comparison
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "v${BuildConfig.VERSION_NAME}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Text(
                                text = " ➔ ",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = PulseCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "v${updateInfo.latestVersionName} (${updateInfo.apkSize})",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = PulseCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Release Notes
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (isArabic) "ما الجديد في هذا الإصدار:" else "What's new in this release:",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 11.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                val notes = if (isArabic) updateInfo.releaseNotesAr else updateInfo.releaseNotesEn
                                notes.forEach { note ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text("• ", color = PulseCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = note,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 11.sp,
                                                lineHeight = 16.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Actions: Update Now & Later
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.Transparent,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onDismiss()
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = if (isArabic) "لاحقاً" else "Later",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    step = UpdateDialogStep.DOWNLOADING
                                },
                                modifier = Modifier
                                    .weight(1.4f)
                                    .height(46.dp)
                                    .testTag("in_app_update_start_button"),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDownload,
                                    contentDescription = null,
                                    tint = TextDark,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isArabic) "تحديث الآن" else "Update Now",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextDark
                                    )
                                )
                            }
                        }
                    }

                    UpdateDialogStep.DOWNLOADING -> {
                        // Downloading Progress Step
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0x2200F2FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SystemUpdate,
                                contentDescription = null,
                                tint = PulseCyan,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (isArabic) "جارٍ تنزيل التحديث..." else "Downloading Update...",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        val percent = (downloadProgress * 100).toInt()
                        Text(
                            text = "$percent% (${(downloadProgress * 18.4f * 10).toInt() / 10f} MB / ${updateInfo.apkSize})",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = PulseCyan,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        LinearProgressIndicator(
                            progress = { downloadProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = PulseCyan,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (isArabic) "يرجى الانتظار حتى اكتمال التنزيل" else "Please keep the app open while downloading",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }

                    UpdateDialogStep.READY_TO_INSTALL -> {
                        // Complete Step
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0x2200E676)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DownloadDone,
                                contentDescription = null,
                                tint = Color(0xFF00E676),
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (isArabic) "تم تنزيل التحديث بنجاح!" else "Update Downloaded Successfully!",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isArabic) "التحديث جاهز للتثبيت فوق النسخة الحالية دون فقدان أي بيانات."
                            else "Ready to install directly over existing build without data loss.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onInstallComplete()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("in_app_update_install_button"),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = TextDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "تثبيت وإعادة التشغيل" else "Install & Restart",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
