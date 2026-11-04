package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VideoPulseColorScheme = darkColorScheme(
    primary = PulseCyan,
    onPrimary = TextDark,
    primaryContainer = Charcoal800,
    onPrimaryContainer = TextPrimary,
    secondary = PulseViolet,
    onSecondary = TextPrimary,
    secondaryContainer = Charcoal850,
    onSecondaryContainer = TextPrimary,
    tertiary = PulseAmber,
    onTertiary = TextDark,
    background = Charcoal900,
    onBackground = TextPrimary,
    surface = Charcoal850,
    onSurface = TextPrimary,
    surfaceVariant = Charcoal800,
    onSurfaceVariant = TextSecondary,
    outline = Charcoal700,
    outlineVariant = Charcoal600
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Streaming apps look best in immersive dark mode
    dynamicColor: Boolean = false, // Keep consistent cinematic look
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = VideoPulseColorScheme,
        typography = Typography,
        content = content
    )
}
