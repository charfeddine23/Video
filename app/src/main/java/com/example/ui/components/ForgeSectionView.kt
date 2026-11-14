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
import com.example.ui.theme.StoreTheme
import com.example.ui.theme.aura
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

private fun paletteForTier(tier: ForgeKeyTier, isDark: Boolean = true): ForgeTierPalette = when (tier) {
    ForgeKeyTier.BRONZE -> ForgeTierPalette(
        primary = if (isDark) Color(0xFFE59A54) else Color(0xFFB45309),
        secondary = Color(0xFFB45309),
        glow = Color(0x44CD7F32),
        cardBgTop = if (isDark) Color(0xFF1A1412) else Color(0xFFFFF7ED),
        cardBgBottom = if (isDark) Color(0xFF0E131F) else Color(0xFFFFFBF7),
        icon = Icons.Default.VpnKey
    )
    ForgeKeyTier.SILVER -> ForgeTierPalette(
        primary = if (isDark) Color(0xFFE2E8F0) else Color(0xFF475569),
        secondary = Color(0xFF64748B),
        glow = Color(0x44CBD5E1),
        cardBgTop = if (isDark) Color(0xFF151C28) else Color(0xFFF1F5F9),
        cardBgBottom = if (isDark) Color(0xFF0D121E) else Color(0xFFFFFFFF),
        icon = Icons.Default.VpnKey
    )
    ForgeKeyTier.GOLD -> ForgeTierPalette(
        primary = if (isDark) Color(0xFFFFD700) else Color(0xFFA16207),
        secondary = Color(0xFFD97706),
        glow = Color(0x44FFD700),
        cardBgTop = if (isDark) Color(0xFF1E1910) else Color(0xFFFEFCE8),
        cardBgBottom = if (isDark) Color(0xFF0F131F) else Color(0xFFFFFFFF),
        icon = Icons.Default.VpnKey
    )
    ForgeKeyTier.DIAMOND -> ForgeTierPalette(
        primary = if (isDark) Color(0xFF00F2FE) else Color(0xFF0284C7),
        secondary = if (isDark) Color(0xFFA855F7) else Color(0xFF6D28D9),
        glow = Color(0x4400F2FE),
        cardBgTop = if (isDark) Color(0xFF101C2A) else Color(0xFFF0F9FF),
        cardBgBottom = if (isDark) Color(0xFF121024) else Color(0xFFFFFFFF),
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
    currentStoreTheme: StoreTheme,
    onSelectStoreTheme: (StoreTheme) -> Unit,
    isArabic: Boolean,
    activeView: ForgeTabDestination,
    onNavigateView: (ForgeTabDestination) -> Unit,
    onStartForge: (ForgeKeyTier) -> Unit,
    onClaimForgeKey: (ForgeKeyTier) -> Unit,
    onCancelForge: (ForgeKeyTier) -> Unit,
    modifier: Modifier = Modifier
) {
    val aura = MaterialTheme.aura
    val haptic = LocalHapticFeedback.current
    var hammerInfoTier by remember { mutableStateOf<ForgeKeyTier?>(null) }
    @Suppress("UNUSED_VARIABLE")
    val unusedCancelHandler = onCancelForge

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
                // THE FORGE HUB: THE FORGE Portal + Realistic Storefront Shop Portal
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .testTag("shop_hub_list"),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 36.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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

                    // High-Precision Storefront Shop Card
                    item(key = "shop_portal_card_item") {
                        ShopStorefrontPortalCard(
                            forgeState = forgeState,
                            furnacePulse = furnacePulse,
                            isArabic = isArabic,
                            onEnterShop = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onNavigateView(ForgeTabDestination.INSIDE_SHOP)
                            }
                        )
                    }
                }
            }

            ForgeTabDestination.INSIDE_FORGE -> {
                // INSIDE THE FORGE: Compact Back Header + 4 Workshops ONLY
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .testTag("inside_forge_chamber")
                ) {
                    // Compact Workshop Navigation Bar
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (aura.isDark) Color(0xCC161013) else aura.surfaceElevated,
                        border = BorderStroke(
                            1.dp,
                            Brush.horizontalGradient(
                                listOf(aura.accentFlame.copy(alpha = 0.5f), aura.accentGold.copy(alpha = 0.5f))
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
                                        .background(aura.iconButtonBg)
                                        .testTag("forge_exit_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = if (isArabic) "العودة إلى The Forge" else "Back to The Forge",
                                        tint = aura.textPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isArabic) "الورشات" else "Workshops",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = aura.accentGold,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 15.sp
                                        )
                                    )
                                    Text(
                                        text = if (isArabic) "تعمل أثناء استعمال التطبيق" else "Ticks on active app usage",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = aura.textSecondary,
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = aura.accentFlameSoft,
                                border = BorderStroke(1.dp, aura.accentFlame.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Whatshot,
                                        contentDescription = null,
                                        tint = aura.accentFlame,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isArabic) "4 ورشات" else "4 Bays",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = aura.accentFlame,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Four RPG Workshop Bays ONLY (No Hero Furnace Container inside)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("shop_forge_list"),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 36.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
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
                                onHammerClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    hammerInfoTier = tier
                                }
                            )
                        }
                    }
                }
            }

            ForgeTabDestination.INSIDE_SHOP -> {
                StoreThemeShopPage(
                    forgeState = forgeState,
                    currentStoreTheme = currentStoreTheme,
                    onSelectStoreTheme = onSelectStoreTheme,
                    isArabic = isArabic,
                    onBack = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onNavigateView(ForgeTabDestination.HUB)
                    }
                )
            }
        }
    }

    // Hammer Click Explanation Dialog
    val infoTier = hammerInfoTier
    if (infoTier != null) {
        val infoPalette = paletteForTier(infoTier, aura.isDark)
        val infoWorkshopTitle = when (infoTier) {
            ForgeKeyTier.BRONZE -> if (isArabic) "ورشة صهر البرونز" else "Bronze Smelting Bay"
            ForgeKeyTier.SILVER -> if (isArabic) "ورشة صهر الفضة" else "Silver Crucible Bay"
            ForgeKeyTier.GOLD -> if (isArabic) "ورشة السبيكة الذهبية" else "Royal Gold Foundry"
            ForgeKeyTier.DIAMOND -> if (isArabic) "ورشة الجوهر الماسي" else "Diamond Core Reactor"
        }

        AlertDialog(
            onDismissRequest = { hammerInfoTier = null },
            containerColor = aura.dialogBg,
            modifier = Modifier.testTag("forge_hammer_explanation_dialog"),
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(aura.accentFlameSoft)
                        .border(1.2.dp, infoPalette.primary.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Hardware,
                        contentDescription = null,
                        tint = infoPalette.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            title = {
                Text(
                    text = infoWorkshopTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = aura.textPrimary,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = aura.accentFlameSoft,
                        border = BorderStroke(1.dp, aura.accentFlame.copy(alpha = 0.45f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Furnace active • Ticks on app usage",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = aura.accentFlame,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                    if (isArabic) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "الفرن يعمل الآن • يتقدم مؤقت الصهر تلقائياً أثناء استخدامك النشط للتطبيق.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = aura.textSecondary,
                                lineHeight = 20.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "This workshop furnace advances smelting progress automatically while you actively use the app.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = aura.textSecondary,
                                lineHeight = 20.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { hammerInfoTier = null }) {
                    Text(
                        text = if (isArabic) "فهمت" else "Got it",
                        color = aura.accentCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}

@Composable
private fun ShopStorefrontPortalCard(
    forgeState: ForgeUiState,
    furnacePulse: Float,
    isArabic: Boolean,
    onEnterShop: () -> Unit
) {
    val aura = MaterialTheme.aura
    val totalKeys = forgeState.bronzeKeys + forgeState.silverKeys + forgeState.goldenKeys + forgeState.diamondKeys

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = if (aura.isDark) {
                        listOf(Color(0xFF171D2E), Color(0xFF101624), Color(0xFF0A0E18))
                    } else {
                        listOf(Color(0xFFFFFBF0), Color(0xFFFFFFFF), Color(0xFFF3F6FB))
                    }
                )
            )
            .border(
                width = 1.4.dp,
                brush = Brush.linearGradient(
                    listOf(
                        aura.accentGold.copy(alpha = 0.72f),
                        aura.accentCyan.copy(alpha = 0.55f),
                        aura.accentGold.copy(alpha = 0.65f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable { onEnterShop() }
            .testTag("shop_portal_card")
    ) {
        // Boutique Storefront Awning & Spotlight Glow
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        aura.accentGold.copy(alpha = if (aura.isDark) 0.14f else 0.10f),
                        Color.Transparent
                    ),
                    radius = size.width * 0.62f
                ),
                center = Offset(size.width * 0.5f, size.height * 0.08f)
            )
            // Architectural glass display divider lines
            val awningY = 6.dp.toPx()
            val stripeCount = 9
            val segW = size.width / stripeCount
            for (s in 0 until stripeCount) {
                if (s % 2 == 0) {
                    drawRect(
                        color = aura.accentGold.copy(alpha = if (aura.isDark) 0.14f else 0.12f),
                        topLeft = Offset(s * segW, 0f),
                        size = Size(segW, awningY)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // 1. Storefront Marquee Header (Emblem + Brand + Currency Balance Counter)
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
                            .size(50.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        aura.accentGoldBg,
                                        aura.accentCyanSoft
                                    )
                                )
                            )
                            .border(
                                1.3.dp,
                                Brush.linearGradient(listOf(aura.accentGold, aura.accentCyan)),
                                RoundedCornerShape(15.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Shop",
                            tint = aura.accentGold,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SHOP",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = aura.accentGold,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.8.sp,
                                    fontSize = 18.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = aura.accentCyanSoft,
                                border = BorderStroke(0.8.dp, aura.accentCyan.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = if (isArabic) "10 ثيمات" else "10 THEMES",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = aura.accentCyan,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }
                        Text(
                            text = if (isArabic) "تصفح وتطبيق 10 ثيمات احترافية" else "Browse & Apply 10 Signature Themes",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = aura.textSecondary,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                // Live Storefront Wallet Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = aura.accentGoldBg,
                    border = BorderStroke(1.dp, aura.accentGold.copy(alpha = 0.55f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = null,
                            tint = aura.accentGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "$totalKeys",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = aura.textPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Boutique Glass Display Window (3 Showcase Pedestals mimicking a real storefront)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (aura.isDark) Color(0x99080C14) else aura.surfaceSubtle,
                border = BorderStroke(1.dp, aura.borderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val showcaseSlots = listOf(
                        Triple(
                            if (isArabic) "سايبر وموكا" else "Cyber & Cozy",
                            if (isArabic) "Tokyo • Mocha" else "5 Palettes",
                            if (aura.isDark) Color(0xFFE59A54) else Color(0xFFB45309)
                        ),
                        Triple(
                            if (isArabic) "زجاج سائل" else "Liquid Glass",
                            if (isArabic) "Holo • Frosted" else "Glassmorphism",
                            aura.accentGold
                        ),
                        Triple(
                            if (isArabic) "تصميم بارز" else "Soft & PSD",
                            if (isArabic) "Neumorphism" else "Depth UI",
                            aura.accentCyan
                        )
                    )
                    showcaseSlots.forEachIndexed { idx, (slotTitle, tierHint, tint) ->
                        val iconVec = when (idx) {
                            0 -> Icons.Default.VpnKey
                            1 -> Icons.Default.AutoAwesome
                            else -> Icons.Default.Diamond
                        }
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = if (aura.isDark) Color(0xCC111827) else aura.surfaceElevated,
                            border = BorderStroke(0.9.dp, tint.copy(alpha = 0.42f))
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(tint.copy(alpha = 0.14f))
                                        .border(1.dp, tint.copy(alpha = 0.4f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = iconVec,
                                        contentDescription = slotTitle,
                                        tint = tint,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = slotTitle,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = aura.textPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = tierHint,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = tint,
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
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Boutique Storefront Entry Action Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                if (aura.isDark) Color(0xFF1F2937) else Color(0xFF0F172A),
                                if (aura.isDark) Color(0xFF2B2214) else Color(0xFF1E293B)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFFFFD700).copy(alpha = 0.7f * furnacePulse),
                                PulseCyan.copy(alpha = 0.6f)
                            )
                        ),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "فتح المتجر" else "Open Shop",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
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
    val aura = MaterialTheme.aura
    val activeCount = forgeState.activeSlotsCount
    val readyCount = forgeState.readySlotsCount
    val hasLiveFire = activeCount > 0 || readyCount > 0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = if (aura.isDark) {
                        listOf(Color(0xFF1B1312), Color(0xFF101626), Color(0xFF0A0E18))
                    } else {
                        listOf(Color(0xFFFFF8EE), Color(0xFFFDFDFD), Color(0xFFF2F6FC))
                    }
                )
            )
            .border(
                width = 1.4.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        aura.accentFlame.copy(alpha = if (hasLiveFire) furnacePulse else 0.65f),
                        aura.accentGold.copy(alpha = 0.75f),
                        aura.accentCyan.copy(alpha = 0.60f)
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
                        Color(0x45FF5722).copy(alpha = (if (aura.isDark) 0.25f else 0.14f) * furnacePulse),
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
                    1 -> aura.accentGold
                    else -> aura.accentCyan
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
                                    colors = if (aura.isDark) {
                                        listOf(Color(0x55FF5722), Color(0x33FFD700), Color(0xFF141925))
                                    } else {
                                        listOf(Color(0x44FF7A1A), Color(0x33F59E0B), Color(0xFFFFF3E0))
                                    }
                                )
                            )
                            .border(
                                1.4.dp,
                                Brush.linearGradient(
                                    listOf(aura.accentFlame, aura.accentGold)
                                ),
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hardware,
                            contentDescription = "The Forge",
                            tint = aura.accentGold,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "THE FORGE",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = aura.accentGold,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.6.sp,
                                    fontSize = 18.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = aura.accentFlame,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = if (isArabic) "مصهر الدروع والمفاتيح الملكي" else "Royal Shield & Key Crucible",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = aura.textSecondary,
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
                    readyCount > 0 -> aura.accentEmerald
                    activeCount > 0 -> aura.accentFlame
                    else -> aura.accentCyan
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
                    accent = aura.accentCyan,
                    icon = Icons.Default.Shield,
                    modifier = Modifier.weight(1.1f)
                )
                WalletCurrencyChip(
                    label = if (isArabic) "برونزي" else "Bronze",
                    count = forgeState.bronzeKeys,
                    accent = if (aura.isDark) Color(0xFFE59A54) else Color(0xFFB45309),
                    icon = Icons.Default.VpnKey,
                    modifier = Modifier.weight(1f)
                )
                WalletCurrencyChip(
                    label = if (isArabic) "فضي" else "Silver",
                    count = forgeState.silverKeys,
                    accent = if (aura.isDark) Color(0xFFE2E8F0) else Color(0xFF475569),
                    icon = Icons.Default.VpnKey,
                    modifier = Modifier.weight(1f)
                )
                WalletCurrencyChip(
                    label = if (isArabic) "ذهبي" else "Gold",
                    count = forgeState.goldenKeys,
                    accent = aura.accentGold,
                    icon = Icons.Default.VpnKey,
                    modifier = Modifier.weight(1f)
                )
                WalletCurrencyChip(
                    label = if (isArabic) "ماسي" else "Diamond",
                    count = forgeState.diamondKeys,
                    accent = if (aura.isDark) Color(0xFF38BDF8) else Color(0xFF0284C7),
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
                            text = if (isArabic) "دخول" else "Enter",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = Color(0xFF07090D),
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
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
    val aura = MaterialTheme.aura
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (aura.isDark) Color(0xAA0B0F19) else aura.surfaceElevated,
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
                    color = aura.textPrimary,
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
    onHammerClick: () -> Unit
) {
    val aura = MaterialTheme.aura
    val palette = remember(tier, aura.isDark) { paletteForTier(tier, aura.isDark) }
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
            if (aura.isDark) listOf(Color(0xFF0E251F), Color(0xFF0A141E), Color(0xFF070B12))
            else listOf(Color(0xFFECFDF5), Color(0xFFF6FEF9), Color(0xFFFFFFFF))
        )
        isActive -> Brush.verticalGradient(
            if (aura.isDark) listOf(Color(0xFF28150E), palette.cardBgTop, Color(0xFF0A0E17))
            else listOf(Color(0xFFFFF4E6), palette.cardBgTop, Color(0xFFFFFFFF))
        )
        canStart -> Brush.verticalGradient(
            if (aura.isDark) listOf(Color(0xFF121824), Color(0xFF0B1019), Color(0xFF070A11))
            else listOf(palette.cardBgTop, Color(0xFFF9FBFE), Color(0xFFFFFFFF))
        )
        else -> Brush.verticalGradient(
            if (aura.isDark) listOf(Color(0xFF0E121B), Color(0xFF090C14), Color(0xFF06080E))
            else listOf(Color(0xFFF1F5F9), Color(0xFFF8FAFC), Color(0xFFFFFFFF))
        )
    }

    val borderBrush = when {
        isComplete -> Brush.horizontalGradient(
            listOf(aura.accentEmerald, palette.primary, aura.accentEmerald)
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
                palette.primary.copy(alpha = 0.55f),
                aura.borderSubtle,
                palette.primary.copy(alpha = 0.50f)
            )
        )
        else -> Brush.horizontalGradient(
            listOf(aura.borderSubtle, aura.borderMuted, aura.borderSubtle)
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
                            isComplete -> aura.accentEmeraldSoft
                            isActive -> aura.accentFlameSoft
                            else -> aura.surfaceSubtle
                        },
                        border = BorderStroke(
                            1.dp,
                            when {
                                isComplete -> aura.accentEmerald
                                isActive -> aura.accentFlame
                                canStart -> palette.primary.copy(alpha = 0.55f)
                                else -> aura.borderSubtle
                            }
                        )
                    ) {
                        Text(
                            text = "#$bayIndex",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = when {
                                    isComplete -> aura.accentEmerald
                                    isActive -> aura.accentFlame
                                    canStart -> palette.primary
                                    else -> aura.textSecondary
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
                                    color = if (isIdle && !canStart) aura.textSecondary else aura.textPrimary,
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
                                    color = if (isIdle && !canStart) aura.textTertiary else palette.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = " • ",
                                color = aura.textTertiary,
                                fontSize = 10.sp
                            )
                            Text(
                                text = if (isArabic) "في الخزينة: $ownedTierCount" else "Vault: $ownedTierCount",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = aura.textSecondary,
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
                    isComplete -> aura.accentEmerald
                    isActive -> aura.accentFlame
                    canStart -> palette.primary
                    else -> aura.textTertiary
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = statusColor.copy(alpha = if (isActive || isComplete) 0.18f else 0.12f),
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
                        when {
                            aura.isDark && isActive -> Color(0xB3090C14)
                            aura.isDark -> Color(0x99060910)
                            isActive -> Color(0xFFFFF7ED)
                            else -> aura.surfaceSubtle
                        }
                    )
                    .border(
                        1.dp,
                        when {
                            isComplete -> aura.accentEmerald.copy(alpha = 0.45f)
                            isActive -> aura.accentFlame.copy(alpha = 0.45f)
                            else -> aura.borderSubtle
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
                        iconTint = if (tier.usesShields) aura.accentCyan else aura.accentGold,
                        title = inputQtyText,
                        subtitle = inputStockText,
                        subtitleColor = if (availableInput >= requiredInput) aura.accentEmerald else aura.textSecondary,
                        borderColor = if (isActive) aura.accentCyan.copy(alpha = 0.5f) else aura.borderSubtle,
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

                        // Center Core Emblem (Clickable 48dp Hammer -> Explains Furnace active • Ticks on app usage)
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .clickable { onHammerClick() }
                                .testTag("forge_hammer_${tier.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isComplete -> aura.accentEmeraldSoft
                                            isActive -> aura.accentFlameSoft
                                            else -> aura.surfaceSubtle
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
                                    imageVector = if (isComplete) Icons.Default.AutoAwesome else Icons.Default.Hardware,
                                    contentDescription = if (isArabic) "شرح عمل الفرن" else "Furnace active • Ticks on app usage",
                                    tint = when {
                                        isComplete -> aura.accentEmerald
                                        isActive -> if (aura.isDark) Color(0xFFFFD700) else aura.accentFlame
                                        canStart -> palette.primary.copy(alpha = 0.8f)
                                        else -> aura.textTertiary
                                    },
                                    modifier = Modifier
                                        .size(21.dp)
                                        .alpha(if (isIdle && !canStart) 0.55f else 1f)
                                )
                            }
                        }
                    }

                    // RIGHT CHAMBER: Output Key Mold
                    WorkshopStationPod(
                        icon = palette.icon,
                        iconTint = if (isIdle && !canStart) aura.textTertiary else palette.primary,
                        title = if (isArabic) "1× ${tier.nameAr}" else "1× ${tier.nameEn}",
                        subtitle = if (isArabic) "${tier.durationHours} ساعة عمل" else "${tier.durationHours}h Craft",
                        subtitleColor = if (isActive) aura.accentFlame else aura.textSecondary,
                        borderColor = when {
                            isComplete -> aura.accentEmerald
                            isActive -> palette.primary.copy(alpha = 0.75f)
                            else -> aura.borderSubtle
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
                                    tint = aura.accentFlame,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (isArabic) "متبقي $remainingFormatted" else "$remainingFormatted remaining",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = aura.textPrimary,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp
                                    )
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = aura.accentFlameSoft,
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
                                    listOf(Color(0xFFFF7A1A), if (aura.isDark) palette.primary else Color(0xFFFFB703))
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
                        color = aura.surfaceSubtle,
                        border = BorderStroke(1.dp, aura.borderSubtle),
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
                                tint = aura.textTertiary,
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
                                    color = aura.textSecondary,
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
    val aura = MaterialTheme.aura
    Surface(
        modifier = modifier.alpha(if (isDimmed) 0.72f else 1f),
        shape = RoundedCornerShape(14.dp),
        color = if (aura.isDark) Color(0xCC0D1320) else aura.surfaceElevated,
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
                    color = aura.textPrimary,
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
    val aura = MaterialTheme.aura
    val clamped = progress.coerceIn(0f, 1f)
    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (aura.isDark) Color(0xFF0A0E17) else aura.progressTrack)
            .border(1.dp, aura.borderSubtle, RoundedCornerShape(8.dp))
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

private fun formatForgeCountdown(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    val hours = safe / 3600
    val minutes = (safe % 3600) / 60
    val seconds = safe % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
