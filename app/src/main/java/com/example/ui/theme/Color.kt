package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Video Pulse Dark Palette
val Charcoal950 = Color(0xFF07090D) // Deepest backdrop
val Charcoal900 = Color(0xFF0D1117) // Standard dark background
val Charcoal850 = Color(0xFF141922) // Surface
val Charcoal800 = Color(0xFF1B222E) // Surface variant / Card backdrop
val Charcoal700 = Color(0xFF263040) // Card borders / outline
val Charcoal600 = Color(0xFF374359)

// Accents and Glowing Gradients
val PulseCyan = Color(0xFF00F2FE)
val PulseNeonBlue = Color(0xFF4FACFE)
val PulseViolet = Color(0xFF8A2BE2)
val PulsePurple = Color(0xFF6B11FF)
val PulseAmber = Color(0xFFFFB800)
val PulseEmerald = Color(0xFF00E676)
val PulseRed = Color(0xFFFF3366)

// Glassmorphism and Translucent Backdrops
val GlassBackground = Color(0xCC111622)
val GlassSurface = Color(0x801A2232)
val GlassBorder = Color(0x26FFFFFF)
val TranslucentDark = Color(0x66000000)
val TranslucentWhite = Color(0x1AFFFFFF)

// Text tokens
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF9EA8B6)
val TextTertiary = Color(0xFF677384)
val TextDark = Color(0xFF0A0C10)

// Brushes
val PulseGlowBrush = Brush.linearGradient(
    colors = listOf(PulseCyan, PulseViolet)
)

val HeroVignetteBrush = Brush.verticalGradient(
    colors = listOf(
        Color.Transparent,
        Color(0x880D1117),
        Charcoal900
    )
)

val CardGlowBorder = Brush.sweepGradient(
    colors = listOf(
        PulseCyan.copy(alpha = 0.5f),
        PulseViolet.copy(alpha = 0.5f),
        Color.Transparent,
        PulseCyan.copy(alpha = 0.5f)
    )
)
