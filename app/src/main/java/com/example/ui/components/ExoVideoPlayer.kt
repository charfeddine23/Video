package com.example.ui.components

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.util.Rational
import android.view.TextureView
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Rotate90DegreesCw
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.StayCurrentLandscape
import androidx.compose.material.icons.filled.StayCurrentPortrait
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem as ExoMediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
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
    val isAmbientGlowEnabled: Boolean = true,
    val hideFileExtensions: Boolean = true,
    val videoRotationDegrees: Int = 0,
    val isLandscapeOrientation: Boolean = false,
    val selectedAudioTrack: String = "Stereo 2.0 (AAC)",
    val selectedSubtitle: String = "Off",
    val isScreenLocked: Boolean = false
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
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val prefsRepo = remember { com.example.data.preferences.StreakPreferencesRepository.getInstance(context) }
    val streakState = streakViewModel?.uiState?.collectAsState()?.value
    val streakDays = streakState?.streakDays ?: 0
    val isAntiCheatSeekingLocked = streakState != null && !streakState.isGoalCompletedToday

    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager }
    val maxStreamVolume = remember(audioManager) {
        audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)?.coerceAtLeast(1) ?: 15
    }

    // Core Player State
    var uiState by remember {
        val currentSysVol = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: (maxStreamVolume / 2)
        val initialRatio = (currentSysVol.toFloat() / maxStreamVolume.toFloat()).coerceIn(0f, 1f)
        mutableStateOf(
            VideoPlayerUiState(
                isAntiCheatLocked = isAntiCheatSeekingLocked,
                isAmbientGlowEnabled = true,
                hideFileExtensions = prefsRepo.getHideFileExtensions(),
                isLandscapeOrientation = isLandscape,
                volume = initialRatio
            )
        )
    }

    LaunchedEffect(isAntiCheatSeekingLocked) {
        uiState = uiState.copy(isAntiCheatLocked = isAntiCheatSeekingLocked)
    }

    // Dynamic Video Dimensions & Aspect Ratio
    var videoWidth by remember { mutableStateOf(1920) }
    var videoHeight by remember { mutableStateOf(1080) }

    // Dynamic Ambient Dominant Colors
    var ambientPrimaryColor by remember { mutableStateOf(NeonCyan) }
    var ambientSecondaryColor by remember { mutableStateOf(NeonBlue) }
    var ambientTertiaryColor by remember { mutableStateOf(Color(0xFF7928CA)) }

    val ambientPalette = remember {
        listOf(
            Triple(Color(0xFF00FFCC), Color(0xFF0088FF), Color(0xFF7928CA)),
            Triple(Color(0xFFFF007F), Color(0xFF7928CA), Color(0xFF0088FF)),
            Triple(Color(0xFF00E5FF), Color(0xFF00E676), Color(0xFF1E88E5)),
            Triple(Color(0xFFFF9100), Color(0xFFFF3D00), Color(0xFF7C4DFF)),
            Triple(Color(0xFF00F2FE), Color(0xFF4FACFE), Color(0xFF00C6FF))
        )
    }

    // Animated Smooth transitions between ambient tones
    val animatedAmbientPrimary by animateColorAsState(
        targetValue = ambientPrimaryColor,
        animationSpec = tween(1200, easing = LinearEasing),
        label = "ambient_primary_anim"
    )
    val animatedAmbientSecondary by animateColorAsState(
        targetValue = ambientSecondaryColor,
        animationSpec = tween(1200, easing = LinearEasing),
        label = "ambient_secondary_anim"
    )
    val animatedAmbientTertiary by animateColorAsState(
        targetValue = ambientTertiaryColor,
        animationSpec = tween(1200, easing = LinearEasing),
        label = "ambient_tertiary_anim"
    )

    // Modals & Floating Dialogs
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showAudioSubtitleSheet by remember { mutableStateOf(false) }
    var showStrikeLockPopup by remember { mutableStateOf(false) }
    var strikeLockPopupMessage by remember { mutableStateOf("Locked. Unlocks at the required level.") }

    var gestureOverlayType by remember { mutableStateOf<String?>(null) }
    var seekPreviewText by remember { mutableStateOf("") }
    var doubleTapFeedback by remember { mutableStateOf<String?>(null) }
    var isDraggingSeekbar by remember { mutableStateOf(false) }
    var isDraggingGesture by remember { mutableStateOf(false) }
    var dragGestureMode by remember { mutableStateOf<String?>(null) }
    var dragAccumulatedX by remember { mutableFloatStateOf(0f) }
    var dragAccumulatedY by remember { mutableFloatStateOf(0f) }
    var scrubbedPositionMs by remember { mutableLongStateOf(0L) }
    var isSeekingForward by remember { mutableStateOf(true) }
    var showUnlockButton by remember { mutableStateOf(false) }
    var isLeftToolbarCollapsed by remember { mutableStateOf(false) }
    // Interactive 1:1 finger tracking progress (0f = fully expanded, 1f = fully collapsed)
    val toolbarCollapseProgress = remember { Animatable(0f) }
    var showToolbarGestureHint by remember { mutableStateOf(false) }
    var showGoalCelebrationBanner by remember { mutableStateOf(false) }

    // Auto-dismiss gesture hint after 3 seconds
    LaunchedEffect(showToolbarGestureHint) {
        if (showToolbarGestureHint) {
            delay(3000)
            showToolbarGestureHint = false
        }
    }

    // When the 5-minute goal is reached today, trigger smooth celebration and hide anti-cheat banner
    LaunchedEffect(streakState?.isGoalCompletedToday) {
        if (streakState?.isGoalCompletedToday == true && uiState.isAntiCheatLocked) {
            showGoalCelebrationBanner = true
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            delay(3500)
            showGoalCelebrationBanner = false
        }
    }

    var playerViewRef by remember { mutableStateOf<PlayerView?>(null) }

    // Helper to format title with or without file extension
    fun formatDisplayTitle(rawTitle: String, hideExt: Boolean): String {
        return if (hideExt) {
            rawTitle.replace(Regex("\\.(mp4|mkv|avi|mov|flv|webm|ts|m4v|3gp|wmv)$", RegexOption.IGNORE_CASE), "")
        } else {
            rawTitle
        }
    }

    // Trigger Dynamic Strike Lock Alert Popup
    fun triggerStrikeLockAlert(featureName: String, requiredDay: Int = 0) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        strikeLockPopupMessage = if (requiredDay > 0) {
            "مغلق. يتطلب الوصول لليوم $requiredDay من السلسلة.\nLocked. Unlocks at day $requiredDay."
        } else {
            "مغلق. يفتح عند الوصول للمستوى المطلوب.\nLocked. Unlocks at the required level."
        }
        showStrikeLockPopup = true
    }

    // Initialize ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_ALL
        }
    }

    val handleClose: () -> Unit = {
        try {
            val pos = exoPlayer.currentPosition
            val dur = exoPlayer.duration.coerceAtLeast(0L)
            if (pos > 500L && dur > 0L) {
                prefsRepo.saveVideoProgress(
                    id = mediaItem.id,
                    uri = mediaItem.uriString,
                    title = mediaItem.title,
                    durationMs = dur,
                    positionMs = pos,
                    thumbnailRes = mediaItem.posterRes
                )
            }
        } catch (_: Exception) {}
        onClose()
    }

    BackHandler {
        handleClose()
    }

    // Media item attachment & Automatic Resume from Saved Progress
    LaunchedEffect(mediaItem) {
        val uri = when {
            mediaItem.uriString != null -> Uri.parse(mediaItem.uriString)
            mediaItem.category == "Live Channels" -> Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
            else -> Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
        }

        try {
            val exoItem = ExoMediaItem.fromUri(uri)
            exoPlayer.setMediaItem(exoItem)
            exoPlayer.prepare()

            // Check if there is saved playback progress for this specific video
            val savedProgress = prefsRepo.getVideoProgress(mediaItem.id, mediaItem.uriString)
            val savedPos = savedProgress?.positionMs ?: 0L
            val savedDur = savedProgress?.durationMs ?: 0L

            // Resume playback if video was in progress (more than 1.5s and not within 3s of the end)
            if (savedPos > 1500L && (savedDur <= 0L || savedPos < savedDur - 3000L)) {
                exoPlayer.seekTo(savedPos)
                uiState = uiState.copy(currentPositionMs = savedPos)
            }

            exoPlayer.play()
        } catch (_: Exception) {}
    }

    // Player Event Listener
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                val dur = exoPlayer.duration.coerceAtLeast(0L)
                uiState = uiState.copy(
                    isBuffering = playbackState == Player.STATE_BUFFERING,
                    durationMs = dur
                )

                // Secondary check for resume when player reaches STATE_READY
                if (playbackState == Player.STATE_READY) {
                    val savedProgress = prefsRepo.getVideoProgress(mediaItem.id, mediaItem.uriString)
                    val savedPos = savedProgress?.positionMs ?: 0L
                    if (savedPos > 1500L && exoPlayer.currentPosition < 1000L && (dur <= 0L || savedPos < dur - 3000L)) {
                        exoPlayer.seekTo(savedPos)
                        uiState = uiState.copy(currentPositionMs = savedPos)
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                uiState = uiState.copy(isPlaying = isPlaying)
            }

            override fun onVideoSizeChanged(videoSize: VideoSize) {
                if (videoSize.width > 0 && videoSize.height > 0) {
                    videoWidth = videoSize.width
                    videoHeight = videoSize.height
                }
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            try {
                val pos = exoPlayer.currentPosition
                val dur = exoPlayer.duration.coerceAtLeast(0L)
                if (pos > 500L && dur > 0L) {
                    prefsRepo.saveVideoProgress(
                        id = mediaItem.id,
                        uri = mediaItem.uriString,
                        title = mediaItem.title,
                        durationMs = dur,
                        positionMs = pos,
                        thumbnailRes = mediaItem.posterRes
                    )
                }
            } catch (_: Exception) {}
            exoPlayer.removeListener(listener)
            exoPlayer.release()
            (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    // Real-time Progress, 5-Minute Daily Goal Ticker & Ambient Color Loop
    LaunchedEffect(uiState.isPlaying, isDraggingSeekbar) {
        var secondAccumulatorMs = 0L
        while (uiState.isPlaying && !isDraggingSeekbar) {
            delay(250)
            val pos = exoPlayer.currentPosition
            val dur = exoPlayer.duration.coerceAtLeast(0L)
            uiState = uiState.copy(
                currentPositionMs = pos,
                durationMs = dur
            )

            // 1. Core Fix: Tick genuine watch time towards the 5-minute daily watch goal!
            // Strictly counts real playback (not seeking, not paused, not buffering)
            if (!uiState.isBuffering && exoPlayer.isPlaying) {
                secondAccumulatorMs += 250L
                if (secondAccumulatorMs >= 1000L) {
                    if (streakViewModel != null) {
                        streakViewModel.tickGenuineWatchTime(seconds = 1, isSeeking = false)
                    } else {
                        prefsRepo.tickDailyWatchTime(seconds = 1)
                    }
                    secondAccumulatorMs -= 1000L
                }
            }

            // 2. Periodically persist playback progress for this video and Continue Watching
            if (pos > 500L && dur > 0L && (pos % 2000L < 300L)) {
                prefsRepo.saveVideoProgress(
                    id = mediaItem.id,
                    uri = mediaItem.uriString,
                    title = mediaItem.title,
                    durationMs = dur,
                    positionMs = pos,
                    thumbnailRes = mediaItem.posterRes
                )
            }

            // Extract dominant colors from TextureView frame if available
            if (uiState.isAmbientGlowEnabled && (pos % 1000L < 300L)) {
                playerViewRef?.videoSurfaceView?.let { surfaceView ->
                    if (surfaceView is TextureView) {
                        val bitmap = surfaceView.getBitmap(16, 16)
                        if (bitmap != null) {
                            try {
                                val c1 = bitmap.getPixel(2, 2)
                                val c2 = bitmap.getPixel(13, 8)
                                val c3 = bitmap.getPixel(8, 13)
                                ambientPrimaryColor = Color(c1)
                                ambientSecondaryColor = Color(c2)
                                ambientTertiaryColor = Color(c3)
                                bitmap.recycle()
                            } catch (_: Throwable) {}
                        }
                    } else {
                        val paletteIdx = ((pos / 2500) % ambientPalette.size).toInt()
                        val selected = ambientPalette[paletteIdx]
                        ambientPrimaryColor = selected.first
                        ambientSecondaryColor = selected.second
                        ambientTertiaryColor = selected.third
                    }
                }
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

    // Dismiss Strike Lock Popup automatically after 3.2 seconds
    LaunchedEffect(showStrikeLockPopup) {
        if (showStrikeLockPopup) {
            delay(3200)
            showStrikeLockPopup = false
        }
    }

    // Auto-dismiss floating unlock button after 3.5 seconds
    LaunchedEffect(showUnlockButton) {
        if (showUnlockButton) {
            delay(3500)
            showUnlockButton = false
        }
    }

    // Glowing Neon Pulse
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

    val ambientBreathScale by infiniteTransition.animateFloat(
        initialValue = 1.02f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambient_breath_scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("high_end_exo_video_player")
            // Double Tap (10s seek) & Single Tap (toggle controls)
            .pointerInput(uiState.isScreenLocked) {
                detectTapGestures(
                    onDoubleTap = { offset ->
                        if (uiState.isScreenLocked) {
                            showUnlockButton = true
                            return@detectTapGestures
                        }
                        val isRightSide = offset.x > (size.width / 2f)
                        if (uiState.isAntiCheatLocked) {
                            triggerStrikeLockAlert("Seeking Anti-Cheat")
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
                        if (uiState.isScreenLocked) {
                            showUnlockButton = true
                        } else {
                            uiState = uiState.copy(areControlsVisible = !uiState.areControlsVisible)
                        }
                    }
                )
            }
            // Vertical Drag (Brightness on Left, Volume on Right) & Explicit Horizontal Drag (Seek)
            .pointerInput(uiState.isScreenLocked) {
                detectDragGestures(
                    onDragStart = {
                        if (uiState.isScreenLocked) {
                            showUnlockButton = true
                            return@detectDragGestures
                        }
                        isDraggingGesture = true
                        dragGestureMode = null
                        dragAccumulatedX = 0f
                        dragAccumulatedY = 0f
                        uiState = uiState.copy(areControlsVisible = false)
                    },
                    onDragEnd = {
                        isDraggingGesture = false
                        dragGestureMode = null
                        gestureOverlayType = null
                        uiState = uiState.copy(areControlsVisible = false)
                    },
                    onDragCancel = {
                        isDraggingGesture = false
                        dragGestureMode = null
                        gestureOverlayType = null
                        uiState = uiState.copy(areControlsVisible = false)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        if (uiState.isScreenLocked) {
                            showUnlockButton = true
                            return@detectDragGestures
                        }
                        dragAccumulatedX += dragAmount.x
                        dragAccumulatedY += dragAmount.y

                        // Only lock into a mode once an explicit threshold is exceeded
                        if (dragGestureMode == null) {
                            val absX = kotlin.math.abs(dragAccumulatedX)
                            val absY = kotlin.math.abs(dragAccumulatedY)
                            val threshold = 16f

                            if (absX > threshold && absX > absY * 1.75f) {
                                // Explicit horizontal drag only -> Seeking
                                dragGestureMode = "SEEK"
                            } else if (absY > threshold && absY > absX * 1.4f) {
                                // Explicit vertical drag -> Volume or Brightness
                                val isRightSide = change.position.x > (size.width / 2f)
                                dragGestureMode = if (isRightSide) "VOL" else "BRIGHT"
                            }
                        }

                        when (dragGestureMode) {
                            "VOL" -> {
                                val delta = -dragAmount.y / 450f
                                val newVol = (uiState.volume + delta).coerceIn(0f, 1f)
                                uiState = uiState.copy(volume = newVol)
                                exoPlayer.volume = newVol
                                // Control device system volume
                                audioManager?.let { am ->
                                    val targetIndex = (newVol * maxStreamVolume).toInt().coerceIn(0, maxStreamVolume)
                                    try {
                                        am.setStreamVolume(AudioManager.STREAM_MUSIC, targetIndex, 0)
                                    } catch (_: Exception) {}
                                }
                                gestureOverlayType = "VOLUME"
                            }
                            "BRIGHT" -> {
                                val delta = -dragAmount.y / 450f
                                val newBrightness = (uiState.brightness + delta).coerceIn(0.05f, 1f)
                                uiState = uiState.copy(brightness = newBrightness)
                                (context as? Activity)?.let { act ->
                                    val lp = act.window.attributes
                                    lp.screenBrightness = newBrightness
                                    act.window.attributes = lp
                                }
                                gestureOverlayType = "BRIGHTNESS"
                            }
                            "SEEK" -> {
                                if (uiState.isAntiCheatLocked) {
                                    triggerStrikeLockAlert("Seeking Anti-Cheat")
                                } else {
                                    isSeekingForward = dragAmount.x >= 0f
                                    val deltaMs = (dragAmount.x * 120).toLong()
                                    val targetPos = (exoPlayer.currentPosition + deltaMs).coerceIn(0L, uiState.durationMs)
                                    exoPlayer.seekTo(targetPos)
                                    uiState = uiState.copy(currentPositionMs = targetPos)
                                    gestureOverlayType = "SEEK"
                                    seekPreviewText = "${if (deltaMs >= 0) "+" else ""}${deltaMs / 1000}s (${formatDuration(targetPos)})"
                                }
                            }
                        }
                    }
                )
            }
    ) {
        // =========================================================================
        // DUAL-LAYER AMBIENT GLOW SYSTEM
        // =========================================================================

        // LAYER 1 (Background): Scaled ambient blurred video glow feed
        if (uiState.isAmbientGlowEnabled && uiState.resizeMode != VideoResizeMode.CROP) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = ambientBreathScale * 1.15f
                        scaleY = ambientBreathScale * 1.15f
                        alpha = 0.85f
                    }
                    .drawBehind {
                        drawRect(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    animatedAmbientPrimary.copy(alpha = 0.55f),
                                    animatedAmbientSecondary.copy(alpha = 0.35f),
                                    animatedAmbientTertiary.copy(alpha = 0.20f),
                                    Color.Black.copy(alpha = 0.90f)
                                ),
                                radius = size.maxDimension * 0.72f
                            )
                        )
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(60.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    animatedAmbientPrimary.copy(alpha = 0.30f),
                                    animatedAmbientSecondary.copy(alpha = 0.15f),
                                    Color.Transparent,
                                    animatedAmbientSecondary.copy(alpha = 0.15f),
                                    animatedAmbientPrimary.copy(alpha = 0.30f)
                                )
                            )
                        )
                )
            }
        }

        // LAYER 2 (Foreground): Main video surface centered
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isAmbientGlowEnabled && uiState.resizeMode == VideoResizeMode.FIT) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        animatedAmbientPrimary.copy(alpha = 0.40f),
                                        Color.Transparent,
                                        Color.Transparent,
                                        animatedAmbientSecondary.copy(alpha = 0.40f)
                                    )
                                )
                            )
                        }
                )
            }

            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                        resizeMode = uiState.resizeMode.exoMode
                        keepScreenOn = true
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        playerViewRef = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Buffering Indicator - hidden during drag gestures and no dark circle background
        if (uiState.isBuffering && !isDraggingGesture && gestureOverlayType != "SEEK") {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = NeonCyan,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(36.dp)
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

        // -------------------------------------------------------------------------
        // GESTURE OVERLAYS: Sleek Vertical Bars on Sides for Volume & Brightness
        // No central box for volume/brightness; only a minimal capsule for seek.
        // -------------------------------------------------------------------------
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            // Brightness Vertical Bar (Left edge)
            AnimatedVisibility(
                visible = gestureOverlayType == "BRIGHTNESS",
                enter = fadeIn(tween(160)) + slideInHorizontally { -it },
                exit = fadeOut(tween(220)) + slideOutHorizontally { -it },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 24.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = Color(0xD90E1422),
                    border = BorderStroke(1.2.dp, Color(0x66FFD700)),
                    shadowElevation = 16.dp,
                    modifier = Modifier
                        .width(42.dp)
                        .height(200.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 12.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(
                            imageVector = Icons.Default.BrightnessHigh,
                            contentDescription = "Brightness",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(20.dp)
                        )

                        // Vertical level indicator track
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .width(10.dp)
                                .padding(vertical = 8.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color(0x33FFFFFF)),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(uiState.brightness.coerceIn(0.05f, 1f))
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color(0xFFFFD700), Color(0xFFFF9100))
                                        )
                                    )
                            )
                        }

                        Text(
                            text = "${(uiState.brightness * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Volume Vertical Bar (Right edge)
            AnimatedVisibility(
                visible = gestureOverlayType == "VOLUME",
                enter = fadeIn(tween(160)) + slideInHorizontally { it },
                exit = fadeOut(tween(220)) + slideOutHorizontally { it },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 24.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = Color(0xD90E1422),
                    border = BorderStroke(1.2.dp, NeonCyan.copy(alpha = 0.7f)),
                    shadowElevation = 16.dp,
                    modifier = Modifier
                        .width(42.dp)
                        .height(200.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 12.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Volume",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )

                        // Vertical level indicator track
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .width(10.dp)
                                .padding(vertical = 8.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color(0x33FFFFFF)),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(uiState.volume.coerceIn(0f, 1f))
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(NeonCyan, Color(0xFF0088FF))
                                        )
                                    )
                            )
                        }

                        Text(
                            text = "${(uiState.volume * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Seek Capsule (Center of screen only for horizontal seek)
            AnimatedVisibility(
                visible = gestureOverlayType == "SEEK",
                enter = fadeIn(tween(150)) + scaleIn(initialScale = 0.9f),
                exit = fadeOut(tween(200)) + scaleOut(targetScale = 0.9f),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = Color(0xE60E1422),
                    border = BorderStroke(1.2.dp, NeonCyan.copy(alpha = 0.7f)),
                    shadowElevation = 16.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSeekingForward) Icons.Default.FastForward else Icons.Default.FastRewind,
                            contentDescription = if (isSeekingForward) "Forward" else "Rewind",
                            tint = NeonCyan,
                            modifier = Modifier.size(24.dp)
                        )
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

        // =========================================================================
        // CLEAN NON-OVERLAPPING ARCHITECTURE (Single Column with Arrangement.SpaceBetween)
        // =========================================================================
        AnimatedVisibility(
            visible = uiState.areControlsVisible && !uiState.isScreenLocked,
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // -------------------------------------------------------------
                    // TOP BAR: Clean Liquid Glass Container
                    // Contains ONLY: Back Arrow & Video Title on the Right, and Quality Badge on the Left
                    // -------------------------------------------------------------
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(22.dp),
                        color = GlassContainerBg,
                        border = BorderStroke(1.dp, GlassBorderStroke)
                    ) {
                        val displayTitle = formatDisplayTitle(
                            mediaItem.titleArabic ?: mediaItem.title,
                            uiState.hideFileExtensions
                        )

                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Left Side: Back Button & Video Title (العنوان في الجهة اليسرى)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f, fill = false)
                                ) {
                                    IconButton(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            handleClose()
                                        },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .testTag("video_player_back_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back",
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(
                                            text = displayTitle,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                fontSize = 14.sp
                                            ),
                                            maxLines = 1,
                                            modifier = Modifier.basicMarquee()
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Right Side: Quality Badge (دقة الفيديو في الجهة اليمنى)
                                QualityBadgeChip(
                                    qualityText = mediaItem.qualityTag.ifBlank { "4K HDR" }
                                )
                            }
                        }
                    }

                    // -------------------------------------------------------------
                    // CENTER CONTROLS: Forward 10s on LEFT, Play/Pause in Center, Rewind 10s on RIGHT
                    // Completely free of white circular borders / backgrounds
                    // Hidden during any dragging gesture (Seek/Volume/Brightness/Seekbar)
                    // -------------------------------------------------------------
                    val isAnyDragActive = isDraggingGesture || isDraggingSeekbar || gestureOverlayType != null
                    if (!isAnyDragActive) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // FORWARD 10s ON LEFT - Clean glowing icon without white circle
                            IconButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    if (uiState.isAntiCheatLocked) {
                                        triggerStrikeLockAlert("Seeking Anti-Cheat")
                                    } else {
                                        val target = (exoPlayer.currentPosition + 10_000L).coerceAtMost(uiState.durationMs)
                                        exoPlayer.seekTo(target)
                                        uiState = uiState.copy(currentPositionMs = target)
                                    }
                                },
                                modifier = Modifier
                                    .size(54.dp)
                                    .testTag("video_player_forward_10s_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Forward10,
                                    contentDescription = "Forward 10s (Left)",
                                    tint = if (uiState.isAntiCheatLocked) Color(0xFF64748B) else Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(36.dp))

                            // Clean Play/Pause Button - No glow effect, no shadow/circle
                            IconButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    if (exoPlayer.isPlaying) {
                                        exoPlayer.pause()
                                        uiState = uiState.copy(isPlaying = false)
                                        val pos = exoPlayer.currentPosition
                                        val dur = exoPlayer.duration.coerceAtLeast(0L)
                                        if (pos > 500L && dur > 0L) {
                                            prefsRepo.saveVideoProgress(
                                                id = mediaItem.id,
                                                uri = mediaItem.uriString,
                                                title = mediaItem.title,
                                                durationMs = dur,
                                                positionMs = pos,
                                                thumbnailRes = mediaItem.posterRes
                                            )
                                        }
                                    } else {
                                        exoPlayer.play()
                                        uiState = uiState.copy(isPlaying = true)
                                    }
                                },
                                modifier = Modifier
                                    .size(76.dp)
                                    .testTag("video_player_center_play_pause_button")
                            ) {
                                Icon(
                                    imageVector = if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (uiState.isPlaying) "Pause" else "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(56.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(36.dp))

                            // REWIND 10s ON RIGHT - Clean glowing icon without white circle
                            IconButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    if (uiState.isAntiCheatLocked) {
                                        triggerStrikeLockAlert("Seeking Anti-Cheat")
                                    } else {
                                        val target = (exoPlayer.currentPosition - 10_000L).coerceAtLeast(0L)
                                        exoPlayer.seekTo(target)
                                        uiState = uiState.copy(currentPositionMs = target)
                                    }
                                },
                                modifier = Modifier
                                    .size(54.dp)
                                    .testTag("video_player_rewind_10s_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Replay10,
                                    contentDescription = "Rewind 10s (Right)",
                                    tint = if (uiState.isAntiCheatLocked) Color(0xFF64748B) else Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }

                    // -------------------------------------------------------------
                    // BOTTOM BAR: Unified Frosted Surface with LTR Progress Slider
                    // Progress Slider starts from Left and ends at Right
                    // -------------------------------------------------------------
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = GlassContainerBg,
                        border = BorderStroke(1.dp, GlassBorderStroke)
                    ) {
                        // Standard LTR Layout Direction: progress bar starts from Left and ends at Right
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                // Progress Slider (RTL Mirrored)
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
                                            prefsRepo.saveVideoProgress(
                                                id = mediaItem.id,
                                                uri = mediaItem.uriString,
                                                title = mediaItem.title,
                                                durationMs = uiState.durationMs,
                                                positionMs = scrubbedPositionMs,
                                                thumbnailRes = mediaItem.posterRes
                                            )
                                        }
                                    },
                                    colors = SliderDefaults.colors(
                                        thumbColor = if (uiState.isAntiCheatLocked) Color(0xFF64748B) else animatedAmbientPrimary,
                                        activeTrackColor = if (uiState.isAntiCheatLocked) Color(0xFF64748B) else animatedAmbientPrimary,
                                        inactiveTrackColor = Color(0x33FFFFFF),
                                        disabledThumbColor = Color(0xFF64748B),
                                        disabledActiveTrackColor = Color(0xFF475569)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(22.dp)
                                        .testTag("video_player_progress_slider")
                                )

                                // Reversed Timestamps Row (RTL Mirrored)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // In RTL: This appears on the Right
                                    Text(
                                        text = formatDuration(displayMs),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.5.sp
                                        )
                                    )

                                    // Dynamic Animated Anti-Cheat Status & Goal Celebration
                                    AnimatedVisibility(
                                        visible = uiState.isAntiCheatLocked || showGoalCelebrationBanner,
                                        enter = fadeIn(tween(300)) + scaleIn(),
                                        exit = fadeOut(tween(600)) + scaleOut(targetScale = 0.7f)
                                    ) {
                                        if (showGoalCelebrationBanner) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Goal Achieved",
                                                    tint = NeonCyan,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "تم تحقيق الهدف اليومي! فك قفل التمرير",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = NeonCyan,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )
                                            }
                                        } else if (uiState.isAntiCheatLocked) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.clickable {
                                                    triggerStrikeLockAlert("Seeking Anti-Cheat")
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = null,
                                                    tint = Color(0xFFFFB74D),
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Anti-Cheat Active (Seek Locked)",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = Color(0xFFFFB74D),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                )
                                            }
                                        }
                                    }

                                    if (!uiState.isAntiCheatLocked && !showGoalCelebrationBanner) {
                                        Text(
                                            text = "Swipe L: Brightness • R: Volume",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color(0xFF94A3B8),
                                                fontSize = 10.sp
                                            )
                                        )
                                    }

                                    // In RTL: This appears on the Left
                                    Text(
                                        text = formatDuration(uiState.durationMs),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White.copy(alpha = 0.75f),
                                            fontSize = 11.5.sp
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Bottom Action Row (Speed options + PiP + Audio/Subtitles) - RTL Mirrored
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Speed Selector Badges
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val speedOptions = listOf(0.5f, 1.0f, 1.25f, 1.5f, 2.0f)
                                        speedOptions.forEach { speed ->
                                            val isSelected = uiState.playbackSpeed == speed
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (isSelected) animatedAmbientPrimary else Color(0x22FFFFFF),
                                                border = BorderStroke(
                                                    1.dp,
                                                    if (isSelected) animatedAmbientPrimary else Color(0x22FFFFFF)
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

                                    // PiP & Audio/Subtitle actions (with Strike Lock integration)
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Audio Track & Subtitles Button - Clean icon without white circle
                                        IconButton(
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                showAudioSubtitleSheet = true
                                            },
                                            modifier = Modifier
                                                .size(34.dp)
                                                .testTag("audio_subtitles_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Subtitles,
                                                contentDescription = "Audio and Subtitles",
                                                tint = animatedAmbientPrimary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        // Picture-in-Picture (PiP) Trigger Button (Strike Locked if day < 40)
                                        val isPipLocked = streakDays < 40
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clickable {
                                                    if (isPipLocked) {
                                                        triggerStrikeLockAlert("Picture-in-Picture (PiP)", 40)
                                                    } else {
                                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        triggerPictureInPicture(context)
                                                    }
                                                }
                                                .testTag("pip_trigger_button"),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PictureInPicture,
                                                contentDescription = "Picture in Picture",
                                                tint = if (isPipLocked) Color(0xFF64748B) else Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            if (isPipLocked) {
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = "Locked",
                                                    tint = Color(0xFFFFB74D),
                                                    modifier = Modifier
                                                        .size(11.dp)
                                                        .align(Alignment.TopEnd)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // -------------------------------------------------------------
                // LEFT VERTICAL TOOLBAR: Positioned directly below top container on the physical LEFT side
                // Container height animates smoothly with hand gesture, while icons remain anchored in place
                // -------------------------------------------------------------
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // 0f = fully open (height ~240dp), 1f = fully collapsed (height 56dp, only Settings gear)
                        val currentProgress = toolbarCollapseProgress.value
                        val containerHeight = (248.dp - (192.dp * currentProgress))

                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .statusBarsPadding()
                                .padding(start = 16.dp, top = 76.dp)
                                .width(52.dp)
                                .height(containerHeight)
                                .clip(RoundedCornerShape(22.dp))
                                .drawBehind {
                                    drawRoundRect(
                                        brush = Brush.verticalGradient(
                                            listOf(
                                                animatedAmbientPrimary.copy(alpha = 0.35f),
                                                animatedAmbientSecondary.copy(alpha = 0.20f),
                                                Color(0xFF00FFCC).copy(alpha = 0.30f)
                                            )
                                        ),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(22.dp.toPx()),
                                        size = size
                                    )
                                }
                                .pointerInput(isLeftToolbarCollapsed) {
                                    detectDragGestures(
                                        onDragStart = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        },
                                        onDragEnd = {
                                            scope.launch {
                                                // Snap container open or closed with spring physics
                                                if (toolbarCollapseProgress.value < 0.5f) {
                                                    toolbarCollapseProgress.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                                                    isLeftToolbarCollapsed = false
                                                } else {
                                                    toolbarCollapseProgress.animateTo(1f, spring(stiffness = Spring.StiffnessMediumLow))
                                                    isLeftToolbarCollapsed = true
                                                }
                                            }
                                        },
                                        onDragCancel = {
                                            scope.launch {
                                                val target = if (isLeftToolbarCollapsed) 1f else 0f
                                                toolbarCollapseProgress.animateTo(target, spring(stiffness = Spring.StiffnessMediumLow))
                                            }
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            scope.launch {
                                                // Dragging down expands container (decreases progress)
                                                // Dragging up collapses container (increases progress)
                                                val delta = -dragAmount.y / 192f
                                                val newProgress = (toolbarCollapseProgress.value + delta).coerceIn(0f, 1f)
                                                toolbarCollapseProgress.snapTo(newProgress)
                                                isLeftToolbarCollapsed = newProgress > 0.5f
                                            }
                                        }
                                    )
                                },
                            shape = RoundedCornerShape(22.dp),
                            color = Color(0xD90A0E18),
                            border = BorderStroke(
                                1.5.dp,
                                Brush.verticalGradient(
                                    listOf(
                                        animatedAmbientPrimary.copy(alpha = 0.85f),
                                        animatedAmbientSecondary.copy(alpha = 0.65f),
                                        NeonCyan.copy(alpha = 0.85f)
                                    )
                                )
                            ),
                            shadowElevation = 14.dp
                        ) {
                            // Fixed Column inside the clipped container - Icons stay in place!
                            Column(
                                modifier = Modifier
                                    .width(52.dp)
                                    .padding(horizontal = 8.dp, vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // 1. Settings (الاعدادات)
                                SettingsGearButton(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        showSettingsSheet = true
                                    }
                                )

                                // 2. Rotate Screen Orientation (زر تدوير الشاشة بدون تشويه)
                                RotateFrameButton(
                                    isLandscape = isLandscape,
                                    onRotate = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        val act = context as? Activity
                                        val targetOrientation = if (isLandscape) {
                                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                        } else {
                                            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                                        }
                                        act?.requestedOrientation = targetOrientation
                                    }
                                )

                                // 3. Aspect Ratio / Format (زر تنسيق الفيديو)
                                AspectRatioButton(
                                    resizeMode = uiState.resizeMode,
                                    onToggle = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        val nextMode = when (uiState.resizeMode) {
                                            VideoResizeMode.FIT -> VideoResizeMode.CROP
                                            VideoResizeMode.CROP -> VideoResizeMode.STRETCH
                                            VideoResizeMode.STRETCH -> VideoResizeMode.FIT
                                        }
                                        uiState = uiState.copy(resizeMode = nextMode)
                                        playerViewRef?.resizeMode = nextMode.exoMode
                                    }
                                )

                                // 4. Ambient Interactive Glow (زر الاضائة التفاعلية)
                                AmbientGlowToggleChip(
                                    isEnabled = uiState.isAmbientGlowEnabled,
                                    onToggle = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        uiState = uiState.copy(isAmbientGlowEnabled = !uiState.isAmbientGlowEnabled)
                                    }
                                )

                                // 5. Screen Lock (زر قفل شاشة العرض)
                                ScreenLockButton(
                                    isLocked = uiState.isScreenLocked,
                                    onToggle = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        val newLock = !uiState.isScreenLocked
                                        uiState = uiState.copy(
                                            isScreenLocked = newLock,
                                            areControlsVisible = false
                                        )
                                        if (newLock) {
                                            showUnlockButton = true
                                        }
                                    }
                                )
                            }
                        }

                        // Helpful Floating Gesture Hint Chip (تلميح لمعرفة تقنية السحب والطي)
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .statusBarsPadding()
                                .padding(start = 74.dp, top = 82.dp)
                                .clickable {
                                    showToolbarGestureHint = !showToolbarGestureHint
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xCC0E1424),
                            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TouchApp,
                                    contentDescription = "Hint",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isLeftToolbarCollapsed) "اسحب لأسفل لتمديد الوعاء ↕" else "اسحب لأعلى لطي الوعاء ↕",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // =========================================================================
        // FLOATING SCREEN UNLOCK BUTTON (Elevated higher to completely avoid bottom seekbar/scrubber)
        // =========================================================================
        AnimatedVisibility(
            visible = uiState.isScreenLocked && showUnlockButton,
            enter = fadeIn(tween(220)) + scaleIn(initialScale = 0.85f),
            exit = fadeOut(tween(260)) + scaleOut(targetScale = 0.85f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 145.dp) // Elevated safely away from gesture scrubbing zone
        ) {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = Color(0xF20E1424),
                border = BorderStroke(
                    1.6.dp,
                    Brush.horizontalGradient(
                        listOf(Color(0xFFFFB74D), NeonCyan, Color(0xFFFFB74D))
                    )
                ),
                shadowElevation = 22.dp,
                modifier = Modifier
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        uiState = uiState.copy(isScreenLocked = false, areControlsVisible = true)
                        showUnlockButton = false
                    }
                    .testTag("video_player_floating_unlock_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Unlock",
                        tint = Color(0xFFFFB74D),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "انقر لفك قفل الشاشة",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    )
                }
            }
        }

        // =========================================================================
        // DYNAMIC STRIKE LOCK POPUP BANNER (Liquid Glass Backdrop & Glowing Border)
        // =========================================================================
        AnimatedVisibility(
            visible = showStrikeLockPopup,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 16.dp, start = 20.dp, end = 20.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xEE121826),
                border = BorderStroke(
                    1.4.dp,
                    Brush.horizontalGradient(
                        listOf(Color(0xFFFFB74D), NeonCyan, Color(0xFFFF5252))
                    )
                ),
                shadowElevation = 18.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showStrikeLockPopup = false }
                    .testTag("strike_lock_alert_popup")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFB74D)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFFFFB74D),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ميزة مقفلة في نظام السلسلة",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = strikeLockPopupMessage,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }

        // =========================================================================
        // SETTINGS BOTTOM SHEET (Hide Extensions Toggle, Ambient Glow, Strike Features)
        // =========================================================================
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
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Player Settings & Preferences",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 1. Hide File Extensions Toggle (CRITICAL REQUIREMENT)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = if (uiState.hideFileExtensions) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Hide File Extensions (إخفاء الامتداد)",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = if (uiState.hideFileExtensions) "Titles display clean without .mp4, .mkv" else "Show full filenames with extensions",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                        Switch(
                            checked = uiState.hideFileExtensions,
                            onCheckedChange = { isChecked ->
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                uiState = uiState.copy(hideFileExtensions = isChecked)
                                prefsRepo.saveHideFileExtensions(isChecked)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = NeonCyan,
                                uncheckedThumbColor = Color(0xFF64748B),
                                uncheckedTrackColor = Color(0x33FFFFFF)
                            ),
                            modifier = Modifier.testTag("hide_file_extensions_switch")
                        )
                    }

                    // 2. Ambient Video Glow Fill Setting
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Ambient Video Glow Fill",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "Dynamic scene-matched backlight radiance",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                        Switch(
                            checked = uiState.isAmbientGlowEnabled,
                            onCheckedChange = { isChecked ->
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                uiState = uiState.copy(isAmbientGlowEnabled = isChecked)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = NeonCyan,
                                uncheckedThumbColor = Color(0xFF64748B),
                                uncheckedTrackColor = Color(0x33FFFFFF)
                            ),
                            modifier = Modifier.testTag("ambient_video_glow_switch")
                        )
                    }

                    // 3. Background Play (Strike-Locked at Day 35)
                    val isBackgroundPlayLocked = streakDays < 35
                    PlayerSettingItem(
                        icon = Icons.Default.GraphicEq,
                        title = "Background Audio Playback (التشغيل في الخلفية)",
                        subtitle = if (isBackgroundPlayLocked) "Locked: Unlocks at Streak Day 35" else "Active (Plays with screen locked)",
                        badge = if (isBackgroundPlayLocked) "LOCKED" else "UNLOCKED",
                        isLocked = isBackgroundPlayLocked,
                        onLockedClick = { triggerStrikeLockAlert("Background Play", 35) }
                    )

                    // 4. Offline Sharing (Strike-Locked at Day 20)
                    val isSharingLocked = streakDays < 20
                    PlayerSettingItem(
                        icon = Icons.Default.Share,
                        title = "High-Speed Offline Sharing (مشاركة الفيديو)",
                        subtitle = if (isSharingLocked) "Locked: Unlocks at Streak Day 20" else "Active (P2P Transfer)",
                        badge = if (isSharingLocked) "LOCKED" else "UNLOCKED",
                        isLocked = isSharingLocked,
                        onLockedClick = { triggerStrikeLockAlert("Offline Sharing", 20) }
                    )

                    // 5. Hardware Acceleration
                    PlayerSettingItem(
                        icon = Icons.Default.GraphicEq,
                        title = "Hardware Acceleration",
                        subtitle = "GPU HW+ Decoder Active (120 FPS)",
                        badge = "ENABLED"
                    )

                    // 6. Display Mode
                    PlayerSettingItem(
                        icon = Icons.Default.AspectRatio,
                        title = "Display Mode",
                        subtitle = "Current: ${uiState.resizeMode.title}",
                        badge = uiState.resizeMode.name
                    )

                    // 7. Playback Speed
                    PlayerSettingItem(
                        icon = Icons.Default.Speed,
                        title = "Playback Speed",
                        subtitle = "${uiState.playbackSpeed}x Normal",
                        badge = "${uiState.playbackSpeed}x"
                    )

                    // 8. Video Frame Rotation
                    PlayerSettingItem(
                        icon = Icons.Default.Rotate90DegreesCw,
                        title = "Video Frame Rotation",
                        subtitle = "Angle: ${uiState.videoRotationDegrees}° (Tap to rotate +90°)",
                        badge = "${uiState.videoRotationDegrees}°"
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        // =========================================================================
        // AUDIO & SUBTITLES BOTTOM SHEET
        // =========================================================================
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

// -----------------------------------------------------------------------------
// REUSABLE SUB-COMPONENTS
// -----------------------------------------------------------------------------

@Composable
private fun QualityBadgeChip(qualityText: String = "4K HDR") {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0x2200FFCC),
        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.8f))
    ) {
        Text(
            text = qualityText,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                color = NeonCyan,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp
            )
        )
    }
}

@Composable
private fun AmbientGlowToggleChip(
    isEnabled: Boolean,
    onToggle: () -> Unit
) {
    IconButton(
        onClick = onToggle,
        modifier = Modifier
            .size(32.dp)
            .testTag("video_player_ambient_glow_toggle")
    ) {
        Icon(
            imageVector = Icons.Default.Lightbulb,
            contentDescription = "Toggle Ambient Glow",
            tint = if (isEnabled) NeonCyan else Color(0xFF94A3B8),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun AspectRatioButton(
    resizeMode: VideoResizeMode,
    onToggle: () -> Unit
) {
    IconButton(
        onClick = onToggle,
        modifier = Modifier
            .size(32.dp)
            .testTag("video_player_aspect_ratio_button")
    ) {
        Icon(
            imageVector = when (resizeMode) {
                VideoResizeMode.FIT -> Icons.Default.FitScreen
                VideoResizeMode.CROP -> Icons.Default.Crop
                VideoResizeMode.STRETCH -> Icons.Default.AspectRatio
            },
            contentDescription = "Aspect Ratio: ${resizeMode.title}",
            tint = NeonCyan,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun RotateFrameButton(
    isLandscape: Boolean,
    onRotate: () -> Unit
) {
    IconButton(
        onClick = onRotate,
        modifier = Modifier
            .size(32.dp)
            .testTag("video_player_rotate_frame_button")
    ) {
        Icon(
            imageVector = Icons.Default.ScreenRotation,
            contentDescription = if (isLandscape) "Switch to Portrait" else "Switch to Landscape",
            tint = NeonCyan,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun ScreenLockButton(
    isLocked: Boolean,
    onToggle: () -> Unit
) {
    IconButton(
        onClick = onToggle,
        modifier = Modifier
            .size(32.dp)
            .testTag("video_player_screen_lock_button")
    ) {
        Icon(
            imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
            contentDescription = if (isLocked) "Unlock Screen" else "Lock Screen",
            tint = if (isLocked) Color(0xFFFFB74D) else Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun ScreenOrientationButton(
    isLandscape: Boolean,
    onToggle: () -> Unit
) {
    IconButton(
        onClick = onToggle,
        modifier = Modifier
            .size(32.dp)
            .testTag("video_player_screen_orientation_button")
    ) {
        Icon(
            imageVector = if (isLandscape) Icons.Default.StayCurrentPortrait else Icons.Default.StayCurrentLandscape,
            contentDescription = "Toggle Screen Orientation",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsGearButton(
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(32.dp)
            .testTag("video_player_settings_button")
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun PlayerSettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badge: String,
    isLocked: Boolean = false,
    onLockedClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = isLocked && onLockedClick != null) {
                onLockedClick?.invoke()
            }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isLocked) Color(0xFF64748B) else NeonCyan,
                    modifier = Modifier.size(20.dp)
                )
                if (isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color(0xFFFFB74D),
                        modifier = Modifier
                            .size(10.dp)
                            .align(Alignment.BottomEnd)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isLocked) Color(0xFF94A3B8) else Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (isLocked) Color(0xFFFFB74D) else Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                )
            }
        }
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isLocked) Color(0x33FFB74D) else Color(0x3300FFCC),
            border = BorderStroke(1.dp, if (isLocked) Color(0xFFFFB74D) else NeonCyan.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFFFFB74D),
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                }
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isLocked) Color(0xFFFFB74D) else NeonCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
            }
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
            } catch (_: Exception) {}
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
