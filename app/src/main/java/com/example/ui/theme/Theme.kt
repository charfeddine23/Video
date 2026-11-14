package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
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

@Immutable
data class AuraPalette(
    val isDark: Boolean,
    val background: Color,
    val backgroundAlt: Color,
    val surfaceCard: Color,
    val surfaceElevated: Color,
    val surfaceSubtle: Color,
    val surfaceGlass: Color,
    val dialogBg: Color,
    val bottomBarBg: Color,
    val iconButtonBg: Color,
    val borderSubtle: Color,
    val borderMuted: Color,
    val progressTrack: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val accentCyan: Color,
    val accentCyanSoft: Color,
    val accentViolet: Color,
    val accentEmerald: Color,
    val accentEmeraldSoft: Color,
    val accentGold: Color,
    val accentGoldBg: Color,
    val accentFlame: Color,
    val accentFlameSoft: Color,
    val accentDanger: Color,
    val primaryCtaBg: Color,
    val primaryCtaContent: Color,
    val unselectedChipBg: Color
)

val DarkAuraPalette = AuraPalette(
    isDark = true,
    background = Color(0xFF0B0F19),
    backgroundAlt = Color(0xFF07090D),
    surfaceCard = Color(0xD9121826),
    surfaceElevated = Color(0xE6101626),
    surfaceSubtle = Color(0x40162030),
    surfaceGlass = Color(0x1AFFFFFF),
    dialogBg = Color(0xF5121826),
    bottomBarBg = Color(0xF2090C12),
    iconButtonBg = Color(0x22FFFFFF),
    borderSubtle = Color(0x26FFFFFF),
    borderMuted = Color(0x18FFFFFF),
    progressTrack = Color(0x24FFFFFF),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFF94A3B8),
    textTertiary = Color(0xFF64748B),
    accentCyan = Color(0xFF00F2FE),
    accentCyanSoft = Color(0x2800F2FE),
    accentViolet = Color(0xFFA855F7),
    accentEmerald = Color(0xFF34D399),
    accentEmeraldSoft = Color(0x2610B981),
    accentGold = Color(0xFFFFD700),
    accentGoldBg = Color(0x26FFD700),
    accentFlame = Color(0xFFFF7A1A),
    accentFlameSoft = Color(0x28FF7A1A),
    accentDanger = Color(0xFFFCA5A5),
    primaryCtaBg = Color(0xFFFFFFFF),
    primaryCtaContent = Color(0xFF0A0C10),
    unselectedChipBg = Color(0x1F1E293B)
)

val LightAuraPalette = AuraPalette(
    isDark = false,
    background = Color(0xFFF3F6FB),
    backgroundAlt = Color(0xFFE7EDF6),
    surfaceCard = Color(0xFAFFFFFF),
    surfaceElevated = Color(0xFFFFFFFF),
    surfaceSubtle = Color(0xFFE9EFF8),
    surfaceGlass = Color(0x100F172A),
    dialogBg = Color(0xFCFFFFFF),
    bottomBarBg = Color(0xFAFFFFFF),
    iconButtonBg = Color(0x140F172A),
    borderSubtle = Color(0xFFC8D4E4),
    borderMuted = Color(0xFFDCE4F0),
    progressTrack = Color(0xFFD7E1EE),
    textPrimary = Color(0xFF0F172A),
    textSecondary = Color(0xFF475569),
    textTertiary = Color(0xFF64748B),
    accentCyan = Color(0xFF0284C7),
    accentCyanSoft = Color(0x180284C7),
    accentViolet = Color(0xFF6D28D9),
    accentEmerald = Color(0xFF059669),
    accentEmeraldSoft = Color(0x18059669),
    accentGold = Color(0xFFB45309),
    accentGoldBg = Color(0x1CD97706),
    accentFlame = Color(0xFFC2410C),
    accentFlameSoft = Color(0x1AEA580C),
    accentDanger = Color(0xFFDC2626),
    primaryCtaBg = Color(0xFF0F172A),
    primaryCtaContent = Color(0xFFFFFFFF),
    unselectedChipBg = Color(0xFFE6EDF7)
)

val LocalAuraPalette = staticCompositionLocalOf { DarkAuraPalette }

val MaterialTheme.aura: AuraPalette
    @Composable
    get() = LocalAuraPalette.current

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
    primary = Color(0xFF0284C7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0C4A6E),
    secondary = Color(0xFF6D28D9),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    onSecondaryContainer = Color(0xFF2E1065),
    tertiary = Color(0xFFB45309),
    onTertiary = Color.White,
    background = Color(0xFFF3F6FB),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE7EDF6),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFC8D4E4),
    outlineVariant = Color(0xFFDCE4F0)
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
    val auraPalette = if (isDark) DarkAuraPalette else LightAuraPalette

    CompositionLocalProvider(LocalAuraPalette provides auraPalette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
