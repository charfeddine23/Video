package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

data class FlameParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var radius: Float,
    var alpha: Float,
    val color: Color,
    val maxLife: Float,
    var life: Float = 0f
)

@Composable
fun FlameParticleComponent(
    streakDays: Int,
    onLongPressToRoadmap: () -> Unit,
    isArabic: Boolean = true,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    // Normalized progression ratio: 0.0 at Day 0, 1.0 at Day 365
    val progression = (streakDays.toFloat() / 365f).coerceIn(0f, 1f)
    val isFrozen = streakDays == 0

    // Dynamic fire intensity variables scaling from gentle warmth to an all-out raging inferno
    val maxParticles = when {
        isFrozen -> 24
        streakDays < 7 -> 35
        streakDays < 30 -> 55
        streakDays < 100 -> 75
        streakDays < 200 -> 95
        else -> 125 // Raging inferno at peak levels
    }

    // Flicker speed accelerates as the flame burns hotter and fiercer
    val breathingDuration = (850 - (progression * 520)).toInt().coerceIn(300, 850)
    val glowDuration = (700 - (progression * 420)).toInt().coerceIn(260, 700)

    val infiniteTransition = rememberInfiniteTransition(label = "flame_breathing")
    val flameScale by infiniteTransition.animateFloat(
        initialValue = if (isFrozen) 0.94f else (0.95f + progression * 0.05f),
        targetValue = if (isFrozen) 1.05f else (1.08f + progression * 0.22f), // Flame expands significantly as it roars
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = breathingDuration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = if (isFrozen) 0.45f else (0.55f + progression * 0.25f),
        targetValue = if (isFrozen) 0.85f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = glowDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    // 1-second long-press charge up ring animation
    val chargeProgress = remember { Animatable(0f) }
    var isPressing by remember { mutableStateOf(false) }

    // Rising particle sparks
    val particles = remember { mutableStateListOf<FlameParticle>() }

    // Particle color palettes transitioning from frozen blue/cyan -> orange/red -> ultra hot blue/violet core & solar gold
    val activeParticleColors = remember(streakDays) {
        when {
            streakDays == 0 -> listOf(
                Color(0xFF80DEEA), // Ice Cyan
                Color(0xFFE0F7FA), // Glacial White
                Color(0xFF00E5FF), // Electric Frost
                Color(0xFFB2EBF2), // Pale Frost
                Color(0xFF4DD0E1)  // Crystalline Cyan
            )
            streakDays < 10 -> listOf(
                Color(0xFFFFB74D), // Gentle Amber
                Color(0xFFFF9800), // Soft Orange
                Color(0xFFFF7043), // Ember Orange
                Color(0xFF80DEEA), // Lingering thawed frost spark
                Color(0xFFFFEB3B)  // Gold Spark
            )
            streakDays < 60 -> listOf(
                Color(0xFFFF3D00), // Hot Orange/Red
                Color(0xFFFF9100), // Vivid Orange
                Color(0xFFFF2D73), // Hot Neon Pink
                Color(0xFFFFEA00), // Luminous Gold
                Color(0xFFFF6D00)  // Deep Amber
            )
            streakDays < 180 -> listOf(
                Color(0xFFFF1744), // Scorching Crimson
                Color(0xFFFF5252), // Blaze Red
                Color(0xFFFF9100), // Electric Orange
                Color(0xFFFFEA00), // Solar Gold
                Color(0xFFFF007F)  // Ultra Neon Magenta
            )
            else -> listOf( // Raging Cosmic Inferno (Days 180 - 365)
                Color(0xFFFF0055), // Supernova Crimson
                Color(0xFFFF7700), // Blazing Solar Fire
                Color(0xFFFFEE00), // White-Hot Flare
                Color(0xFF7928CA), // Ultra Hot Core Violet
                Color(0xFF00F2FE), // Plasma Cyan Edge
                Color(0xFFFFFFFF)  // Pure White Hot Sparks
            )
        }
    }

    // Particle update simulation loop
    LaunchedEffect(streakDays) {
        val random = Random(System.currentTimeMillis())
        while (isActive) {
            delay(16) // ~60fps particle updates

            // Emit sparks from the base of the flame with higher velocity & spread as fire rages
            val emitCount = if (progression > 0.6f) 3 else 2
            if (particles.size < maxParticles) {
                repeat(emitCount) {
                    val spread = 80f + progression * 70f
                    val upwardSpeed = 2.5f + (progression * 4.5f)
                    particles.add(
                        FlameParticle(
                            x = random.nextFloat() * spread - (spread / 2f),
                            y = 20f + random.nextFloat() * 20f,
                            vx = (random.nextFloat() - 0.5f) * (1.8f + progression * 2.2f),
                            vy = -upwardSpeed - random.nextFloat() * (3.5f + progression * 3.5f),
                            radius = (3.5f + random.nextFloat() * 4.5f) * (1f + progression * 0.65f),
                            alpha = 0.95f,
                            color = activeParticleColors.random(random),
                            maxLife = 35f + random.nextFloat() * (25f + progression * 20f)
                        )
                    )
                }
            }

            // Update existing particles
            val iterator = particles.iterator()
            while (iterator.hasNext()) {
                val p = iterator.next()
                p.x += p.vx
                p.y += p.vy
                p.life += 1f
                p.alpha = (1f - (p.life / p.maxLife)).coerceIn(0f, 1f)
                p.radius = (p.radius * 0.97f).coerceAtLeast(1.2f)

                if (p.life >= p.maxLife || p.alpha <= 0.05f) {
                    iterator.remove()
                }
            }
        }
    }

    // Central Flame Tint & Aura Gradient Interpolation
    val auraRadiusScale = 0.75f + (progression * 0.55f)
    val flameIconSize = (76.dp + (progression * 24).dp)

    val flamePrimaryTint = when {
        isFrozen -> Color(0xFF80DEEA)
        streakDays < 10 -> Color(0xFFFFB74D) // Thawing Warm Gold
        streakDays < 60 -> Color(0xFFFF6D00) // Deep Orange
        streakDays < 180 -> Color(0xFFFF3D00) // Fierce Blaze
        else -> Color(0xFFFF1744) // Raging White-Hot Crimson
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(200.dp)
                .testTag("flame_interactive_component")
                .pointerInput(Unit) {
                    var triggeredDuringPress = false
                    detectTapGestures(
                        onPress = {
                            isPressing = true
                            triggeredDuringPress = false
                            val job = scope.launch {
                                chargeProgress.animateTo(
                                    targetValue = 1f,
                                    animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
                                )
                            }
                            tryAwaitRelease()
                            isPressing = false
                            if (!triggeredDuringPress && chargeProgress.value >= 0.98f) {
                                triggeredDuringPress = true
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onLongPressToRoadmap()
                            }
                            job.cancel()
                            chargeProgress.snapTo(0f)
                        },
                        onLongPress = {
                            if (!triggeredDuringPress) {
                                triggeredDuringPress = true
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onLongPressToRoadmap()
                            }
                        }
                    )
                }
        ) {
            // Particle Canvas Layer (Rising Glowing Sparks)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerX = size.width / 2f
                val centerY = size.height / 2f

                particles.forEach { particle ->
                    drawCircle(
                        color = particle.color.copy(alpha = particle.alpha),
                        radius = particle.radius,
                        center = Offset(centerX + particle.x, centerY + particle.y)
                    )
                }
            }

            // Outer Ambient Glowing Pulsing Aura (Scales smoothly with level)
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .scale(flameScale)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = when {
                                    isFrozen -> listOf(
                                        Color(0xFF00E5FF).copy(alpha = 0.45f * glowAlpha),
                                        Color(0xFF80DEEA).copy(alpha = 0.25f * glowAlpha),
                                        Color.Transparent
                                    )
                                    streakDays < 10 -> listOf(
                                        Color(0xFFFFB74D).copy(alpha = 0.45f * glowAlpha),
                                        Color(0xFF80DEEA).copy(alpha = 0.20f * (1f - progression * 3f).coerceAtLeast(0f)), // Thawing ice edge
                                        Color(0xFFFF9800).copy(alpha = 0.25f * glowAlpha),
                                        Color.Transparent
                                    )
                                    streakDays < 60 -> listOf(
                                        Color(0xFFFF5722).copy(alpha = 0.55f * glowAlpha),
                                        Color(0xFFFF2D73).copy(alpha = 0.40f * glowAlpha),
                                        Color(0xFFFF9100).copy(alpha = 0.25f * glowAlpha),
                                        Color.Transparent
                                    )
                                    streakDays < 180 -> listOf(
                                        Color(0xFFFF1744).copy(alpha = 0.65f * glowAlpha),
                                        Color(0xFFFF9100).copy(alpha = 0.45f * glowAlpha),
                                        Color(0xFFFF2D73).copy(alpha = 0.35f * glowAlpha),
                                        Color.Transparent
                                    )
                                    else -> listOf( // Max Raging Fire at Day 365
                                        Color(0xFFFF0055).copy(alpha = 0.75f * glowAlpha),
                                        Color(0xFFFF9100).copy(alpha = 0.55f * glowAlpha),
                                        Color(0xFF7928CA).copy(alpha = 0.45f * glowAlpha),
                                        Color(0xFFFFEA00).copy(alpha = 0.30f * glowAlpha),
                                        Color.Transparent
                                    )
                                },
                                radius = size.width * auraRadiusScale
                            )
                        )
                    }
            )

            // 1-Second Long-Press Charge-Up Ring
            Canvas(modifier = Modifier.size(160.dp)) {
                if (chargeProgress.value > 0f) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            when {
                                isFrozen -> listOf(
                                    Color(0xFF00E5FF),
                                    Color(0xFF80DEEA),
                                    Color(0xFFE0F7FA),
                                    Color(0xFF00E5FF)
                                )
                                streakDays < 30 -> listOf(
                                    Color(0xFFFF9800),
                                    Color(0xFFFFB74D),
                                    Color(0xFF00F2FE),
                                    Color(0xFFFF9800)
                                )
                                else -> listOf(
                                    Color(0xFFFF1744),
                                    Color(0xFFFF9100),
                                    Color(0xFFFFEA00),
                                    Color(0xFFFF007F),
                                    Color(0xFFFF1744)
                                )
                            }
                        ),
                        startAngle = -90f,
                        sweepAngle = 360f * chargeProgress.value,
                        useCenter = false,
                        style = Stroke(width = (4.dp + (progression * 2).dp).toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            // Liquid Glass Inner Card Base for the Flame
            Box(
                modifier = Modifier
                    .size(118.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            when {
                                isFrozen -> listOf(
                                    Color(0x3300E5FF),
                                    Color(0x1A081324),
                                    Color(0x800B0F19)
                                )
                                streakDays < 20 -> listOf(
                                    Color(0x33FF9800),
                                    Color(0x1A121826),
                                    Color(0x800B0F19)
                                )
                                else -> listOf(
                                    Color(0x4DFF3D00),
                                    Color(0x2B1E1026),
                                    Color(0x900B0F19)
                                )
                            }
                        )
                    )
                    .border(
                        width = (1.dp + (progression * 1.5).dp),
                        brush = Brush.linearGradient(
                            if (isFrozen) {
                                listOf(Color(0x6600E5FF), Color(0x2280DEEA))
                            } else {
                                listOf(flamePrimaryTint.copy(alpha = 0.8f), Color(0x33FFA000))
                            }
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Central Glowing Animated Flame Icon
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = "Streak Flame - Long press 1s for Roadmap",
                    modifier = Modifier
                        .size(flameIconSize)
                        .scale(flameScale)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    when {
                                        isFrozen -> listOf(
                                            Color(0xFFE0F7FA).copy(alpha = 0.85f),
                                            Color(0xFF00E5FF).copy(alpha = 0.5f),
                                            Color.Transparent
                                        )
                                        streakDays < 10 -> listOf(
                                            Color(0xFFFFECB3).copy(alpha = 0.85f),
                                            Color(0xFFFF9800).copy(alpha = 0.5f),
                                            Color.Transparent
                                        )
                                        streakDays < 60 -> listOf(
                                            Color(0xFFFFD54F).copy(alpha = 0.85f),
                                            Color(0xFFFF3D00).copy(alpha = 0.55f),
                                            Color.Transparent
                                        )
                                        else -> listOf(
                                            Color(0xFFFFFFFF).copy(alpha = 0.95f),
                                            Color(0xFFFFEA00).copy(alpha = 0.80f),
                                            Color(0xFFFF1744).copy(alpha = 0.65f),
                                            Color.Transparent
                                        )
                                    },
                                    radius = size.width * (0.6f + progression * 0.25f)
                                )
                            )
                        },
                    tint = flamePrimaryTint
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Instructional hint label reflecting heat status
        val statusTextAr = when {
            streakDays == 0 -> "السلسلة مجمدة • ابدأ المشاهدة لإذابة الجليد"
            streakDays < 7 -> "الشعلة تذيب الجليد وتبدأ بالاشتعال • يوم $streakDays"
            streakDays < 30 -> "شعلة ملتهبة بنشاط • يوم $streakDays"
            streakDays < 100 -> "نار مشتعلة متأججة • يوم $streakDays"
            streakDays < 250 -> "جحيم مستعر بقوة فائقة • يوم $streakDays"
            else -> "نار مستعرة قصوى في ذروتها الأسطورية 🔥 • يوم $streakDays / 365"
        }

        Text(
            text = statusTextAr,
            style = MaterialTheme.typography.labelSmall.copy(
                color = flamePrimaryTint,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = if (isArabic) "اضغط مطولاً (1 ثانية) لفتح صفحة Vault" else "Hold 1s on flame to open Vault",
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (isFrozen) Color(0xFFB2EBF2) else Color(0xFF00F2FE).copy(alpha = 0.85f),
                fontSize = 10.sp,
                letterSpacing = 0.3.sp
            )
        )
    }
}
