package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.aura
import com.example.ui.theme.stableStatusBarsPadding

@Composable
fun VaultScreen(
    onBack: () -> Unit,
    isArabic: Boolean = true,
    modifier: Modifier = Modifier
) {
    val aura = MaterialTheme.aura
    val haptic = LocalHapticFeedback.current

    BackHandler {
        onBack()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "vault_glow")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "vault_pulse_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(aura.background)
            .testTag("vault_screen")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(
                                aura.accentCyan.copy(alpha = 0.14f),
                                aura.accentViolet.copy(alpha = 0.10f),
                                Color.Transparent
                            ),
                            radius = size.width * 0.95f
                        ),
                        center = Offset(size.width * 0.5f, size.height * 0.35f)
                    )
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .stableStatusBarsPadding()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onBack()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(aura.iconButtonBg)
                        .testTag("vault_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = if (isArabic) "رجوع" else "Back",
                        tint = aura.textPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Vault",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = aura.textPrimary
                    )
                )
            }

            // Center Vault Coming Soon Card
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = aura.surfaceCard,
                    border = BorderStroke(
                        1.4.dp,
                        Brush.horizontalGradient(
                            listOf(
                                aura.accentCyan.copy(alpha = pulseAlpha),
                                aura.accentViolet.copy(alpha = pulseAlpha * 0.85f)
                            )
                        )
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 42.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(86.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(aura.accentCyanSoft, aura.accentViolet.copy(alpha = 0.14f))
                                    )
                                )
                                .border(1.8.dp, aura.accentCyan.copy(alpha = pulseAlpha), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = aura.accentCyan,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(22.dp))

                        Text(
                            text = "Vault",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = aura.textPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isArabic) "قريباً" else "Soon",
                            modifier = Modifier.testTag("vault_soon_label"),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = aura.accentCyan,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isArabic) {
                                "صفحة الخزينة الخاصة قيد التطوير وستتوفر قريباً"
                            } else {
                                "The private Vault experience is coming soon"
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = aura.textSecondary,
                                fontSize = 13.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
