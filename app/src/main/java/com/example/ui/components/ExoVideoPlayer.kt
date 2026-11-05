package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem as ExoMediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.data.model.MediaItem
import com.example.ui.theme.PulseCyan
import com.example.ui.theme.PulseViolet
import com.example.ui.theme.TextDark
import com.example.ui.viewmodel.StreakViewModel
import kotlinx.coroutines.delay
import kotlin.math.abs

@OptIn(UnstableApi::class)
@Composable
fun ExoVideoPlayer(
    mediaItem: MediaItem,
    streakViewModel: StreakViewModel?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    BackHandler {
        onClose()
    }

    val streakState = streakViewModel?.uiState?.collectAsState()?.value
    val streakDays = streakState?.streakDays ?: 14
    val isAntiCheatSeekingLocked = streakState != null && !streakState.isGoalCompletedToday

    var isPlaying by remember { mutableStateOf(true) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember { mutableLongStateOf(240_000L) }
    var areControlsVisible by remember { mutableStateOf(true) }

    // Dynamic unlocked features state from Part 1
    val isSharingUnlocked = streakDays >= 20
    val isBackgroundPlayUnlocked = streakDays >= 35
    val isPipUnlocked = streakDays >= 40

    var isBackgroundPlayEnabled by remember { mutableStateOf(false) }
    var playerFeedbackToast by remember { mutableStateOf<String?>(null) }

    // Smart Gestures state (Volume, Brightness, Seeking)
    var currentVolume by remember { mutableFloatStateOf(0.75f) }
    var currentBrightness by remember { mutableFloatStateOf(0.70f) }
    var gestureOverlayType by remember { mutableStateOf<String?>(null) } // "VOLUME", "BRIGHTNESS", "SEEK"
    var seekPreviewText by remember { mutableStateOf("") }

    // Media3 ExoPlayer instance
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

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    val d = exoPlayer.duration
                    if (d > 0) durationMs = d
                }
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.stop()
            exoPlayer.release()
        }
    }

    // Actual 1-second watch tracking towards 5-minute goal
    LaunchedEffect(exoPlayer) {
        while (true) {
            delay(1000)
            if (exoPlayer.isPlaying) {
                currentPositionMs = exoPlayer.currentPosition
                val d = exoPlayer.duration
                if (d > 0) durationMs = d
                // Ticks genuine watch time when playing
                streakViewModel?.tickGenuineWatchTime(1, isSeeking = false)
            }
        }
    }

    // Auto-hide controls & gesture feedback overlay
    LaunchedEffect(areControlsVisible, isPlaying) {
        if (areControlsVisible && isPlaying) {
            delay(4500)
            areControlsVisible = false
        }
    }

    LaunchedEffect(gestureOverlayType) {
        if (gestureOverlayType != null) {
            delay(1500)
            gestureOverlayType = null
        }
    }

    LaunchedEffect(playerFeedbackToast) {
        if (playerFeedbackToast != null) {
            delay(2800)
            playerFeedbackToast = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("exo_video_player_advanced")
            // Smart Gestures Detection: Left vertical = Brightness, Right vertical = Volume, Horizontal = Seek
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        areControlsVisible = true
                    },
                    onDragEnd = {
                        gestureOverlayType = null
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val isRightSide = change.position.x > (size.width / 2f)

                        if (abs(dragAmount.y) > abs(dragAmount.x)) {
                            // Vertical swipe gesture
                            val delta = -dragAmount.y / 350f
                            if (isRightSide) {
                                // Right side: Volume
                                currentVolume = (currentVolume + delta).coerceIn(0f, 1f)
                                exoPlayer.volume = currentVolume
                                gestureOverlayType = "VOLUME"
                            } else {
                                // Left side: Brightness
                                currentBrightness = (currentBrightness + delta).coerceIn(0.1f, 1f)
                                (context as? Activity)?.let { act ->
                                    val lp = act.window.attributes
                                    lp.screenBrightness = currentBrightness
                                    act.window.attributes = lp
                                }
                                gestureOverlayType = "BRIGHTNESS"
                            }
                        } else {
                            // Horizontal swipe gesture: Seeking
                            if (isAntiCheatSeekingLocked) {
                                // Anti-Cheat locked during 5-minute goal!
                                gestureOverlayType = "SEEK_LOCKED"
                                playerFeedbackToast = "🔒 Anti-Cheat Active: Seeking locked until 5-min daily goal is complete!"
                            } else {
                                val seekDeltaMs = (dragAmount.x * 200).toLong()
                                val targetPos = (exoPlayer.currentPosition + seekDeltaMs).coerceIn(0L, durationMs)
                                exoPlayer.seekTo(targetPos)
                                currentPositionMs = targetPos
                                gestureOverlayType = "SEEK"
                                seekPreviewText = "${if (seekDeltaMs >= 0) "+" else ""}${seekDeltaMs / 1000}s (${formatTime(targetPos)})"
                            }
                        }
                    }
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                areControlsVisible = !areControlsVisible
            }
    ) {
        // ExoPlayer Viewport
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    layoutParams = android.view.ViewGroup.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Center Smart Gesture HUD Indicator (Volume, Brightness, Seeking)
        if (gestureOverlayType != null) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xD90B0F19),
                border = androidx.compose.foundation.BorderStroke(1.dp, PulseCyan.copy(alpha = 0.6f)),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (gestureOverlayType) {
                        "VOLUME" -> {
                            Icon(Icons.Default.VolumeUp, null, tint = PulseCyan, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Volume: ${(currentVolume * 100).toInt()}%",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                        "BRIGHTNESS" -> {
                            Icon(Icons.Default.BrightnessHigh, null, tint = Color(0xFFFFD54F), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Brightness: ${(currentBrightness * 100).toInt()}%",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                        "SEEK_LOCKED" -> {
                            Icon(Icons.Default.Lock, null, tint = Color(0xFFFF5722), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Seeking Locked (Anti-Cheat)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF9800)
                                )
                            )
                        }
                        else -> {
                            Icon(Icons.Default.FastForward, null, tint = PulseCyan, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
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

        // Frosted Liquid Glass M5 Controls Overlay
        AnimatedVisibility(
            visible = areControlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xD90B0F19), Color(0x33000000), Color(0xFA0B0F19))
                        )
                    )
            ) {
                // Top Floating Glass Header
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0x80121826),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onClose()
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x33FFFFFF))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Close player",
                                    tint = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = mediaItem.titleArabic ?: mediaItem.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    ),
                                    maxLines = 1
                                )
                                Text(
                                    text = "Liquid Glass M5 Player • 120fps",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = PulseCyan,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        // Anti-Cheat Streak Tracking Pill
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isAntiCheatSeekingLocked) Color(0x33FF5722) else Color(0x3310B981),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isAntiCheatSeekingLocked) Color(0xFFFF5722) else Color(0xFF10B981)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isAntiCheatSeekingLocked) Icons.Default.Lock else Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = if (isAntiCheatSeekingLocked) Color(0xFFFF9800) else Color(0xFF34D399),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (isAntiCheatSeekingLocked) {
                                        val min = (streakState?.dailyWatchSeconds ?: 0) / 60
                                        val sec = (streakState?.dailyWatchSeconds ?: 0) % 60
                                        String.format("Anti-Cheat: %02d:%02d/05:00", min, sec)
                                    } else {
                                        "Goal Met (Seek Unlocked)"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isAntiCheatSeekingLocked) Color(0xFFFFB74D) else Color(0xFF34D399),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Dynamic Controls Toolbar: Sharing (Day 20), Background Play (Day 35), PiP (Day 40)
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(top = 70.dp, end = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xD9121826),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF))
                ) {
                    Column(
                        modifier = Modifier.padding(6.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. Media Sharing (Milestone Day 20)
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                if (isSharingUnlocked) {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "Playing ${mediaItem.title} on Video Pulse M5!")
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Media"))
                                } else {
                                    playerFeedbackToast = "🔒 Media Sharing unlocks at Day 20 streak milestone!"
                                }
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (isSharingUnlocked) Color(0x3300F2FE) else Color(0x1F475569))
                        ) {
                            Icon(
                                imageVector = if (isSharingUnlocked) Icons.Default.Share else Icons.Default.Lock,
                                contentDescription = "Share",
                                tint = if (isSharingUnlocked) PulseCyan else Color(0xFF94A3B8),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // 2. Background Play (Milestone Day 35)
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                if (isBackgroundPlayUnlocked) {
                                    isBackgroundPlayEnabled = !isBackgroundPlayEnabled
                                    playerFeedbackToast = if (isBackgroundPlayEnabled) "🎧 Background Play ON" else "🎧 Background Play OFF"
                                } else {
                                    playerFeedbackToast = "🔒 Background Play unlocks at Day 35 streak milestone!"
                                }
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isBackgroundPlayUnlocked && isBackgroundPlayEnabled) PulseCyan
                                    else if (isBackgroundPlayUnlocked) Color(0x3300F2FE)
                                    else Color(0x1F475569)
                                )
                        ) {
                            Icon(
                                imageVector = if (isBackgroundPlayUnlocked) Icons.Default.Headset else Icons.Default.Lock,
                                contentDescription = "Background Play",
                                tint = if (isBackgroundPlayUnlocked && isBackgroundPlayEnabled) TextDark
                                else if (isBackgroundPlayUnlocked) PulseCyan
                                else Color(0xFF94A3B8),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // 3. Pop-up / PiP Mode (Milestone Day 40)
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                if (isPipUnlocked) {
                                    playerFeedbackToast = "📺 Floating Picture-in-Picture window activated!"
                                } else {
                                    playerFeedbackToast = "🔒 Pop-up PiP Mode unlocks at Day 40 pinnacle milestone!"
                                }
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (isPipUnlocked) Color(0x3300F2FE) else Color(0x1F475569))
                        ) {
                            Icon(
                                imageVector = if (isPipUnlocked) Icons.Default.PictureInPicture else Icons.Default.Lock,
                                contentDescription = "PiP Mode",
                                tint = if (isPipUnlocked) PulseCyan else Color(0xFF94A3B8),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Center Play/Rewind/Forward Controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rewind 10s (Respects Anti-Cheat)
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0x66000000))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                if (isAntiCheatSeekingLocked) {
                                    playerFeedbackToast = "🔒 Seeking disabled until 5-minute daily watch goal is reached!"
                                } else {
                                    val newPos = (exoPlayer.currentPosition - 10_000L).coerceAtLeast(0L)
                                    exoPlayer.seekTo(newPos)
                                    currentPositionMs = newPos
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastRewind,
                            contentDescription = "Rewind 10s",
                            tint = if (isAntiCheatSeekingLocked) Color(0xFF64748B) else Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(36.dp))

                    // Big Play/Pause with Glowing Neon Rim
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .drawBehind {
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        listOf(PulseCyan.copy(alpha = 0.5f), PulseViolet.copy(alpha = 0.2f), Color.Transparent),
                                        radius = size.width * 0.95f
                                    )
                                )
                            }
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                if (exoPlayer.isPlaying) {
                                    exoPlayer.pause()
                                    isPlaying = false
                                } else {
                                    exoPlayer.play()
                                    isPlaying = true
                                }
                            }
                            .testTag("exo_play_pause_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = TextDark,
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(36.dp))

                    // Fast-Forward 10s (Respects Anti-Cheat)
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0x66000000))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                if (isAntiCheatSeekingLocked) {
                                    playerFeedbackToast = "🔒 Fast-forward disabled until 5-minute daily watch goal is reached!"
                                } else {
                                    val newPos = (exoPlayer.currentPosition + 10_000L).coerceAtMost(durationMs)
                                    exoPlayer.seekTo(newPos)
                                    currentPositionMs = newPos
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastForward,
                            contentDescription = "Forward 10s",
                            tint = if (isAntiCheatSeekingLocked) Color(0xFF64748B) else Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Toast Feedback Badge inside player
                if (playerFeedbackToast != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xF2121826),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PulseCyan),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 120.dp, start = 20.dp, end = 20.dp)
                    ) {
                        Text(
                            text = playerFeedbackToast ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }

                // Bottom Floating Glass Control Bar (Scrubber & Smart Gesture Guide)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    shape = RoundedCornerShape(22.dp),
                    color = Color(0xE6121826),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF))
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                        val progressRatio = if (durationMs > 0) (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

                        Slider(
                            value = progressRatio,
                            enabled = !isAntiCheatSeekingLocked,
                            onValueChange = { ratio ->
                                if (!isAntiCheatSeekingLocked) {
                                    val targetMs = (ratio * durationMs).toLong()
                                    currentPositionMs = targetMs
                                }
                            },
                            onValueChangeFinished = {
                                if (!isAntiCheatSeekingLocked) {
                                    exoPlayer.seekTo(currentPositionMs)
                                }
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = if (isAntiCheatSeekingLocked) Color(0xFF64748B) else PulseCyan,
                                activeTrackColor = if (isAntiCheatSeekingLocked) Color(0xFF64748B) else PulseCyan,
                                inactiveTrackColor = Color(0x33FFFFFF),
                                disabledThumbColor = Color(0xFF64748B),
                                disabledActiveTrackColor = Color(0xFF475569)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatTime(currentPositionMs),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )

                            Text(
                                text = "Swipe Left: Brightness • Swipe Right: Volume",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            )

                            Text(
                                text = formatTime(durationMs),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
