package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PulseCyan
import com.example.ui.theme.PulseViolet

@Composable
fun SmartFallbackCover(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector = Icons.Default.VideoFile,
    aspectRatio: Float = 1.77f,
    badgeText: String? = null,
    gradientVariant: Int = 0
) {
    val gradientBrushes = listOf(
        // Variant 0: Cyan & Deep Violet
        listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF0B0F19)),
        // Variant 1: Electric Teal & Obsidian
        listOf(Color(0xFF064E3B), Color(0xFF0F2937), Color(0xFF080C14)),
        // Variant 2: Amber Glow & Dark Smoke
        listOf(Color(0xFF451A03), Color(0xFF1C1917), Color(0xFF0B0F19)),
        // Variant 3: Deep Cosmic Magenta & Night
        listOf(Color(0xFF4A044E), Color(0xFF1E1B4B), Color(0xFF090D16))
    )

    val currentGradient = gradientBrushes[gradientVariant % gradientBrushes.size]

    Box(
        modifier = modifier
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(currentGradient))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Color(0x4000F2FE),
                        Color(0x15FFFFFF),
                        Color(0x307928CA)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        // Frosted crystal geometric lines on canvas for Liquid Glass M5 depth
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Diagonal light refraction sheen
            drawLine(
                brush = Brush.linearGradient(
                    listOf(Color.Transparent, Color(0x3300F2FE), Color.Transparent)
                ),
                start = Offset(0f, h * 0.7f),
                end = Offset(w * 0.8f, 0f),
                strokeWidth = 2.dp.toPx()
            )

            // Curved ambient glass dome
            val path = Path().apply {
                moveTo(0f, h)
                cubicTo(w * 0.3f, h * 0.6f, w * 0.7f, h * 0.4f, w, h * 0.2f)
                lineTo(w, h)
                close()
            }
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    listOf(Color(0x1400F2FE), Color(0x280B0F19))
                )
            )
        }

        // Top Corner Badge (e.g. "4K HDR" or "12 VIDEOS")
        if (badgeText != null) {
            Surface(
                shape = RoundedCornerShape(bottomStart = 10.dp, topEnd = 16.dp),
                color = Color(0xCC0B0F19),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4400F2FE)),
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text(
                    text = badgeText,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = PulseCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }

        // Center Frosted Glass Icon Pod
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0x4D121826))
                    .border(1.dp, Color(0x5500F2FE), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PulseCyan,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 12.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
