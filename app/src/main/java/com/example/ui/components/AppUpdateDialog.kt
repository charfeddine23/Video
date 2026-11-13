package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.BuildConfig
import com.example.data.update.ApkDownloadState
import com.example.data.update.ApkInstallLaunchResult
import com.example.data.update.AppUpdateChecker
import com.example.data.update.AppUpdateInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

private enum class InAppUpdatePhase {
    PROMPT,
    DOWNLOADING,
    NEEDS_INSTALL_PERMISSION,
    READY_TO_INSTALL,
    ERROR
}

/**
 * Floating, compact modal App Update Dialog with 100% in-app APK download and direct
 * FileProvider installation (no external browser or external downloader involved).
 */
@Composable
fun AppUpdateDialog(
    updateInfo: AppUpdateInfo,
    isArabic: Boolean = true,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    var phase by remember { mutableStateOf(InAppUpdatePhase.PROMPT) }
    var progress by remember { mutableFloatStateOf(0f) }
    var bytesDownloaded by remember { mutableLongStateOf(0L) }
    var totalBytes by remember { mutableLongStateOf(updateInfo.apkSizeBytes) }
    var downloadedApkFile by remember { mutableStateOf<File?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var downloadJob by remember { mutableStateOf<Job?>(null) }

    val triggerApkInstall: (File) -> Unit = { file ->
        when (AppUpdateChecker.launchApkInstaller(context, file)) {
            ApkInstallLaunchResult.LAUNCHED -> {
                phase = InAppUpdatePhase.READY_TO_INSTALL
            }
            ApkInstallLaunchResult.NEEDS_INSTALL_PERMISSION -> {
                phase = InAppUpdatePhase.NEEDS_INSTALL_PERMISSION
            }
            ApkInstallLaunchResult.FILE_MISSING -> {
                errorMessage = if (isArabic) {
                    "تعذر العثور على ملف التحديث المحمّل. أعد المحاولة."
                } else {
                    "Downloaded APK file could not be found. Please retry."
                }
                phase = InAppUpdatePhase.ERROR
            }
            ApkInstallLaunchResult.FAILED -> {
                errorMessage = if (isArabic) {
                    "تعذر تشغيل مثبت النظام للتحديث."
                } else {
                    "Unable to launch system package installer."
                }
                phase = InAppUpdatePhase.ERROR
            }
        }
    }

    val startInAppDownload: () -> Unit = {
        if (updateInfo.apkDownloadUrl.isNullOrBlank()) {
            errorMessage = if (isArabic) {
                "ملف APK غير متوفر بعد في هذا الإصدار على GitHub."
            } else {
                "APK package is not attached to this GitHub release yet."
            }
            phase = InAppUpdatePhase.ERROR
        } else {
            downloadJob?.cancel()
            phase = InAppUpdatePhase.DOWNLOADING
            progress = 0f
            bytesDownloaded = 0L
            totalBytes = updateInfo.apkSizeBytes
            errorMessage = null

            downloadJob = scope.launch {
                AppUpdateChecker.downloadUpdateApk(context, updateInfo).collect { state ->
                    when (state) {
                        is ApkDownloadState.Downloading -> {
                            progress = state.progress
                            bytesDownloaded = state.bytesDownloaded
                            if (state.totalBytes > 0L) {
                                totalBytes = state.totalBytes
                            }
                        }
                        is ApkDownloadState.Completed -> {
                            downloadedApkFile = state.apkFile
                            progress = 1f
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            triggerApkInstall(state.apkFile)
                        }
                        is ApkDownloadState.Error -> {
                            errorMessage = state.message
                            phase = InAppUpdatePhase.ERROR
                        }
                    }
                }
            }
        }
    }

    // Auto-resume installation right when returning from Android's Install Unknown Apps setting
    DisposableEffect(lifecycleOwner, phase, downloadedApkFile) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && phase == InAppUpdatePhase.NEEDS_INSTALL_PERMISSION) {
                val file = downloadedApkFile
                if (file != null && AppUpdateChecker.canInstallPackages(context)) {
                    triggerApkInstall(file)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "update_rocket_anim")
    val rocketFloatY by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rocket_float_y"
    )
    val ringGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring_glow_alpha"
    )
    val flameScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(420, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_scale"
    )

    val crimsonPrimary = Color(0xFFE50914)
    val crimsonBright = Color(0xFFFF2438)
    val crimsonDeep = Color(0xFFB70710)

    val cancelAndDismiss: () -> Unit = {
        downloadJob?.cancel()
        downloadJob = null
        onDismiss()
    }

    Dialog(
        onDismissRequest = {
            if (phase != InAppUpdatePhase.DOWNLOADING) {
                cancelAndDismiss()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = phase != InAppUpdatePhase.DOWNLOADING,
            dismissOnClickOutside = phase != InAppUpdatePhase.DOWNLOADING,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 370.dp)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    crimsonBright.copy(alpha = 0.22f * ringGlowAlpha),
                                    Color.Transparent
                                ),
                                radius = size.width * 0.7f
                            ),
                            center = Offset(size.width * 0.5f, size.height * 0.22f)
                        )
                    }
                    .border(
                        width = 1.1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                crimsonBright.copy(alpha = 0.5f),
                                Color(0x28FFFFFF),
                                Color(0x14FFFFFF)
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .testTag("app_update_dialog_card"),
                shape = RoundedCornerShape(28.dp),
                color = Color(0xF5111218),
                shadowElevation = 24.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 20.dp)
                ) {
                    // Top Close (X) Button
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            cancelAndDismiss()
                        },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0x14FFFFFF))
                            .testTag("app_update_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = if (isArabic) "إغلاق" else "Close",
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Concentric Crimson Rings + Custom Animated Rocket / Status Badge
                        Box(
                            modifier = Modifier.size(116.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.matchParentSize()) {
                                val cx = size.width / 2f
                                val cy = size.height / 2f
                                val outerR = size.minDimension / 2f

                                drawCircle(
                                    color = crimsonPrimary.copy(alpha = 0.14f * ringGlowAlpha),
                                    radius = outerR,
                                    center = Offset(cx, cy)
                                )
                                drawCircle(
                                    color = Color(0xFF1F0C10),
                                    radius = outerR * 0.85f,
                                    center = Offset(cx, cy)
                                )
                                drawCircle(
                                    color = when (phase) {
                                        InAppUpdatePhase.READY_TO_INSTALL -> Color(0xFF10B981)
                                        InAppUpdatePhase.NEEDS_INSTALL_PERMISSION -> Color(0xFFFFB74D)
                                        else -> crimsonBright.copy(alpha = 0.88f)
                                    },
                                    radius = outerR * 0.74f,
                                    center = Offset(cx, cy),
                                    style = Stroke(width = 2.2.dp.toPx())
                                )
                                drawCircle(
                                    color = Color(0xFF160B0E),
                                    radius = outerR * 0.71f,
                                    center = Offset(cx, cy)
                                )

                                drawRect(
                                    color = crimsonBright.copy(alpha = 0.85f),
                                    topLeft = Offset(cx - outerR * 0.42f, cy - outerR * 0.25f),
                                    size = androidx.compose.ui.geometry.Size(3.5.dp.toPx(), 3.5.dp.toPx())
                                )
                                drawRect(
                                    color = Color(0xFFFFFFFF),
                                    topLeft = Offset(cx + outerR * 0.42f, cy - outerR * 0.24f),
                                    size = androidx.compose.ui.geometry.Size(3.5.dp.toPx(), 3.5.dp.toPx())
                                )
                                drawRect(
                                    color = Color(0xFFFFC107),
                                    topLeft = Offset(cx - outerR * 0.36f, cy + outerR * 0.24f),
                                    size = androidx.compose.ui.geometry.Size(3.5.dp.toPx(), 3.5.dp.toPx())
                                )
                                drawRect(
                                    color = crimsonBright.copy(alpha = 0.75f),
                                    topLeft = Offset(cx + outerR * 0.36f, cy + outerR * 0.22f),
                                    size = androidx.compose.ui.geometry.Size(3.dp.toPx(), 3.dp.toPx())
                                )
                            }

                            when (phase) {
                                InAppUpdatePhase.READY_TO_INSTALL -> {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF34D399),
                                        modifier = Modifier.size(44.dp)
                                    )
                                }
                                InAppUpdatePhase.NEEDS_INSTALL_PERMISSION -> {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB74D),
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                                InAppUpdatePhase.ERROR -> {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = crimsonBright,
                                        modifier = Modifier.size(42.dp)
                                    )
                                }
                                InAppUpdatePhase.PROMPT,
                                InAppUpdatePhase.DOWNLOADING -> {
                                    Canvas(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .offset(y = rocketFloatY.dp)
                                    ) {
                                        val w = size.width
                                        val h = size.height
                                        val cx = w / 2f

                                        val flameHeight = h * 0.22f * flameScale
                                        val outerFlame = Path().apply {
                                            moveTo(cx - w * 0.08f, h * 0.67f)
                                            lineTo(cx, h * 0.67f + flameHeight)
                                            lineTo(cx + w * 0.08f, h * 0.67f)
                                            close()
                                        }
                                        drawPath(outerFlame, color = Color(0xFFFF6D00))

                                        val innerFlame = Path().apply {
                                            moveTo(cx - w * 0.045f, h * 0.67f)
                                            lineTo(cx, h * 0.67f + flameHeight * 0.68f)
                                            lineTo(cx + w * 0.045f, h * 0.67f)
                                            close()
                                        }
                                        drawPath(innerFlame, color = Color(0xFFFFD54F))

                                        val leftFin = Path().apply {
                                            moveTo(cx - w * 0.13f, h * 0.44f)
                                            lineTo(cx - w * 0.29f, h * 0.62f)
                                            lineTo(cx - w * 0.12f, h * 0.60f)
                                            close()
                                        }
                                        drawPath(leftFin, color = Color(0xFFD32F2F))

                                        val rightFin = Path().apply {
                                            moveTo(cx + w * 0.13f, h * 0.44f)
                                            lineTo(cx + w * 0.29f, h * 0.62f)
                                            lineTo(cx + w * 0.12f, h * 0.60f)
                                            close()
                                        }
                                        drawPath(rightFin, color = Color(0xFFD32F2F))

                                        drawRect(
                                            color = Color(0xFF546E7A),
                                            topLeft = Offset(cx - w * 0.085f, h * 0.62f),
                                            size = androidx.compose.ui.geometry.Size(w * 0.17f, h * 0.06f)
                                        )

                                        val bodyPath = Path().apply {
                                            moveTo(cx, h * 0.08f)
                                            cubicTo(
                                                cx + w * 0.18f, h * 0.25f,
                                                cx + w * 0.17f, h * 0.50f,
                                                cx + w * 0.11f, h * 0.63f
                                            )
                                            lineTo(cx - w * 0.11f, h * 0.63f)
                                            cubicTo(
                                                cx - w * 0.17f, h * 0.50f,
                                                cx - w * 0.18f, h * 0.25f,
                                                cx, h * 0.08f
                                            )
                                            close()
                                        }
                                        drawPath(bodyPath, color = Color(0xFFF8FAFC))

                                        val noseCone = Path().apply {
                                            moveTo(cx, h * 0.08f)
                                            cubicTo(
                                                cx + w * 0.10f, h * 0.17f,
                                                cx + w * 0.12f, h * 0.24f,
                                                cx + w * 0.12f, h * 0.26f
                                            )
                                            lineTo(cx - w * 0.12f, h * 0.26f)
                                            cubicTo(
                                                cx - w * 0.12f, h * 0.24f,
                                                cx - w * 0.10f, h * 0.17f,
                                                cx, h * 0.08f
                                            )
                                            close()
                                        }
                                        drawPath(noseCone, color = Color(0xFFD32F2F))

                                        drawCircle(
                                            color = Color(0xFF37474F),
                                            radius = w * 0.10f,
                                            center = Offset(cx, h * 0.40f)
                                        )
                                        drawCircle(
                                            color = Color(0xFF29E3FF),
                                            radius = w * 0.072f,
                                            center = Offset(cx, h * 0.40f)
                                        )
                                        drawCircle(
                                            color = Color.White.copy(alpha = 0.65f),
                                            radius = w * 0.022f,
                                            center = Offset(cx - w * 0.022f, h * 0.375f)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Headline
                        Text(
                            text = when (phase) {
                                InAppUpdatePhase.PROMPT -> if (isArabic) "تحديث جديد متاح" else "New Update Available"
                                InAppUpdatePhase.DOWNLOADING -> if (isArabic) "جارٍ تحميل التحديث..." else "Downloading Update..."
                                InAppUpdatePhase.NEEDS_INSTALL_PERMISSION -> if (isArabic) "تفعيل التثبيت المباشر" else "Enable Direct Install"
                                InAppUpdatePhase.READY_TO_INSTALL -> if (isArabic) "التحديث جاهز للتثبيت" else "Update Ready to Install"
                                InAppUpdatePhase.ERROR -> if (isArabic) "تعذر إكمال التحديث" else "Update Failed"
                            },
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 21.sp
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Subtitle Label
                        Text(
                            text = when (phase) {
                                InAppUpdatePhase.READY_TO_INSTALL -> if (isArabic) "تحميل داخلي مكتمل ✓" else "In-App Download Complete ✓"
                                InAppUpdatePhase.NEEDS_INSTALL_PERMISSION -> if (isArabic) "صلاحية واحدة مطلوبة" else "One-Time Permission Needed"
                                InAppUpdatePhase.DOWNLOADING -> if (isArabic) "التنزيل داخل التطبيق مباشرة" else "Downloading directly inside app"
                                else -> if (isArabic) "تحديث جديد" else "New Release"
                            },
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = when (phase) {
                                    InAppUpdatePhase.READY_TO_INSTALL -> Color(0xFF34D399)
                                    InAppUpdatePhase.NEEDS_INSTALL_PERMISSION -> Color(0xFFFFB74D)
                                    else -> crimsonBright
                                },
                                fontSize = 14.sp
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Version Transition Pill (Current -> New) + Size
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0x18FFFFFF),
                            border = BorderStroke(0.8.dp, Color(0x28FFFFFF))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "v${BuildConfig.VERSION_NAME}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF94A3B8),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
                                    )
                                )
                                Text(
                                    text = "→",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = crimsonBright,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                )
                                Text(
                                    text = "v${updateInfo.versionName}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.5.sp
                                    )
                                )
                                val sizeStr = AppUpdateChecker.formatBytes(updateInfo.apkSizeBytes)
                                if (sizeStr.isNotEmpty()) {
                                    Text(
                                        text = "• $sizeStr",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF94A3B8),
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        when (phase) {
                            InAppUpdatePhase.PROMPT -> {
                                val notesText = updateInfo.releaseNotes
                                    .takeIf { it.isNotBlank() }
                                    ?: if (isArabic) {
                                        "تحديث جديد متاح يتضمن تحسينات في الأداء وإصلاحات للمشغل."
                                    } else {
                                        "A new version is available with performance and playback improvements."
                                    }

                                Text(
                                    text = notesText,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF9CA3AF),
                                        fontSize = 13.sp,
                                        lineHeight = 19.sp
                                    ),
                                    textAlign = TextAlign.Center,
                                    maxLines = 4,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )

                                Spacer(modifier = Modifier.height(22.dp))

                                PrimaryCrimsonPillButton(
                                    label = if (isArabic) "تحديث" else "Update",
                                    crimsonBright = crimsonBright,
                                    crimsonPrimary = crimsonPrimary,
                                    crimsonDeep = crimsonDeep,
                                    testTag = "app_update_confirm_button",
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        startInAppDownload()
                                    }
                                )
                            }

                            InAppUpdatePhase.DOWNLOADING -> {
                                val downloadedFormatted = AppUpdateChecker.formatBytes(bytesDownloaded).ifEmpty { "0 KB" }
                                val totalFormatted = AppUpdateChecker.formatBytes(totalBytes)
                                val percentText = if (progress >= 0f) "${(progress * 100).toInt()}%" else ""

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (totalFormatted.isNotEmpty()) "$downloadedFormatted / $totalFormatted" else downloadedFormatted,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = Color(0xFFCBD5E1),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp
                                        )
                                    )
                                    if (percentText.isNotEmpty()) {
                                        Text(
                                            text = percentText,
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                color = crimsonBright,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 13.sp
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                if (progress >= 0f) {
                                    LinearProgressIndicator(
                                        progress = { progress.coerceIn(0f, 1f) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .testTag("app_update_download_progress"),
                                        color = crimsonBright,
                                        trackColor = Color(0x26FFFFFF)
                                    )
                                } else {
                                    LinearProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .testTag("app_update_download_progress"),
                                        color = crimsonBright,
                                        trackColor = Color(0x26FFFFFF)
                                    )
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color(0x18FFFFFF),
                                    border = BorderStroke(1.dp, Color(0x2EFFFFFF)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(50))
                                        .clickable {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            cancelAndDismiss()
                                        }
                                        .testTag("app_update_cancel_download_button")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = if (isArabic) "إلغاء التنزيل" else "Cancel Download",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                color = Color(0xFFCBD5E1),
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }

                            InAppUpdatePhase.NEEDS_INSTALL_PERMISSION -> {
                                Text(
                                    text = if (isArabic) {
                                        "تم تنزيل التحديث داخل التطبيق بنجاح! فعّل خيار \"السماح من هذا المصدر\" لإتمام التثبيت مباشرة."
                                    } else {
                                        "Update downloaded in-app! Allow \"Install unknown apps\" once to finish installing directly."
                                    },
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 12.5.sp,
                                        lineHeight = 18.sp
                                    ),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                PrimaryCrimsonPillButton(
                                    label = if (isArabic) "منح الصلاحية والتثبيت" else "Allow & Install",
                                    crimsonBright = Color(0xFFFFB74D),
                                    crimsonPrimary = Color(0xFFF59E0B),
                                    crimsonDeep = Color(0xFFD97706),
                                    testTag = "app_update_grant_install_button",
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        AppUpdateChecker.openInstallPermissionSettings(context)
                                    }
                                )
                            }

                            InAppUpdatePhase.READY_TO_INSTALL -> {
                                Text(
                                    text = if (isArabic) {
                                        "تم التحقق من حزمة التحديث وتجهيزها للتثبيت المباشر فوق الإصدار الحالي دون فقدان بياناتك."
                                    } else {
                                        "Update package verified and ready to install over the existing app without data loss."
                                    },
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 12.5.sp,
                                        lineHeight = 18.sp
                                    ),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                PrimaryCrimsonPillButton(
                                    label = if (isArabic) "تثبيت التحديث الآن" else "Install Update Now",
                                    crimsonBright = Color(0xFF34D399),
                                    crimsonPrimary = Color(0xFF10B981),
                                    crimsonDeep = Color(0xFF059669),
                                    testTag = "app_update_install_now_button",
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        downloadedApkFile?.let { triggerApkInstall(it) }
                                    }
                                )
                            }

                            InAppUpdatePhase.ERROR -> {
                                Text(
                                    text = errorMessage ?: (if (isArabic) "حدث خطأ غير متوقع أثناء التنزيل." else "Unexpected error during download."),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFFFCA5A5),
                                        fontSize = 12.5.sp,
                                        lineHeight = 18.sp
                                    ),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                PrimaryCrimsonPillButton(
                                    label = if (isArabic) "إعادة المحاولة" else "Retry",
                                    crimsonBright = crimsonBright,
                                    crimsonPrimary = crimsonPrimary,
                                    crimsonDeep = crimsonDeep,
                                    testTag = "app_update_retry_button",
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        startInAppDownload()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrimaryCrimsonPillButton(
    label: String,
    crimsonBright: Color,
    crimsonPrimary: Color,
    crimsonDeep: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.Transparent,
        shadowElevation = 10.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(50))
            .background(
                Brush.horizontalGradient(
                    listOf(crimsonBright, crimsonPrimary, crimsonDeep)
                )
            )
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.5.sp
                )
            )
        }
    }
}
