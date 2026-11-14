package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import com.example.ui.theme.stableStatusBarsPadding
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
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.data.preferences.StreakPreferencesRepository
import com.example.ui.components.ContinueWatchingSection
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
import com.example.data.model.LocalDeviceVideo
import com.example.data.model.MediaItem
import com.example.data.util.LocalMediaScanner
import com.example.ui.components.SmartFallbackCover
import com.example.ui.theme.PulseCyan
import com.example.ui.theme.PulseViolet
import com.example.ui.theme.TextDark
import com.example.ui.theme.aura
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    val scope = rememberCoroutineScope()
    val aura = MaterialTheme.aura
    val prefsRepo = remember { StreakPreferencesRepository.getInstance(context) }
    var selectedViewMode by remember {
        val savedMode = try {
            WatchViewMode.valueOf(prefsRepo.getWatchViewMode())
        } catch (_: Exception) {
            WatchViewMode.LIST
        }
        mutableStateOf(savedMode)
    }
    var isFoldersMagazineLayout by remember { mutableStateOf(false) }
    var lastFolderTapTime by remember { mutableStateOf(0L) }

    var searchQuery by remember { mutableStateOf("") }
    var activeFolderId by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    var hasStoragePermission by remember {
        mutableStateOf(LocalMediaScanner.hasStorageOrVideoPermission(context))
    }

    // ONLY real local videos from the device storage (starts empty with NO mock/dummy videos)
    val localVideos = remember { mutableStateListOf<LocalDeviceVideo>() }

    // Helper to refresh / scan device videos instantaneously
    val scanVideosJob: () -> Unit = {
        scope.launch {
            isLoading = true
            val scanned = withContext(Dispatchers.IO) {
                LocalMediaScanner.scanDeviceVideos(context)
            }
            // Preserve any user-imported files from the system picker
            val userImported = localVideos.filter { it.id.startsWith("picked_") }
            val existingUris = userImported.mapNotNull { it.uriString }.toSet()
            val freshList = (userImported + scanned.filter { it.uriString !in existingUris })

            // Instant display of all videos in zero milliseconds
            localVideos.clear()
            localVideos.addAll(freshList)
            isLoading = false

            // Asynchronously fetch thumbnails in background coroutine without blocking UI
            launch(Dispatchers.IO) {
                freshList.forEachIndexed { index, vid ->
                    if (vid.bitmap == null && !vid.uriString.isNullOrBlank()) {
                        val uri = Uri.parse(vid.uriString)
                        val durMs = try {
                            val parts = vid.durationText.split(":")
                            if (parts.size == 2) {
                                (parts[0].toLong() * 60 + parts[1].toLong()) * 1000L
                            } else if (parts.size == 3) {
                                (parts[0].toLong() * 3600 + parts[1].toLong() * 60 + parts[2].toLong()) * 1000L
                            } else 60_000L
                        } catch (_: Exception) { 60_000L }

                        val thumb = LocalMediaScanner.extractRandomMidpointThumbnail(context, uri, durMs)
                        if (thumb != null) {
                            withContext(Dispatchers.Main) {
                                val currentIdx = localVideos.indexOfFirst { it.id == vid.id }
                                if (currentIdx != -1) {
                                    localVideos[currentIdx] = localVideos[currentIdx].copy(bitmap = thumb)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Permission launcher for accessing local device videos
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        val granted = LocalMediaScanner.hasStorageOrVideoPermission(context)
        hasStoragePermission = granted
        if (granted) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            scanVideosJob()
        }
    }

    // Android System File Picker command launcher (Opens Phone Files / SAF)
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri>? ->
        if (!uris.isNullOrEmpty()) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            scope.launch {
                val newVideos = withContext(Dispatchers.IO) {
                    uris.map { uri -> LocalMediaScanner.parseUriToLocalVideo(context, uri) }
                }
                for (vid in newVideos) {
                    if (localVideos.none { it.uriString == vid.uriString }) {
                        localVideos.add(0, vid)
                    }
                }
            }
        }
    }

    // Android Visual Media Picker launcher (Photo & Video Picker)
    val quickMediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris: List<Uri>? ->
        if (!uris.isNullOrEmpty()) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            scope.launch {
                val newVideos = withContext(Dispatchers.IO) {
                    uris.map { uri -> LocalMediaScanner.parseUriToLocalVideo(context, uri) }
                }
                for (vid in newVideos) {
                    if (localVideos.none { it.uriString == vid.uriString }) {
                        localVideos.add(0, vid)
                    }
                }
            }
        }
    }

    // Scan videos automatically when storage permission is available
    LaunchedEffect(hasStoragePermission) {
        if (hasStoragePermission) {
            scanVideosJob()
        }
    }

    // Dynamic folders grouped strictly from actual local videos found on the phone
    val folders = remember(localVideos.toList()) {
        localVideos.groupBy { it.folderName.ifBlank { "Internal Storage" } }.map { (folderName, videos) ->
            val safeFolderName = folderName.ifBlank { "Internal Storage" }
            VideoFolder(
                id = "folder_${safeFolderName.hashCode()}_${videos.size}",
                name = safeFolderName,
                count = videos.size,
                totalSize = calculateTotalFolderSize(videos),
                videos = videos
            )
        }.distinctBy { it.id }.sortedByDescending { it.count }
    }

    val filteredVideos = remember(searchQuery, localVideos.toList(), activeFolderId) {
        val baseList = if (activeFolderId != null) {
            folders.find { it.id == activeFolderId }?.videos ?: localVideos
        } else {
            localVideos
        }
        val matched = if (searchQuery.isBlank()) baseList
        else baseList.filter { it.title.contains(searchQuery, ignoreCase = true) }
        matched.distinctBy { it.id }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(aura.background)
            .testTag("watch_screen_part2")
    ) {
        // Liquid Glass M5 Ambient Glow extending into hidden status bar area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                aura.accentCyan.copy(alpha = if (aura.isDark) 0.14f else 0.08f),
                                aura.accentViolet.copy(alpha = if (aura.isDark) 0.08f else 0.04f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = size.height * 0.22f
                        )
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(
                                aura.accentCyan.copy(alpha = if (aura.isDark) 0.10f else 0.06f),
                                aura.accentViolet.copy(alpha = if (aura.isDark) 0.06f else 0.03f),
                                Color.Transparent
                            ),
                            radius = size.width * 0.95f
                        ),
                        center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.18f)
                    )
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .stableStatusBarsPadding()
        ) {
            // Watch Top Bar Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (activeFolderId != null) {
                        IconButton(
                            onClick = { activeFolderId = null },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(aura.iconButtonBg)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to folders",
                                tint = aura.textPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    } else {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(aura.accentCyan, aura.accentViolet))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideoLibrary,
                                contentDescription = null,
                                tint = if (aura.isDark) TextDark else Color.White,
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
                                if (isArabic) "فيديوهات" else "Videos"
                            },
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = aura.textPrimary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (isArabic) "${localVideos.size} فيديو" else "${localVideos.size} videos",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = aura.textSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Permission Request Banner (if permission is not yet granted)
            if (!hasStoragePermission) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, aura.accentCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .testTag("storage_permission_banner"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = aura.accentCyanSoft)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = aura.accentCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "إذن قراءة ملفات الفيديو في الهاتف" else "Device Storage Permission Required",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = aura.textPrimary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isArabic) {
                                "يحتاج التطبيق لإذن قراءة ملفات الفيديو لعرض جميع مقاطع الفيديو المحفوظة في ذاكرة هاتفك وتشغيلها مباشرة."
                            } else {
                                "Grant video access permission to automatically discover and list all videos on your device storage."
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = aura.textSecondary,
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    permissionLauncher.launch(LocalMediaScanner.getRequiredPermissions())
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = aura.accentCyan,
                                    contentColor = if (aura.isDark) TextDark else Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (isArabic) "منح الصلاحية الآن" else "Grant Permission",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    filePickerLauncher.launch(arrayOf("video/*"))
                                },
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, aura.borderSubtle),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = aura.textPrimary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (isArabic) "تصفح ملفات الهاتف" else "Browse Files",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.5.sp
                                )
                            }
                        }
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
                    val isFoldersMode = mode == WatchViewMode.FOLDERS

                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) aura.accentCyanSoft else aura.unselectedChipBg,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) aura.accentCyan else aura.borderSubtle
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onDoubleTap = {
                                            if (isFoldersMode) {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                // Double tap: toggle magazine grid layout for folders
                                                isFoldersMagazineLayout = !isFoldersMagazineLayout
                                                selectedViewMode = WatchViewMode.FOLDERS
                                                prefsRepo.saveWatchViewMode(WatchViewMode.FOLDERS.name)
                                                activeFolderId = null
                                            }
                                        },
                                        onTap = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            val now = System.currentTimeMillis()
                                            if (isFoldersMode && (now - lastFolderTapTime < 350L)) {
                                                // Software double tap backup
                                                isFoldersMagazineLayout = !isFoldersMagazineLayout
                                            }
                                            lastFolderTapTime = now
                                            selectedViewMode = mode
                                            prefsRepo.saveWatchViewMode(mode.name)
                                            activeFolderId = null
                                        }
                                    )
                                }
                                .testTag("view_mode_${mode.name.lowercase()}")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 7.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = if (isFoldersMode && isFoldersMagazineLayout) Icons.Default.GridView else mode.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) aura.accentCyan else aura.textSecondary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (isArabic) {
                                            if (isFoldersMode && isFoldersMagazineLayout) "مجلدات (مجلة)" else mode.titleAr
                                        } else {
                                            if (isFoldersMode && isFoldersMagazineLayout) "Folders (Grid)" else mode.titleEn
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) aura.accentCyan else aura.textPrimary,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Compact / Slim Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 4.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    placeholder = {
                        Text(
                            text = if (isArabic) "بحث..." else "Search videos...",
                            color = aura.textTertiary,
                            fontSize = 11.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = aura.accentCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = aura.accentCyan,
                        unfocusedBorderColor = aura.borderSubtle,
                        focusedContainerColor = aura.surfaceCard,
                        unfocusedContainerColor = aura.surfaceSubtle,
                        focusedTextColor = aura.textPrimary,
                        unfocusedTextColor = aura.textPrimary
                    )
                )
            }

            // Production-ready "Continue Watching" Section (متابعة المشاهدة)
            if (activeFolderId == null && searchQuery.isBlank()) {
                ContinueWatchingSection(
                    localVideos = localVideos,
                    onPlayMedia = onPlayMedia,
                    isArabic = isArabic
                )
            }

            // Loading Indicator
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            color = aura.accentCyan,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isArabic) "جاري فحص فيديوهات الهاتف..." else "Scanning device videos...",
                            style = MaterialTheme.typography.bodySmall.copy(color = aura.textSecondary)
                        )
                    }
                }
            }

            // Empty State (if no real videos on the phone yet)
            if (!isLoading && filteredVideos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .border(1.dp, aura.borderSubtle, RoundedCornerShape(22.dp)),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = aura.surfaceCard)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(aura.accentCyanSoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FolderOpen,
                                    contentDescription = null,
                                    tint = aura.accentCyan,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = if (searchQuery.isNotBlank()) {
                                    if (isArabic) "لا توجد نتائج مطابقة" else "No Matching Videos"
                                } else {
                                    if (isArabic) "لا توجد فيديوهات في الهاتف" else "No Videos Found on Phone"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = aura.textPrimary,
                                    textAlign = TextAlign.Center
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (searchQuery.isNotBlank()) {
                                    if (isArabic) "جرب البحث باسم ملف آخر." else "Try searching with a different file name."
                                } else {
                                    if (isArabic) {
                                        "لم يتم العثور على أي مقطع فيديو في ذاكرة الهاتف. اضغط على زر فتح ملفات الهاتف لاختيار الفيديوهات أو امنح صلاحية الوصول."
                                    } else {
                                        "No local videos found on your phone. Tap 'Open Phone Files' to choose videos from device storage, or grant permissions."
                                    }
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = aura.textSecondary,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    filePickerLauncher.launch(arrayOf("video/*"))
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = aura.accentCyan,
                                    contentColor = if (aura.isDark) TextDark else Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                Icon(Icons.Default.FileOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isArabic) "فتح ملفات الهاتف" else "Open Phone Files",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    quickMediaPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                    )
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = aura.textPrimary),
                                border = androidx.compose.foundation.BorderStroke(1.dp, aura.borderSubtle),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                Text(
                                    text = if (isArabic) "معرض الفيديوهات (Picker)" else "Video Gallery Picker",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Content Area based on View Mode
            AnimatedContent(
                targetState = if (activeFolderId != null) WatchViewMode.LIST else selectedViewMode,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "watch_view_mode_transition"
            ) { mode ->
                when (mode) {
                    WatchViewMode.FOLDERS -> {
                        // 1. Folders View: toggles between Magazine Grid (on double-tap) and Linear Column
                        if (isFoldersMagazineLayout) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(folders, key = { it.id }) { folder ->
                                    FolderGridCard(
                                        folder = folder,
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            activeFolderId = folder.id
                                        }
                                    )
                                }
                            }
                        } else {
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
fun FolderGridCard(
    folder: VideoFolder,
    onClick: () -> Unit
) {
    val aura = MaterialTheme.aura
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, aura.borderSubtle, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("folder_grid_card_${folder.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = aura.surfaceCard)
    ) {
        Column {
            // Clean Smart Fallback Cover for Folder in Grid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.35f)
            ) {
                SmartFallbackCover(
                    title = "",
                    subtitle = null,
                    icon = Icons.Default.Folder,
                    aspectRatio = 1.35f,
                    badgeText = null,
                    gradientVariant = folder.id.hashCode(),
                    showText = false
                )
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = folder.name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = aura.textPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = aura.accentCyanSoft
                    ) {
                        Text(
                            text = "${folder.count} Videos",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = aura.accentCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp
                            )
                        )
                    }

                    if (folder.totalSize.isNotBlank()) {
                        Text(
                            text = folder.totalSize,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = aura.textSecondary,
                                fontSize = 10.sp
                            )
                        )
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
    val aura = MaterialTheme.aura
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, aura.borderSubtle, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("folder_card_${folder.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = aura.surfaceCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clean Smart Fallback Cover for Folder without filename, size, count or items on thumbnail
            Box(modifier = Modifier.size(68.dp)) {
                SmartFallbackCover(
                    title = "",
                    subtitle = null,
                    icon = Icons.Default.Folder,
                    aspectRatio = 1f,
                    badgeText = null,
                    gradientVariant = folder.id.hashCode(),
                    showText = false
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = folder.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = aura.textPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = aura.accentCyanSoft
                    ) {
                        Text(
                            text = "${folder.count} Videos",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = aura.accentCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = folder.totalSize,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = aura.textSecondary,
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
    val context = LocalContext.current
    val aura = MaterialTheme.aura
    val savedProgress = remember(video.id, video.uriString) {
        StreakPreferencesRepository.getInstance(context).getVideoProgress(video.id, video.uriString)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, aura.borderSubtle, RoundedCornerShape(16.dp))
            .clickable(onClick = onPlay)
            .testTag("file_list_card_${video.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = aura.surfaceCard)
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
                if (video.bitmap != null) {
                    Image(
                        bitmap = video.bitmap.asImageBitmap(),
                        contentDescription = video.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (video.thumbnailRes != 0) {
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

                // Persistent Progress Bar for this video
                if (savedProgress != null && savedProgress.progress > 0.02f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .align(Alignment.BottomCenter)
                            .background(Color(0x66000000))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(savedProgress.progress.coerceIn(0.02f, 1f))
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(PulseCyan, Color(0xFF0088FF))
                                    )
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = aura.textPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = aura.accentCyanSoft
                    ) {
                        Text(
                            text = video.resolutionText,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = aura.accentCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = video.sizeText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = aura.textSecondary,
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
                    .background(aura.primaryCtaBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = aura.primaryCtaContent,
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
    val context = LocalContext.current
    val aura = MaterialTheme.aura
    val savedProgress = remember(video.id, video.uriString) {
        StreakPreferencesRepository.getInstance(context).getVideoProgress(video.id, video.uriString)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, aura.borderSubtle, RoundedCornerShape(16.dp))
            .clickable(onClick = onPlay)
            .testTag("magazine_card_${video.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = aura.surfaceCard)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
            ) {
                if (video.bitmap != null) {
                    Image(
                        bitmap = video.bitmap.asImageBitmap(),
                        contentDescription = video.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (video.thumbnailRes != 0) {
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

                // Persistent Progress Bar for this video
                if (savedProgress != null && savedProgress.progress > 0.02f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.5.dp)
                            .align(Alignment.BottomCenter)
                            .background(Color(0x66000000))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(savedProgress.progress.coerceIn(0.02f, 1f))
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(PulseCyan, Color(0xFF0088FF))
                                    )
                                )
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = aura.textPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = aura.accentCyanSoft
                    ) {
                        Text(
                            text = video.resolutionText,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = aura.accentCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }

                    Text(
                        text = video.sizeText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = aura.textSecondary,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

private fun calculateTotalFolderSize(videos: List<LocalDeviceVideo>): String {
    return if (videos.isEmpty()) "" else "${videos.size} vids"
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
