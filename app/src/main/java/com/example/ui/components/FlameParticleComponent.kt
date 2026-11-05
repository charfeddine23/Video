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
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    // Breathing flame flicker animation
    val infiniteTransition = rememberInfiniteTransition(label = "flame_breathing")
    val flameScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.92f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    // 1-second long-press charge up ring animation
    val chargeProgress = remember { Animatable(0f) }
    var isPressing by remember { mutableStateOf(false) }

    // Rising particle sparks
    val particles = remember { mutableStateListOf<FlameParticle>() }
    val particleColors = remember {
        listOf(
            Color(0xFFFF3D00), // Hot Orange/Red
            Color(0xFFFF9100), // Vivid Orange
            Color(0xFFFF2D73), // Hot Neon Pink
            Color(0xFFFFEA00), // Luminous Gold
            Color(0xFFFF6D00)  // Deep Amber
        )
    }

    // Particle update simulation loop
    LaunchedEffect(Unit) {
        val random = Random(System.currentTimeMillis())
        while (isActive) {
            delay(16) // ~60fps particle updates

            // Emit 2 new sparks from the base of the flame
            if (particles.size < 45) {
                particles.add(
                    FlameParticle(
                        x = random.nextFloat() * 80f - 40f, // Center offset
                        y = 20f + random.nextFloat() * 20f,
                        vx = (random.nextFloat() - 0.5f) * 1.8f,
                        vy = -2.5f - random.nextFloat() * 3.5f, // Rising upward
                        radius = 3.5f + random.nextFloat() * 4.5f,
                        alpha = 0.95f,
                        color = particleColors.random(random),
                        maxLife = 35f + random.nextFloat() * 25f
                    )
                )
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(190.dp)
                .testTag("flame_interactive_component")
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressing = true
                            val job = scope.launch {
                                chargeProgress.animateTo(
                                    targetValue = 1f,
                                    animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
                                )
                            }
                            val completed = tryAwaitRelease()
                            isPressing = false
                            if (chargeProgress.value >= 0.98f) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onLongPressToRoadmap()
                            }
                            job.cancel()
                            chargeProgress.snapTo(0f)
                        },
                        onLongPress = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onLongPressToRoadmap()
                        }
                    )
                }
        ) {
            // Particle Canvas Layer (Rising Glowing Orange & Pink Sparks)
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

            // Outer Ambient Glowing Pulsing Aura
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(flameScale)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFF5722).copy(alpha = 0.5f * glowAlpha),
                                    Color(0xFFFF2D73).copy(alpha = 0.35f * glowAlpha),
                                    Color(0xFFFF9100).copy(alpha = 0.15f * glowAlpha),
                                    Color.Transparent
                                ),
                                radius = size.width * 0.75f
                            )
                        )
                    }
            )

            // 1-Second Long-Press Charge-Up Ring (Neon Cyan/Orange gradient)
            Canvas(modifier = Modifier.size(150.dp)) {
                if (chargeProgress.value > 0f) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                Color(0xFFFF5722),
                                Color(0xFFFF2D73),
                                Color(0xFF00F2FE),
                                Color(0xFFFF5722)
                            )
                        ),
                        startAngle = -90f,
                        sweepAngle = 360f * chargeProgress.value,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            // Liquid Glass Inner Card Base for the Flame
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0x33FF5722),
                                Color(0x1A121826),
                                Color(0x800B0F19)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Central Glowing Animated Flame Icon
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = "Active Streak Flame - Long press 1s for Roadmap",
                    modifier = Modifier
                        .size(76.dp)
                        .scale(flameScale)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    listOf(
                                        Color(0xFFFFD54F).copy(alpha = 0.85f),
                                        Color(0xFFFF3D00).copy(alpha = 0.5f),
                                        Color.Transparent
                                    ),
                                    radius = size.width * 0.6f
                                )
                            )
                        },
                    tint = Color(0xFFFF6D00)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Instructional hint label
        Text(
            text = "اضغط مطولاً (1 ثانية) لفتح خارطة الإنجازات",
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        )
        Text(
            text = "Hold 1s on flame to open Milestone Roadmap",
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF00F2FE).copy(alpha = 0.85f),
                fontSize = 10.sp,
                letterSpacing = 0.3.sp
            )
        )
    }
}
