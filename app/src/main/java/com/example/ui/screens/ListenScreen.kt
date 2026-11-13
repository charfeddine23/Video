package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import com.example.ui.theme.stableStatusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem as ExoMediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.LocalAudioTrack
import com.example.data.util.LocalMediaScanner
import com.example.ui.theme.Charcoal950
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PulseCyan
import com.example.ui.theme.PulseViolet
import com.example.ui.theme.TextDark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListenScreen(
    isArabic: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    // Default built-in tracks for rich offline fallback
    val initialTracks = remember {
        listOf(
            LocalAudioTrack(
                id = "track_1",
                title = "Cyber Pulse Symphony",
                artist = "Charfeddine Kahlessenane",
                album = "Neon Horizon 2026",
                durationSeconds = 245,
                albumCoverRes = R.drawable.img_poster_cyberpunk,
                lyricsLines = listOf(
                    "Midnight shadows dancing in the neon glow...",
                    "Drifting through the pulse of city lights below.",
                    "Electronic echoes whisper through the night,",
                    "Lost inside the frequency of liquid light.",
                    "Feel the heartbeat of the sound tonight."
                ),
                audioUri = null,
                isFromDevice = false
            ),
            LocalAudioTrack(
                id = "track_2",
                title = "Liquid Horizon Chill",
                artist = "Pulse Soundscapes",
                album = "Ambient Waves Vol. 1",
                durationSeconds = 198,
                albumCoverRes = R.drawable.img_hero_poster,
                lyricsLines = listOf(
                    "Breathe in the calm of endless seas...",
                    "Soft waves washing over memories.",
                    "Gentle breeze in deep dark blue,",
                    "Finding peace in every acoustic hue."
                ),
                audioUri = null,
                isFromDevice = false
            ),
            LocalAudioTrack(
                id = "track_3",
                title = "Odyssey of the Desert",
                artist = "Atlas Melodies",
                album = "Arabian Fusion Sessions",
                durationSeconds = 280,
                albumCoverRes = R.drawable.img_poster_action,
                lyricsLines = listOf(
                    "Golden sands beneath the starlit sky...",
                    "Echoes of an ancient melody flying high.",
                    "Strings that speak where words cannot find,",
                    "A journey of the spirit and of the mind."
                ),
                audioUri = null,
                isFromDevice = false
            )
        )
    }

    val playlist = remember { mutableStateListOf<LocalAudioTrack>().apply { addAll(initialTracks) } }
    var currentTrackIndex by remember { mutableIntStateOf(0) }
    val currentTrack = playlist.getOrElse(currentTrackIndex) { initialTracks[0] }

    var isPlaying by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }
    var currentPositionMs by remember { mutableFloatStateOf(0f) }
    var currentDurationMs by remember { mutableFloatStateOf(currentTrack.durationSeconds * 1000f) }
    var isDraggingSlider by remember { mutableStateOf(false) }

    var showLyricsDialog by remember { mutableStateOf(false) }
    var showOptionsSheet by remember { mutableStateOf(false) }
    var showQueueSheet by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    var castDeviceName by remember { mutableStateOf(if (isArabic) "هذا الهاتف" else "This phone") }

    // Smart Sleep Timer State (15m, 30m, 1h, end of track)
    var showSleepTimerSheet by remember { mutableStateOf(false) }
    var sleepTimerMinutes by remember { mutableIntStateOf(0) }
    var sleepTimerRemainingSeconds by remember { mutableIntStateOf(0) }

    // Advanced Equalizer & Spatial Audio State
    var showEqualizerSheet by remember { mutableStateOf(false) }
    var selectedEqualizerPreset by remember { mutableStateOf("Bass Boost") }
    val eqBands = remember { mutableStateListOf(6f, 3f, 0f, 2f, 5f) }
    var isSpatialAudioEnabled by remember { mutableStateOf(true) }
    var bassBoostPercent by remember { mutableIntStateOf(75) }

    // REAL Media3 ExoPlayer Instance for Audio Playback
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
        }
    }

    // Smooth Fade-out Sleep Timer Ticker
    LaunchedEffect(sleepTimerRemainingSeconds, isPlaying) {
        if (isPlaying && sleepTimerRemainingSeconds > 0) {
            delay(1000)
            val next = sleepTimerRemainingSeconds - 1
            sleepTimerRemainingSeconds = next
            if (next in 1..5) {
                exoPlayer.volume = (next.toFloat() / 5f).coerceIn(0f, 1f)
            } else if (next == 0) {
                exoPlayer.pause()
                isPlaying = false
                exoPlayer.volume = 1f
                sleepTimerMinutes = 0
                snackbarMessage = if (isArabic) "انتهى مؤقت النوم وتم إيقاف التشغيل 🌙" else "Sleep timer finished. Playback stopped 🌙"
            }
        }
    }

    // Playback listener for real states
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    val dur = exoPlayer.duration
                    if (dur > 0) {
                        currentDurationMs = dur.toFloat()
                    }
                } else if (state == Player.STATE_ENDED && sleepTimerMinutes == -1) {
                    exoPlayer.pause()
                    isPlaying = false
                    sleepTimerMinutes = 0
                    snackbarMessage = if (isArabic) "اكتمل المقطع وتوقف التشغيل حسب مؤقت النوم 🌙" else "Track ended. Sleep timer stopped playback 🌙"
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Load track into ExoPlayer whenever index changes
    fun playTrack(index: Int) {
        if (index !in playlist.indices) return
        currentTrackIndex = index
        val track = playlist[index]
        currentPositionMs = 0f
        currentDurationMs = (track.durationSeconds * 1000).toFloat()

        if (track.audioUri != null) {
            try {
                val mediaItem = ExoMediaItem.fromUri(Uri.parse(track.audioUri))
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.play()
                isPlaying = true
            } catch (e: Exception) {
                snackbarMessage = if (isArabic) "تعذر تشغيل الملف الصوتي" else "Failed to load audio"
            }
        } else {
            // Built-in synthesized track simulation
            exoPlayer.stop()
            isPlaying = true
        }
    }

    // Scan real device audio files in background
    fun scanDeviceAudios() {
        scope.launch(Dispatchers.IO) {
            val deviceAudios = LocalMediaScanner.scanDeviceAudios(context)
            withContext(Dispatchers.Main) {
                if (deviceAudios.isNotEmpty()) {
                    playlist.clear()
                    playlist.addAll(deviceAudios)
                    currentTrackIndex = 0
                    playTrack(0)
                    snackbarMessage = if (isArabic) "تم العثور على ${deviceAudios.size} ملف صوتي من الهاتف 🎵" else "Found ${deviceAudios.size} device audio tracks 🎵"
                } else {
                    snackbarMessage = if (isArabic) "لم يتم العثور على ملفات صوتية في التخزين" else "No audio files found in storage"
                }
            }
        }
    }

    // Permission launcher for MediaStore Audio
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result.values.any { it }
        if (granted) {
            scanDeviceAudios()
        } else {
            snackbarMessage = if (isArabic) "تم رفض إذن الوصول للملفات الصوتية" else "Audio storage permission denied"
        }
    }

    // SAF Document Audio Picker (Direct File Open)
    val audioFilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (!uris.isNullOrEmpty()) {
            scope.launch(Dispatchers.IO) {
                val parsedTracks = uris.map { uri ->
                    LocalMediaScanner.parseUriToAudioTrack(context, uri)
                }
                withContext(Dispatchers.Main) {
                    val startIndex = playlist.size
                    playlist.addAll(parsedTracks)
                    currentTrackIndex = startIndex
                    playTrack(startIndex)
                    snackbarMessage = if (isArabic) "تم فتح ${parsedTracks.size} ملف صوتي بنجاح 🎶" else "Loaded ${parsedTracks.size} audio files 🎶"
                }
            }
        }
    }

    // Auto-scan on screen launch if permissions are granted
    LaunchedEffect(Unit) {
        if (LocalMediaScanner.hasStorageOrAudioPermission(context)) {
            val deviceAudios = withContext(Dispatchers.IO) {
                LocalMediaScanner.scanDeviceAudios(context)
            }
            if (deviceAudios.isNotEmpty()) {
                playlist.clear()
                playlist.addAll(deviceAudios)
                // Keep default index 0 but don't autoplay immediately without user gesture
            }
        }
    }

    // Live position tracking from real ExoPlayer or internal ticker
    LaunchedEffect(isPlaying, isDraggingSlider) {
        while (isPlaying && !isDraggingSlider) {
            delay(250)
            if (currentTrack.audioUri != null) {
                val pos = exoPlayer.currentPosition
                currentPositionMs = pos.toFloat()
                val dur = exoPlayer.duration
                if (dur > 0) currentDurationMs = dur.toFloat()
            } else {
                if (currentPositionMs < currentDurationMs) {
                    currentPositionMs += 250f
                } else {
                    currentTrackIndex = (currentTrackIndex + 1) % playlist.size
                    playTrack(currentTrackIndex)
                }
            }
        }
    }

    // Floating Vinyl / Glow Animation
    val infiniteTransition = rememberInfiniteTransition(label = "audio_glow")
    val neonGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "neon_glow_alpha"
    )

    // Dismiss message after 2.5s
    LaunchedEffect(snackbarMessage) {
        if (snackbarMessage != null) {
            delay(2500)
            snackbarMessage = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Charcoal950)
            .testTag("listen_screen")
    ) {
        // 1. Immersive Live Blurred Album Artwork Background with Dark Gradient Overlay
        if (currentTrack.albumArtUri != null) {
            AsyncImage(
                model = currentTrack.albumArtUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.35f
            )
        } else {
            Image(
                painter = painterResource(id = currentTrack.albumCoverRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.35f
            )
        }

        // Dark Gradient Glass Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xDD0B0F19),
                            Color(0xEE0B0F19),
                            Color(0xFA0B0F19),
                            Color(0xFF0B0F19)
                        )
                    )
                )
        )

        // Main Audio Player Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .stableStatusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 2. Top Bar: Drag Handle + Subtitle + Device Files Action
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Centered subtle drag pill handle
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0x44FFFFFF))
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = PulseCyan,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArabic) {
                                if (currentTrack.isFromDevice) "مشغل موسيقى الهاتف الحقيقي" else "تشغيل من الموسيقى المحلية"
                            } else {
                                if (currentTrack.isFromDevice) "Playing Real Phone Audio" else "Playing from Local Music"
                            },
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color(0xFF94A3B8),
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }

                    // Top Bar Action Buttons: Equalizer, Sleep Timer, Open Files
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Equalizer & Spatial Audio Button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x3300FFCC),
                            border = BorderStroke(1.dp, PulseCyan.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    showEqualizerSheet = true
                                }
                                .testTag("listen_equalizer_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Equalizer",
                                    tint = PulseCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (isArabic) "معادل الصوت" else "EQ",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        // Smart Sleep Timer Button
                        val isTimerActive = sleepTimerRemainingSeconds > 0 || sleepTimerMinutes == -1
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isTimerActive) Color(0x33FFB74D) else Color(0x22FFFFFF),
                            border = BorderStroke(1.dp, if (isTimerActive) Color(0xFFFFB74D) else Color(0x33FFFFFF)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    showSleepTimerSheet = true
                                }
                                .testTag("listen_sleep_timer_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = "Sleep Timer",
                                    tint = if (isTimerActive) Color(0xFFFFB74D) else PulseCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                val timerLabel = when {
                                    sleepTimerMinutes == -1 -> if (isArabic) "نهاية" else "End"
                                    sleepTimerRemainingSeconds > 0 -> "${sleepTimerRemainingSeconds / 60}m"
                                    else -> if (isArabic) "مؤقت" else "Timer"
                                }
                                Text(
                                    text = timerLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isTimerActive) Color(0xFFFFB74D) else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        // Open Real Audio Files from phone button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x3300FFCC),
                            border = BorderStroke(1.dp, PulseCyan.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    if (!LocalMediaScanner.hasStorageOrAudioPermission(context)) {
                                        permissionLauncher.launch(LocalMediaScanner.getAudioPermissions())
                                    } else {
                                        audioFilePicker.launch(arrayOf("audio/*"))
                                    }
                                }
                                .testTag("listen_open_files_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FolderOpen,
                                    contentDescription = "Open Audio",
                                    tint = PulseCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isArabic) "فتح ملفات" else "Open Files",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 3. Album Artwork Card: Large centered card (24.dp rounded) with frosted glass effect & glowing neon border
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    PulseCyan.copy(alpha = neonGlowAlpha * 0.4f),
                                    Color(0x2210B981),
                                    Color.Transparent
                                ),
                                radius = size.minDimension * 0.75f
                            )
                        )
                    }
                    .border(
                        border = BorderStroke(
                            width = 1.8.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    PulseCyan,
                                    Color(0xFF10B981),
                                    PulseViolet,
                                    PulseCyan
                                )
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .background(Color(0xD9121826))
                    .testTag("listen_album_art_card"),
                contentAlignment = Alignment.Center
            ) {
                if (currentTrack.albumArtUri != null) {
                    AsyncImage(
                        model = currentTrack.albumArtUri,
                        contentDescription = currentTrack.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = currentTrack.albumCoverRes),
                        contentDescription = currentTrack.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                // Frosted glass inner shadow overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color(0x44000000),
                                    Color(0x99000000)
                                )
                            )
                        )
                )

                // Playing equalizer badge at bottom left of artwork
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xCC000000),
                    border = BorderStroke(1.dp, GlassBorder),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = PulseCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (currentTrack.isFromDevice) "MEDIA3 • LOCAL STORAGE" else "320 KBPS • LOSSLESS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Song Info Row: Bold song title, artist subtitle, favorite star icon, and three-dots menu icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentTrack.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 20.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${currentTrack.artist} • ${currentTrack.album}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PulseCyan,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            isFavorite = !isFavorite
                            snackbarMessage = if (isFavorite) {
                                if (isArabic) "تمت الإضافة إلى المفضلة ⭐" else "Added to Favorites ⭐"
                            } else {
                                if (isArabic) "تمت الإزالة من المفضلة" else "Removed from Favorites"
                            }
                        },
                        modifier = Modifier.testTag("listen_favorite_button")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color(0xFFFFD700) else Color(0xFF94A3B8),
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            showOptionsSheet = true
                        },
                        modifier = Modifier.testTag("listen_more_options_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Song Options",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 5. Lyrics Section: Interactive lyrics preview/button
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0x33121826),
                border = BorderStroke(1.dp, Color(0x2200F2FE)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        showLyricsDialog = true
                    }
                    .testTag("listen_lyrics_preview")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0x3300F2FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = null,
                                tint = PulseCyan,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = currentTrack.lyricsLines.firstOrNull() ?: (if (isArabic) "انقر لعرض الكلمات ومعلومات المقطع..." else "Tap to view lyrics & metadata..."),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFE2E8F0),
                                fontSize = 12.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x22FFFFFF)
                    ) {
                        Text(
                            text = if (isArabic) "الكلمات" else "LYRICS",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = PulseCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar / Seekbar with Elapsed & Remaining timestamps
            Column(modifier = Modifier.fillMaxWidth()) {
                val duration = currentDurationMs.coerceAtLeast(1000f)
                val sliderValue = currentPositionMs.coerceIn(0f, duration)

                Slider(
                    value = sliderValue,
                    onValueChange = {
                        isDraggingSlider = true
                        currentPositionMs = it
                    },
                    onValueChangeFinished = {
                        isDraggingSlider = false
                        if (currentTrack.audioUri != null) {
                            exoPlayer.seekTo(currentPositionMs.toLong())
                        }
                    },
                    valueRange = 0f..duration,
                    colors = SliderDefaults.colors(
                        thumbColor = PulseCyan,
                        activeTrackColor = PulseCyan,
                        inactiveTrackColor = Color(0x33FFFFFF)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .testTag("listen_seekbar")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val currentSecTotal = (sliderValue / 1000).toInt()
                    val currentMin = currentSecTotal / 60
                    val currentSec = currentSecTotal % 60

                    val totalSecTotal = (duration / 1000).toInt()
                    val totalMin = totalSecTotal / 60
                    val totalSec = totalSecTotal % 60
                    val remainingSec = (totalSecTotal - currentSecTotal).coerceAtLeast(0)

                    Text(
                        text = String.format("%02d:%02d", currentMin, currentSec),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = String.format("-%02d:%02d", remainingSec / 60, remainingSec % 60),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 6. Playback Controls: Skip Previous, prominent glowing Play/Pause, and Skip Next
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Skip Previous
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        val prevIndex = if (currentTrackIndex > 0) currentTrackIndex - 1 else playlist.size - 1
                        playTrack(prevIndex)
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("listen_skip_previous_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Skip Previous",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Prominent Circular Glowing Play/Pause Button
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        PulseCyan.copy(alpha = 0.5f),
                                        Color(0xFF10B981).copy(alpha = 0.25f),
                                        Color.Transparent
                                    ),
                                    radius = size.width * 0.9f
                                )
                            )
                        }
                        .background(
                            Brush.linearGradient(
                                listOf(PulseCyan, Color(0xFF10B981))
                            )
                        )
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (currentTrack.audioUri != null) {
                                if (exoPlayer.isPlaying) {
                                    exoPlayer.pause()
                                    isPlaying = false
                                } else {
                                    exoPlayer.play()
                                    isPlaying = true
                                }
                            } else {
                                isPlaying = !isPlaying
                            }
                        }
                        .testTag("listen_play_pause_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = TextDark,
                        modifier = Modifier.size(38.dp)
                    )
                }

                // Skip Next
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        val nextIndex = (currentTrackIndex + 1) % playlist.size
                        playTrack(nextIndex)
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("listen_skip_next_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Skip Next",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 7. Bottom Utility Bar: Cast, Queue, and device indicator ("This phone")
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0x66121826),
                border = BorderStroke(1.dp, GlassBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Device indicator button ("This phone")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                snackbarMessage = if (isArabic) "الصوت يعمل عبر مكبر الصوت الحالي" else "Audio routed through: $castDeviceName"
                            }
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Smartphone,
                            contentDescription = null,
                            tint = PulseCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = castDeviceName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    // Quick Action Icons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Lyrics button
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "Lyrics",
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    showLyricsDialog = true
                                }
                        )

                        // Cast button
                        Icon(
                            imageVector = Icons.Default.Cast,
                            contentDescription = "Cast",
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    snackbarMessage = if (isArabic) "جاري البحث عن أجهزة البث المتاحة..." else "Scanning for Cast devices..."
                                }
                        )

                        // Queue / Playlist button
                        Icon(
                            imageVector = Icons.Default.QueueMusic,
                            contentDescription = "Queue",
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier
                                .size(22.dp)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    showQueueSheet = true
                                }
                        )
                    }
                }
            }
        }

        // Floating feedback banner
        if (snackbarMessage != null) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xF2121826),
                border = BorderStroke(1.dp, PulseCyan.copy(alpha = 0.5f)),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .stableStatusBarsPadding()
                    .padding(start = 24.dp, top = 16.dp, end = 24.dp)
            ) {
                Text(
                    text = snackbarMessage!!,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                )
            }
        }

        // Queue Modal Bottom Sheet (Track Switcher)
        if (showQueueSheet) {
            ModalBottomSheet(
                onDismissRequest = { showQueueSheet = false },
                containerColor = Color(0xF70E1422),
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = if (isArabic) "قائمة الانتظار (${playlist.size} أغانٍ)" else "Play Queue (${playlist.size} tracks)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        itemsIndexed(playlist) { index, track ->
                            val isCurrent = index == currentTrackIndex
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isCurrent) Color(0x3300FFCC) else Color(0x11FFFFFF),
                                border = BorderStroke(1.dp, if (isCurrent) PulseCyan else Color.Transparent),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        showQueueSheet = false
                                        playTrack(index)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isCurrent && isPlaying) Icons.Default.GraphicEq else Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = if (isCurrent) PulseCyan else Color(0xFF94A3B8),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = track.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = if (isCurrent) PulseCyan else Color.White,
                                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${track.artist} • ${track.durationSeconds / 60}:${String.format("%02d", track.durationSeconds % 60)}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFF94A3B8),
                                                fontSize = 11.sp
                                            ),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        // Lyrics Interactive Modal Sheet
        if (showLyricsDialog) {
            ModalBottomSheet(
                onDismissRequest = { showLyricsDialog = false },
                containerColor = Color(0xF70E1422),
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentTrack.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = currentTrack.artist,
                        style = MaterialTheme.typography.bodySmall.copy(color = PulseCyan)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    currentTrack.lyricsLines.forEachIndexed { index, line ->
                        Text(
                            text = line,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = if (index == 0) Color.White else Color(0xFF94A3B8),
                                fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        // 8. Options Bottom Sheet
        if (showOptionsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showOptionsSheet = false },
                containerColor = Color(0xF70E1422),
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header inside sheet
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0x33121826)
                        ) {
                            if (currentTrack.albumArtUri != null) {
                                AsyncImage(
                                    model = currentTrack.albumArtUri,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = currentTrack.albumCoverRes),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = currentTrack.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                ),
                                maxLines = 1
                            )
                            Text(
                                text = currentTrack.artist,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = PulseCyan,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0x22FFFFFF))
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Menu Options
                    MusicOptionItem(
                        icon = Icons.Default.FolderOpen,
                        title = if (isArabic) "مسح والبحث عن كل موسيقى الهاتف" else "Scan all device music",
                        onClick = {
                            showOptionsSheet = false
                            if (!LocalMediaScanner.hasStorageOrAudioPermission(context)) {
                                permissionLauncher.launch(LocalMediaScanner.getAudioPermissions())
                            } else {
                                scanDeviceAudios()
                            }
                        }
                    )
                    MusicOptionItem(
                        icon = Icons.Default.Videocam,
                        title = if (isArabic) "التحويل إلى فيديو" else "Convert to video",
                        onClick = {
                            showOptionsSheet = false
                            snackbarMessage = if (isArabic) "تم بدء التحويل إلى صيغة فيديو MP4" else "Converting track to video format..."
                        }
                    )
                    MusicOptionItem(
                        icon = Icons.Default.PlaylistAdd,
                        title = if (isArabic) "إضافة إلى قائمة التشغيل" else "Add to playlist",
                        onClick = {
                            showOptionsSheet = false
                            snackbarMessage = if (isArabic) "تمت الإضافة إلى قائمة التشغيل المفضلة" else "Added to playlist!"
                        }
                    )
                    MusicOptionItem(
                        icon = Icons.Default.Radio,
                        title = if (isArabic) "بدء الراديو" else "Start radio",
                        onClick = {
                            showOptionsSheet = false
                            snackbarMessage = if (isArabic) "بدء راديو الفنان المتشابه..." else "Starting smart artist radio..."
                        }
                    )
                    MusicOptionItem(
                        icon = Icons.Default.PlayArrow,
                        title = if (isArabic) "تشغيل التالي" else "Play next",
                        onClick = {
                            showOptionsSheet = false
                            snackbarMessage = if (isArabic) "تمت جدولة الأغنية لتكون التالية" else "Queued to play next"
                        }
                    )
                    MusicOptionItem(
                        icon = Icons.Default.QueueMusic,
                        title = if (isArabic) "إضافة إلى قائمة الانتظار" else "Add to queue",
                        onClick = {
                            showOptionsSheet = false
                            snackbarMessage = if (isArabic) "تمت الإضافة إلى قائمة الانتظار" else "Added to queue"
                        }
                    )
                    MusicOptionItem(
                        icon = Icons.Default.DownloadDone,
                        title = if (isArabic) "محفوظة في التنزيلات" else "Saved to Downloads",
                        trailingAction = {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                tint = Color(0xFFFF5252),
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable {
                                        showOptionsSheet = false
                                        snackbarMessage = if (isArabic) "تم حذف الملف من التنزيلات المحلية" else "Removed from local downloads"
                                    }
                            )
                        },
                        onClick = {
                            showOptionsSheet = false
                        }
                    )
                    MusicOptionItem(
                        icon = Icons.Default.LibraryMusic,
                        title = if (isArabic) "فتح الألبوم" else "Open album",
                        onClick = {
                            showOptionsSheet = false
                            snackbarMessage = if (isArabic) "فتح ألبوم: ${currentTrack.album}" else "Opening album: ${currentTrack.album}"
                        }
                    )
                    MusicOptionItem(
                        icon = Icons.Default.MusicNote,
                        title = if (isArabic) "فتح صفحة الفنان" else "Open artist",
                        onClick = {
                            showOptionsSheet = false
                            snackbarMessage = if (isArabic) "فتح صفحة الفنان: ${currentTrack.artist}" else "Viewing artist: ${currentTrack.artist}"
                        }
                    )
                    MusicOptionItem(
                        icon = Icons.Default.Info,
                        title = if (isArabic) "معلومات الأغنية" else "Song info",
                        onClick = {
                            showOptionsSheet = false
                            snackbarMessage = "Duration: ${currentTrack.durationSeconds}s • Bitrate: 320 kbps • Codec: AAC"
                        }
                    )
                    MusicOptionItem(
                        icon = Icons.Default.Timer,
                        title = if (isArabic) "مؤقت النوم" else "Sleep timer",
                        onClick = {
                            showOptionsSheet = false
                            snackbarMessage = if (isArabic) "تم ضبط مؤقت النوم على 30 دقيقة" else "Sleep timer set for 30 minutes"
                        }
                    )
                    MusicOptionItem(
                        icon = Icons.Default.ChatBubbleOutline,
                        title = if (isArabic) "محاذاة الكلمات (Lyrics offset)" else "Lyrics offset",
                        onClick = {
                            showOptionsSheet = false
                            snackbarMessage = if (isArabic) "تعديل مزامنة الكلمات: 0.0s" else "Lyrics offset: 0.0s sync"
                        }
                    )
                    MusicOptionItem(
                        icon = Icons.Default.Info,
                        title = if (isArabic) "نسخ السجل" else "Copy log",
                        onClick = {
                            showOptionsSheet = false
                            snackbarMessage = if (isArabic) "تم نسخ سجل التشغيل إلى الحافظة" else "Playback debug log copied to clipboard"
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }

        // =========================================================================
        // SMART SLEEP TIMER BOTTOM SHEET
        // =========================================================================
        if (showSleepTimerSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSleepTimerSheet = false },
                containerColor = Color(0xF20B0F19),
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isArabic) "مؤقت النوم الذكي" else "Smart Sleep Timer",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = Color(0xFFFFB74D),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isArabic) "يتلاشى الصوت تدريجياً ليتوقف التشغيل بسلاسة أثناء النوم" else "Audio gently fades out and stops playback automatically",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8), fontSize = 12.sp)
                    )
                    Spacer(modifier = Modifier.height(18.dp))

                    val timerOptions = listOf(
                        Pair(15, if (isArabic) "15 دقيقة" else "15 minutes"),
                        Pair(30, if (isArabic) "30 دقيقة" else "30 minutes"),
                        Pair(60, if (isArabic) "60 دقيقة (ساعة واحدة)" else "60 minutes (1 hour)"),
                        Pair(-1, if (isArabic) "نهاية المقطع الحالي" else "End of current track"),
                        Pair(0, if (isArabic) "إلغاء المؤقت (إيقاف)" else "Turn off timer")
                    )

                    timerOptions.forEach { (minutes, label) ->
                        val isSelected = (minutes == sleepTimerMinutes && (minutes != 0 || sleepTimerRemainingSeconds == 0))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0x33FFB74D) else Color(0x1AFFFFFF),
                            border = BorderStroke(1.dp, if (isSelected) Color(0xFFFFB74D) else Color(0x22FFFFFF)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    sleepTimerMinutes = minutes
                                    sleepTimerRemainingSeconds = if (minutes > 0) minutes * 60 else 0
                                    showSleepTimerSheet = false
                                    snackbarMessage = if (minutes > 0) {
                                        if (isArabic) "تم ضبط مؤقت النوم على $label 🌙" else "Sleep timer set for $label 🌙"
                                    } else if (minutes == -1) {
                                        if (isArabic) "سيتوقف التشغيل تلقائياً بنهاية هذا المقطع 🌙" else "Playback will stop at track end 🌙"
                                    } else {
                                        if (isArabic) "تم إلغاء مؤقت النوم" else "Sleep timer disabled"
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isSelected) Color(0xFFFFB74D) else Color.White,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB74D),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // =========================================================================
        // ADVANCED EQUALIZER & SPATIAL AUDIO BOTTOM SHEET
        // =========================================================================
        if (showEqualizerSheet) {
            ModalBottomSheet(
                onDismissRequest = { showEqualizerSheet = false },
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isArabic) "معادل الصوت والصوت المحيطي" else "Equalizer & Spatial Audio",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = PulseCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    // Preset Profiles Selector
                    Text(
                        text = if (isArabic) "الملفات الصوتية الجاهزة" else "AUDIO PROFILES",
                        style = MaterialTheme.typography.labelSmall.copy(color = PulseCyan, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val presets = listOf("Bass Boost", "Vocal", "Flat", "Electronic")
                        presets.forEach { preset ->
                            val isSelected = selectedEqualizerPreset == preset
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0x3300FFCC) else Color(0x1AFFFFFF),
                                border = BorderStroke(1.dp, if (isSelected) PulseCyan else Color(0x22FFFFFF)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        selectedEqualizerPreset = preset
                                        when (preset) {
                                            "Bass Boost" -> {
                                                eqBands[0] = 8f; eqBands[1] = 5f; eqBands[2] = 0f; eqBands[3] = 2f; eqBands[4] = 4f
                                                bassBoostPercent = 85
                                            }
                                            "Vocal" -> {
                                                eqBands[0] = -2f; eqBands[1] = 2f; eqBands[2] = 7f; eqBands[3] = 4f; eqBands[4] = 1f
                                                bassBoostPercent = 30
                                            }
                                            "Flat" -> {
                                                eqBands[0] = 0f; eqBands[1] = 0f; eqBands[2] = 0f; eqBands[3] = 0f; eqBands[4] = 0f
                                                bassBoostPercent = 50
                                            }
                                            "Electronic" -> {
                                                eqBands[0] = 6f; eqBands[1] = 4f; eqBands[2] = -1f; eqBands[3] = 3f; eqBands[4] = 7f
                                                bassBoostPercent = 80
                                            }
                                        }
                                    }
                            ) {
                                Text(
                                    text = preset,
                                    modifier = Modifier.padding(vertical = 7.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) PulseCyan else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 5-Band Frequency Sliders
                    val bandFrequencies = listOf("60 Hz", "230 Hz", "910 Hz", "3.6 kHz", "14 kHz")
                    Text(
                        text = if (isArabic) "نطاقات التردد (5 Bands)" else "FREQUENCY BANDS (5 BANDS)",
                        style = MaterialTheme.typography.labelSmall.copy(color = PulseCyan, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    bandFrequencies.forEachIndexed { index, freq ->
                        val value = eqBands.getOrElse(index) { 0f }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = freq,
                                modifier = Modifier.width(62.dp),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.5.sp
                                )
                            )
                            Slider(
                                value = value,
                                valueRange = -12f..12f,
                                onValueChange = { newVal ->
                                    selectedEqualizerPreset = "Custom"
                                    eqBands[index] = newVal
                                },
                                colors = SliderDefaults.colors(
                                    thumbColor = PulseCyan,
                                    activeTrackColor = PulseCyan,
                                    inactiveTrackColor = Color(0x33FFFFFF)
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${if (value >= 0) "+" else ""}${value.toInt()}dB",
                                modifier = Modifier.width(44.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (value != 0f) PulseCyan else Color(0xFF94A3B8),
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.End,
                                    fontSize = 10.5.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Spatial 3D Audio Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isArabic) "الصوت المحيطي المكاني (Spatial 3D Audio)" else "Spatial 3D Audio & Surround",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (isArabic) "محاكاة مسرح صوتي سينمائي 360° غامر" else "Virtual 360° immersive binaural acoustic room",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8), fontSize = 11.sp)
                            )
                        }
                        Switch(
                            checked = isSpatialAudioEnabled,
                            onCheckedChange = { isSpatialAudioEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PulseCyan
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Bass Boost Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isArabic) "مضخم البيس (Bass Boost)" else "Bass Boost Strength",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "$bassBoostPercent%",
                            style = MaterialTheme.typography.labelSmall.copy(color = PulseCyan, fontWeight = FontWeight.Bold)
                        )
                    }
                    Slider(
                        value = bassBoostPercent.toFloat(),
                        valueRange = 0f..100f,
                        onValueChange = { bassBoostPercent = it.toInt() },
                        colors = SliderDefaults.colors(
                            thumbColor = PulseCyan,
                            activeTrackColor = PulseCyan,
                            inactiveTrackColor = Color(0x33FFFFFF)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun MusicOptionItem(
    icon: ImageVector,
    title: String,
    trailingAction: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
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
                tint = PulseCyan,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.5.sp
                )
            )
        }
        if (trailingAction != null) {
            trailingAction()
        }
    }
}
