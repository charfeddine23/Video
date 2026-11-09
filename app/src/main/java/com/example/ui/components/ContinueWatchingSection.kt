package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.LocalDeviceVideo
import com.example.data.model.MediaItem
import com.example.data.model.MediaRepository
import com.example.data.preferences.LastWatchedVideoInfo
import com.example.data.preferences.StreakPreferencesRepository
import java.util.Locale

/**
 * Production-ready "Continue Watching" (متابعة المشاهدة) Component for Video Pulse.
 * Features:
 * - Liquid Glass Dark Mode styling with frosted glass container & neon cyan (#00FFCC) glow border.
 * - Dynamic file extension hiding toggle support.
 * - Real-time progress bar with completion percentage and exact elapsed/remaining timestamps.
 * - Glowing circular Play/Resume quick action button.
 * - Graceful fallback to catalog continue-watching item or sleek empty state prompt.
 */
@Composable
fun ContinueWatchingSection(
    localVideos: List<LocalDeviceVideo>,
    onPlayMedia: (MediaItem) -> Unit,
    modifier: Modifier = Modifier,
    isArabic: Boolean = true
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val prefsRepo = remember { StreakPreferencesRepository.getInstance(context) }

    // Read stored playback history & file extension preference
    var lastWatched by remember { mutableStateOf(prefsRepo.getLastWatched()) }
    val hideFileExtensions = remember { prefsRepo.getHideFileExtensions() }
    var isDismissed by remember { mutableStateOf(false) }

    // Auto-refresh last watched playback state whenever screen displays or local videos change
    LaunchedEffect(Unit, localVideos.size) {
        lastWatched = prefsRepo.getLastWatched()
    }

    // Pulsing live play aura animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_aura")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )
    val buttonPulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "button_scale"
    )

    // Resolve active media item:
    // 1. From real device playback store if available
    // 2. Fallback to catalog continue watching item if no local video was played yet
    val resolvedItem: ResolvedContinueWatchingItem? = remember(lastWatched, localVideos.size, isDismissed) {
        if (isDismissed) return@remember null

        if (lastWatched != null && lastWatched!!.durationMs > 0L) {
            val lw = lastWatched!!
            val matchingLocal = localVideos.find { it.id == lw.id || it.uriString == lw.uriString }
            val mediaItem = matchingLocal?.toMediaItem() ?: MediaItem(
                id = lw.id,
                title = lw.title,
                titleArabic = lw.title,
                subtitleBadge = "OFFLINE MEDIA",
                episodesFreeBadge = "Resume",
                year = "Local",
                seasonsOrDuration = formatTimeMs(lw.durationMs),
                qualityTag = "1080p FHD",
                description = "Offline video resume playback.",
                posterRes = if (lw.thumbnailRes != 0) lw.thumbnailRes else R.drawable.img_hero_poster,
                backdropRes = if (lw.thumbnailRes != 0) lw.thumbnailRes else R.drawable.img_hero_poster,
                uriString = lw.uriString
            )

            ResolvedContinueWatchingItem(
                mediaItem = mediaItem,
                title = lw.title,
                durationMs = lw.durationMs,
                positionMs = lw.positionMs,
                progress = lw.progress,
                progressPercent = lw.progressPercent,
                remainingMs = lw.remainingMs,
                bitmap = matchingLocal?.bitmap,
                posterRes = if (lw.thumbnailRes != 0) lw.thumbnailRes else R.drawable.img_hero_poster,
                isLocal = true
            )
        } else {
            // Out of the box showcase: use continue watching catalog item
            val fallback = MediaRepository.continueWatchingList.firstOrNull()
            if (fallback != null) {
                val totalMs = 72 * 60 * 1000L // 72 mins simulated duration
                val posMs = (totalMs * fallback.progress).toLong()
                val remMs = totalMs - posMs
                ResolvedContinueWatchingItem(
                    mediaItem = fallback,
                    title = if (isArabic && fallback.titleArabic != null) fallback.titleArabic else fallback.title,
                    durationMs = totalMs,
                    positionMs = posMs,
                    progress = fallback.progress,
                    progressPercent = (fallback.progress * 100).toInt(),
                    remainingMs = remMs,
                    bitmap = null,
                    posterRes = fallback.posterRes,
                    isLocal = false
                )
            } else {
                null
            }
        }
    }

    AnimatedVisibility(
        visible = !isDismissed,
        enter = fadeIn(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        if (resolvedItem != null) {
            val item = resolvedItem
            val displayTitle = formatCleanTitle(item.title, hideFileExtensions)

            // Outer Liquid Glass Container with glowing neon cyan border
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .drawBehind {
                        // Soft ambient glowing background aura
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF00FFCC).copy(alpha = 0.12f * pulseAlpha),
                                    Color(0xFF0088FF).copy(alpha = 0.06f),
                                    Color.Transparent
                                ),
                                radius = size.maxDimension * 0.7f
                            ),
                            center = Offset(size.width * 0.35f, size.height * 0.5f)
                        )
                    }
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xCC0E1524))
                    .border(
                        border = BorderStroke(
                            width = 1.2.dp,
                            brush = Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF00FFCC).copy(alpha = 0.75f),
                                    Color(0xFF0088FF).copy(alpha = 0.45f),
                                    Color(0xFF00FFCC).copy(alpha = 0.25f)
                                )
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .testTag("continue_watching_container")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    // Header Row: Neat Title "متابعة المشاهدة" with Live Play Icon & Progress Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Pulsing live play badge
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x3300FFCC)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF00FFCC).copy(alpha = pulseAlpha + 0.15f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "متابعة المشاهدة" else "Continue Watching",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // Completion percent pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0x2600FFCC))
                                    .border(BorderStroke(0.8.dp, Color(0x6600FFCC)), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isArabic) "${item.progressPercent}% تم" else "${item.progressPercent}%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF00FFCC),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        // Dismiss button
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                isDismissed = true
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Horizontal Video Card Item
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onPlayMedia(item.mediaItem)
                            }
                            .padding(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Thumbnail Preview
                        Box(
                            modifier = Modifier
                                .width(108.dp)
                                .height(68.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF141C2E))
                                .border(BorderStroke(1.dp, Color(0x3300FFCC)), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (item.bitmap != null) {
                                Image(
                                    bitmap = item.bitmap.asImageBitmap(),
                                    contentDescription = item.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else if (item.posterRes != 0 && item.posterRes != R.drawable.img_hero_poster) {
                                Image(
                                    painter = painterResource(item.posterRes),
                                    contentDescription = item.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                SmartFallbackCover(
                                    title = item.title,
                                    showText = false,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // Dark gradient overlay on thumbnail
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color(0x99000000))
                                        )
                                    )
                            )

                            // Play watermark icon on thumbnail
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x99000000))
                                    .border(BorderStroke(1.dp, Color(0x66FFFFFF)), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            // Elapsed time pill at bottom corner
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xD9000000))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = formatTimeMs(item.positionMs),
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Middle Info Column: Title, Exact Time Remaining & Neon Progress Bar
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            Text(
                                text = displayTitle,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            // Timestamp: Elapsed / Duration & Remaining
                            Text(
                                text = if (isArabic) {
                                    "${formatTimeMs(item.positionMs)} / ${formatTimeMs(item.durationMs)} • متبقي ${formatTimeMs(item.remainingMs)}"
                                } else {
                                    "${formatTimeMs(item.positionMs)} / ${formatTimeMs(item.durationMs)} • ${formatTimeMs(item.remainingMs)} left"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.5.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Sleek Neon Cyan Progress Bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.5.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0x33FFFFFF))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(item.progress.coerceIn(0.02f, 1f))
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(Color(0xFF00FFCC), Color(0xFF0088FF))
                                            )
                                        )
                                        .drawBehind {
                                            drawRect(
                                                brush = Brush.horizontalGradient(
                                                    listOf(Color(0x8000FFCC), Color(0x400088FF))
                                                )
                                            )
                                        }
                                )
                            }
                        }

                        // Quick Action: Glowing circular Play/Resume Button
                        Box(
                            modifier = Modifier
                                .scale(buttonPulseScale)
                                .size(44.dp)
                                .drawBehind {
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            listOf(
                                                Color(0xFF00FFCC).copy(alpha = 0.5f),
                                                Color(0xFF0088FF).copy(alpha = 0.25f),
                                                Color.Transparent
                                            ),
                                            radius = size.width * 0.85f
                                        )
                                    )
                                }
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF00FFCC), Color(0xFF00B4D8))
                                    )
                                )
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onPlayMedia(item.mediaItem)
                                }
                                .testTag("continue_watching_resume_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play / Resume",
                                tint = Color(0xFF080F1D),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // Empty State: Subtle frosted prompt
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x660E1524))
                    .border(BorderStroke(0.8.dp, Color(0x1FFFFFFF)), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "لا يوجد مقطع قيد المشاهدة حالياً" else "No active watch history",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Resolved model combining local or catalog media info with playback timestamps
 */
data class ResolvedContinueWatchingItem(
    val mediaItem: MediaItem,
    val title: String,
    val durationMs: Long,
    val positionMs: Long,
    val progress: Float,
    val progressPercent: Int,
    val remainingMs: Long,
    val bitmap: Bitmap?,
    val posterRes: Int,
    val isLocal: Boolean
)

/**
 * Formats raw milliseconds to [HH:]MM:SS representation
 */
fun formatTimeMs(ms: Long): String {
    val totalSeconds = (ms / 1000L).coerceAtLeast(0L)
    val hours = totalSeconds / 3600L
    val minutes = (totalSeconds % 3600L) / 60L
    val seconds = totalSeconds % 60L
    return if (hours > 0) {
        String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }
}

/**
 * Strips video extensions (.mp4, .mkv, .avi, etc.) when hideExt is enabled
 */
fun formatCleanTitle(rawTitle: String, hideExt: Boolean): String {
    return if (hideExt) {
        rawTitle.replace(Regex("\\.(mp4|mkv|avi|mov|flv|webm|ts|m4v|3gp|wmv)$", RegexOption.IGNORE_CASE), "")
    } else {
        rawTitle
    }
}

private fun LocalDeviceVideo.toMediaItem(): MediaItem {
    return MediaItem(
        id = id,
        title = title,
        titleArabic = title,
        subtitleBadge = "OFFLINE MEDIA",
        episodesFreeBadge = resolutionText,
        year = "Local",
        seasonsOrDuration = durationText,
        maturityRating = "All",
        qualityTag = resolutionText,
        genresText = "Local Storage • $sizeText",
        description = "Offline video file ready for playback.",
        posterRes = if (thumbnailRes != 0) thumbnailRes else R.drawable.img_hero_poster,
        backdropRes = if (thumbnailRes != 0) thumbnailRes else R.drawable.img_hero_poster,
        uriString = uriString
    )
}
