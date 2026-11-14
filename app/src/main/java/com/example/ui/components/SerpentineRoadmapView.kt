package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VaultLevelMilestone
import com.example.data.model.VaultProgressionRegistry
import com.example.ui.theme.aura
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

private data class AmbientParticle(
    val xFraction: Float,
    val yOffset: Float,
    val radiusDp: Float,
    val speedFactor: Float,
    val color: Color,
    val phase: Float
)

// Alternating 6-beat serpentine curve that keeps orbs safely away from center-screen
// so every level card enjoys ~62% screen width and stays 100% inside screen bounds.
private val SERPENTINE_WAVE_PATTERN = floatArrayOf(0.79f, 0.84f, 0.76f, 0.24f, 0.16f, 0.21f)

private fun snakeNodeXFraction(index: Int): Float {
    return SERPENTINE_WAVE_PATTERN[index % SERPENTINE_WAVE_PATTERN.size]
}

private fun cubicBezierPoint(
    p0: Offset,
    p1: Offset,
    p2: Offset,
    p3: Offset,
    t: Float
): Offset {
    val u = 1f - t
    val tt = t * t
    val uu = u * u
    val uuu = uu * u
    val ttt = tt * t
    return Offset(
        x = uuu * p0.x + 3f * uu * t * p1.x + 3f * u * tt * p2.x + ttt * p3.x,
        y = uuu * p0.y + 3f * uu * t * p1.y + 3f * u * tt * p2.y + ttt * p3.y
    )
}

/**
 * Full 365-Level Continuous Serpentine Game Roadmap with 60fps Canvas plasma paths,
 * traveling energy pulses, pulsing RPG beacon ring, and spacious readable milestone cards.
 */
@Composable
fun SerpentineRoadmapView(
    currentProgressionLevel: Int,
    streakDays: Int,
    claimedLevels: Set<Int>,
    isArabic: Boolean,
    onClaimLevel: (level: Int, shieldsReward: Int) -> Unit,
    onClaimAllReady: (levelsWithRewards: List<Pair<Int, Int>>) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    val allMilestones = remember {
        (1..365).map { lvl -> VaultProgressionRegistry.getMilestone(lvl) }
    }

    val focusIndex = remember(currentProgressionLevel, allMilestones.size) {
        val exact = (currentProgressionLevel - 1).coerceIn(0, allMilestones.lastIndex)
        if (exact > 0) exact - 1 else 0
    }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = focusIndex)

    LaunchedEffect(focusIndex) {
        listState.scrollToItem(focusIndex)
    }

    val readyToClaim = remember(currentProgressionLevel, claimedLevels) {
        allMilestones
            .filter { it.level <= currentProgressionLevel && !claimedLevels.contains(it.level) }
            .map { it.level to it.shieldsReward }
    }

    // Live 60fps Game Animations
    val infiniteTransition = rememberInfiniteTransition(label = "roadmap_game_fx")
    val energyFlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "energy_flow"
    )
    val beaconPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "beacon_pulse"
    )
    val orbitalRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbital_rotation"
    )
    val starDrift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "star_drift"
    )

    val ambientParticles = remember {
        val colors = listOf(
            Color(0xFF00F2FE),
            Color(0xFFFFD700),
            Color(0xFF34D399),
            Color(0xFFA78BFA),
            Color(0xFFFF6D00)
        )
        List(32) { idx ->
            AmbientParticle(
                xFraction = ((idx * 37 + 13) % 94 + 3) / 100f,
                yOffset = ((idx * 53 + 7) % 100) / 100f,
                radiusDp = 1.4f + (idx % 3) * 0.9f,
                speedFactor = 0.6f + (idx % 4) * 0.25f,
                color = colors[idx % colors.size],
                phase = (idx * 0.42f)
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Live Ambient Game Starfield & Rising Luminous Motes
        Canvas(modifier = Modifier.fillMaxSize()) {
            ambientParticles.forEach { p ->
                val driftY = (p.yOffset - starDrift * p.speedFactor).let { raw ->
                    val mod = raw % 1f
                    if (mod < 0f) mod + 1f else mod
                }
                val twinkle = 0.25f + 0.65f * ((sin(starDrift * 2f * PI.toFloat() + p.phase) + 1f) / 2f)
                val center = Offset(
                    x = size.width * p.xFraction + sin(starDrift * 2f * PI.toFloat() + p.phase) * 8.dp.toPx(),
                    y = size.height * driftY
                )
                drawCircle(
                    color = p.color.copy(alpha = twinkle * 0.28f),
                    radius = (p.radiusDp * 2.6f).dp.toPx(),
                    center = center
                )
                drawCircle(
                    color = p.color.copy(alpha = twinkle * 0.85f),
                    radius = p.radiusDp.dp.toPx(),
                    center = center
                )
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Game HUD Header + Quick Warp Bar
            RoadmapHudBar(
                currentProgressionLevel = currentProgressionLevel,
                streakDays = streakDays,
                unclaimedCount = readyToClaim.size,
                isArabic = isArabic,
                onJumpToLevel = { targetLvl ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    val exact = (targetLvl - 1).coerceIn(0, allMilestones.lastIndex)
                    val scrollIdx = if (targetLvl == currentProgressionLevel && exact > 0) exact - 1 else exact
                    scope.launch {
                        listState.animateScrollToItem(scrollIdx)
                    }
                },
                onClaimAllReady = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClaimAllReady(readyToClaim)
                }
            )

            // Continuous 365-Level Winding Snake Path
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("serpentine_365_roadmap_list"),
                contentPadding = PaddingValues(top = 12.dp, bottom = 56.dp)
            ) {
                itemsIndexed(
                    items = allMilestones,
                    key = { _, item -> "snake_lvl_${item.level}" }
                ) { index, item ->
                    val isUnlocked = currentProgressionLevel >= item.level
                    val isCurrent = currentProgressionLevel == item.level
                    val isClaimed = claimedLevels.contains(item.level)

                    SerpentineLevelNodeRow(
                        index = index,
                        totalCount = allMilestones.size,
                        item = item,
                        isUnlocked = isUnlocked,
                        isCurrent = isCurrent,
                        isClaimed = isClaimed,
                        currentProgressionLevel = currentProgressionLevel,
                        energyFlow = energyFlow,
                        beaconPulse = beaconPulse,
                        orbitalRotation = orbitalRotation,
                        isArabic = isArabic,
                        onClaimClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onClaimLevel(item.level, item.shieldsReward)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RoadmapHudBar(
    currentProgressionLevel: Int,
    streakDays: Int,
    unclaimedCount: Int,
    isArabic: Boolean,
    onJumpToLevel: (Int) -> Unit,
    onClaimAllReady: () -> Unit
) {
    val aura = MaterialTheme.aura
    val progressRatio = (currentProgressionLevel.toFloat() / 365f).coerceIn(0f, 1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .border(
                1.1.dp,
                Brush.horizontalGradient(
                    listOf(
                        aura.accentCyan.copy(alpha = 0.45f),
                        aura.accentGold.copy(alpha = 0.45f),
                        aura.accentGold.copy(alpha = 0.4f)
                    )
                ),
                RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = aura.surfaceCard)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(listOf(aura.accentCyanSoft, aura.accentCyan.copy(alpha = 0.1f)))
                            )
                            .border(1.2.dp, aura.accentCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (streakDays >= 365) Icons.Default.EmojiEvents else Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = if (streakDays >= 365) aura.accentGold else aura.accentCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isArabic) "خريطة المستويات: $currentProgressionLevel / 365" else "Roadmap Progress: LVL $currentProgressionLevel / 365",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = aura.textPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.5.sp
                            )
                        )
                        Text(
                            text = if (streakDays >= 365) {
                                if (isArabic) "👑 وصلت إلى قمة الـ 365 مستوى!" else "👑 365-Level Peak Conquered!"
                            } else {
                                if (isArabic) "مسار الأفعى المتواصل • باقي ${365 - currentProgressionLevel} مرحلة" else "Continuous Snake Road • ${365 - currentProgressionLevel} stages left"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (streakDays >= 365) aura.accentGold else aura.textSecondary,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }

                if (unclaimedCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = aura.accentGoldBg,
                        border = BorderStroke(1.dp, aura.accentGold),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(onClick = onClaimAllReady)
                            .testTag("roadmap_claim_all_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = aura.accentGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isArabic) "استلام الكل ($unclaimedCount)" else "Claim ($unclaimedCount)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = aura.accentGold,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progressRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (streakDays >= 365) aura.accentGold else aura.accentCyan,
                trackColor = aura.progressTrack
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick-Warp Stage Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = aura.accentCyanSoft,
                    border = BorderStroke(1.dp, aura.accentCyan.copy(alpha = 0.75f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onJumpToLevel(currentProgressionLevel) }
                        .testTag("roadmap_locate_me_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = null,
                            tint = aura.accentCyan,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isArabic) "موقعي ($currentProgressionLevel)" else "Me ($currentProgressionLevel)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = aura.accentCyan,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }

                listOf(1, 30, 100, 200, 365).forEach { stage ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = aura.unselectedChipBg,
                        border = BorderStroke(0.8.dp, aura.borderSubtle),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onJumpToLevel(stage) }
                    ) {
                        Text(
                            text = if (stage == 365) "👑 365" else "LVL $stage",
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (stage == 365) aura.accentGold else aura.textSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SerpentineLevelNodeRow(
    index: Int,
    totalCount: Int,
    item: VaultLevelMilestone,
    isUnlocked: Boolean,
    isCurrent: Boolean,
    isClaimed: Boolean,
    currentProgressionLevel: Int,
    energyFlow: Float,
    beaconPulse: Float,
    orbitalRotation: Float,
    isArabic: Boolean,
    onClaimClick: () -> Unit
) {
    val aura = MaterialTheme.aura
    val orbSize = if (item.isMajor || isCurrent) 58.dp else 48.dp
    val currXFraction = snakeNodeXFraction(index)
    val prevXFraction = if (index > 0) snakeNodeXFraction(index - 1) else currXFraction
    val nextXFraction = if (index < totalCount - 1) snakeNodeXFraction(index + 1) else currXFraction

    val tierAccent = when {
        item.level >= 201 -> aura.accentGold
        item.level >= 101 -> aura.accentViolet
        item.level >= 31 -> if (aura.isDark) Color(0xFF38BDF8) else Color(0xFF0284C7)
        else -> aura.accentCyan
    }
    val lockedTrackColor = if (aura.isDark) Color(0xFF283548) else aura.borderSubtle

    val nodeIcon = when (item.iconType) {
        "trophy", "legend" -> Icons.Default.EmojiEvents
        "crown", "master" -> Icons.Default.WorkspacePremium
        "diamond" -> Icons.Default.Diamond
        "flame", "sparkle", "aura" -> Icons.Default.LocalFireDepartment
        else -> Icons.Default.Shield
    }

    val parentLayoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    // Force LTR coordinates for pixel-accurate alignment between Canvas Bezier curves and Cards
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            val containerWidth = maxWidth
            val orbCenterDp = containerWidth * currXFraction
            val orbOffsetX = (orbCenterDp - (orbSize / 2)).coerceIn(10.dp, containerWidth - orbSize - 10.dp)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .defaultMinSize(minHeight = 148.dp)
            ) {
                // 1. Live Glowing Serpentine Bezier Snake Track Canvas (matches measured row height)
                Canvas(modifier = Modifier.matchParentSize()) {
                val widthPx = size.width
                val heightPx = size.height
                val centerY = heightPx / 2f
                val currX = currXFraction * widthPx
                val prevX = prevXFraction * widthPx
                val nextX = nextXFraction * widthPx

                // Incoming upper half from previous node
                if (index > 0) {
                    val isPrevSegmentActive = currentProgressionLevel >= item.level
                    val isFrontier = currentProgressionLevel == item.level
                    drawSnakeHalfCurve(
                        start = Offset(0.5f * (prevX + currX), 0f),
                        c1 = Offset(0.25f * prevX + 0.75f * currX, 0.125f * heightPx),
                        c2 = Offset(currX, 0.25f * heightPx),
                        end = Offset(currX, centerY),
                        isUnlocked = isPrevSegmentActive,
                        isFrontier = isFrontier,
                        accentColor = tierAccent,
                        lockedColor = lockedTrackColor,
                        energyFlow = energyFlow
                    )
                }

                // Outgoing lower half toward next node
                if (index < totalCount - 1) {
                    val isNextSegmentActive = currentProgressionLevel >= item.level + 1
                    val isFrontier = currentProgressionLevel == item.level
                    drawSnakeHalfCurve(
                        start = Offset(currX, centerY),
                        c1 = Offset(currX, 0.75f * heightPx),
                        c2 = Offset(0.75f * currX + 0.25f * nextX, 0.875f * heightPx),
                        end = Offset(0.5f * (currX + nextX), heightPx),
                        isUnlocked = isNextSegmentActive,
                        isFrontier = isFrontier,
                        accentColor = tierAccent,
                        lockedColor = lockedTrackColor,
                        energyFlow = energyFlow
                    )
                }

                // Pulsing Shockwave + Orbital Ring around Active Player or Major Boss Node
                if (isCurrent) {
                    val waveRadius = (orbSize.toPx() * 0.52f) + beaconPulse * 18.dp.toPx()
                    drawCircle(
                        color = Color(0xFFFFD700).copy(alpha = (1f - beaconPulse) * 0.65f),
                        radius = waveRadius,
                        center = Offset(currX, centerY),
                        style = Stroke(width = 2.4.dp.toPx())
                    )
                }

                if (isCurrent || (item.isMajor && isUnlocked)) {
                    rotate(degrees = orbitalRotation, pivot = Offset(currX, centerY)) {
                        drawCircle(
                            brush = Brush.sweepGradient(
                                listOf(
                                    Color(0xFFFFD700),
                                    aura.accentCyan,
                                    Color.Transparent,
                                    Color(0xFFFFD700)
                                ),
                                center = Offset(currX, centerY)
                            ),
                            radius = (orbSize.toPx() * 0.60f),
                            center = Offset(currX, centerY),
                            style = Stroke(
                                width = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
                            )
                        )
                    }
                }
            }

            // 2. Positioned Game Level Orb on the Snake Curve
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = orbOffsetX)
                    .size(orbSize)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCurrent -> Brush.radialGradient(listOf(Color(0xFFFFD700), Color(0xFFFF6D00)))
                            isClaimed -> Brush.radialGradient(listOf(Color(0xFF34D399), Color(0xFF059669)))
                            isUnlocked && item.isMajor -> Brush.radialGradient(listOf(Color(0xFFFFD700), Color(0xFF00F2FE)))
                            isUnlocked -> Brush.radialGradient(listOf(Color(0xFF00F2FE), Color(0xFF0284C7)))
                            !aura.isDark && item.isMajor -> Brush.radialGradient(listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1)))
                            !aura.isDark -> Brush.radialGradient(listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0)))
                            item.isMajor -> Brush.radialGradient(listOf(Color(0xFF334155), Color(0xFF1E293B)))
                            else -> Brush.radialGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A)))
                        }
                    )
                    .border(
                        width = if (isCurrent || item.isMajor) 2.2.dp else 1.5.dp,
                        color = when {
                            isCurrent -> Color.White
                            isClaimed -> Color(0xFF6EE7B7)
                            isUnlocked -> tierAccent
                            item.isMajor -> aura.accentGold.copy(alpha = 0.6f)
                            else -> aura.borderSubtle
                        },
                        shape = CircleShape
                    )
                    .clickable(enabled = isUnlocked && !isClaimed, onClick = onClaimClick)
                    .testTag("snake_node_orb_${item.level}"),
                contentAlignment = Alignment.Center
            ) {
                val unlockedOrbContent = Color(0xFF07090D)
                val lockedOrbContent = if (aura.isDark) Color(0xFFCBD5E1) else aura.textPrimary
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (!isUnlocked) {
                        Icon(
                            imageVector = if (item.isMajor) Icons.Default.Star else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (item.isMajor) aura.accentGold else aura.textSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    } else if (isClaimed) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = unlockedOrbContent,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Icon(
                            imageVector = nodeIcon,
                            contentDescription = null,
                            tint = unlockedOrbContent,
                            modifier = Modifier.size(if (item.isMajor) 17.dp else 15.dp)
                        )
                    }
                    Text(
                        text = "${item.level}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isUnlocked) unlockedOrbContent else lockedOrbContent,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = if (item.level >= 100) 10.sp else 11.5.sp
                        )
                    )
                }
            }

            // "YOU ARE HERE" Floating Beacon Tag
            if (isCurrent) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFD700),
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(
                            x = (orbCenterDp - 34.dp).coerceIn(10.dp, containerWidth - 80.dp),
                            y = 6.dp
                        )
                ) {
                    Text(
                        text = if (isArabic) "أنت هنا 📍" else "YOU 📍",
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF07090D),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 9.5.sp
                        )
                    )
                }
            }

            // 3. Spacious Side Milestone Info & Claim Card
            val isOrbOnRight = currXFraction >= 0.50f
            val screenMargin = 14.dp
            val orbGap = 14.dp
            val cardStartPadding = if (isOrbOnRight) screenMargin else (orbOffsetX + orbSize + orbGap)
            val cardEndPadding = if (isOrbOnRight) ((containerWidth - orbOffsetX) + orbGap) else screenMargin

            CompositionLocalProvider(LocalLayoutDirection provides parentLayoutDirection) {
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .padding(
                            start = cardStartPadding,
                            end = cardEndPadding,
                            top = 10.dp,
                            bottom = 10.dp
                        )
                        .border(
                            width = if (isCurrent || item.isMajor || (isUnlocked && !isClaimed)) 1.4.dp else 1.dp,
                            color = when {
                                isCurrent -> aura.accentGold
                                isClaimed -> aura.accentEmerald.copy(alpha = 0.5f)
                                isUnlocked -> tierAccent.copy(alpha = 0.85f)
                                item.isMajor -> aura.accentGold.copy(alpha = 0.45f)
                                else -> aura.borderSubtle
                            },
                            shape = RoundedCornerShape(18.dp)
                        ),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isCurrent -> aura.surfaceElevated
                            isUnlocked -> aura.surfaceCard
                            else -> if (aura.isDark) Color(0xDC0E1422) else aura.surfaceSubtle
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isArabic) item.titleAr else item.titleEn,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = if (isUnlocked) aura.textPrimary else aura.textSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isUnlocked) aura.accentCyanSoft else aura.unselectedChipBg,
                                border = BorderStroke(
                                    0.8.dp,
                                    if (isUnlocked) aura.accentCyan.copy(alpha = 0.6f) else aura.borderSubtle
                                )
                            ) {
                                Text(
                                    text = "+${item.shieldsReward} 🛡️",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isUnlocked) aura.accentCyan else aura.textSecondary,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Text(
                            text = if (isArabic) item.descriptionAr else item.descriptionEn,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = aura.textSecondary,
                                fontSize = 10.5.sp,
                                lineHeight = 14.5.sp
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            when {
                                isUnlocked && !isClaimed -> {
                                    Surface(
                                        shape = RoundedCornerShape(9.dp),
                                        color = aura.accentCyanSoft,
                                        border = BorderStroke(1.dp, aura.accentCyan),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(9.dp))
                                            .clickable(onClick = onClaimClick)
                                            .testTag("claim_level_reward_${item.level}")
                                    ) {
                                        Text(
                                            text = if (isArabic) "🎁 استلام" else "🎁 Claim",
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = aura.accentCyan,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 10.5.sp
                                            )
                                        )
                                    }
                                }
                                isClaimed -> {
                                    Text(
                                        text = if (isArabic) "تم الاستلام ✓" else "Claimed ✓",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = aura.accentEmerald,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }
                                else -> {
                                    Text(
                                        text = if (isArabic) "🔒 المستوى ${item.level}" else "🔒 LVL ${item.level}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = aura.textSecondary,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }

                            if (item.isMajor) {
                                Text(
                                    text = if (isArabic) "★ محطة كبرى" else "★ MAJOR",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = aura.accentGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.5.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
            }
        }
    }
}

private fun DrawScope.drawSnakeHalfCurve(
    start: Offset,
    c1: Offset,
    c2: Offset,
    end: Offset,
    isUnlocked: Boolean,
    isFrontier: Boolean,
    accentColor: Color,
    lockedColor: Color,
    energyFlow: Float
) {
    val path = Path().apply {
        moveTo(start.x, start.y)
        cubicTo(c1.x, c1.y, c2.x, c2.y, end.x, end.y)
    }

    if (isUnlocked || isFrontier) {
        val primaryColor = if (isFrontier) Color(0xFFFFD700) else accentColor
        val secondaryColor = if (isUnlocked) Color(0xFF10B981) else Color(0xFF00F2FE)

        // 1. Wide outer ambient plasma bloom
        drawPath(
            path = path,
            color = primaryColor.copy(alpha = 0.22f),
            style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
        )
        // 2. Mid neon beam
        drawPath(
            path = path,
            brush = Brush.verticalGradient(listOf(secondaryColor, primaryColor)),
            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
        )
        // 3. Crisp bright energy core
        drawPath(
            path = path,
            color = Color.White.copy(alpha = 0.65f),
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )

        // 4. Animated traveling plasma orbs flowing down the curve
        val orb1 = cubicBezierPoint(start, c1, c2, end, energyFlow)
        val orb2T = (energyFlow + 0.5f) % 1f
        val orb2 = cubicBezierPoint(start, c1, c2, end, orb2T)

        drawCircle(
            color = Color.White.copy(alpha = 0.9f),
            radius = 3.6.dp.toPx(),
            center = orb1
        )
        drawCircle(
            color = primaryColor.copy(alpha = 0.5f),
            radius = 7.dp.toPx(),
            center = orb1
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.75f),
            radius = 2.8.dp.toPx(),
            center = orb2
        )
    } else {
        // Locked dashed track
        drawPath(
            path = path,
            color = lockedColor,
            style = Stroke(
                width = 4.dp.toPx(),
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 12f), 0f)
            )
        )
    }
}
