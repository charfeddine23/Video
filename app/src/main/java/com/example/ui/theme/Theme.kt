package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class ThemeMode(val labelEn: String, val labelAr: String) {
    DARK("Dark", "الوضع الليلي"),
    LIGHT("Light", "الوضع النهاري"),
    SYSTEM("System", "حسب النظام")
}

enum class AppLanguage(val labelEn: String, val labelAr: String, val isRtl: Boolean) {
    ARABIC("Arabic", "العربية", true),
    ENGLISH("English", "English", false)
}

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

private val LightPulseColorScheme = lightColorScheme(
    primary = Color(0xFF007A8A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F7FA),
    onPrimaryContainer = Color(0xFF002022),
    secondary = Color(0xFF6B11FF),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE7F6),
    onSecondaryContainer = Color(0xFF1E0D33),
    tertiary = PulseAmber,
    onTertiary = TextDark,
    background = Color(0xFFF4F6F9),
    onBackground = Color(0xFF11141A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF11141A),
    surfaceVariant = Color(0xFFE6E9F0),
    onSurfaceVariant = Color(0xFF5A6475),
    outline = Color(0xFFCBD2DF),
    outlineVariant = Color(0xFFDFE4ED)
)

@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> systemInDark
    }

    val colorScheme = if (isDark) VideoPulseColorScheme else LightPulseColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
