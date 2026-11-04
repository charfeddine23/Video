package com.example.ui.screens

import androidx.compose.foundation.background
import com.example.BuildConfig
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.MediaItem
import com.example.data.model.MediaRepository
import com.example.ui.components.MediaCard
import com.example.ui.theme.Charcoal800
import com.example.ui.theme.Charcoal850
import com.example.ui.theme.Charcoal900
import com.example.ui.theme.Charcoal950
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PulseAmber
import com.example.ui.theme.PulseCyan
import com.example.ui.theme.PulseViolet
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MySpaceScreen(
    onMediaClick: (MediaItem) -> Unit,
    onOpenSubscribe: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    var ultra120fpsEnabled by remember { mutableStateOf(true) }
    var spatialAudioEnabled by remember { mutableStateOf(true) }
    var hapticFeedbackEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Charcoal950)
            .statusBarsPadding()
            .testTag("my_space_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Profile Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // User Avatar with glowing border
                        Box(
                            modifier = Modifier
                                .size(64.dp)
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
                                .background(
                                    Brush.linearGradient(listOf(PulseCyan, PulseViolet))
                                )
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "AD",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextDark
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Alex Drake",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Diamond,
                                    contentDescription = "VIP Member",
                                    tint = PulseAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Text(
                                text = "VIP Annual Member • 4K HDR",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = PulseAmber,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // Settings Icon
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Charcoal850)
                            .border(1.dp, GlassBorder, CircleShape)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // VIP Plan Banner Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(PulseCyan, PulseViolet)),
                        RoundedCornerShape(20.dp)
                    )
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onOpenSubscribe()
                    },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Charcoal850)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Video Pulse VIP Active",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                            )
                        }
                        Text(
                            text = "Next billing on Dec 2026. 4K HDR 120fps enabled.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White
                    ) {
                        Text(
                            text = "Manage",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        )
                    }
                }
            }
        }

        // My Watchlist Carousel
        item {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text(
                    text = "My Watchlist",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(MediaRepository.continueWatchingList, key = { it.id }) { item ->
                        MediaCard(
                            mediaItem = item,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onMediaClick(item)
                            }
                        )
                    }
                }
            }
        }

        // Offline Downloads Section
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Downloaded For Offline (2.4 GB)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Smart Downloads: ON",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PulseCyan,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Charcoal850,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x2200F2FE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DownloadDone,
                                        contentDescription = "Downloaded",
                                        tint = PulseCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "El Sitt Oumour (S1:E1)",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "4K HDR • 1.2 GB • Expires in 29 days",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x2200F2FE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DownloadDone,
                                        contentDescription = "Downloaded",
                                        tint = PulseCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Cyber Pulse 2088 (S1:E1)",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "1080p FHD • 1.1 GB • Expires in 29 days",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Performance & Video Playback Preferences
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Streaming & Performance Settings",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Charcoal850,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // 120 FPS Ultra Motion toggle
                        SettingToggleRow(
                            icon = Icons.Default.Speed,
                            title = "120 FPS Ultra Motion",
                            subtitle = "Ultra smooth high refresh rate stream interpolation",
                            checked = ultra120fpsEnabled,
                            onCheckedChange = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                ultra120fpsEnabled = it
                            }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Spatial Audio
                        SettingToggleRow(
                            icon = Icons.Default.VolumeUp,
                            title = "Dolby Atmos Spatial Audio",
                            subtitle = "Immersive multi-directional audio processing",
                            checked = spatialAudioEnabled,
                            onCheckedChange = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                spatialAudioEnabled = it
                            }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Haptic feedback
                        SettingToggleRow(
                            icon = Icons.Default.Vibration,
                            title = "Haptic UI Feedback",
                            subtitle = "Tactile responses on interactions and playback triggers",
                            checked = hapticFeedbackEnabled,
                            onCheckedChange = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                hapticFeedbackEnabled = it
                            }
                        )
                    }
                }
            }
        }

        // App Info & Version Section
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp)) {
                Text(
                    text = "App Information",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Charcoal850,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x2200F2FE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = PulseCyan, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Version",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                    Text(
                                        text = "Release Build ${BuildConfig.VERSION_CODE}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextSecondary,
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
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PulseCyan,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Developer",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                            )
                            Text(
                                text = "Developed by: Charfeddine Ka",
                                style = MaterialTheme.typography.bodySmall.copy(color = PulseCyan, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Last Updated",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                            )
                            Text(
                                text = "November 4, 2026",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Package ID",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                            )
                            Text(
                                text = BuildConfig.APPLICATION_ID,
                                style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0x22FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = PulseCyan, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PulseCyan,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = Charcoal800
            )
        )
    }
}
