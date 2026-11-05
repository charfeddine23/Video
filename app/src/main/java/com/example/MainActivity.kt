package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.MediaItem
import com.example.data.model.MediaRepository
import com.example.ui.components.ExoVideoPlayer
import com.example.ui.components.InAppUpdateDialog
import com.example.ui.components.NavDestination
import com.example.ui.components.SearchOverlay
import com.example.ui.components.SettingsModalSheet
import com.example.ui.components.VideoPulseBottomBar
import com.example.ui.screens.DeviceVideosScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MilestoneRoadmapScreen
import com.example.ui.screens.MySpaceScreen
import com.example.ui.screens.ShortsScreen
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ThemeMode
import com.example.ui.viewmodel.StreakViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentThemeMode by remember { mutableStateOf(ThemeMode.DARK) }
            var currentLanguage by remember { mutableStateOf(AppLanguage.ARABIC) }

            MyApplicationTheme(themeMode = currentThemeMode) {
                CompositionLocalProvider(
                    LocalLayoutDirection provides (if (currentLanguage.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr)
                ) {
                    VideoPulseApp(
                        currentThemeMode = currentThemeMode,
                        onThemeModeChange = { currentThemeMode = it },
                        currentLanguage = currentLanguage,
                        onLanguageChange = { currentLanguage = it }
                    )
                }
            }
        }
    }
}

@Composable
fun VideoPulseApp(
    currentThemeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    streakViewModel: StreakViewModel = viewModel()
) {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val streakState by streakViewModel.uiState.collectAsState()

    var currentDestination by remember { mutableStateOf(NavDestination.HOME) }
    var activePlayerMedia by remember { mutableStateOf<MediaItem?>(null) }
    var isSettingsSheetOpen by remember { mutableStateOf(false) }
    var isSearchOverlayOpen by remember { mutableStateOf(false) }
    var isUpdateDialogOpen by remember { mutableStateOf(false) }
    var isRoadmapOpen by remember { mutableStateOf(false) }

    val isArabic = currentLanguage == AppLanguage.ARABIC

    // In-App Update: Check on app launch and notify if newer build is available
    LaunchedEffect(Unit) {
        delay(1500)
        isUpdateDialogOpen = true
    }

    // Android System Back Navigation Handling
    BackHandler(
        enabled = isRoadmapOpen || isSearchOverlayOpen || isSettingsSheetOpen || isUpdateDialogOpen || activePlayerMedia != null || currentDestination != NavDestination.HOME
    ) {
        when {
            isRoadmapOpen -> isRoadmapOpen = false
            activePlayerMedia != null -> activePlayerMedia = null
            isUpdateDialogOpen -> isUpdateDialogOpen = false
            isSearchOverlayOpen -> isSearchOverlayOpen = false
            isSettingsSheetOpen -> isSettingsSheetOpen = false
            currentDestination != NavDestination.HOME -> currentDestination = NavDestination.HOME
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                // Fixed bottom bar with Home, Videos (all device videos), Shorts, My Space
                VideoPulseBottomBar(
                    currentDestination = currentDestination,
                    onDestinationSelected = { dest ->
                        currentDestination = dest
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                // Tab Transitions with smooth animated content
                AnimatedContent(
                    targetState = currentDestination,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "tab_navigation_transition"
                ) { destination ->
                    when (destination) {
                        NavDestination.HOME -> {
                            HomeScreen(
                                streakViewModel = streakViewModel,
                                onMediaClick = { media ->
                                    activePlayerMedia = media
                                },
                                onOpenSettings = {
                                    isSettingsSheetOpen = true
                                },
                                onOpenSearch = {
                                    isSearchOverlayOpen = true
                                },
                                onOpenNotifications = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            if (isArabic) "لا توجد إشعارات جديدة" else "No new notifications"
                                        )
                                    }
                                },
                                onNavigateToRoadmap = {
                                    isRoadmapOpen = true
                                },
                                onOpenVideosScreen = {
                                    currentDestination = NavDestination.VIDEOS
                                },
                                isArabic = isArabic
                            )
                        }

                        NavDestination.VIDEOS -> {
                            // User request: "live TV بدلها سميها videos ويجي فيها كل فيديوهات الجهاز"
                            DeviceVideosScreen(
                                onPlayVideo = { media ->
                                    activePlayerMedia = media
                                },
                                isArabic = isArabic
                            )
                        }

                        NavDestination.SHORTS -> {
                            ShortsScreen()
                        }

                        NavDestination.MY_SPACE -> {
                            MySpaceScreen(
                                onMediaClick = { media ->
                                    activePlayerMedia = media
                                },
                                onOpenSubscribe = {
                                    isSettingsSheetOpen = true
                                }
                            )
                        }
                    }
                }
            }
        }

        // Fullscreen Milestone Roadmap Screen (Triggered by 1-second long-press on flame)
        if (isRoadmapOpen) {
            MilestoneRoadmapScreen(
                streakViewModel = streakViewModel,
                streakDays = streakState.streakDays,
                shieldsCount = streakState.shieldsCount,
                onBack = {
                    isRoadmapOpen = false
                },
                isArabic = isArabic
            )
        }

        // Search Overlay
        if (isSearchOverlayOpen) {
            val allMedia = remember {
                listOf(MediaRepository.heroItem) +
                        MediaRepository.continueWatchingList +
                        MediaRepository.pulseOriginals +
                        MediaRepository.trendingNow +
                        MediaRepository.sportsAndLiveList
            }
            SearchOverlay(
                allMedia = allMedia.distinctBy { it.id },
                onMediaClick = { media ->
                    isSearchOverlayOpen = false
                    activePlayerMedia = media
                },
                onClose = {
                    isSearchOverlayOpen = false
                }
            )
        }

        // Full-screen ExoPlayer playback component for local and streaming media
        if (activePlayerMedia != null) {
            ExoVideoPlayer(
                mediaItem = activePlayerMedia!!,
                streakViewModel = streakViewModel,
                onClose = {
                    activePlayerMedia = null
                }
            )
        }

        // Settings Modal Sheet (Language, Theme Mode, Developer Info, Check for Updates)
        SettingsModalSheet(
            isOpen = isSettingsSheetOpen,
            currentThemeMode = currentThemeMode,
            onThemeModeChange = onThemeModeChange,
            currentLanguage = currentLanguage,
            onLanguageChange = onLanguageChange,
            onCheckForUpdates = {
                isSettingsSheetOpen = false
                isUpdateDialogOpen = true
            },
            onDismiss = {
                isSettingsSheetOpen = false
            }
        )

        // In-App Update Dialog
        InAppUpdateDialog(
            isOpen = isUpdateDialogOpen,
            isArabic = isArabic,
            onDismiss = {
                isUpdateDialogOpen = false
            },
            onInstallComplete = {
                isUpdateDialogOpen = false
                scope.launch {
                    snackbarHostState.showSnackbar(
                        if (isArabic) "تم تثبيت أحدث إصدار بنجاح!" else "Latest version installed successfully!"
                    )
                }
            }
        )

        // Global Snackbar for feedback
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }
}
