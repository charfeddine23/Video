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
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
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
import com.example.R
import com.example.ui.theme.Charcoal950
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PulseCyan
import com.example.ui.theme.PulseViolet
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class AudioTrack(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationSeconds: Int,
    val albumCoverRes: Int,
    val lyricsLines: List<String>,
    val audioUri: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListenScreen(
    isArabic: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    // Curated high quality local music tracks
    val playlist = remember {
        listOf(
            AudioTrack(
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
                )
            ),
            AudioTrack(
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
                )
            ),
            AudioTrack(
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
                )
            )
        )
    }

    var currentTrackIndex by remember { mutableIntStateOf(0) }
    val currentTrack = playlist[currentTrackIndex]

    var isPlaying by remember { mutableStateOf(true) }
    var isFavorite by remember { mutableStateOf(false) }
    var currentPositionSeconds by remember { mutableFloatStateOf(42f) }
    var showLyricsDialog by remember { mutableStateOf(false) }
    var showOptionsSheet by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    var castDeviceName by remember { mutableStateOf(if (isArabic) "هذا الهاتف" else "This phone") }

    // Floating Vinyl / Glow Animation
    val infiniteTransition = rememberInfiniteTransition(label = "audio_glow")
    val neonRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "neon_rotation"
    )

    val neonGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "neon_glow_alpha"
    )

    // Playback Progress Simulation Timer
    LaunchedEffect(isPlaying, currentTrackIndex) {
        while (isPlaying) {
            delay(1000)
            if (currentPositionSeconds < currentTrack.durationSeconds) {
                currentPositionSeconds += 1f
            } else {
                // Loop to next track
                currentTrackIndex = (currentTrackIndex + 1) % playlist.size
                currentPositionSeconds = 0f
            }
        }
    }

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
        Image(
            painter = painterResource(id = currentTrack.albumCoverRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.35f
        )

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
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 2. Top Bar: Small Centered Drag Handle with Subtitle
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 12.dp),
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = PulseCyan,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "تشغيل من الموسيقى المحلية" else "Playing from Local Music",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 3. Album Artwork Card: Large centered card (24.dp rounded) with frosted glass effect & subtle glowing neon border
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
                                    Color(0xFF10B981), // Neon Green
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
                Image(
                    painter = painterResource(id = currentTrack.albumCoverRes),
                    contentDescription = currentTrack.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Crop
                )

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
                            text = "320 KBPS • LOSSLESS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

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

            // 5. Lyrics Section: Interactive lyrics preview/button with a smooth seekbar (progress bar)
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
                            text = currentTrack.lyricsLines.firstOrNull() ?: "Tap to view lyrics...",
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

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar / Seekbar with Elapsed & Remaining timestamps
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = currentPositionSeconds,
                    onValueChange = { currentPositionSeconds = it },
                    valueRange = 0f..currentTrack.durationSeconds.toFloat(),
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
                    val currentMin = (currentPositionSeconds.toInt()) / 60
                    val currentSec = (currentPositionSeconds.toInt()) % 60
                    val totalMin = currentTrack.durationSeconds / 60
                    val totalSec = currentTrack.durationSeconds % 60

                    Text(
                        text = String.format("%02d:%02d", currentMin, currentSec),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = String.format("-%02d:%02d", (currentTrack.durationSeconds - currentPositionSeconds.toInt()) / 60, (currentTrack.durationSeconds - currentPositionSeconds.toInt()) % 60),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 6. Playback Controls: Centered row with Skip Previous, prominent circular glowing Play/Pause, and Skip Next
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
                        currentTrackIndex = if (currentTrackIndex > 0) currentTrackIndex - 1 else playlist.size - 1
                        currentPositionSeconds = 0f
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
                            isPlaying = !isPlaying
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
                        currentTrackIndex = (currentTrackIndex + 1) % playlist.size
                        currentPositionSeconds = 0f
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

            Spacer(modifier = Modifier.height(14.dp))

            // 7. Bottom Utility Bar: Icons for Lyrics/Chat, Cast, Queue/Playlist, and device indicator ("This phone")
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
                                    snackbarMessage = if (isArabic) "قائمة الانتظار: 3 أغانٍ محلية" else "Queue: 3 local tracks loaded"
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
                    .statusBarsPadding()
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

        // 8. Options Bottom Sheet: Frosted glass popup menu triggered by three-dots button
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
                        Image(
                            painter = painterResource(id = currentTrack.albumCoverRes),
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
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
