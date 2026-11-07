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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.MediaItem
import com.example.ui.components.ExoVideoPlayer

import com.example.ui.components.NavDestination
import com.example.ui.components.VideoPulseBottomBar
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MilestoneRoadmapScreen
import com.example.ui.screens.MySpaceScreen
import com.example.ui.screens.ListenScreen
import com.example.ui.screens.WatchScreen
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
            val prefsRepo = remember { com.example.data.preferences.StreakPreferencesRepository.getInstance(applicationContext) }
            var currentThemeMode by remember { mutableStateOf(prefsRepo.loadThemeMode()) }
            var currentLanguage by remember { mutableStateOf(prefsRepo.loadLanguage()) }

            MyApplicationTheme(themeMode = currentThemeMode) {
                CompositionLocalProvider(
                    LocalLayoutDirection provides (if (currentLanguage.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr)
                ) {
                    VideoPulseApp(
                        currentThemeMode = currentThemeMode,
                        onThemeModeChange = {
                            currentThemeMode = it
                            prefsRepo.saveThemeMode(it)
                        },
                        currentLanguage = currentLanguage,
                        onLanguageChange = {
                            currentLanguage = it
                            prefsRepo.saveLanguage(it)
                        }
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
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val streakState by streakViewModel.uiState.collectAsState()

    var currentDestination by remember { mutableStateOf(NavDestination.HOME) }
    var activePlayerMedia by remember { mutableStateOf<MediaItem?>(null) }
    var isRoadmapOpen by remember { mutableStateOf(false) }
    var isEditingProfile by remember { mutableStateOf(false) }

    val isArabic = currentLanguage == AppLanguage.ARABIC

    // Android System Back Navigation Handling
    BackHandler(
        enabled = isEditingProfile || isRoadmapOpen || activePlayerMedia != null || currentDestination != NavDestination.HOME
    ) {
        when {
            isEditingProfile -> isEditingProfile = false
            isRoadmapOpen -> isRoadmapOpen = false
            activePlayerMedia != null -> activePlayerMedia = null
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
                // Requirement 2: Clean bottom bar featuring Home and My Space
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
                                onNavigateToRoadmap = {
                                    isRoadmapOpen = true
                                },
                                isArabic = isArabic
                            )
                        }

                        NavDestination.WATCH -> {
                            // Tab 3: Watch (Placeholder for Part 2 with offline media player)
                            WatchScreen(
                                onPlayMedia = { media ->
                                    activePlayerMedia = media
                                },
                                isArabic = isArabic
                            )
                        }

                        NavDestination.LISTEN -> {
                            // Tab 2: Listen Screen (Music & Audio Local Hub)
                            ListenScreen(isArabic = isArabic)
                        }

                        NavDestination.MY_SPACE -> {
                            // Tab 1: "My Space" Screen (Settings & Rewards Hub)
                            MySpaceScreen(
                                streakViewModel = streakViewModel,
                                currentThemeMode = currentThemeMode,
                                onThemeModeChange = onThemeModeChange,
                                currentLanguage = currentLanguage,
                                onLanguageChange = onLanguageChange,
                                onOpenRoadmap = {
                                    isRoadmapOpen = true
                                },
                                onNavigateToEditProfile = {
                                    isEditingProfile = true
                                }
                            )
                        }
                    }
                }
            }
        }

        // Edit Profile Screen Overlay
        if (isEditingProfile) {
            com.example.ui.screens.EditProfileScreen(
                streakViewModel = streakViewModel,
                onNavigateBack = {
                    isEditingProfile = false
                },
                isArabic = isArabic
            )
        }

        // Milestone Roadmap Screen (Triggered by 1-second long-press on flame)
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

        // Full-screen ExoPlayer playback component
        if (activePlayerMedia != null) {
            ExoVideoPlayer(
                mediaItem = activePlayerMedia!!,
                streakViewModel = streakViewModel,
                onClose = {
                    activePlayerMedia = null
                }
            )
        }



        // Global Snackbar for feedback
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }
}
