package com.example.ui.components

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ForgeKeyTier
import com.example.data.model.ForgeSlotState
import com.example.data.model.ForgeUiState
import com.example.ui.theme.PulseCyan
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class ForgeTierPalette(
    val primary: Color,
    val secondary: Color,
    val glow: Color,
    val cardBgTop: Color,
    val cardBgBottom: Color,
    val icon: ImageVector
)

private fun paletteForTier(tier: ForgeKeyTier): ForgeTierPalette = when (tier) {
    ForgeKeyTier.BRONZE -> ForgeTierPalette(
        primary = Color(0xFFE59A54),
        secondary = Color(0xFFB45309),
        glow = Color(0x44CD7F32),
        cardBgTop = Color(0xFF1A1412),
        cardBgBottom = Color(0xFF0E131F),
        icon = Icons.Default.VpnKey
    )
    ForgeKeyTier.SILVER -> ForgeTierPalette(
        primary = Color(0xFFE2E8F0),
        secondary = Color(0xFF64748B),
        glow = Color(0x44CBD5E1),
        cardBgTop = Color(0xFF151C28),
        cardBgBottom = Color(0xFF0D121E),
        icon = Icons.Default.VpnKey
    )
    ForgeKeyTier.GOLD -> ForgeTierPalette(
        primary = Color(0xFFFFD700),
        secondary = Color(0xFFD97706),
        glow = Color(0x44FFD700),
        cardBgTop = Color(0xFF1E1910),
        cardBgBottom = Color(0xFF0F131F),
        icon = Icons.Default.VpnKey
    )
    ForgeKeyTier.DIAMOND -> ForgeTierPalette(
        primary = Color(0xFF00F2FE),
        secondary = Color(0xFFA855F7),
        glow = Color(0x4400F2FE),
        cardBgTop = Color(0xFF101C2A),
        cardBgBottom = Color(0xFF121024),
        icon = Icons.Default.Diamond
    )
}

enum class ForgeTabDestination {
    HUB,
    INSIDE_FORGE,
    INSIDE_SHOP
}

@Composable
fun ForgeSectionView(
    shieldsCount: Int,
    forgeState: ForgeUiState,
    isArabic: Boolean,
    activeView: ForgeTabDestination,
    onNavigateView: (ForgeTabDestination) -> Unit,
    onStartForge: (ForgeKeyTier) -> Unit,
    onClaimForgeKey: (ForgeKeyTier) -> Unit,
    onCancelForge: (ForgeKeyTier) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var pendingCancelTier by remember { mutableStateOf<ForgeKeyTier?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "forge_ambient")
    val emberPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "forge_ember_phase"
    )
    val furnacePulse by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "forge_furnace_pulse"
    )
    val gearPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "forge_gear_rotation"
    )

    AnimatedContent(
        targetState = activeView,
        transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(180)) },
        label = "forge_tab_destination_transition",
        modifier = modifier.fillMaxSize()
    ) { destination ->
        when (destination) {
            ForgeTabDestination.HUB -> {
                // THE FORGE HUB: THE FORGE Portal + Clickable Shop Portal
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .testTag("shop_hub_list"),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 36.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Clickable THE FORGE Entrance Card
                    item(key = "forge_portal_card") {
                        ForgeHeroFurnaceCard(
                            shieldsCount = shieldsCount,
                            forgeState = forgeState,
                            emberPhase = emberPhase,
                            furnacePulse = furnacePulse,
                            isArabic = isArabic,
                            showEnterCta = true,
                            onEnterClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onNavigateView(ForgeTabDestination.INSIDE_FORGE)
                            }
                        )
                    }

                    // Clickable Shop Card
                    item(key = "shop_portal_card_item") {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xAA0F1626),
                            border = BorderStroke(1.dp, Color(0x44FFD700)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onNavigateView(ForgeTabDestination.INSIDE_SHOP)
                                }
                                .testTag("shop_portal_card")
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x22FFD700))
                                        .border(1.dp, Color(0x66FFD700), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Storefront,
                                        contentDescription = "Shop",
                                        tint = Color(0xFFFFD700),
                                        modifier = Modifier.size(23.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Shop",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = Color(0xFFFFD700),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isArabic) {
                                            "اضغط للدخول إلى المتجر وعرض رصيدك من المفاتيح"
                                        } else {
                                            "Tap to open Shop and view your keys balance"
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF94A3B8),
                                            fontSize = 11.5.sp,
                                            lineHeight = 16.sp
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x22FFD700))
                                        .border(1.dp, Color(0x55FFD700), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = Color(0xFFFFD700),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            ForgeTabDestination.INSIDE_FORGE -> {
                // INSIDE THE FORGE CHAMBER: Back Bar + Molten Furnace + Royal Smelting Recipes
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .testTag("inside_forge_chamber")
                ) {
                    // Chamber Header Navigation Bar
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xCC161013),
                        border = BorderStroke(
                            1.dp,
                            Brush.horizontalGradient(
                                listOf(Color(0x88FF7A1A), Color(0x88FFD700))
                            )
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp, bottom = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onNavigateView(ForgeTabDestination.HUB)
                                    },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x22FFFFFF))
                                        .testTag("forge_exit_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = if (isArabic) "العودة إلى The Forge" else "Back to The Forge",
                                        tint = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "THE FORGE",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = Color(0xFFFFD700),
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.4.sp,
                                            fontSize = 15.sp
                                        )
                                    )
                                    Text(
                                        text = if (isArabic) "داخل المصهر • وصفات الصهر الملكية" else "Inside Chamber • Royal Smelting Recipes",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFFCBD5E1),
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0x28FF7A1A),
                                border = BorderStroke(1.dp, Color(0x66FF7A1A))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = Color(0xFFFF7A1A),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isArabic) "المصهر" else "Forge",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFFFFB074),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Unified Interior Forge Container holding Furnace Wallet + Royal Smelting Recipes
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("shop_forge_list"),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 36.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 1. Luxury Pro-Game Forge Hero Furnace & Wallet Bar
                        item(key = "forge_hero_header") {
                            ForgeHeroFurnaceCard(
                                shieldsCount = shieldsCount,
                                forgeState = forgeState,
                                emberPhase = emberPhase,
                                furnacePulse = furnacePulse,
                                isArabic = isArabic,
                                showEnterCta = false,
                                onEnterClick = {}
                            )
                        }

                        // 2. Royal Smelting Recipes Section Label inside The Forge
                        item(key = "forge_recipes_title") {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Whatshot,
                                        contentDescription = null,
                                        tint = Color(0xFFFF7A1A),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isArabic) "وصفات الصهر الملكية" else "Royal Smelting Recipes",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp
                                        )
                                    )
                                }

                                Text(
                                    text = if (isArabic) "تعمل أثناء استعمال التطبيق" else "Ticks on active app usage",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF94A3B8),
                                        fontSize = 10.5.sp
                                    )
                                )
                            }
                        }

                        // 3. Four RPG Workshop Bays (Bronze, Silver, Gold, Diamond) inside The Forge
                        items(ForgeKeyTier.entries, key = { it.id }) { tier ->
                            val slot = forgeState.getSlot(tier)
                            ForgeRecipeCard(
                                tier = tier,
                                slot = slot,
                                shieldsCount = shieldsCount,
                                forgeState = forgeState,
                                emberPhase = emberPhase,
                                furnacePulse = furnacePulse,
                                gearPhase = gearPhase,
                                isArabic = isArabic,
                                onStart = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onStartForge(tier)
                                },
                                onClaim = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onClaimForgeKey(tier)
                                },
                                onRequestCancel = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    pendingCancelTier = tier
                                }
                            )
                        }
                    }
                }
            }

            ForgeTabDestination.INSIDE_SHOP -> {
                EmptyKeyShopPage(
                    forgeState = forgeState,
                    isArabic = isArabic,
                    onBack = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onNavigateView(ForgeTabDestination.HUB)
                    }
                )
            }
        }
    }

    // Cancel & Refund Confirmation Dialog
    val cancelTier = pendingCancelTier
    if (cancelTier != null) {
        val slot = forgeState.getSlot(cancelTier)
        val tierLabel = if (isArabic) cancelTier.nameAr else cancelTier.nameEn
        val refundLabel = if (cancelTier.usesShields) {
            if (isArabic) "${slot.refundShields} درعاً" else "${slot.refundShields} Shields"
        } else {
            val totalKeys = slot.refundBronzeKeys + slot.refundSilverKeys + slot.refundGoldKeys
            if (isArabic) "$totalKeys مفتاحاً" else "$totalKeys Keys"
        }

        AlertDialog(
            onDismissRequest = { pendingCancelTier = null },
            containerColor = Color(0xFF121826),
            title = {
                Text(
                    text = if (isArabic) "إلغاء صهر $tierLabel؟" else "Cancel $tierLabel Smelting?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(
                    text = if (isArabic) {
                        "سيتم إيقاف مؤقت الصهر الحالي واسترداد $refundLabel بالكامل إلى رصيدك."
                    } else {
                        "Current smelting timer will stop and $refundLabel will be fully refunded to your balance."
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFCBD5E1))
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCancelForge(cancelTier)
                        pendingCancelTier = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text(
                        text = if (isArabic) "نعم، استرداد" else "Refund",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingCancelTier = null }) {
                    Text(
                        text = if (isArabic) "متابعة الصهر" else "Keep Forging",
                        color = PulseCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}

@Composable
private fun ForgeHeroFurnaceCard(
    shieldsCount: Int,
    forgeState: ForgeUiState,
    emberPhase: Float,
    furnacePulse: Float,
    isArabic: Boolean,
    showEnterCta: Boolean,
    onEnterClick: () -> Unit
) {
    val activeCount = forgeState.activeSlotsCount
    val readyCount = forgeState.readySlotsCount
    val hasLiveFire = activeCount > 0 || readyCount > 0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1B1312),
                        Color(0xFF101626),
                        Color(0xFF0A0E18)
                    )
                )
            )
            .border(
                width = 1.4.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFF7A1A).copy(alpha = if (hasLiveFire) furnacePulse else 0.65f),
                        Color(0xFFFFD700).copy(alpha = 0.75f),
                        PulseCyan.copy(alpha = 0.60f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .then(
                if (showEnterCta) Modifier.clickable { onEnterClick() } else Modifier
            )
            .testTag("forge_hero_card")
    ) {
        // Animated Molten Ember & Furnace Glow Canvas
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x45FF5722).copy(alpha = 0.25f * furnacePulse),
                        Color(0x22FFD700),
                        Color.Transparent
                    ),
                    radius = size.width * 0.65f
                ),
                center = Offset(size.width * 0.5f, size.height * 0.12f)
            )

            // Subtle drifting forge sparks
            val sparkCount = 12
            for (i in 0 until sparkCount) {
                val seed = (i * 0.137f) % 1f
                val t = (emberPhase + seed) % 1f
                val xRatio = 0.08f + ((i * 0.079f + sin(t * 6.28f + i) * 0.025f) % 0.84f)
                val yRatio = 0.92f - (t * 0.85f)
                val alpha = (1f - t) * (if (hasLiveFire) 0.75f else 0.42f)
                val radius = (1.8f + (i % 3) * 1.1f).dp.toPx()
                val sparkColor = when (i % 3) {
                    0 -> Color(0xFFFF7A1A)
                    1 -> Color(0xFFFFD700)
                    else -> PulseCyan
                }
                drawCircle(
                    color = sparkColor.copy(alpha = alpha.coerceIn(0f, 1f)),
                    radius = radius,
                    center = Offset(size.width * xRatio, size.height * yRatio)
                )
            }
        }

        if (activeCount > 0) {
            ActiveMetalworkingParticleOverlay(
                tierAccent = Color(0xFFFFD700),
                showIngotOnAnvil = false,
                emissionOriginY = 0.30f,
                sparkScale = 1.15f,
                modifier = Modifier.matchParentSize()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Top Title + Live Crucible Status Badge
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
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0x55FF5722),
                                        Color(0x33FFD700),
                                        Color(0xFF141925)
                                    )
                                )
                            )
                            .border(
                                1.4.dp,
                                Brush.linearGradient(
                                    listOf(Color(0xFFFF7A1A), Color(0xFFFFD700))
                                ),
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hardware,
                            contentDescription = "The Forge",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "THE FORGE",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.6.sp,
                                    fontSize = 18.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color(0xFFFF6D00),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = if (isArabic) "مصهر الدروع والمفاتيح الملكي" else "Royal Shield & Key Crucible",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                // Live Furnace Status Chip
                val statusText = when {
                    readyCount > 0 -> if (isArabic) "$readyCount جاهز للاستلام ✨" else "$readyCount Ready ✨"
                    activeCount > 0 -> if (isArabic) "$activeCount قيد الصهر 🔥" else "$activeCount Active 🔥"
                    else -> if (isArabic) "جاهز للصهر" else "Standby"
                }
                val statusAccent = when {
                    readyCount > 0 -> Color(0xFF34D399)
                    activeCount > 0 -> Color(0xFFFF7A1A)
                    else -> PulseCyan
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusAccent.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, statusAccent.copy(alpha = 0.6f))
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = statusAccent,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.5.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5-Currency Inventory Wallet Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                WalletCurrencyChip(
                    label = if (isArabic) "الدروع" else "Shields",
                    count = shieldsCount,
                    accent = PulseCyan,
                    icon = Icons.Default.Shield,
                    modifier = Modifier.weight(1.1f)
                )
                WalletCurrencyChip(
                    label = if (isArabic) "برونزي" else "Bronze",
                    count = forgeState.bronzeKeys,
                    accent = Color(0xFFE59A54),
                    icon = Icons.Default.VpnKey,
                    modifier = Modifier.weight(1f)
                )
                WalletCurrencyChip(
                    label = if (isArabic) "فضي" else "Silver",
                    count = forgeState.silverKeys,
                    accent = Color(0xFFE2E8F0),
                    icon = Icons.Default.VpnKey,
                    modifier = Modifier.weight(1f)
                )
                WalletCurrencyChip(
                    label = if (isArabic) "ذهبي" else "Gold",
                    count = forgeState.goldenKeys,
                    accent = Color(0xFFFFD700),
                    icon = Icons.Default.VpnKey,
                    modifier = Modifier.weight(1f)
                )
                WalletCurrencyChip(
                    label = if (isArabic) "ماسي" else "Diamond",
                    count = forgeState.diamondKeys,
                    accent = Color(0xFF38BDF8),
                    icon = Icons.Default.Diamond,
                    modifier = Modifier.weight(1f)
                )
            }

            if (showEnterCta) {
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFF7A1A), Color(0xFFFFD700))
                            )
                        )
                        .clickable { onEnterClick() }
                        .testTag("enter_the_forge_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = Color(0xFF07090D),
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "دخول إلى المصهر (وصفات الصهر الملكية)" else "Enter The Forge (Royal Recipes)",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = Color(0xFF07090D),
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WalletCurrencyChip(
    label: String,
    count: Int,
    accent: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xAA0B0F19),
        border = BorderStroke(0.9.dp, accent.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = accent,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "$count",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                ),
                maxLines = 1
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = accent.copy(alpha = 0.9f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ForgeRecipeCard(
    tier: ForgeKeyTier,
    slot: ForgeSlotState,
    shieldsCount: Int,
    forgeState: ForgeUiState,
    emberPhase: Float,
    furnacePulse: Float,
    gearPhase: Float,
    isArabic: Boolean,
    onStart: () -> Unit,
    onClaim: () -> Unit,
    onRequestCancel: () -> Unit
) {
    val palette = remember(tier) { paletteForTier(tier) }
    val ownedTierCount = forgeState.getKeyCount(tier)
    val availableInput = if (tier.usesShields) shieldsCount else forgeState.availableKeysFor(tier)
    val requiredInput = if (tier.usesShields) tier.requiredShields else tier.requiredKeys
    val canStart = forgeState.canStartForging(tier, shieldsCount)
    val missingAmount = (requiredInput - availableInput).coerceAtLeast(0)

    val isActive = slot.isActive
    val isComplete = slot.isReadyToClaim
    val isIdle = !isActive && !isComplete

    val bayIndex = when (tier) {
        ForgeKeyTier.BRONZE -> "01"
        ForgeKeyTier.SILVER -> "02"
        ForgeKeyTier.GOLD -> "03"
        ForgeKeyTier.DIAMOND -> "04"
    }

    val workshopName = when (tier) {
        ForgeKeyTier.BRONZE -> if (isArabic) "ورشة صهر البرونز" else "Bronze Smelting Bay"
        ForgeKeyTier.SILVER -> if (isArabic) "ورشة صهر الفضة" else "Silver Crucible Bay"
        ForgeKeyTier.GOLD -> if (isArabic) "ورشة السبيكة الذهبية" else "Royal Gold Foundry"
        ForgeKeyTier.DIAMOND -> if (isArabic) "ورشة الجوهر الماسي" else "Diamond Core Reactor"
    }

    val bayBrush = when {
        isComplete -> Brush.verticalGradient(
            listOf(Color(0xFF0E251F), Color(0xFF0A141E), Color(0xFF070B12))
        )
        isActive -> Brush.verticalGradient(
            listOf(Color(0xFF28150E), palette.cardBgTop, Color(0xFF0A0E17))
        )
        canStart -> Brush.verticalGradient(
            listOf(Color(0xFF121824), Color(0xFF0B1019), Color(0xFF070A11))
        )
        else -> Brush.verticalGradient(
            listOf(Color(0xFF0E121B), Color(0xFF090C14), Color(0xFF06080E))
        )
    }

    val borderBrush = when {
        isComplete -> Brush.horizontalGradient(
            listOf(Color(0xFF34D399), palette.primary, Color(0xFF34D399))
        )
        isActive -> Brush.horizontalGradient(
            listOf(
                Color(0xFFFF6D00).copy(alpha = furnacePulse),
                palette.primary,
                Color(0xFFFF6D00).copy(alpha = furnacePulse)
            )
        )
        canStart -> Brush.horizontalGradient(
            listOf(
                palette.primary.copy(alpha = 0.50f),
                Color(0x44334155),
                palette.primary.copy(alpha = 0.45f)
            )
        )
        else -> Brush.horizontalGradient(
            listOf(Color(0x28475569), Color(0x181E293B), Color(0x28475569))
        )
    }

    val bayHeatColor = if (isActive) Color(0xFFFF6D00) else palette.primary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(bayBrush)
            .border(
                width = when {
                    isComplete || isActive -> 1.8.dp
                    canStart -> 1.1.dp
                    else -> 0.9.dp
                },
                brush = borderBrush,
                shape = RoundedCornerShape(24.dp)
            )
            .testTag("forge_tier_card_${tier.id}")
    ) {
        // Atmospheric Workshop Ambient Canvas (Hot Furnace Glow + Sparks vs Cold Dormant Ash)
        Canvas(modifier = Modifier.matchParentSize()) {
            when {
                isActive -> {
                    // Active molten furnace glow radiating from bottom crucible
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x55FF5722).copy(alpha = 0.38f * furnacePulse),
                                palette.glow.copy(alpha = 0.24f * furnacePulse),
                                Color.Transparent
                            ),
                            radius = size.width * 0.72f
                        ),
                        center = Offset(size.width * 0.5f, size.height * 0.44f)
                    )
                    // Rising glowing forge embers across the bay
                    for (i in 0 until 14) {
                        val seed = (i * 0.173f) % 1f
                        val t = (emberPhase + seed) % 1f
                        val x = size.width * (0.14f + ((i * 0.067f + sin(t * 6.28f + i) * 0.03f) % 0.72f))
                        val y = size.height * (0.90f - t * 0.82f)
                        val alpha = ((1f - t) * 0.85f).coerceIn(0f, 1f)
                        val color = if (i % 2 == 0) Color(0xFFFF7A1A) else palette.primary
                        drawCircle(
                            color = color.copy(alpha = alpha),
                            radius = (1.6f + (i % 3) * 0.9f).dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }
                isComplete -> {
                    // Victory emerald-gold radiance
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x4410B981).copy(alpha = 0.35f * furnacePulse),
                                palette.glow.copy(alpha = 0.28f),
                                Color.Transparent
                            ),
                            radius = size.width * 0.68f
                        ),
                        center = Offset(size.width * 0.5f, size.height * 0.45f)
                    )
                    for (i in 0 until 10) {
                        val seed = (i * 0.19f) % 1f
                        val t = (emberPhase + seed) % 1f
                        val x = size.width * (0.12f + (i * 0.085f) % 0.76f)
                        val y = size.height * (0.85f - t * 0.7f)
                        drawCircle(
                            color = Color(0xFF34D399).copy(alpha = ((1f - t) * 0.75f).coerceIn(0f, 1f)),
                            radius = 2.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }
                else -> {
                    // Idle Workshop: Faint cold iron vignette + slow drifting cool ash dust
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                if (canStart) palette.glow.copy(alpha = 0.09f) else Color(0x1464748B),
                                Color.Transparent
                            ),
                            radius = size.width * 0.55f
                        ),
                        center = Offset(size.width * 0.5f, size.height * 0.44f)
                    )
                    for (i in 0 until 5) {
                        val seed = (i * 0.23f) % 1f
                        val t = ((emberPhase * 0.5f) + seed) % 1f
                        val x = size.width * (0.28f + i * 0.11f + sin(t * 4f + i) * 0.015f)
                        val y = size.height * (0.62f - t * 0.38f)
                        drawCircle(
                            color = Color(0xFF64748B).copy(alpha = ((1f - t) * 0.22f).coerceIn(0f, 1f)),
                            radius = 1.5.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(14.dp)) {
            // 1. Workshop Top Plaque (Bay ID + Title + Live Workshop Beacon)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Bay Number Badge
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = when {
                            isComplete -> Color(0x3310B981)
                            isActive -> Color(0x33FF6D00)
                            else -> Color(0x22334155)
                        },
                        border = BorderStroke(
                            1.dp,
                            when {
                                isComplete -> Color(0xFF34D399)
                                isActive -> Color(0xFFFF7A1A)
                                canStart -> palette.primary.copy(alpha = 0.55f)
                                else -> Color(0x44475569)
                            }
                        )
                    ) {
                        Text(
                            text = "#$bayIndex",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = when {
                                    isComplete -> Color(0xFF34D399)
                                    isActive -> Color(0xFFFFB074)
                                    canStart -> palette.primary
                                    else -> Color(0xFF94A3B8)
                                },
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = workshopName,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = if (isIdle && !canStart) Color(0xFFCBD5E1) else Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isArabic) tier.rarityAr else tier.rarityEn,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isIdle && !canStart) Color(0xFF64748B) else palette.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = " • ",
                                color = Color(0xFF475569),
                                fontSize = 10.sp
                            )
                            Text(
                                text = if (isArabic) "في الخزينة: $ownedTierCount" else "Vault: $ownedTierCount",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                // Live Workshop Activity Status Pill
                val statusLabel = when {
                    isComplete -> if (isArabic) "جاهز ✨" else "READY ✨"
                    isActive -> if (isArabic) "تعمل 🔥" else "ACTIVE 🔥"
                    canStart -> if (isArabic) "خاملة ⚡" else "IDLE ⚡"
                    else -> if (isArabic) "خاملة 💤" else "IDLE 💤"
                }
                val statusColor = when {
                    isComplete -> Color(0xFF34D399)
                    isActive -> Color(0xFFFF7A1A)
                    canStart -> palette.primary
                    else -> Color(0xFF64748B)
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = statusColor.copy(alpha = if (isActive || isComplete) 0.22f else 0.12f),
                    border = BorderStroke(
                        1.dp,
                        statusColor.copy(alpha = if (isActive) furnacePulse else 0.55f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(
                                    statusColor.copy(
                                        alpha = if (isActive || isComplete) furnacePulse else 0.55f
                                    )
                                )
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = statusLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = statusColor,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. RPG 3-Chamber Crafting Bench Arena (Ore Hopper -> Crucible Machine -> Key Mold)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (isActive) Color(0xB3090C14) else Color(0x99060910)
                    )
                    .border(
                        1.dp,
                        when {
                            isComplete -> Color(0x4434D399)
                            isActive -> Color(0x55FF7A1A)
                            else -> Color(0x1EFFFFFF)
                        },
                        RoundedCornerShape(18.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 14.dp)
            ) {
                // Conduit Pipes & Flow Particles drawn behind the 3 Chambers
                Canvas(
                    modifier = Modifier
                        .matchParentSize()
                        .align(Alignment.Center)
                ) {
                    val centerY = size.height * 0.44f
                    val leftStart = size.width * 0.24f
                    val leftEnd = size.width * 0.39f
                    val rightStart = size.width * 0.61f
                    val rightEnd = size.width * 0.76f

                    if (isActive) {
                        // Hot molten glowing conduits
                        drawLine(
                            brush = Brush.horizontalGradient(listOf(PulseCyan.copy(alpha = 0.3f), Color(0xFFFF7A1A))),
                            start = Offset(leftStart, centerY),
                            end = Offset(leftEnd, centerY),
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            brush = Brush.horizontalGradient(listOf(Color(0xFFFF7A1A), palette.primary)),
                            start = Offset(rightStart, centerY),
                            end = Offset(rightEnd, centerY),
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )

                        // Flowing magma droplets along conduits
                        for (p in 0 until 2) {
                            val pt = (emberPhase * 2f + p * 0.5f) % 1f
                            val lx = leftStart + (leftEnd - leftStart) * pt
                            val rx = rightStart + (rightEnd - rightStart) * pt
                            drawCircle(
                                color = Color(0xFFFFD700),
                                radius = 3.dp.toPx(),
                                center = Offset(lx, centerY)
                            )
                            drawCircle(
                                color = palette.primary,
                                radius = 3.2.dp.toPx(),
                                center = Offset(rx, centerY)
                            )
                        }
                    } else if (isComplete) {
                        drawLine(
                            color = Color(0x8834D399),
                            start = Offset(leftStart, centerY),
                            end = Offset(leftEnd, centerY),
                            strokeWidth = 2.5.dp.toPx()
                        )
                        drawLine(
                            color = Color(0xAA34D399),
                            start = Offset(rightStart, centerY),
                            end = Offset(rightEnd, centerY),
                            strokeWidth = 2.5.dp.toPx()
                        )
                    } else {
                        // Dormant / Idle dashed cold steel pipes
                        val dash = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                        drawLine(
                            color = Color(0x44475569),
                            start = Offset(leftStart, centerY),
                            end = Offset(leftEnd, centerY),
                            strokeWidth = 2.dp.toPx(),
                            pathEffect = dash
                        )
                        drawLine(
                            color = Color(0x44475569),
                            start = Offset(rightStart, centerY),
                            end = Offset(rightEnd, centerY),
                            strokeWidth = 2.dp.toPx(),
                            pathEffect = dash
                        )
                    }
                }

                // Real-time ballistic spark & glowing molten metalworking physics layer when smelting is active
                if (isActive) {
                    ActiveMetalworkingParticleOverlay(
                        tierAccent = palette.primary,
                        showIngotOnAnvil = true,
                        emissionOriginY = 0.62f,
                        sparkScale = 1.0f,
                        modifier = Modifier.matchParentSize()
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // LEFT CHAMBER: Raw Ore Hopper (Shields / Keys)
                    val inputQtyText = when (tier) {
                        ForgeKeyTier.BRONZE -> if (isArabic) "10 دروع" else "10 Shields"
                        ForgeKeyTier.SILVER -> if (isArabic) "20 درعاً" else "20 Shields"
                        ForgeKeyTier.GOLD -> if (isArabic) "30 مفتاحاً" else "30 Keys"
                        ForgeKeyTier.DIAMOND -> if (isArabic) "100 مفتاح" else "100 Keys"
                    }
                    val inputStockText = "$availableInput / $requiredInput"
                    WorkshopStationPod(
                        icon = if (tier.usesShields) Icons.Default.Shield else Icons.Default.VpnKey,
                        iconTint = if (tier.usesShields) PulseCyan else Color(0xFFFFD700),
                        title = inputQtyText,
                        subtitle = inputStockText,
                        subtitleColor = if (availableInput >= requiredInput) Color(0xFF34D399) else Color(0xFF94A3B8),
                        borderColor = if (isActive) Color(0x6600F2FE) else Color(0x28475569),
                        isDimmed = isIdle && !canStart,
                        modifier = Modifier.weight(1f)
                    )

                    // CENTER CHAMBER: Mechanical Crucible / Anvil Reactor
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(84.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val activeAngle = if (isActive) gearPhase else if (isComplete) gearPhase * 0.5f else 0f
                        val strikeBeat = if (isActive) {
                            ((sin(emberPhase * 4f * PI.toFloat()) + 1f) * 0.5f)
                        } else 0f

                        Canvas(modifier = Modifier.size(76.dp)) {
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val outerR = size.minDimension * 0.45f
                            val innerR = size.minDimension * 0.32f

                            if (isActive) {
                                // Outer glowing rotating gear/runic ring
                                rotate(activeAngle, pivot = center) {
                                    drawCircle(
                                        color = bayHeatColor.copy(alpha = 0.85f),
                                        radius = outerR,
                                        center = center,
                                        style = Stroke(
                                            width = 2.8.dp.toPx(),
                                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 10f), 0f)
                                        )
                                    )
                                    // 4 cardinal gear teeth nodes
                                    for (g in 0 until 4) {
                                        val rad = (g * PI / 2.0)
                                        val tx = center.x + (outerR * cos(rad)).toFloat()
                                        val ty = center.y + (outerR * sin(rad)).toFloat()
                                        drawCircle(
                                            color = Color(0xFFFFD700),
                                            radius = 3.2.dp.toPx(),
                                            center = Offset(tx, ty)
                                        )
                                    }
                                }
                                // Counter-rotating inner molten arc ring
                                rotate(-activeAngle * 1.4f, pivot = center) {
                                    drawCircle(
                                        color = palette.primary.copy(alpha = 0.9f),
                                        radius = innerR,
                                        center = center,
                                        style = Stroke(
                                            width = 2.dp.toPx(),
                                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                                        )
                                    )
                                }
                                // Rhythmic forge hammer strike energy ring
                                drawCircle(
                                    color = Color(0xFFFFD700).copy(alpha = strikeBeat * 0.42f),
                                    radius = outerR * (0.72f + strikeBeat * 0.25f),
                                    center = center,
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            } else if (isComplete) {
                                // Celebratory Emerald-Gold Halo
                                rotate(activeAngle, pivot = center) {
                                    drawCircle(
                                        color = Color(0xFF34D399),
                                        radius = outerR,
                                        center = center,
                                        style = Stroke(
                                            width = 2.4.dp.toPx(),
                                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 8f), 0f)
                                        )
                                    )
                                }
                                drawCircle(
                                    color = Color(0x3310B981),
                                    radius = innerR,
                                    center = center
                                )
                            } else {
                                // IDLE WORKSHOP: Stationary cooled iron gear frame (zero motion)
                                drawCircle(
                                    color = if (canStart) palette.primary.copy(alpha = 0.38f) else Color(0x38475569),
                                    radius = outerR,
                                    center = center,
                                    style = Stroke(
                                        width = 2.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
                                    )
                                )
                                drawCircle(
                                    color = Color(0x28334155),
                                    radius = innerR,
                                    center = center,
                                    style = Stroke(width = 1.4.dp.toPx())
                                )
                            }
                        }

                        // Center Core Emblem (Animated striking hammer vs cooled resting anvil)
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isComplete -> Color(0x3310B981)
                                        isActive -> Color(0x44FF5722)
                                        else -> Color(0x660F172A)
                                    }
                                )
                                .graphicsLayer {
                                    if (isActive) {
                                        val beat = sin(emberPhase * 4f * PI.toFloat())
                                        rotationZ = beat * 11f
                                        scaleX = 1f + (beat * 0.06f)
                                        scaleY = 1f + (beat * 0.06f)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when {
                                    isComplete -> Icons.Default.AutoAwesome
                                    isActive -> Icons.Default.Hardware
                                    canStart -> Icons.Default.Hardware
                                    else -> Icons.Default.Hardware
                                },
                                contentDescription = null,
                                tint = when {
                                    isComplete -> Color(0xFF34D399)
                                    isActive -> Color(0xFFFFD700)
                                    canStart -> palette.primary.copy(alpha = 0.72f)
                                    else -> Color(0xFF475569)
                                },
                                modifier = Modifier
                                    .size(21.dp)
                                    .alpha(if (isIdle && !canStart) 0.5f else 1f)
                            )
                        }
                    }

                    // RIGHT CHAMBER: Output Key Mold
                    WorkshopStationPod(
                        icon = palette.icon,
                        iconTint = if (isIdle && !canStart) Color(0xFF64748B) else palette.primary,
                        title = if (isArabic) "1× ${tier.nameAr}" else "1× ${tier.nameEn}",
                        subtitle = if (isArabic) "${tier.durationHours} ساعة عمل" else "${tier.durationHours}h Craft",
                        subtitleColor = if (isActive) Color(0xFFFFB074) else Color(0xFF94A3B8),
                        borderColor = when {
                            isComplete -> Color(0xFF34D399)
                            isActive -> palette.primary.copy(alpha = 0.75f)
                            else -> Color(0x28475569)
                        },
                        isDimmed = isIdle && !canStart,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Bottom Control Deck (Molten Gauge when Active, Ignite CTA when Idle-Ready, Locked Slate when Idle-Low, Claim when Complete)
            when {
                isComplete -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF10B981), palette.primary)
                                )
                            )
                            .clickable { onClaim() }
                            .testTag("forge_claim_${tier.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF07090D),
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "اكتمل الصهر! استلام ${tier.nameAr} ✨" else "Complete! Claim ${tier.nameEn} ✨",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = Color(0xFF07090D),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }

                isActive -> {
                    val percent = (slot.progress * 100).toInt().coerceIn(0, 100)
                    val remainingFormatted = formatForgeCountdown(slot.remainingSeconds)

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = Color(0xFFFF7A1A),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (isArabic) "متبقي $remainingFormatted" else "$remainingFormatted remaining",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp
                                    )
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0x28FF7A1A),
                                border = BorderStroke(0.8.dp, palette.primary.copy(alpha = 0.55f))
                            ) {
                                Text(
                                    text = "$percent%",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = palette.primary,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(7.dp))

                        // Custom Molten Gauge with Glowing Ember Tip
                        WorkshopMoltenProgressBar(
                            progress = slot.progress,
                            primaryColor = palette.primary,
                            furnacePulse = furnacePulse,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp)
                                .testTag("forge_progress_${tier.id}")
                        )

                        Spacer(modifier = Modifier.height(9.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isArabic) {
                                    "الفرن يعمل الآن • يتقدم مع استعمال التطبيق"
                                } else {
                                    "Furnace active • Ticks on app usage"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFFFFB074),
                                    fontSize = 10.5.sp
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            OutlinedButton(
                                onClick = onRequestCancel,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0x55EF4444)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("forge_cancel_${tier.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = if (isArabic) "إلغاء الصهر" else "Cancel",
                                    tint = Color(0xFFFCA5A5),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isArabic) "إلغاء واسترداد" else "Cancel",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFFFCA5A5),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }

                canStart -> {
                    // Idle Workshop Ready to Ignite
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFF7A1A), palette.primary)
                                )
                            )
                            .clickable { onStart() }
                            .testTag("forge_start_${tier.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = Color(0xFF07090D),
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isArabic) {
                                    "إشعال الورشة وبدء الصهر (${tier.durationHours} س)"
                                } else {
                                    "Ignite Workshop (${tier.durationHours}h)"
                                },
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = Color(0xFF07090D),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }

                else -> {
                    // Dormant Idle Workshop with insufficient materials
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0x440F172A),
                        border = BorderStroke(1.dp, Color(0x28475569)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("forge_locked_${tier.id}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (tier.usesShields) {
                                    if (isArabic) {
                                        "خاملة • تحتاج $missingAmount درعاً إضافياً للإشعال"
                                    } else {
                                        "Idle • Need $missingAmount more Shields"
                                    }
                                } else {
                                    if (isArabic) {
                                        "خاملة • تحتاج $missingAmount مفتاحاً إضافياً للإشعال"
                                    } else {
                                        "Idle • Need $missingAmount more Keys"
                                    }
                                },
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color(0xFF94A3B8),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkshopStationPod(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    subtitleColor: Color,
    borderColor: Color,
    isDimmed: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.alpha(if (isDimmed) 0.72f else 1f),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xCC0D1320),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 9.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = subtitleColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 9.5.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WorkshopMoltenProgressBar(
    progress: Float,
    primaryColor: Color,
    furnacePulse: Float,
    modifier: Modifier = Modifier
) {
    val clamped = progress.coerceIn(0f, 1f)
    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0A0E17))
            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(8.dp))
    ) {
        val barWidth = size.width * clamped
        if (barWidth > 2f) {
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFFF5722),
                        Color(0xFFFF9800),
                        primaryColor
                    )
                ),
                size = size.copy(width = barWidth)
            )
            // Glowing molten leading ember bead
            drawCircle(
                color = Color.White.copy(alpha = 0.85f * furnacePulse),
                radius = (size.height * 0.42f),
                center = Offset(barWidth.coerceIn(size.height * 0.5f, size.width - 2f), size.height * 0.5f)
            )
        }
    }
}

private class ForgeSparkParticle(
    var x: Float = 0.5f,
    var y: Float = 0.5f,
    var prevX: Float = 0.5f,
    var prevY: Float = 0.5f,
    var vx: Float = 0f,
    var vy: Float = 0f,
    var life: Float = 0f,
    var maxLife: Float = 1f,
    var sizePx: Float = 2f,
    var isBallistic: Boolean = true
)

private class MetalworkingEngine(
    private val originY: Float
) {
    val sparks = MutableList(48) { ForgeSparkParticle() }
    var strikeCycleSec: Float = 0f
    var strikeFlash: Float = 1f
    private var ambientSpawnTimer: Float = 0f
    private var nextSlot: Int = 0

    init {
        triggerHammerBurst(count = 20)
    }

    fun step(dtSec: Float) {
        val dt = dtSec.coerceIn(0.001f, 0.05f)
        strikeCycleSec += dt
        if (strikeCycleSec >= 1.15f) {
            strikeCycleSec -= 1.15f
            strikeFlash = 1f
            triggerHammerBurst(count = 18)
        } else {
            strikeFlash = (strikeFlash - dt * 2.4f).coerceAtLeast(0f)
        }

        ambientSpawnTimer += dt
        if (ambientSpawnTimer >= 0.06f) {
            ambientSpawnTimer = 0f
            spawnRisingEmber()
        }

        for (p in sparks) {
            if (p.life <= 0f) continue
            p.prevX = p.x
            p.prevY = p.y
            p.life -= dt

            if (p.isBallistic) {
                p.vy += 1.55f * dt // gravity pulls glowing metal sparks downward
                p.vx *= 0.985f
                p.x += p.vx * dt
                p.y += p.vy * dt
                // Bounce lightly off workshop floor
                if (p.y > 0.92f && p.vy > 0f) {
                    p.y = 0.92f
                    p.vy = -p.vy * 0.36f
                    p.vx *= 0.72f
                }
            } else {
                // Rising radiant heat embers curl subtly upward
                p.x += (p.vx + sin(p.life * 9f) * 0.04f) * dt
                p.y += p.vy * dt
            }
        }
    }

    private fun allocateSpark(): ForgeSparkParticle {
        val p = sparks[nextSlot]
        nextSlot = (nextSlot + 1) % sparks.size
        return p
    }

    private fun triggerHammerBurst(count: Int) {
        repeat(count) {
            val p = allocateSpark()
            val angle = Random.nextFloat() * PI.toFloat() * 1.15f - PI.toFloat() * 1.075f // upward & sideways arc
            val speed = 0.24f + Random.nextFloat() * 0.56f
            val sx = 0.5f + (Random.nextFloat() - 0.5f) * 0.05f
            val sy = originY + (Random.nextFloat() - 0.5f) * 0.03f
            p.x = sx
            p.y = sy
            p.prevX = sx
            p.prevY = sy
            p.vx = cos(angle) * speed * 0.85f
            p.vy = sin(angle) * speed - 0.16f
            p.maxLife = 0.42f + Random.nextFloat() * 0.55f
            p.life = p.maxLife
            p.sizePx = 1.6f + Random.nextFloat() * 2.2f
            p.isBallistic = true
        }
    }

    private fun spawnRisingEmber() {
        val p = allocateSpark()
        val sx = 0.5f + (Random.nextFloat() - 0.5f) * 0.14f
        val sy = originY + (Random.nextFloat() - 0.5f) * 0.04f
        p.x = sx
        p.y = sy
        p.prevX = sx
        p.prevY = sy
        p.vx = (Random.nextFloat() - 0.5f) * 0.08f
        p.vy = -(0.18f + Random.nextFloat() * 0.28f)
        p.maxLife = 0.75f + Random.nextFloat() * 0.65f
        p.life = p.maxLife
        p.sizePx = 1.3f + Random.nextFloat() * 1.6f
        p.isBallistic = false
    }
}

@Composable
private fun ActiveMetalworkingParticleOverlay(
    tierAccent: Color,
    showIngotOnAnvil: Boolean,
    emissionOriginY: Float,
    sparkScale: Float,
    modifier: Modifier = Modifier
) {
    val engine = remember(emissionOriginY) { MetalworkingEngine(emissionOriginY) }
    var frameTick by remember { mutableLongStateOf(0L) }

    LaunchedEffect(engine) {
        var lastNanos = 0L
        while (true) {
            withFrameNanos { now ->
                if (lastNanos != 0L) {
                    val dt = (now - lastNanos) / 1_000_000_000f
                    engine.step(dt)
                }
                lastNanos = now
                frameTick = now
            }
        }
    }

    Canvas(modifier = modifier) {
        // Defer state read exclusively to the Draw phase so zero Composables recompose at 60fps
        @Suppress("UNUSED_VARIABLE")
        val tick = frameTick
        val w = size.width
        val h = size.height
        val origin = Offset(w * 0.5f, h * emissionOriginY)
        val flash = engine.strikeFlash

        // 1. Heated Glowing Metal Ingot & Hammer Impact Shockwave
        if (showIngotOnAnvil) {
            val ingotW = (w * 0.14f).coerceIn(28.dp.toPx(), 48.dp.toPx())
            val ingotH = 6.5.dp.toPx()
            val ingotTopLeft = Offset(origin.x - ingotW / 2f, origin.y - ingotH / 2f)

            // Thermal bloom radiating from heated metal billet
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFF59D).copy(alpha = 0.34f + flash * 0.42f),
                        Color(0xFFFF6D00).copy(alpha = 0.26f + flash * 0.22f),
                        Color.Transparent
                    ),
                    radius = ingotW * (1.15f + flash * 0.55f)
                ),
                center = origin
            )

            // Dark steel anvil horn base beneath glowing billet
            drawRoundRect(
                color = Color(0xFF1E293B),
                topLeft = Offset(origin.x - ingotW * 0.65f, origin.y + ingotH * 0.35f),
                size = Size(ingotW * 1.3f, 5.dp.toPx()),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )

            // Incandescent heated metal bar (white-yellow hot on strike -> molten orange between blows)
            val coreColor = lerp(Color(0xFFFF9100), Color(0xFFFFFDE7), flash)
            val edgeColor = lerp(Color(0xFFD50000), Color(0xFFFF6D00), flash)
            drawRoundRect(
                brush = Brush.horizontalGradient(listOf(edgeColor, coreColor, edgeColor)),
                topLeft = ingotTopLeft,
                size = Size(ingotW, ingotH),
                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )

            // Expanding hammer-strike shockwave ring
            if (flash > 0.02f) {
                val expand = 1f - flash
                drawCircle(
                    color = Color(0xFFFFE082).copy(alpha = flash * 0.65f),
                    radius = ingotW * (0.45f + expand * 1.15f),
                    center = origin,
                    style = Stroke(width = (2.4f * flash).dp.toPx())
                )
            }
        } else if (flash > 0.05f) {
            // Subtle incandescent furnace burst pulse in Hero card
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFE082).copy(alpha = flash * 0.24f),
                        Color(0xFFFF6D00).copy(alpha = flash * 0.14f),
                        Color.Transparent
                    ),
                    radius = w * 0.38f
                ),
                center = origin
            )
        }

        // 2. Render all active ballistic sparks & glowing embers with blackbody cooling colors
        for (p in engine.sparks) {
            if (p.life <= 0f) continue
            val heat = (p.life / p.maxLife).coerceIn(0f, 1f)
            val cur = Offset(p.x * w, p.y * h)
            val prev = Offset(p.prevX * w, p.prevY * h)

            val sparkColor = when {
                heat > 0.72f -> lerp(Color(0xFFFFD54F), Color(0xFFFFFDE7), (heat - 0.72f) / 0.28f)
                heat > 0.35f -> lerp(Color(0xFFFF6D00), tierAccent, (heat - 0.35f) / 0.37f)
                else -> lerp(Color(0xFF991B1B), Color(0xFFFF5722), heat / 0.35f)
            }.copy(alpha = (heat * 1.15f).coerceIn(0f, 1f))

            if (p.isBallistic) {
                // Stretched hot-metal streak trail
                val trailVec = cur - prev
                val streakStart = cur - (trailVec * 2.2f)
                drawLine(
                    color = sparkColor,
                    start = streakStart,
                    end = cur,
                    strokeWidth = (p.sizePx * sparkScale).coerceAtLeast(1.2f),
                    cap = StrokeCap.Round
                )
                drawCircle(
                    color = Color.White.copy(alpha = heat * 0.65f),
                    radius = (p.sizePx * 0.45f * sparkScale),
                    center = cur
                )
            } else {
                drawCircle(
                    color = sparkColor.copy(alpha = heat * 0.78f),
                    radius = p.sizePx * sparkScale,
                    center = cur
                )
            }
        }
    }
}

@Composable
private fun EmptyKeyShopPage(
    forgeState: ForgeUiState,
    isArabic: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalKeys = forgeState.bronzeKeys + forgeState.silverKeys + forgeState.goldenKeys + forgeState.diamondKeys

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("empty_key_shop_screen")
    ) {
        // Top Navigation Bar
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xCC0F1626),
            border = BorderStroke(
                1.dp,
                Brush.horizontalGradient(
                    listOf(Color(0x66FFD700), PulseCyan.copy(alpha = 0.5f))
                )
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, bottom = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0x22FFFFFF))
                            .testTag("shop_exit_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isArabic) "العودة إلى The Forge" else "Back to The Forge",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Shop",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color(0xFFFFD700),
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = if (isArabic) "متجر المفاتيح" else "Key Shop",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0x22FFD700),
                    border = BorderStroke(1.dp, Color(0x55FFD700))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Shop",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFFFFD700),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }
            }
        }

        // Top Keys Balance Card (رصيدي من المفاتيح)
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xDD111726),
            border = BorderStroke(
                1.2.dp,
                Brush.linearGradient(
                    listOf(Color(0x88FFD700), PulseCyan.copy(alpha = 0.45f))
                )
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("shop_keys_balance_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "رصيدي من المفاتيح" else "My Keys Balance",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0x22FFD700),
                        border = BorderStroke(1.dp, Color(0x44FFD700))
                    ) {
                        Text(
                            text = if (isArabic) "الإجمالي: $totalKeys" else "Total: $totalKeys",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFFFFD700),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WalletCurrencyChip(
                        label = if (isArabic) "برونزي" else "Bronze",
                        count = forgeState.bronzeKeys,
                        accent = Color(0xFFE59A54),
                        icon = Icons.Default.VpnKey,
                        modifier = Modifier.weight(1f)
                    )
                    WalletCurrencyChip(
                        label = if (isArabic) "فضي" else "Silver",
                        count = forgeState.silverKeys,
                        accent = Color(0xFFE2E8F0),
                        icon = Icons.Default.VpnKey,
                        modifier = Modifier.weight(1f)
                    )
                    WalletCurrencyChip(
                        label = if (isArabic) "ذهبي" else "Gold",
                        count = forgeState.goldenKeys,
                        accent = Color(0xFFFFD700),
                        icon = Icons.Default.VpnKey,
                        modifier = Modifier.weight(1f)
                    )
                    WalletCurrencyChip(
                        label = if (isArabic) "ماسي" else "Diamond",
                        count = forgeState.diamondKeys,
                        accent = Color(0xFF38BDF8),
                        icon = Icons.Default.Diamond,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Empty Shop Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 24.dp)
                .testTag("shop_empty_state"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(74.dp)
                        .clip(CircleShape)
                        .background(Color(0x18FFFFFF))
                        .border(1.dp, Color(0x28FFD700), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(34.dp)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = if (isArabic) "المتجر فارغ" else "Shop is empty",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun formatForgeCountdown(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    val hours = safe / 3600
    val minutes = (safe % 3600) / 60
    val seconds = safe % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
