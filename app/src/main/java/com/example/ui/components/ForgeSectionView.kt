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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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
import kotlin.math.sin

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

@Composable
fun ForgeSectionView(
    shieldsCount: Int,
    forgeState: ForgeUiState,
    isArabic: Boolean,
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

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("shop_forge_list"),
        contentPadding = PaddingValues(top = 8.dp, bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Luxury Pro-Game Forge Hero Furnace & Wallet Bar
        item(key = "forge_hero_header") {
            ForgeHeroFurnaceCard(
                shieldsCount = shieldsCount,
                forgeState = forgeState,
                emberPhase = emberPhase,
                furnacePulse = furnacePulse,
                isArabic = isArabic
            )
        }

        // 2. Section Label
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

        // 3. Four Smelting Crucible Cards (Bronze, Silver, Gold, Diamond)
        items(ForgeKeyTier.entries, key = { it.id }) { tier ->
            val slot = forgeState.getSlot(tier)
            ForgeRecipeCard(
                tier = tier,
                slot = slot,
                shieldsCount = shieldsCount,
                forgeState = forgeState,
                furnacePulse = furnacePulse,
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

        // 4. Compact Shop Perks Coming Soon Card
        item(key = "shop_coming_soon_footer") {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xAA0F1626),
                border = BorderStroke(1.dp, Color(0x33FFD700)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("shop_tab_coming_soon")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0x22FFD700))
                            .border(1.dp, Color(0x66FFD700), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isArabic) "صناديق وجوائز المتجر • قريباً" else "Key Vault Chests & Perks • Soon",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Color(0xFFFFD700),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isArabic) {
                                "اجمع وصهر المفاتيح البرونزية والفضية والذهبية والماسية لفتح مكافآت المتجر القادمة."
                            } else {
                                "Forge Bronze, Silver, Golden & Diamond Keys now to unlock upcoming Shop chests."
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
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
    isArabic: Boolean
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
    furnacePulse: Float,
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

    val borderBrush = when {
        slot.isReadyToClaim -> Brush.horizontalGradient(
            listOf(Color(0xFF34D399), palette.primary, Color(0xFF34D399))
        )
        slot.isActive -> Brush.horizontalGradient(
            listOf(
                Color(0xFFFF7A1A).copy(alpha = furnacePulse),
                palette.primary,
                Color(0xFFFF7A1A).copy(alpha = furnacePulse)
            )
        )
        else -> Brush.horizontalGradient(
            listOf(
                palette.primary.copy(alpha = 0.72f),
                palette.secondary.copy(alpha = 0.35f),
                palette.primary.copy(alpha = 0.48f)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(palette.cardBgTop, palette.cardBgBottom)
                )
            )
            .border(
                width = if (slot.isActive || slot.isReadyToClaim) 1.6.dp else 1.1.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(22.dp)
            )
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            palette.glow.copy(alpha = if (slot.isActive) 0.34f * furnacePulse else 0.18f),
                            Color.Transparent
                        ),
                        radius = size.width * 0.75f
                    ),
                    center = Offset(size.width * 0.15f, size.height * 0.25f)
                )
            }
            .testTag("forge_tier_card_${tier.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Header: Emblem + Key Name + Rarity Tag + Owned Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Metallic Tier Sigil
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        palette.primary.copy(alpha = 0.28f),
                                        palette.secondary.copy(alpha = 0.16f)
                                    )
                                )
                            )
                            .border(1.4.dp, palette.primary.copy(alpha = 0.85f), RoundedCornerShape(15.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = palette.icon,
                            contentDescription = if (isArabic) tier.nameAr else tier.nameEn,
                            tint = palette.primary,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = palette.primary.copy(alpha = 0.16f),
                            border = BorderStroke(0.6.dp, palette.primary.copy(alpha = 0.45f))
                        ) {
                            Text(
                                text = if (isArabic) tier.rarityAr else tier.rarityEn,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = palette.primary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 9.5.sp,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isArabic) tier.nameAr else tier.nameEn,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.5.sp
                            )
                        )
                    }
                }

                // Owned Count Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x990A0E18),
                    border = BorderStroke(1.dp, palette.primary.copy(alpha = 0.45f))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "× $ownedTierCount",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = palette.primary,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = if (isArabic) "في الخزينة" else "Owned",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Visual Smelting Recipe Formula Strip
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0x99080C15),
                border = BorderStroke(0.9.dp, Color(0x24FFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Input Ingredient
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (tier.usesShields) Icons.Default.Shield else Icons.Default.VpnKey,
                            contentDescription = null,
                            tint = if (tier.usesShields) PulseCyan else Color(0xFFFFD700),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = when (tier) {
                                    ForgeKeyTier.BRONZE -> if (isArabic) "10 دروع" else "10 Shields"
                                    ForgeKeyTier.SILVER -> if (isArabic) "20 درعاً" else "20 Shields"
                                    ForgeKeyTier.GOLD -> if (isArabic) "30 مفتاحاً" else "30 Keys"
                                    ForgeKeyTier.DIAMOND -> if (isArabic) "100 مفتاح" else "100 Keys"
                                },
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.5.sp
                                )
                            )
                            Text(
                                text = when (tier) {
                                    ForgeKeyTier.BRONZE, ForgeKeyTier.SILVER -> if (isArabic) "المتوفر: $availableInput" else "Have: $availableInput"
                                    ForgeKeyTier.GOLD -> if (isArabic) "برونزي/فضي: $availableInput" else "Bronze/Silver: $availableInput"
                                    ForgeKeyTier.DIAMOND -> if (isArabic) "كل المفاتيح: $availableInput" else "Lower Keys: $availableInput"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (availableInput >= requiredInput) Color(0xFF34D399) else Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    // Smelting Arrow
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(palette.primary.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = palette.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Output + Duration
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = palette.icon,
                                contentDescription = null,
                                tint = palette.primary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isArabic) "1× مفتاح" else "1× Key",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = palette.primary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.5.sp
                                )
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isArabic) {
                                    "${tier.durationHours} ساعة استعمال للتطبيق"
                                } else {
                                    "${tier.durationHours}h App Usage"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dynamic Crucible State Area
            when {
                slot.isReadyToClaim -> {
                    // Ready to claim state!
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0x2810B981),
                        border = BorderStroke(1.dp, Color(0xFF34D399).copy(alpha = 0.65f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF34D399),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isArabic) "اكتمل الصهر 100%! جاهز للاستلام" else "Smelting Complete 100%! Ready",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = Color(0xFF34D399),
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = palette.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

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
                                imageVector = palette.icon,
                                contentDescription = null,
                                tint = Color(0xFF07090D),
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "استلام ${tier.nameAr}" else "Claim ${tier.nameEn}",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = Color(0xFF07090D),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.5.sp
                                )
                            )
                        }
                    }
                }

                slot.isActive -> {
                    // Actively smelting state!
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
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (isArabic) "جاري الصهر... $remainingFormatted متبقي" else "Smelting... $remainingFormatted left",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                )
                            }

                            Text(
                                text = "$percent%",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = palette.primary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { slot.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .testTag("forge_progress_${tier.id}"),
                            color = palette.primary,
                            trackColor = Color(0x28FFFFFF),
                            strokeCap = StrokeCap.Round
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isArabic) {
                                    "يتقدم تلقائياً مع كل ثانية استعمال للتطبيق"
                                } else {
                                    "Progresses live during active app usage"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF94A3B8),
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

                else -> {
                    // Idle state - Start Smelting CTA
                    if (canStart) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(palette.primary, palette.secondary)
                                    )
                                )
                                .clickable { onStart() }
                                .testTag("forge_start_${tier.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = Color(0xFF07090D),
                                    modifier = Modifier.size(19.dp)
                                )
                                Spacer(modifier = Modifier.width(7.dp))
                                Text(
                                    text = if (isArabic) {
                                        "بدء صهر ${tier.nameAr} (${tier.durationHours} س)"
                                    } else {
                                        "Forge ${tier.nameEn} (${tier.durationHours}h)"
                                    },
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = Color(0xFF07090D),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0x281E293B),
                            border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("forge_locked_${tier.id}")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (tier.usesShields) {
                                        if (isArabic) {
                                            "تحتاج $missingAmount درعاً إضافياً لبدء الصهر"
                                        } else {
                                            "Need $missingAmount more Shields to forge"
                                        }
                                    } else {
                                        if (isArabic) {
                                            "تحتاج $missingAmount مفتاحاً إضافياً لبدء الصهر"
                                        } else {
                                            "Need $missingAmount more Keys to forge"
                                        }
                                    },
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = Color(0xFF94A3B8),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
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
