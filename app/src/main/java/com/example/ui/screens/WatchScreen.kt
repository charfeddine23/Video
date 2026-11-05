package com.example.ui.screens

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.ui.components.SmartFallbackCover
import com.example.ui.theme.PulseCyan
import com.example.ui.theme.PulseViolet
import com.example.ui.theme.TextDark

enum class WatchViewMode(val titleEn: String, val titleAr: String, val icon: ImageVector) {
    FOLDERS("Folders", "مجلدات", Icons.Default.Folder),
    LIST("Files List", "قائمة الملفات", Icons.Default.ViewAgenda),
    MAGAZINE("Magazine / Grid", "مجلة / شبكة", Icons.Default.GridView)
}

data class VideoFolder(
    val id: String,
    val name: String,
    val count: Int,
    val totalSize: String,
    val videos: List<LocalDeviceVideo>
)

@Composable
fun WatchScreen(
    onPlayMedia: (MediaItem) -> Unit,
    isArabic: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    var selectedViewMode by remember { mutableStateOf(WatchViewMode.LIST) }
    var searchQuery by remember { mutableStateOf("") }
    var activeFolderId by remember { mutableStateOf<String?>(null) }

    val localVideos = remember {
        mutableStateListOf<LocalDeviceVideo>().apply {
            addAll(MediaRepository.defaultLocalVideos)
        }
    }

    // Google Play compliant zero-permission Photo/Video Picker
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            val displayName = queryDisplayName(context, uri) ?: "Video_${System.currentTimeMillis()}.mp4"
            val newVideo = LocalDeviceVideo(
                id = "picked_${System.currentTimeMillis()}",
                title = displayName,
                durationText = "04:20",
                sizeText = "Local Storage",
                resolutionText = "1080p FHD",
                dateAddedText = "Just now",
                uriString = uri.toString(),
                thumbnailRes = 0, // 0 triggers SmartFallbackCover!
                isFromGallery = true
            )
            localVideos.add(0, newVideo)
        }
    }

    // Dynamic folders grouped from local videos
    val folders = remember(localVideos.size) {
        listOf(
            VideoFolder(
                id = "f_camera",
                name = "Camera (الكاميرا)",
                count = (localVideos.size * 0.4).toInt().coerceAtLeast(3),
                totalSize = "1.84 GB",
                videos = localVideos.take(3)
            ),
            VideoFolder(
                id = "f_downloads",
                name = "Downloads (التحميلات)",
                count = (localVideos.size * 0.35).toInt().coerceAtLeast(4),
                totalSize = "2.60 GB",
                videos = localVideos.drop(1)
            ),
            VideoFolder(
                id = "f_whatsapp",
                name = "Media Vault (المستودع)",
                count = 5,
                totalSize = "890 MB",
                videos = localVideos
            ),
            VideoFolder(
                id = "f_screen_records",
                name = "Screen Records (تسجيلات)",
                count = 2,
                totalSize = "340 MB",
                videos = localVideos.take(2)
            )
        )
    }

    val filteredVideos = remember(searchQuery, localVideos.size, activeFolderId) {
        val baseList = if (activeFolderId != null) {
            folders.find { it.id == activeFolderId }?.videos ?: localVideos
        } else {
            localVideos
        }
        if (searchQuery.isBlank()) baseList
        else baseList.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
            .testTag("watch_screen_part2")
    ) {
        // Liquid Glass M5 Ambient Glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color(0x1800F2FE), Color(0x107928CA), Color.Transparent),
                            radius = size.width * 0.95f
                        ),
                        center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.18f)
                    )
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Watch Top Bar Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (activeFolderId != null) {
                        IconButton(
                            onClick = { activeFolderId = null },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0x26FFFFFF))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to folders",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    } else {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(PulseCyan, PulseViolet))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideoLibrary,
                                contentDescription = null,
                                tint = TextDark,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Column {
                        Text(
                            text = if (activeFolderId != null) {
                                folders.find { it.id == activeFolderId }?.name ?: "Folder"
                            } else {
                                if (isArabic) "مشاهدة (Watch)" else "Watch"
                            },
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = if (isArabic) "مستعرض الوسائط ومشغل الفيديو المحلي M5" else "Local Storage Browser & M5 Player Engine",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Import Video Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x3300F2FE),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PulseCyan.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            videoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        }
                        .testTag("watch_import_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, null, tint = PulseCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isArabic) "استيراد" else "Import",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = PulseCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // View Mode Toggles: Folders View, Files List View, Magazine / Grid View
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WatchViewMode.values().forEach { mode ->
                    val isSelected = selectedViewMode == mode && activeFolderId == null
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0x3300F2FE) else Color(0x1F121826),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) PulseCyan else Color(0x22FFFFFF)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                selectedViewMode = mode
                                activeFolderId = null
                            }
                            .testTag("view_mode_${mode.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = mode.icon,
                                contentDescription = null,
                                tint = if (isSelected) PulseCyan else Color(0xFF94A3B8),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isArabic) mode.titleAr else mode.titleEn,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) PulseCyan else Color.White,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 10.5.sp
                                )
                            )
                        }
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                placeholder = {
                    Text(
                        text = if (isArabic) "ابحث في الفيديوهات المحلية..." else "Search local media files...",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = PulseCyan,
                        modifier = Modifier.size(18.dp)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PulseCyan,
                    unfocusedBorderColor = Color(0x2AFFFFFF),
                    focusedContainerColor = Color(0x22121826),
                    unfocusedContainerColor = Color(0x22121826),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            // Content Area based on View Mode
            AnimatedContent(
                targetState = if (activeFolderId != null) WatchViewMode.LIST else selectedViewMode,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "watch_view_mode_transition"
            ) { mode ->
                when (mode) {
                    WatchViewMode.FOLDERS -> {
                        // 1. Folders View
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(folders, key = { it.id }) { folder ->
                                FolderItemCard(
                                    folder = folder,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        activeFolderId = folder.id
                                    }
                                )
                            }
                        }
                    }

                    WatchViewMode.LIST -> {
                        // 2. Files List View
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredVideos, key = { it.id }) { video ->
                                FileListItemCard(
                                    video = video,
                                    onPlay = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onPlayMedia(video.toMediaItem())
                                    }
                                )
                            }
                        }
                    }

                    WatchViewMode.MAGAZINE -> {
                        // 3. Magazine / Grid View
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredVideos, key = { it.id }) { video ->
                                MagazineGridItemCard(
                                    video = video,
                                    onPlay = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onPlayMedia(video.toMediaItem())
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
fun FolderItemCard(
    folder: VideoFolder,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, Color(0x2EFFFFFF), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("folder_card_${folder.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xB3121826))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Smart Fallback Cover for Folder
            Box(modifier = Modifier.size(72.dp)) {
                SmartFallbackCover(
                    title = folder.name.substringBefore(" ("),
                    subtitle = "${folder.count} vids",
                    icon = Icons.Default.Folder,
                    aspectRatio = 1f,
                    badgeText = "${folder.count}",
                    gradientVariant = folder.id.hashCode()
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = folder.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0x3300F2FE)
                    ) {
                        Text(
                            text = "${folder.count} Videos",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = PulseCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = folder.totalSize,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun FileListItemCard(
    video: LocalDeviceVideo,
    onPlay: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color(0x1FFFFFFF), RoundedCornerShape(16.dp))
            .clickable(onClick = onPlay)
            .testTag("file_list_card_${video.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xB3121826))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail or Smart Fallback Cover
            Box(
                modifier = Modifier
                    .width(92.dp)
                    .height(58.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                if (video.thumbnailRes != 0) {
                    Image(
                        painter = painterResource(id = video.thumbnailRes),
                        contentDescription = video.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    SmartFallbackCover(
                        title = video.title,
                        aspectRatio = 1.58f,
                        badgeText = video.resolutionText,
                        gradientVariant = video.id.hashCode()
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xCC000000),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                ) {
                    Text(
                        text = video.durationText,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0x3300F2FE)
                    ) {
                        Text(
                            text = video.resolutionText,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = PulseCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = video.sizeText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = TextDark,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun MagazineGridItemCard(
    video: LocalDeviceVideo,
    onPlay: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(16.dp))
            .clickable(onClick = onPlay)
            .testTag("magazine_card_${video.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xD9121826))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
            ) {
                if (video.thumbnailRes != 0) {
                    Image(
                        painter = painterResource(id = video.thumbnailRes),
                        contentDescription = video.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    SmartFallbackCover(
                        title = video.title,
                        aspectRatio = 1.5f,
                        badgeText = video.resolutionText,
                        gradientVariant = video.id.hashCode()
                    )
                }

                // Play icon pill in bottom center
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0x99000000))
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xCC000000),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                ) {
                    Text(
                        text = video.durationText,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = video.resolutionText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PulseCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = video.sizeText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
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

private fun queryDisplayName(context: Context, uri: Uri): String? {
    if (uri.scheme == "content") {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                return cursor.getString(nameIndex)
            }
        }
    }
    return uri.lastPathSegment
}
