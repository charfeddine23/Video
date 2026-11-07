package com.example.ui.components

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.util.Rational
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem as ExoMediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.data.model.MediaItem
import com.example.ui.theme.Charcoal950
import com.example.ui.theme.GlassBorder
import com.example.ui.viewmodel.StreakViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

// Design Tokens for Liquid Glass Dark Mode
val NeonCyan = Color(0xFF00FFCC)
val NeonBlue = Color(0xFF0088FF)
val GlassContainerBg = Color(0x99101622)
val GlassBorderStroke = Color(0x3300FFCC)

enum class VideoResizeMode(val title: String, val exoMode: Int) {
    FIT("Fit", AspectRatioFrameLayout.RESIZE_MODE_FIT),
    CROP("Crop", AspectRatioFrameLayout.RESIZE_MODE_ZOOM),
    STRETCH("Stretch", AspectRatioFrameLayout.RESIZE_MODE_FILL)
}

/**
 * Custom Glassmorphism Modifier: Liquid Glass Container
 */
fun Modifier.liquidGlassContainer(
    shape: Shape = RoundedCornerShape(20.dp),
    bgColor: Color = GlassContainerBg,
    borderColor: Color = Color(0x33FFFFFF)
): Modifier = this
    .clip(shape)
    .background(bgColor)
    .border(BorderStroke(1.dp, borderColor), shape)

/**
 * Custom Neon Glow Border Modifier
 */
fun Modifier.neonGlowBorder(
    shape: Shape = RoundedCornerShape(20.dp),
    glowColor: Color = NeonCyan
): Modifier = this.border(
    border = BorderStroke(
        width = 1.2.dp,
        brush = Brush.horizontalGradient(
            listOf(glowColor.copy(alpha = 0.8f), NeonBlue.copy(alpha = 0.8f))
        )
    ),
    shape = shape
)

data class VideoPlayerUiState(
    val isPlaying: Boolean = true,
    val isBuffering: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val playbackSpeed: Float = 1.0f,
    val resizeMode: VideoResizeMode = VideoResizeMode.FIT,
    val volume: Float = 0.75f,
    val brightness: Float = 0.70f,
    val areControlsVisible: Boolean = true,
    val isAntiCheatLocked: Boolean = false,
    val selectedAudioTrack: String = "Stereo 2.0 (AAC)",
    val selectedSubtitle: String = "Off"
)

@kotlin.OptIn(UnstableApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExoVideoPlayer(
    mediaItem: MediaItem,
    streakViewModel: StreakViewModel?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    BackHandler {
        onClose()
    }

    val streakState = streakViewModel?.uiState?.collectAsState()?.value
    val isAntiCheatSeekingLocked = streakState != null && !streakState.isGoalCompletedToday

    // Core Player State
    var uiState by remember {
        mutableStateOf(
            VideoPlayerUiState(
                isAntiCheatLocked = isAntiCheatSeekingLocked
            )
        )
    }

    // Audio Manager for System Volume
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val maxVolume = remember { audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1) }

    // Dialog sheets state
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showAudioSubtitleSheet by remember { mutableStateOf(false) }
    var gestureOverlayType by remember { mutableStateOf<String?>(null) } // "VOLUME", "BRIGHTNESS", "SEEK", "SEEK_LOCKED"
    var seekPreviewText by remember { mutableStateOf("") }
    var doubleTapFeedback by remember { mutableStateOf<String?>(null) } // "LEFT_10", "RIGHT_10"
    var isDraggingSeekbar by remember { mutableStateOf(false) }
    var scrubbedPositionMs by remember { mutableLongStateOf(0L) }
    var playerViewRef by remember { mutableStateOf<PlayerView?>(null) }

    // Media3 ExoPlayer Instance
    val exoPlayer = remember(mediaItem.id) {
        ExoPlayer.Builder(context).build().apply {
            val uri = if (!mediaItem.uriString.isNullOrBlank()) {
                Uri.parse(mediaItem.uriString)
            } else {
                Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
            }
            val exoItem = ExoMediaItem.fromUri(uri)
            setMediaItem(exoItem)
            prepare()
            playWhenReady = true
        }
    }

    // Listen to Player Events
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                uiState = uiState.copy(isPlaying = playing)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                val isBuffering = playbackState == Player.STATE_BUFFERING
                val isReady = playbackState == Player.STATE_READY
                val d = exoPlayer.duration
                uiState = uiState.copy(
                    isBuffering = isBuffering,
                    durationMs = if (isReady && d > 0) d else uiState.durationMs
                )
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.stop()
            exoPlayer.release()
        }
    }

    // Update Progress Every 500ms
    LaunchedEffect(exoPlayer) {
        while (true) {
            delay(500)
            if (exoPlayer.isPlaying && !isDraggingSeekbar) {
                val pos = exoPlayer.currentPosition
                val d = exoPlayer.duration
                uiState = uiState.copy(
                    currentPositionMs = pos,
                    durationMs = if (d > 0) d else uiState.durationMs
                )
                // Genuine local watch tracking for streak gamification (1s steps)
                streakViewModel?.tickGenuineWatchTime(1, isSeeking = false)
            }
        }
    }

    // Auto-hide controls after 4 seconds of inactivity
    LaunchedEffect(uiState.areControlsVisible, uiState.isPlaying, isDraggingSeekbar) {
        if (uiState.areControlsVisible && uiState.isPlaying && !isDraggingSeekbar) {
            delay(4000)
            uiState = uiState.copy(areControlsVisible = false)
        }
    }

    // Dismiss HUD overlays
    LaunchedEffect(gestureOverlayType) {
        if (gestureOverlayType != null) {
            delay(1400)
            gestureOverlayType = null
        }
    }

    LaunchedEffect(doubleTapFeedback) {
        if (doubleTapFeedback != null) {
            delay(800)
            doubleTapFeedback = null
        }
    }

    // Glowing Neon Ambient Pulse
    val infiniteTransition = rememberInfiniteTransition(label = "player_neon_pulse")
    val neonGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.90f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "neon_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("high_end_exo_video_player")
            // Double Tap (10s seek) & Single Tap (toggle controls) Detection
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { offset ->
                        val isRightSide = offset.x > (size.width / 2f)
                        if (uiState.isAntiCheatLocked) {
                            gestureOverlayType = "SEEK_LOCKED"
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        } else {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            if (isRightSide) {
                                val target = (exoPlayer.currentPosition + 10_000L).coerceAtMost(uiState.durationMs)
                                exoPlayer.seekTo(target)
                                uiState = uiState.copy(currentPositionMs = target)
                                doubleTapFeedback = "RIGHT_10"
                            } else {
                                val target = (exoPlayer.currentPosition - 10_000L).coerceAtLeast(0L)
                                exoPlayer.seekTo(target)
                                uiState = uiState.copy(currentPositionMs = target)
                                doubleTapFeedback = "LEFT_10"
                            }
                        }
                    },
                    onTap = {
                        uiState = uiState.copy(areControlsVisible = !uiState.areControlsVisible)
                    }
                )
            }
            // Vertical Drag (Brightness on Left, Volume on Right) & Horizontal Drag (Seek)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        uiState = uiState.copy(areControlsVisible = true)
                    },
                    onDragEnd = {
                        gestureOverlayType = null
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val isRightSide = change.position.x > (size.width / 2f)

                        if (abs(dragAmount.y) > abs(dragAmount.x)) {
                            // Vertical gestures: Volume & Brightness
                            val delta = -dragAmount.y / 400f
                            if (isRightSide) {
                                // Volume Control (Right)
                                val newVol = (uiState.volume + delta).coerceIn(0f, 1f)
                                uiState = uiState.copy(volume = newVol)
                                exoPlayer.volume = newVol
                                gestureOverlayType = "VOLUME"
                            } else {
                                // Brightness Control (Left)
                                val newBrightness = (uiState.brightness + delta).coerceIn(0.05f, 1f)
                                uiState = uiState.copy(brightness = newBrightness)
                                (context as? Activity)?.let { act ->
                                    val lp = act.window.attributes
                                    lp.screenBrightness = newBrightness
                                    act.window.attributes = lp
                                }
                                gestureOverlayType = "BRIGHTNESS"
                            }
                        } else {
                            // Horizontal gesture: Smooth Seeking
                            if (uiState.isAntiCheatLocked) {
                                gestureOverlayType = "SEEK_LOCKED"
                            } else {
                                val seekDeltaMs = (dragAmount.x * 250).toLong()
                                val targetPos = (exoPlayer.currentPosition + seekDeltaMs).coerceIn(0L, uiState.durationMs)
                                exoPlayer.seekTo(targetPos)
                                uiState = uiState.copy(currentPositionMs = targetPos)
                                gestureOverlayType = "SEEK"
                                seekPreviewText = "${if (seekDeltaMs >= 0) "+" else ""}${seekDeltaMs / 1000}s (${formatDuration(targetPos)})"
                            }
                        }
                    }
                )
            }
    ) {
        // 1. AndroidView / PlayerView Surface
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = uiState.resizeMode.exoMode
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    playerViewRef = this
                }
            },
            update = { view ->
                view.resizeMode = uiState.resizeMode.exoMode
            },
            modifier = Modifier.fillMaxSize()
        )

        // Buffering Indicator
        if (uiState.isBuffering) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(Color(0x99000000))
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = NeonCyan,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(38.dp)
                )
            }
        }

        // Double-Tap Visual Ripples (10s Seek)
        if (doubleTapFeedback != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 48.dp),
                contentAlignment = if (doubleTapFeedback == "LEFT_10") Alignment.CenterStart else Alignment.CenterEnd
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xB3101622),
                    border = BorderStroke(1.5.dp, NeonCyan),
                    modifier = Modifier.size(90.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (doubleTapFeedback == "LEFT_10") Icons.Default.Replay10 else Icons.Default.Forward10,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = if (doubleTapFeedback == "LEFT_10") "-10s" else "+10s",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        // Center HUD Overlay (Volume, Brightness, Seek)
        if (gestureOverlayType != null) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color(0xE60E1422),
                border = BorderStroke(1.2.dp, NeonCyan.copy(alpha = 0.7f)),
                shadowElevation = 16.dp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (gestureOverlayType) {
                        "VOLUME" -> {
                            Icon(Icons.Default.VolumeUp, null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Volume: ${(uiState.volume * 100).toInt()}%",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                        "BRIGHTNESS" -> {
                            Icon(Icons.Default.BrightnessHigh, null, tint = Color(0xFFFFD700), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Brightness: ${(uiState.brightness * 100).toInt()}%",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                        "SEEK_LOCKED" -> {
                            Icon(Icons.Default.Lock, null, tint = Color(0xFFFF5252), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "🔒 Seeking Locked (Anti-Cheat Active)",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFB74D)
                                )
                            )
                        }
                        else -> {
                            Icon(Icons.Default.FastForward, null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = seekPreviewText,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // 2. Liquid Glass Dark Mode Overlay Controls (Auto-Hiding)
        AnimatedVisibility(
            visible = uiState.areControlsVisible,
            enter = fadeIn(animationSpec = tween(280, easing = FastOutSlowInEasing)),
            exit = fadeOut(animationSpec = tween(350, easing = FastOutSlowInEasing)),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xD907090F),
                                Color(0x3307090F),
                                Color(0x2207090F),
                                Color(0xF007090F)
                            )
                        )
                    )
            ) {
                // 3. Top Navigation Bar: Back button, marquee title, 4K HDR badge, aspect ratio, settings
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(22.dp),
                    color = GlassContainerBg,
                    border = BorderStroke(1.dp, GlassBorderStroke)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Back Button & Title
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            IconButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onClose()
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x33FFFFFF))
                                    .testTag("video_player_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = mediaItem.titleArabic ?: mediaItem.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 14.5.sp
                                    ),
                                    maxLines = 1,
                                    modifier = Modifier.basicMarquee()
                                )
                                Text(
                                    text = "Offline Liquid Glass M5 Player",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = NeonCyan,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Glowing "4K HDR" Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0x2200FFCC),
                            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.8f))
                        ) {
                            Text(
                                text = "4K HDR",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = NeonCyan,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Aspect Ratio Toggle Button (Fit, Crop, Stretch)
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                val nextMode = when (uiState.resizeMode) {
                                    VideoResizeMode.FIT -> VideoResizeMode.CROP
                                    VideoResizeMode.CROP -> VideoResizeMode.STRETCH
                                    VideoResizeMode.STRETCH -> VideoResizeMode.FIT
                                }
                                uiState = uiState.copy(resizeMode = nextMode)
                                playerViewRef?.resizeMode = nextMode.exoMode
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0x22FFFFFF))
                                .testTag("video_player_aspect_ratio_button")
                        ) {
                            Icon(
                                imageVector = when (uiState.resizeMode) {
                                    VideoResizeMode.FIT -> Icons.Default.FitScreen
                                    VideoResizeMode.CROP -> Icons.Default.Crop
                                    VideoResizeMode.STRETCH -> Icons.Default.AspectRatio
                                },
                                contentDescription = "Aspect Ratio: ${uiState.resizeMode.title}",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Settings Gear Button
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                showSettingsSheet = true
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0x22FFFFFF))
                                .testTag("video_player_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // 4. Center Controls: Rewind 10s, Large Circular Glowing Neon Play/Pause with Shadow, Forward 10s
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rewind 10s
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0x770E1422))
                            .border(BorderStroke(1.dp, Color(0x33FFFFFF)), CircleShape)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                if (uiState.isAntiCheatLocked) {
                                    gestureOverlayType = "SEEK_LOCKED"
                                } else {
                                    val target = (exoPlayer.currentPosition - 10_000L).coerceAtLeast(0L)
                                    exoPlayer.seekTo(target)
                                    uiState = uiState.copy(currentPositionMs = target)
                                }
                            }
                            .testTag("video_player_rewind_10s_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay10,
                            contentDescription = "Rewind 10s",
                            tint = if (uiState.isAntiCheatLocked) Color(0xFF64748B) else Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(36.dp))

                    // Large Circular Glowing Neon Play/Pause Button
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .drawBehind {
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            NeonCyan.copy(alpha = neonGlowAlpha * 0.7f),
                                            NeonBlue.copy(alpha = 0.3f),
                                            Color.Transparent
                                        ),
                                        radius = size.width * 0.95f
                                    )
                                )
                            }
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(NeonCyan, NeonBlue)
                                )
                            )
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                if (exoPlayer.isPlaying) {
                                    exoPlayer.pause()
                                    uiState = uiState.copy(isPlaying = false)
                                } else {
                                    exoPlayer.play()
                                    uiState = uiState.copy(isPlaying = true)
                                }
                            }
                            .testTag("video_player_center_play_pause_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (uiState.isPlaying) "Pause" else "Play",
                            tint = Charcoal950,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(36.dp))

                    // Forward 10s
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0x770E1422))
                            .border(BorderStroke(1.dp, Color(0x33FFFFFF)), CircleShape)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                if (uiState.isAntiCheatLocked) {
                                    gestureOverlayType = "SEEK_LOCKED"
                                } else {
                                    val target = (exoPlayer.currentPosition + 10_000L).coerceAtMost(uiState.durationMs)
                                    exoPlayer.seekTo(target)
                                    uiState = uiState.copy(currentPositionMs = target)
                                }
                            }
                            .testTag("video_player_forward_10s_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forward10,
                            contentDescription = "Forward 10s",
                            tint = if (uiState.isAntiCheatLocked) Color(0xFF64748B) else Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                // 5. Bottom Bar & Seekbar: Smooth progress slider, live timestamps, playback speed, audio/subtitles, PiP
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = GlassContainerBg,
                    border = BorderStroke(1.dp, GlassBorderStroke)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        // Progress Slider
                        val displayMs = if (isDraggingSeekbar) scrubbedPositionMs else uiState.currentPositionMs
                        val progressRatio = if (uiState.durationMs > 0) {
                            (displayMs.toFloat() / uiState.durationMs.toFloat()).coerceIn(0f, 1f)
                        } else 0f

                        Slider(
                            value = progressRatio,
                            enabled = !uiState.isAntiCheatLocked,
                            onValueChange = { ratio ->
                                if (!uiState.isAntiCheatLocked) {
                                    isDraggingSeekbar = true
                                    scrubbedPositionMs = (ratio * uiState.durationMs).toLong()
                                }
                            },
                            onValueChangeFinished = {
                                if (!uiState.isAntiCheatLocked) {
                                    exoPlayer.seekTo(scrubbedPositionMs)
                                    uiState = uiState.copy(currentPositionMs = scrubbedPositionMs)
                                    isDraggingSeekbar = false
                                }
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = if (uiState.isAntiCheatLocked) Color(0xFF64748B) else NeonCyan,
                                activeTrackColor = if (uiState.isAntiCheatLocked) Color(0xFF64748B) else NeonCyan,
                                inactiveTrackColor = Color(0x33FFFFFF),
                                disabledThumbColor = Color(0xFF64748B),
                                disabledActiveTrackColor = Color(0xFF475569)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(22.dp)
                                .testTag("video_player_progress_slider")
                        )

                        // Live Timestamps Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatDuration(displayMs),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                )
                            )

                            if (uiState.isAntiCheatLocked) {
                                Text(
                                    text = "🔒 Anti-Cheat Active (Seek locked)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFFFFB74D),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            } else {
                                Text(
                                    text = "Swipe L: Brightness • R: Volume",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF94A3B8),
                                        fontSize = 10.sp
                                    )
                                )
                            }

                            Text(
                                text = formatDuration(uiState.durationMs),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontSize = 11.5.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Bottom Action Controls: Speed selector, Audio/Subtitles, and Picture-in-Picture
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Playback Speed Selector (0.5x, 1.0x, 1.25x, 1.5x, 2.0x)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val speedOptions = listOf(0.5f, 1.0f, 1.25f, 1.5f, 2.0f)
                                speedOptions.forEach { speed ->
                                    val isSelected = uiState.playbackSpeed == speed
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) NeonCyan else Color(0x22FFFFFF),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) NeonCyan else Color(0x22FFFFFF)
                                        ),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                uiState = uiState.copy(playbackSpeed = speed)
                                                exoPlayer.playbackParameters = PlaybackParameters(speed)
                                            }
                                            .testTag("speed_option_${speed}x")
                                    ) {
                                        Text(
                                            text = "${speed}x",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isSelected) Charcoal950 else Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }
                            }

                            // Audio Track / Subtitles & PiP Action Buttons
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Audio Track & Subtitles Button
                                IconButton(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        showAudioSubtitleSheet = true
                                    },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x22FFFFFF))
                                        .testTag("audio_subtitles_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Subtitles,
                                        contentDescription = "Audio and Subtitles",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Picture-in-Picture (PiP) Trigger Button
                                IconButton(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        triggerPictureInPicture(context)
                                    },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x22FFFFFF))
                                        .testTag("pip_trigger_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PictureInPicture,
                                        contentDescription = "Picture in Picture",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Settings Bottom Sheet (Decoder, Lock, Equalizer info)
        if (showSettingsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSettingsSheet = false },
                containerColor = Color(0xF20B0F19),
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "Player Settings",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    PlayerSettingItem(
                        icon = Icons.Default.GraphicEq,
                        title = "Hardware Acceleration",
                        subtitle = "GPU HW+ Decoder Active (120 FPS)",
                        badge = "ENABLED"
                    )

                    PlayerSettingItem(
                        icon = Icons.Default.AspectRatio,
                        title = "Display Mode",
                        subtitle = "Current: ${uiState.resizeMode.title}",
                        badge = uiState.resizeMode.name
                    )

                    PlayerSettingItem(
                        icon = Icons.Default.Speed,
                        title = "Playback Speed",
                        subtitle = "${uiState.playbackSpeed}x Normal",
                        badge = "${uiState.playbackSpeed}x"
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        // Audio & Subtitles Bottom Sheet
        if (showAudioSubtitleSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAudioSubtitleSheet = false },
                containerColor = Color(0xF20B0F19),
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "Audio & Subtitles",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "AUDIO TRACKS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    listOf("Stereo 2.0 (AAC)", "Surround 5.1 (Dolby Digital)", "Original Studio Audio").forEach { track ->
                        val isSelected = uiState.selectedAudioTrack == track
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    uiState = uiState.copy(selectedAudioTrack = track)
                                }
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = track,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (isSelected) NeonCyan else Color.White,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                            if (isSelected) {
                                Icon(Icons.Default.Check, null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "SUBTITLES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    listOf("Off", "English [CC]", "Arabic (العربية)", "French (Français)").forEach { sub ->
                        val isSelected = uiState.selectedSubtitle == sub
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    uiState = uiState.copy(selectedSubtitle = sub)
                                }
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = sub,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (isSelected) NeonCyan else Color.White,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                            if (isSelected) {
                                Icon(Icons.Default.Check, null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun PlayerSettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badge: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold))
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8), fontSize = 11.sp))
            }
        }
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0x3300FFCC),
            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f))
        ) {
            Text(
                text = badge,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )
        }
    }
}

private fun triggerPictureInPicture(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        (context as? Activity)?.let { activity ->
            try {
                val params = PictureInPictureParams.Builder()
                    .setAspectRatio(Rational(16, 9))
                    .build()
                activity.enterPictureInPictureMode(params)
            } catch (_: Exception) {
                // Ignore if PiP not supported on current device/window state
            }
        }
    }
}

private fun formatDuration(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
