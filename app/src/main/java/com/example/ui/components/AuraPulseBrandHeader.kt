package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StoreTheme
import com.example.ui.theme.aura
import com.example.ui.theme.auraCardBorderBrush
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class Snowflake(
    var x: Float,
    var y: Float,
    var radius: Float,
    var speedY: Float,
    var swayPhase: Float,
    var swaySpeed: Float,
    var alpha: Float
)

@Composable
fun AuraPulseBrandHeader(
    streakDays: Int,
    isArabic: Boolean = false,
    modifier: Modifier = Modifier
) {
    val aura = MaterialTheme.aura
    val isFrozen = streakDays == 0

    // Infinite transitions for living liquid aura glow (active when streakDays > 0)
    val infiniteTransition = rememberInfiniteTransition(label = "aura_liquid_glow")

    // Slow continuous color rotation angle for liquid aura (0 to 360 deg)
    val liquidRotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "liquid_angle"
    )

    // Breathing glow intensity for liquid aura
    val auraPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_pulse_scale"
    )

    // Falling Snow Particle simulation (active when streakDays == 0)
    val snowflakes = remember { mutableStateListOf<Snowflake>() }

    LaunchedEffect(isFrozen) {
        if (isFrozen) {
            val random = Random(System.currentTimeMillis())
            // Initialize 45 snowflakes
            snowflakes.clear()
            repeat(45) {
                snowflakes.add(
                    Snowflake(
                        x = random.nextFloat() * 1000f,
                        y = random.nextFloat() * 400f,
                        radius = 1.5f + random.nextFloat() * 3.0f,
                        speedY = 0.8f + random.nextFloat() * 1.6f,
                        swayPhase = random.nextFloat() * 6.28f,
                        swaySpeed = 0.02f + random.nextFloat() * 0.04f,
                        alpha = 0.35f + random.nextFloat() * 0.60f
                    )
                )
            }

            while (isActive) {
                delay(16) // ~60fps smooth snowfall
                snowflakes.forEach { flake ->
                    flake.y += flake.speedY
                    flake.swayPhase += flake.swaySpeed
                    flake.x += sin(flake.swayPhase.toDouble()).toFloat() * 0.8f

                    // Wrap around to top when fallen off canvas
                    if (flake.y > 320f) {
                        flake.y = -10f
                        flake.x = random.nextFloat() * 1000f
                    }
                }
            }
        } else {
            snowflakes.clear()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .testTag("aura_pulse_brand_header"),
        contentAlignment = Alignment.Center
    ) {
        // Dynamic Glow Layer (When streak > 0)
        if (!isFrozen) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(130.dp)
                    .drawBehind {
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val rad = size.width * 0.55f * auraPulseScale

                        // Flowing liquid spectrum: Cyan -> Violet -> Pink -> Amber -> Emerald
                        val sweepColors = listOf(
                            Color(0xFF00F2FE).copy(alpha = 0.45f),
                            Color(0xFF7928CA).copy(alpha = 0.40f),
                            Color(0xFFFF007A).copy(alpha = 0.42f),
                            Color(0xFFFF9100).copy(alpha = 0.38f),
                            Color(0xFF00F5A0).copy(alpha = 0.42f),
                            Color(0xFF00F2FE).copy(alpha = 0.45f)
                        )

                        // Smoothly animated liquid sweep gradient
                        drawCircle(
                            brush = Brush.sweepGradient(
                                colors = sweepColors,
                                center = Offset(centerX, centerY)
                            ),
                            radius = rad,
                            center = Offset(centerX, centerY)
                        )

                        // Ambient soft central radial bloom
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0x5500F2FE),
                                    Color(0x337928CA),
                                    Color.Transparent
                                ),
                                radius = size.width * 0.48f,
                                center = Offset(centerX, centerY)
                            ),
                            radius = size.width * 0.5f,
                            center = Offset(centerX, centerY)
                        )
                    }
            )
        }

        // Falling Snow Canvas Layer (When streak == 0)
        if (isFrozen) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                val canvasWidth = size.width

                snowflakes.forEach { flake ->
                    // Normalize X to fit canvas width
                    val realX = (flake.x % canvasWidth)
                    drawCircle(
                        color = (if (aura.isDark) Color(0xFFE0F7FA) else Color(0xFF0284C7)).copy(alpha = flake.alpha),
                        radius = flake.radius,
                        center = Offset(realX, flake.y)
                    )
                }

                // Accumulated frost/snow on top edge of container
                val frostPath = Path().apply {
                    moveTo(size.width * 0.15f, 32f)
                    val step = (size.width * 0.70f) / 12f
                    var currentX = size.width * 0.15f
                    for (i in 0..12) {
                        val dip = if (i % 2 == 0) 36f else 30f
                        lineTo(currentX, dip)
                        currentX += step
                    }
                    lineTo(size.width * 0.85f, 32f)
                }
                drawPath(
                    path = frostPath,
                    color = if (aura.isDark) Color(0x66B2EBF2) else Color(0x8838BDF8),
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        // Liquid Glass Text Container Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(26.dp))
                .border(
                    width = if (isFrozen) 1.5.dp else 1.dp,
                    brush = if (isFrozen) {
                        // Frosted crystalline ice border
                        Brush.linearGradient(
                            if (aura.isDark) {
                                listOf(
                                    Color(0xFFE0F7FA).copy(alpha = 0.9f),
                                    Color(0xFF80DEEA).copy(alpha = 0.5f),
                                    Color(0xFFE0F2FE).copy(alpha = 0.8f)
                                )
                            } else {
                                listOf(
                                    Color(0xFF0284C7).copy(alpha = 0.65f),
                                    Color(0xFF38BDF8).copy(alpha = 0.45f),
                                    Color(0xFF0369A1).copy(alpha = 0.65f)
                                )
                            }
                        )
                    } else if (aura.storeTheme != StoreTheme.DEFAULT) {
                        auraCardBorderBrush(aura)
                    } else {
                        // Liquid iridescent glass border
                        Brush.sweepGradient(
                            listOf(
                                aura.accentCyan.copy(alpha = 0.7f),
                                aura.accentViolet.copy(alpha = 0.55f),
                                Color(0xFFFF007A).copy(alpha = 0.55f),
                                aura.accentEmerald.copy(alpha = 0.6f),
                                aura.accentCyan.copy(alpha = 0.7f)
                            )
                        )
                    },
                    shape = RoundedCornerShape(26.dp)
                ),
            shape = RoundedCornerShape(26.dp),
            color = when {
                isFrozen && aura.isDark -> Color(0xCC081324)
                isFrozen -> Color(0xF5EBF8FF)
                aura.storeTheme != StoreTheme.DEFAULT -> aura.surfaceCard
                aura.isDark -> Color(0xD90B0F19)
                else -> aura.surfaceElevated
            },
            shadowElevation = if (isFrozen) 4.dp else 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Top Status Badge (Active Aura vs Streak Frozen)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isFrozen) {
                        // Frosted Ice Badge
                        val iceTint = if (aura.isDark) Color(0xFF80DEEA) else Color(0xFF0369A1)
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (aura.isDark) Color(0x3300E5FF) else Color(0x1E0284C7),
                            border = androidx.compose.foundation.BorderStroke(1.dp, iceTint.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AcUnit,
                                    contentDescription = "Frozen",
                                    tint = iceTint,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (isArabic) "السلسلة مجمدة • 0 أيام" else "STREAK FROZEN • 0 DAYS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (aura.isDark) Color(0xFFE0F7FA) else Color(0xFF0C4A6E),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp,
                                        letterSpacing = 1.2.sp
                                    )
                                )
                            }
                        }
                    } else {
                        // Active Liquid Aura Indicator
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = aura.accentCyanSoft,
                            border = androidx.compose.foundation.BorderStroke(1.dp, aura.accentCyan.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(aura.accentCyan)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isArabic) "الهالة متوهجة نشطة" else "LIVING AURA ACTIVE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = aura.accentCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        letterSpacing = 1.5.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Official App Title: "Video Pulse"
                Text(
                    text = "VIDEO PULSE",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 32.sp,
                        letterSpacing = 4.sp,
                        color = when {
                            isFrozen && aura.isDark -> Color(0xFFE0F7FA)
                            isFrozen -> Color(0xFF0C4A6E)
                            else -> aura.textPrimary
                        }
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.drawBehind {
                        if (isFrozen) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    listOf(Color(0x4438BDF8), Color.Transparent),
                                    radius = size.width * 0.6f
                                ),
                                center = Offset(size.width / 2f, size.height / 2f)
                            )
                        } else {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    listOf(aura.accentCyan.copy(alpha = 0.25f), Color.Transparent),
                                    radius = size.width * 0.7f
                                ),
                                center = Offset(size.width / 2f, size.height / 2f)
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                // High-End Liquid Glass Subtitle
                Text(
                    text = if (isFrozen) {
                        if (isArabic) "توقفت الهالة المتوهجة وتساقطت الثلوج • ابدأ المشاهدة لإذابة الجليد"
                        else "Living aura paused • Watch 5 min to thaw and ignite your streak"
                    } else {
                        if (isArabic) "مشغل وسائط الفاخر مدعوم بالذكاء الإصطناعي"
                        else "AI-Powered Luxury Media Player"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = when {
                            isFrozen && aura.isDark -> Color(0xFF80DEEA)
                            isFrozen -> Color(0xFF0369A1)
                            else -> aura.textSecondary
                        },
                        fontSize = 11.sp,
                        letterSpacing = if (isFrozen) 0.2.sp else 0.8.sp,
                        fontWeight = if (isFrozen) FontWeight.Medium else FontWeight.Normal
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
