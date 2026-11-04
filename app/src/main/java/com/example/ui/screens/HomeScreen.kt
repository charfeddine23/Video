package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.MediaItem
import com.example.data.model.MediaRepository
import com.example.ui.components.FloatingFilterTabs
import com.example.ui.components.HeroSpotlightCard
import com.example.ui.components.MediaRowSection
import com.example.ui.components.VideoPulseHeader
import com.example.ui.theme.Charcoal900
import com.example.ui.theme.Charcoal950
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onMediaClick: (MediaItem) -> Unit,
    onOpenSubscribe: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenNotifications: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedCategory by remember { mutableStateOf("TV Shows") }
    val myListIds = remember { mutableStateListOf<String>() }

    val hero = MediaRepository.heroItem
    val continueWatching = MediaRepository.continueWatchingList
    val pulseOriginals = MediaRepository.pulseOriginals
    val trending = MediaRepository.trendingNow
    val sportsAndListen = MediaRepository.sportsAndLiveList

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Charcoal950)
            .testTag("home_screen")
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp) // Generous space so floating pill doesn't obscure content
        ) {
            // Requirement 1: Header with Subscribe pill on top-left and Search on top-right
            item(key = "header") {
                VideoPulseHeader(
                    onSubscribeClick = onOpenSubscribe,
                    onSearchClick = onOpenSearch,
                    onNotificationsClick = onOpenNotifications
                )
            }

            // Requirement 2 & 3: Hero Spotlight Banner with poster, Arabic title, badges, and Call to Action buttons
            item(key = "hero_spotlight") {
                HeroSpotlightCard(
                    mediaItem = hero,
                    onWatchNowClick = { item ->
                        onMediaClick(item)
                    },
                    onMoreInfoClick = { item ->
                        onMediaClick(item)
                    },
                    onToggleMyList = { item, isInList ->
                        if (isInList) {
                            if (!myListIds.contains(item.id)) myListIds.add(item.id)
                            scope.launch {
                                snackbarHostState.showSnackbar("Added to My List: ${item.title}")
                            }
                        } else {
                            myListIds.remove(item.id)
                            scope.launch {
                                snackbarHostState.showSnackbar("Removed from My List")
                            }
                        }
                    },
                    isSavedInMyList = myListIds.contains(hero.id)
                )
            }

            // Continue Watching Section
            item(key = "continue_watching") {
                MediaRowSection(
                    title = "Continue Watching",
                    subtitle = "Resume where you left off in 4K HDR",
                    items = continueWatching,
                    onItemClick = onMediaClick,
                    showProgress = true
                )
            }

            // Video Pulse Originals Section
            item(key = "pulse_originals") {
                MediaRowSection(
                    title = "Pulse Originals",
                    subtitle = "Award-winning dramas & groundbreaking cinema",
                    items = pulseOriginals,
                    onItemClick = onMediaClick
                )
            }

            // Trending Now Section
            item(key = "trending_now") {
                MediaRowSection(
                    title = "Trending Now",
                    subtitle = "Top streamed shows & movies this week",
                    items = trending,
                    onItemClick = onMediaClick
                )
            }

            // Sports & Sound Sessions
            item(key = "sports_and_sound") {
                MediaRowSection(
                    title = "Live Sports & Sound Sessions",
                    subtitle = "Ultra HD 60fps matches & spatial music",
                    items = sportsAndListen,
                    onItemClick = onMediaClick
                )
            }
        }

        // Requirement 4: Floating semi-transparent bottom navigation pill overlaying the content
        // containing horizontal category filters: "TV Shows", "Movies", "Listen", "Sports", and "Watch for Free"
        FloatingFilterTabs(
            categories = MediaRepository.categories,
            selectedCategory = selectedCategory,
            onCategorySelected = { cat ->
                selectedCategory = cat
                scope.launch {
                    snackbarHostState.showSnackbar("Filtered by: $cat")
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 68.dp) // Floats comfortably right above the fixed bottom navigation bar
        )

        // Feedback Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
        )
    }
}
