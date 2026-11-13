package com.example.ui.theme

import android.content.res.Configuration
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Provides a stable top status-bar height so hiding the system status bar in Full Screen Mode
 * never causes screen layouts to shrink or shift upward.
 */
val LocalStableStatusBarTop = compositionLocalOf<Dp?> { null }

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun rememberStableStatusBarTopDp(
    isFullScreenMode: Boolean = false,
    cachedPortraitHeightDp: Dp = 0.dp
): Dp {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val visibleTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val ignoringVisTop = WindowInsets.statusBarsIgnoringVisibility.asPaddingValues().calculateTopPadding()
    val measuredTop = maxOf(visibleTop, ignoringVisTop)
    return if (measuredTop > 0.dp) {
        measuredTop
    } else if (isFullScreenMode && isPortrait) {
        if (cachedPortraitHeightDp > 0.dp) cachedPortraitHeightDp else 24.dp
    } else {
        0.dp
    }
}

fun Modifier.stableStatusBarsPadding(): Modifier = composed {
    val localTop = LocalStableStatusBarTop.current
    val topPadding = localTop ?: rememberStableStatusBarTopDp()
    padding(top = topPadding)
}
