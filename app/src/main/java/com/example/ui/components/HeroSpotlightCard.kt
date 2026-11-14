package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaItem
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.Charcoal700
import com.example.ui.theme.Charcoal900
import com.example.ui.theme.Charcoal950
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PulseAmber
import com.example.ui.theme.PulseCyan
import com.example.ui.theme.PulseViolet
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.aura

@Composable
fun HeroSpotlightCard(
    mediaItem: MediaItem,
    onWatchNowClick: (MediaItem) -> Unit,
    onMoreInfoClick: (MediaItem) -> Unit,
    onToggleMyList: (MediaItem, Boolean) -> Unit,
    isSavedInMyList: Boolean = false,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val aura = MaterialTheme.aura
    var isInList by remember(isSavedInMyList) { mutableStateOf(isSavedInMyList) }

    val infiniteTransition = rememberInfiniteTransition(label = "ambient_glow")
    val glowOffset by infiniteTransition.animateFloat(
        initialValue = -30f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambient_shift"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Requirement 2: Hero Spotlight Banner - Prominent, immersive featured movie/series poster card in center
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 12.dp)
                .drawBehind {
                    // Ambient cinematic backlight glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                PulseCyan.copy(alpha = 0.22f),
                                PulseViolet.copy(alpha = 0.12f),
                                Color.Transparent
                            ),
                            center = Offset(size.width / 2 + glowOffset, size.height * 0.45f),
                            radius = size.width * 0.7f
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .aspectRatio(0.72f)
                    .clip(RoundedCornerShape(26.dp))
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0x6600F2FE),
                                Color(0x228A2BE2),
                                Color(0x33FFFFFF),
                                Color(0x10000000)
                            )
                        ),
                        shape = RoundedCornerShape(26.dp)
                    )
                    .shadow(elevation = 20.dp, shape = RoundedCornerShape(26.dp), spotColor = PulseCyan)
                    .testTag("hero_spotlight_card"),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Charcoal950)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Dramatic portrait poster photography
                    Image(
                        painter = painterResource(id = mediaItem.posterRes),
                        contentDescription = mediaItem.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Cinematic vertical gradient scrims
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0x33000000),
                                        Color.Transparent,
                                        Color(0x6607090D),
                                        Color(0xDD07090D),
                                        Charcoal950
                                    ),
                                    startY = 0f,
                                    endY = Float.POSITIVE_INFINITY
                                )
                            )
                    )

                    // Content overlay inside the card
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 18.dp, vertical = 18.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Top Badges Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Subtitle badge 1: e.g. "Top 01 in TV Shows"
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xD9101622),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PulseAmber.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Whatshot,
                                        contentDescription = "Hot ranking",
                                        tint = PulseAmber,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = mediaItem.subtitleBadge,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = PulseAmber,
                                            letterSpacing = 0.3.sp
                                        )
                                    )
                                }
                            }

                            // Subtitle badge 2: e.g. "4 episodes Free for you"
                            if (mediaItem.episodesFreeBadge != null) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xB3151D2A),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                                ) {
                                    Text(
                                        text = mediaItem.episodesFreeBadge,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = PulseCyan,
                                            letterSpacing = 0.2.sp
                                        )
                                    )
                                }
                            }
                        }

                        // Bottom info area on the poster
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Bold Arabic or modern title text ("الست أمور" or "Video Pulse")
                            if (!mediaItem.titleArabic.isNullOrBlank()) {
                                Text(
                                    text = mediaItem.titleArabic,
                                    fontFamily = CairoFontFamily,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 36.sp,
                                    lineHeight = 44.sp,
                                    color = TextPrimary,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.displayMedium
                                )
                            }

                            Text(
                                text = mediaItem.title.uppercase(),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 3.sp,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                ),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Release year, season info & 4K badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = mediaItem.year,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = TextSecondary
                                    )
                                )
                                Text(
                                    text = " • ",
                                    style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary)
                                )
                                Text(
                                    text = mediaItem.seasonsOrDuration,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = TextSecondary
                                    )
                                )
                                Text(
                                    text = " • ",
                                    style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary)
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0x33FFFFFF)
                                ) {
                                    Text(
                                        text = mediaItem.qualityTag,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                                Text(
                                    text = " • ",
                                    style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary)
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0x33FFFFFF)
                                ) {
                                    Text(
                                        text = mediaItem.maturityRating,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Genre tags ("Social | Drama | Romance")
                            Text(
                                text = mediaItem.genresText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Requirement 3: Call to Action below the poster
        // Large primary action button ("Watch Now") styled with a rounded pill shape,
        // solid white background, black text, and a play icon,
        // flanked by circular secondary icon buttons ("+" and "...") with translucent dark backdrops.
        Row(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Secondary Button: "+" (My List toggle)
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(aura.surfaceCard)
                    .border(1.dp, aura.borderSubtle, CircleShape)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        isInList = !isInList
                        onToggleMyList(mediaItem, isInList)
                    }
                    .testTag("hero_add_list_button"),
                contentAlignment = Alignment.Center
            ) {
                if (isInList) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "In My List",
                        tint = aura.accentCyan,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add to My List",
                        tint = aura.textPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Primary Call to Action: "Watch Now" rounded pill shape
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onWatchNowClick(mediaItem)
                    }
                    .testTag("hero_watch_now_button"),
                shape = RoundedCornerShape(25.dp),
                color = aura.primaryCtaBg,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Icon",
                        tint = aura.primaryCtaContent,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Watch Now",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = aura.primaryCtaContent,
                            fontSize = 16.sp,
                            letterSpacing = 0.3.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Right Secondary Button: "..."
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(aura.surfaceCard)
                    .border(1.dp, aura.borderSubtle, CircleShape)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onMoreInfoClick(mediaItem)
                    }
                    .testTag("hero_more_info_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "More Info",
                    tint = aura.textPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
