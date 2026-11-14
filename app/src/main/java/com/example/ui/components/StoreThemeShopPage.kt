package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ForgeKeyTier
import com.example.data.model.ForgeUiState
import com.example.ui.theme.AuraPalette
import com.example.ui.theme.StoreTheme
import com.example.ui.theme.StoreThemeCategory
import com.example.ui.theme.aura
import com.example.ui.theme.auraCardBorderBrush
import com.example.ui.theme.auraPaletteFor
import com.example.ui.viewmodel.ThemeTrialState
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private enum class ShopCategoryDestination {
    HUB,
    THEMES,
    AVATAR_SOON,
    SEEKBARE_SOON,
    MIMIC_SOON
}

@Composable
fun StoreThemeShopPage(
    shieldsCount: Int,
    forgeState: ForgeUiState,
    currentStoreTheme: StoreTheme,
    purchasedThemeIds: Set<String>,
    activeThemeTrial: ThemeTrialState?,
    onEquipOwnedTheme: (StoreTheme) -> Unit,
    onPurchaseTheme: (StoreTheme) -> Unit,
    onStartTrialTheme: (StoreTheme) -> Unit,
    isArabic: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val aura = MaterialTheme.aura
    val haptic = LocalHapticFeedback.current
    val totalKeys = forgeState.bronzeKeys + forgeState.silverKeys + forgeState.goldenKeys + forgeState.diamondKeys

    var shopDestination by remember { mutableStateOf(ShopCategoryDestination.HUB) }
    var selectedCategory by remember { mutableStateOf<StoreThemeCategory?>(null) }
    var inspectedTheme by remember { mutableStateOf<StoreTheme?>(null) }

    BackHandler(enabled = shopDestination != ShopCategoryDestination.HUB) {
        shopDestination = ShopCategoryDestination.HUB
    }

    // Purchased themes automatically move to Multi-Theme Engine and vanish from the Store!
    val unownedThemes = remember(purchasedThemeIds) {
        StoreTheme.storeCatalog.filter { !it.isUnlockedByDefault && !purchasedThemeIds.contains(it.id) }
    }
    val displayedThemes = remember(unownedThemes, selectedCategory) {
        if (selectedCategory == null) {
            unownedThemes
        } else {
            unownedThemes.filter { it.category == selectedCategory }
        }
    }
    val themeRows = remember(displayedThemes) { displayedThemes.chunked(2) }

    val fxTransition = rememberInfiniteTransition(label = "shop_category_live_fx")
    val animPhase by fxTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shop_fx_phase"
    )
    val pulsePhase by fxTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shop_pulse_phase"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("store_themes_shop_screen")
    ) {
        // Top Navigation Bar (adapts dynamically to Hub vs Themes vs Avatar/Seekbare/Mimic)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = aura.surfaceElevated,
            border = BorderStroke(
                1.dp,
                Brush.horizontalGradient(
                    listOf(aura.accentGold.copy(alpha = 0.55f), aura.accentCyan.copy(alpha = 0.55f))
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
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            if (shopDestination == ShopCategoryDestination.HUB) {
                                onBack()
                            } else {
                                shopDestination = ShopCategoryDestination.HUB
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(aura.iconButtonBg)
                            .testTag("shop_exit_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isArabic) "رجوع" else "Back",
                            tint = aura.textPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = when (shopDestination) {
                                ShopCategoryDestination.HUB -> "Shop"
                                ShopCategoryDestination.THEMES -> "Themes"
                                ShopCategoryDestination.AVATAR_SOON -> "Avatar"
                                ShopCategoryDestination.SEEKBARE_SOON -> "Seekbare"
                                ShopCategoryDestination.MIMIC_SOON -> "Mimic"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = aura.accentGold,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = when (shopDestination) {
                                ShopCategoryDestination.HUB -> if (isArabic) "أقسام المتجر والتخصيص" else "Store Categories & Upgrades"
                                ShopCategoryDestination.THEMES -> if (isArabic) "متجر الثيمات والمفاتيح" else "Themes & Keys Store"
                                ShopCategoryDestination.AVATAR_SOON -> if (isArabic) "قريباً في المتجر" else "Coming Soon"
                                ShopCategoryDestination.SEEKBARE_SOON -> if (isArabic) "قريباً في المتجر" else "Coming Soon"
                                ShopCategoryDestination.MIMIC_SOON -> if (isArabic) "قريباً في المتجر" else "Coming Soon"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = aura.textSecondary,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = aura.accentGoldBg,
                    border = BorderStroke(1.dp, aura.accentGold.copy(alpha = 0.45f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (shopDestination) {
                                ShopCategoryDestination.HUB -> Icons.Default.AutoAwesome
                                ShopCategoryDestination.THEMES -> Icons.Default.Palette
                                ShopCategoryDestination.AVATAR_SOON -> Icons.Default.Person
                                ShopCategoryDestination.SEEKBARE_SOON -> Icons.Default.Tune
                                ShopCategoryDestination.MIMIC_SOON -> Icons.Default.AutoFixHigh
                            },
                            contentDescription = null,
                            tint = aura.accentGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (shopDestination) {
                                ShopCategoryDestination.HUB -> if (isArabic) "4 أقسام" else "4 Sections"
                                ShopCategoryDestination.THEMES -> if (isArabic) "${unownedThemes.size} متاح" else "${unownedThemes.size} Left"
                                else -> if (isArabic) "قريباً" else "Soon"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = aura.accentGold,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }
            }
        }

        AnimatedContent(
            targetState = shopDestination,
            transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(160)) },
            label = "shop_section_transition",
            modifier = Modifier.fillMaxSize()
        ) { dest ->
            when (dest) {
                ShopCategoryDestination.HUB -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("shop_categories_hub"),
                        contentPadding = PaddingValues(bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Keys & Shields Balance Card
                        item(key = "hub_keys_balance") {
                            ShopBalanceSummaryCard(
                                totalKeys = totalKeys,
                                shieldsCount = shieldsCount,
                                forgeState = forgeState,
                                isArabic = isArabic
                            )
                        }

                        // Live 1-Minute Trial Countdown Banner if running
                        if (activeThemeTrial != null) {
                            item(key = "hub_trial_banner") {
                                ActiveTrialBannerCard(activeThemeTrial = activeThemeTrial, isArabic = isArabic)
                            }
                        }

                        // Row 1: Themes + Avatar (2 per Row with distinct live visual FX)
                        item(key = "shop_cat_row_1") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ShopCategoryEffectTile(
                                    title = "Themes",
                                    subtitle = if (isArabic) "ثيمات الواجهة والألوان" else "UI Themes & Glass",
                                    badgeText = if (isArabic) "${unownedThemes.size} متاح" else "${unownedThemes.size} Avail",
                                    badgeColor = aura.accentCyan,
                                    accentPrimary = aura.accentCyan,
                                    accentSecondary = aura.accentViolet,
                                    effectType = ShopTileEffectType.THEMES,
                                    animPhase = animPhase,
                                    pulsePhase = pulsePhase,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        shopDestination = ShopCategoryDestination.THEMES
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("shop_category_themes")
                                )

                                ShopCategoryEffectTile(
                                    title = "Avatar",
                                    subtitle = if (isArabic) "أفاتار وإطارات مضيئة" else "Animated Avatars",
                                    badgeText = if (isArabic) "قريباً" else "Soon",
                                    badgeColor = aura.accentViolet,
                                    accentPrimary = aura.accentViolet,
                                    accentSecondary = aura.accentGold,
                                    effectType = ShopTileEffectType.AVATAR,
                                    animPhase = animPhase,
                                    pulsePhase = pulsePhase,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        shopDestination = ShopCategoryDestination.AVATAR_SOON
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("shop_category_avatar")
                                )
                            }
                        }

                        // Row 2: Seekbare + Mimic (2 per Row with distinct live visual FX)
                        item(key = "shop_cat_row_2") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ShopCategoryEffectTile(
                                    title = "Seekbare",
                                    subtitle = if (isArabic) "أشرطة تشغيل وتمرير" else "Reactive Seekbars",
                                    badgeText = if (isArabic) "قريباً" else "Soon",
                                    badgeColor = aura.accentEmerald,
                                    accentPrimary = aura.accentEmerald,
                                    accentSecondary = aura.accentCyan,
                                    effectType = ShopTileEffectType.SEEKBARE,
                                    animPhase = animPhase,
                                    pulsePhase = pulsePhase,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        shopDestination = ShopCategoryDestination.SEEKBARE_SOON
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("shop_category_seekbare")
                                )

                                ShopCategoryEffectTile(
                                    title = "Mimic",
                                    subtitle = if (isArabic) "محاكاة وطيف حركي" else "Kinetic Echo FX",
                                    badgeText = if (isArabic) "قريباً" else "Soon",
                                    badgeColor = aura.accentFlame,
                                    accentPrimary = aura.accentFlame,
                                    accentSecondary = aura.accentGold,
                                    effectType = ShopTileEffectType.MIMIC,
                                    animPhase = animPhase,
                                    pulsePhase = pulsePhase,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        shopDestination = ShopCategoryDestination.MIMIC_SOON
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("shop_category_mimic")
                                )
                            }
                        }
                    }
                }

                ShopCategoryDestination.THEMES -> {
                    // Scrollable Unowned Themes Catalog
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("store_themes_list"),
                        contentPadding = PaddingValues(bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
            // Top Keys & Shields Balance Card
            item(key = "shop_keys_balance") {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = aura.surfaceCard,
                    border = BorderStroke(
                        1.2.dp,
                        Brush.linearGradient(
                            listOf(aura.accentGold.copy(alpha = 0.55f), aura.accentCyan.copy(alpha = 0.45f))
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
                                    tint = aura.accentGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isArabic) "رصيدي من الدروع والمفاتيح" else "My Shields & Keys",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = aura.textPrimary,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp
                                    )
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = aura.accentGoldBg,
                                border = BorderStroke(1.dp, aura.accentGold.copy(alpha = 0.35f))
                            ) {
                                Text(
                                    text = if (isArabic) "المفاتيح: $totalKeys" else "Keys: $totalKeys",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = aura.accentGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            StoreKeyBalanceChip(
                                label = if (isArabic) "دروع" else "Shields",
                                count = shieldsCount,
                                accent = aura.accentEmerald,
                                icon = Icons.Default.Shield,
                                modifier = Modifier.weight(1f)
                            )
                            StoreKeyBalanceChip(
                                label = if (isArabic) "برونزي" else "Bronze",
                                count = forgeState.bronzeKeys,
                                accent = if (aura.isDark) Color(0xFFE59A54) else Color(0xFFB45309),
                                icon = Icons.Default.VpnKey,
                                modifier = Modifier.weight(1f)
                            )
                            StoreKeyBalanceChip(
                                label = if (isArabic) "فضي" else "Silver",
                                count = forgeState.silverKeys,
                                accent = if (aura.isDark) Color(0xFFE2E8F0) else Color(0xFF475569),
                                icon = Icons.Default.VpnKey,
                                modifier = Modifier.weight(1f)
                            )
                            StoreKeyBalanceChip(
                                label = if (isArabic) "ذهبي" else "Gold",
                                count = forgeState.goldenKeys,
                                accent = aura.accentGold,
                                icon = Icons.Default.VpnKey,
                                modifier = Modifier.weight(1f)
                            )
                            StoreKeyBalanceChip(
                                label = if (isArabic) "ماسي" else "Diamond",
                                count = forgeState.diamondKeys,
                                accent = if (aura.isDark) Color(0xFF38BDF8) else Color(0xFF0284C7),
                                icon = Icons.Default.Diamond,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Live 1-Minute Trial Countdown Banner (shown when trial is running)
            if (activeThemeTrial != null) {
                item(key = "active_trial_banner") {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = aura.accentFlameSoft,
                        border = BorderStroke(
                            1.2.dp,
                            Brush.horizontalGradient(listOf(aura.accentFlame, aura.accentGold))
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("active_theme_trial_banner")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = aura.accentFlame,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (isArabic) {
                                            "تجربة مجانية نشطة: ${activeThemeTrial.theme.displayName}"
                                        } else {
                                            "1-Min Trial Active: ${activeThemeTrial.theme.displayName}"
                                        },
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = aura.textPrimary,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 12.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = if (isArabic) {
                                            "يعود الثيم تلقائياً بعد انتهاء الدقيقة"
                                        } else {
                                            "Auto-reverts when timer reaches 00:00"
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = aura.textSecondary,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = aura.surfaceElevated,
                                border = BorderStroke(1.dp, aura.accentFlame.copy(alpha = 0.6f))
                            ) {
                                Text(
                                    text = String.format("00:%02d", activeThemeTrial.remainingSeconds.coerceIn(0, 60)),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = aura.accentFlame,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Currently Applied Theme Banner + Reset
            item(key = "active_theme_banner") {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = aura.surfaceElevated,
                    border = BorderStroke(1.dp, auraCardBorderBrush(aura)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("active_store_theme_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(aura.accentCyanSoft)
                                    .border(1.dp, aura.accentCyan.copy(alpha = 0.55f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = themeIconFor(currentStoreTheme),
                                    contentDescription = null,
                                    tint = aura.accentCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isArabic) "الثيم المطبّق حالياً" else "Currently Active Theme",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = aura.textTertiary,
                                        fontSize = 10.sp
                                    )
                                )
                                Text(
                                    text = currentStoreTheme.displayName,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = aura.textPrimary,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        if (currentStoreTheme != StoreTheme.DEFAULT) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = aura.surfaceSubtle,
                                border = BorderStroke(1.dp, aura.borderSubtle),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onEquipOwnedTheme(StoreTheme.DEFAULT)
                                    }
                                    .testTag("store_reset_default_theme")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = aura.textSecondary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isArabic) "الافتراضي" else "Reset",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = aura.textPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = aura.accentEmeraldSoft,
                                border = BorderStroke(1.dp, aura.accentEmerald.copy(alpha = 0.45f))
                            ) {
                                Text(
                                    text = if (isArabic) "الأساسي" else "Default",
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = aura.accentEmerald,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Category Filter Chips (counting only remaining unowned Store themes)
            item(key = "theme_category_filters") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StoreCategoryChip(
                        label = if (isArabic) "الكل (${unownedThemes.size})" else "All (${unownedThemes.size})",
                        isSelected = selectedCategory == null,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedCategory = null
                        }
                    )
                    StoreThemeCategory.entries.forEach { category ->
                        val count = unownedThemes.count { it.category == category }
                        StoreCategoryChip(
                            label = if (isArabic) "${category.labelAr} ($count)" else "${category.labelEn} ($count)",
                            isSelected = selectedCategory == category,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                selectedCategory = category
                            }
                        )
                    }
                }
            }

            if (displayedThemes.isEmpty()) {
                item(key = "all_themes_owned_state") {
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = aura.surfaceCard,
                        border = BorderStroke(1.2.dp, aura.accentEmerald.copy(alpha = 0.55f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = aura.accentEmerald,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = if (isArabic) "تمتلك جميع ثيمات هذا القسم!" else "All themes in this section owned!",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = aura.textPrimary,
                                    fontWeight = FontWeight.Black
                                ),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = if (isArabic) {
                                    "الثيمات المشتراة نُقلت تلقائياً إلى الإعدادات ← Multi-Theme Engine لتفعيلها في أي وقت."
                                } else {
                                    "Purchased themes moved automatically to Settings → Multi-Theme Engine."
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = aura.textSecondary,
                                    lineHeight = 18.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                // Catalog Theme Showcase Grid (2 Themes per Row)
                items(
                    items = themeRows,
                    key = { row -> row.joinToString("_") { it.id } }
                ) { rowThemes ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowThemes.forEach { theme ->
                            val isUnlocked = theme.isUnlockedByDefault || purchasedThemeIds.contains(theme.id)
                            val isApplied = currentStoreTheme == theme
                            val isOnTrial = activeThemeTrial?.theme == theme
                            val trialSec = if (isOnTrial) activeThemeTrial?.remainingSeconds ?: 0 else 0

                            StoreThemeShowcaseCard(
                                theme = theme,
                                isUnlocked = isUnlocked,
                                isApplied = isApplied,
                                isOnTrial = isOnTrial,
                                trialRemainingSeconds = trialSec,
                                parentIsDark = aura.isDark,
                                isArabic = isArabic,
                                onInspect = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    inspectedTheme = theme
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowThemes.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
                    }
                }

                ShopCategoryDestination.AVATAR_SOON -> {
                    ShopFeatureSoonPage(
                        title = "Avatar",
                        subtitleAr = "أفاتار متحركة وإطارات هولوغرافية للملف الشخصي تُصاغ قريباً بأسلوب Video Pulse",
                        subtitleEn = "Animated profile avatars and holographic aura frames coming soon",
                        effectType = ShopTileEffectType.AVATAR,
                        accentPrimary = aura.accentViolet,
                        accentSecondary = aura.accentGold,
                        animPhase = animPhase,
                        pulsePhase = pulsePhase,
                        isArabic = isArabic
                    )
                }

                ShopCategoryDestination.SEEKBARE_SOON -> {
                    ShopFeatureSoonPage(
                        title = "Seekbare",
                        subtitleAr = "أشرطة تمرير تفاعلية بأمواج نيون ومؤشرات تشغيل مخصصة قريباً للمشغّل",
                        subtitleEn = "Custom glowing audio/video waveforms and reactive seekbar skins coming soon",
                        effectType = ShopTileEffectType.SEEKBARE,
                        accentPrimary = aura.accentEmerald,
                        accentSecondary = aura.accentCyan,
                        animPhase = animPhase,
                        pulsePhase = pulsePhase,
                        isArabic = isArabic
                    )
                }

                ShopCategoryDestination.MIMIC_SOON -> {
                    ShopFeatureSoonPage(
                        title = "Mimic",
                        subtitleAr = "تأثيرات صدى حركية وتفاعلات بصرية ذكية للبطاقات والشاشات قريباً",
                        subtitleEn = "Kinetic echo trails and reactive motion mimicry effects coming soon",
                        effectType = ShopTileEffectType.MIMIC,
                        accentPrimary = aura.accentFlame,
                        accentSecondary = aura.accentGold,
                        animPhase = animPhase,
                        pulsePhase = pulsePhase,
                        isArabic = isArabic
                    )
                }
            }
        }
    }

    // Detailed Popup Page on Theme Tap (Full Description, Live Preview, Colors, Buy & 1-Min Trial)
    val activeInspection = inspectedTheme
    if (activeInspection != null) {
        val previewPalette = remember(activeInspection, aura.isDark) {
            val useDark = when (activeInspection) {
                StoreTheme.NEUMORPHISM -> false
                StoreTheme.LIQUID_FROSTED_GLASS, StoreTheme.GLASS_MORPHISM_PSD_MOCKUP -> aura.isDark
                else -> true
            }
            auraPaletteFor(activeInspection, useDark)
        }
        val isUnlocked = activeInspection.isUnlockedByDefault || purchasedThemeIds.contains(activeInspection.id)
        val isAlreadyApplied = currentStoreTheme == activeInspection
        val isOnTrial = activeThemeTrial?.theme == activeInspection
        val availableTierKeys = activeInspection.costTier?.let { forgeState.getKeyCount(it) } ?: Int.MAX_VALUE
        val canAffordPurchase = isUnlocked || availableTierKeys >= activeInspection.costAmount
        val tierAccent = keyTierColor(activeInspection.costTier, aura.isDark)

        Dialog(
            onDismissRequest = { inspectedTheme = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = aura.dialogBg,
                border = BorderStroke(
                    1.4.dp,
                    Brush.linearGradient(listOf(previewPalette.accentCyan, previewPalette.accentViolet))
                ),
                shadowElevation = 18.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .testTag("store_theme_preview_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Popup Header: Icon, Name, Badges & Close Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(13.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                previewPalette.accentCyan.copy(alpha = 0.24f),
                                                previewPalette.accentViolet.copy(alpha = 0.24f)
                                            )
                                        )
                                    )
                                    .border(
                                        1.1.dp,
                                        Brush.linearGradient(listOf(previewPalette.accentCyan, previewPalette.accentViolet)),
                                        RoundedCornerShape(13.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = themeIconFor(activeInspection),
                                    contentDescription = null,
                                    tint = previewPalette.accentCyan,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = activeInspection.displayName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = aura.textPrimary,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(7.dp),
                                        color = previewPalette.accentViolet.copy(alpha = 0.16f),
                                        border = BorderStroke(0.8.dp, previewPalette.accentViolet.copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            text = activeInspection.badgeLabel,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (aura.isDark) previewPalette.accentViolet else aura.textPrimary,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 9.5.sp
                                            )
                                        )
                                    }
                                    PricePillBadge(theme = activeInspection, isUnlocked = isUnlocked, isArabic = isArabic)
                                }
                            }
                        }

                        IconButton(
                            onClick = { inspectedTheme = null },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(aura.iconButtonBg)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = if (isArabic) "إغلاق" else "Close",
                                tint = aura.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Full Interactive Theme Live Preview
                    ThemeLiveMiniPreview(
                        theme = activeInspection,
                        previewAura = previewPalette,
                        isArabic = isArabic,
                        isCompact = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(136.dp)
                    )

                    // Theme Description / Explanation Card
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = aura.surfaceSubtle,
                        border = BorderStroke(0.9.dp, aura.borderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (isArabic) "وصف الثيم والتفاصيل" else "Theme Details",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = previewPalette.accentCyan,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.5.sp
                                )
                            )
                            Text(
                                text = if (isArabic) activeInspection.descriptionAr else activeInspection.descriptionEn,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = aura.textPrimary,
                                    lineHeight = 19.sp,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    // 5-Color Theme Palette Swatches
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            previewPalette.background,
                            previewPalette.surfaceElevated,
                            previewPalette.accentCyan,
                            previewPalette.accentViolet,
                            previewPalette.accentGold
                        ).forEach { swatch ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(22.dp)
                                    .clip(RoundedCornerShape(7.dp))
                                    .background(swatch)
                                    .border(0.8.dp, aura.borderSubtle, RoundedCornerShape(7.dp))
                            )
                        }
                    }

                    // Key Cost vs Owned Balance Summary when Locked
                    if (!isUnlocked && activeInspection.costTier != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = tierAccent.copy(alpha = 0.10f),
                            border = BorderStroke(0.9.dp, tierAccent.copy(alpha = 0.45f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isArabic) {
                                        "السعر: ${formatThemeCostText(activeInspection, true)}"
                                    } else {
                                        "Price: ${formatThemeCostText(activeInspection, false)}"
                                    },
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = tierAccent,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                                Text(
                                    text = if (isArabic) {
                                        "رصيدك: $availableTierKeys"
                                    } else {
                                        "Your Balance: $availableTierKeys"
                                    },
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (canAffordPurchase) aura.accentEmerald else aura.accentFlame,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    // Popup Action Buttons: Purchase / Apply + 1-Minute Shield Trial
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                if (isUnlocked) {
                                    onEquipOwnedTheme(activeInspection)
                                } else {
                                    onPurchaseTheme(activeInspection)
                                }
                                inspectedTheme = null
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when {
                                    isAlreadyApplied && isUnlocked -> aura.accentEmerald
                                    isUnlocked -> previewPalette.accentCyan
                                    canAffordPurchase -> aura.accentGold
                                    else -> aura.surfaceSubtle
                                },
                                contentColor = when {
                                    isAlreadyApplied && isUnlocked -> Color.White
                                    isUnlocked || canAffordPurchase -> Color(0xFF07090D)
                                    else -> aura.textSecondary
                                }
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("dialog_apply_theme_button")
                        ) {
                            Icon(
                                imageVector = when {
                                    isUnlocked && isAlreadyApplied -> Icons.Default.CheckCircle
                                    isUnlocked -> Icons.Default.Palette
                                    canAffordPurchase -> Icons.Default.VpnKey
                                    else -> Icons.Default.Lock
                                },
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when {
                                    isUnlocked && isAlreadyApplied -> if (isArabic) "مُطبّق حالياً ✓" else "Applied ✓"
                                    isUnlocked -> if (isArabic) "تطبيق على التطبيق" else "Apply Theme"
                                    else -> {
                                        val costLabel = formatThemeCostText(activeInspection, isArabic)
                                        if (isArabic) "شراء وتفعيل ($costLabel)" else "Buy & Apply ($costLabel)"
                                    }
                                },
                                fontWeight = FontWeight.Black,
                                fontSize = 13.5.sp
                            )
                        }

                        if (!isUnlocked) {
                            OutlinedButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onStartTrialTheme(activeInspection)
                                    inspectedTheme = null
                                },
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(
                                    1.1.dp,
                                    if (isOnTrial) aura.accentFlame else aura.accentEmerald.copy(alpha = 0.7f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("dialog_trial_theme_button")
                            ) {
                                Icon(
                                    imageVector = if (isOnTrial) Icons.Default.AccessTime else Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = if (isOnTrial) aura.accentFlame else aura.accentEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isOnTrial) {
                                        val sec = String.format("%02d", activeThemeTrial?.remainingSeconds ?: 0)
                                        if (isArabic) "تجربة جارية (00:$sec)" else "Trial Running (00:$sec)"
                                    } else {
                                        if (isArabic) "تجربة 1 دقيقة بدرع واحد (🛡️ 1)" else "Try 1 Minute (1 Shield 🛡️)"
                                    },
                                    color = if (isOnTrial) aura.accentFlame else aura.accentEmerald,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.5.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreThemeShowcaseCard(
    theme: StoreTheme,
    isUnlocked: Boolean,
    isApplied: Boolean,
    isOnTrial: Boolean,
    trialRemainingSeconds: Int,
    parentIsDark: Boolean,
    isArabic: Boolean,
    onInspect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val aura = MaterialTheme.aura
    val previewAura = remember(theme, parentIsDark) {
        val useDark = when (theme) {
            StoreTheme.NEUMORPHISM -> false
            StoreTheme.LIQUID_FROSTED_GLASS, StoreTheme.GLASS_MORPHISM_PSD_MOCKUP -> parentIsDark
            else -> true
        }
        auraPaletteFor(theme, useDark)
    }

    val cardBorderColor by animateColorAsState(
        targetValue = when {
            isOnTrial -> aura.accentFlame
            isApplied -> previewAura.accentCyan
            else -> aura.borderSubtle
        },
        animationSpec = tween(250),
        label = "theme_card_border"
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = aura.surfaceCard,
        border = BorderStroke(
            width = if (isApplied || isOnTrial) 1.8.dp else 1.dp,
            brush = if (isApplied || isOnTrial) {
                Brush.linearGradient(
                    if (isOnTrial) listOf(aura.accentFlame, aura.accentGold)
                    else listOf(previewAura.accentCyan, previewAura.accentViolet)
                )
            } else {
                Brush.linearGradient(listOf(cardBorderColor, aura.borderMuted))
            }
        ),
        shadowElevation = if (isApplied) 8.dp else 2.dp,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onInspect() }
            .testTag("store_theme_card_${theme.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Compact Theme Preview with Active / Trial indicator
            Box(modifier = Modifier.fillMaxWidth()) {
                ThemeLiveMiniPreview(
                    theme = theme,
                    previewAura = previewAura,
                    isArabic = isArabic,
                    isCompact = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(78.dp)
                )

                if (isApplied || isOnTrial) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isOnTrial) aura.accentFlame else aura.accentEmerald,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                    ) {
                        Text(
                            text = if (isOnTrial) {
                                String.format("00:%02d", trialRemainingSeconds.coerceIn(0, 60))
                            } else {
                                if (isArabic) "مُطبّق" else "Active"
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF07090D),
                                fontWeight = FontWeight.Black,
                                fontSize = 8.5.sp
                            )
                        )
                    }
                }
            }

            // Clean Theme Name Only (No Description Clutter on Storefront)
            Text(
                text = theme.displayName,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = aura.textPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.5.sp,
                    lineHeight = 16.sp
                ),
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Price / Ownership Pill & Details Hint
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PricePillBadge(theme = theme, isUnlocked = isUnlocked, isArabic = isArabic)

                if (!isUnlocked) {
                    Text(
                        text = "1 🛡️",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = aura.accentEmerald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun PricePillBadge(
    theme: StoreTheme,
    isUnlocked: Boolean,
    isArabic: Boolean
) {
    val aura = MaterialTheme.aura
    val tierColor = keyTierColor(theme.costTier, aura.isDark)

    when {
        theme.isUnlockedByDefault -> {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = aura.accentEmeraldSoft,
                border = BorderStroke(0.9.dp, aura.accentEmerald.copy(alpha = 0.6f))
            ) {
                Text(
                    text = if (isArabic) "مجاني • افتراضي" else "FREE • DEFAULT",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = aura.accentEmerald,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 9.5.sp
                    )
                )
            }
        }
        isUnlocked -> {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = aura.accentEmeraldSoft,
                border = BorderStroke(0.9.dp, aura.accentEmerald.copy(alpha = 0.6f))
            ) {
                Text(
                    text = if (isArabic) "مملوك ✓" else "OWNED ✓",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = aura.accentEmerald,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 9.5.sp
                    )
                )
            }
        }
        else -> {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = tierColor.copy(alpha = 0.16f),
                border = BorderStroke(0.9.dp, tierColor.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (theme.costTier == ForgeKeyTier.DIAMOND) Icons.Default.Diamond else Icons.Default.VpnKey,
                        contentDescription = null,
                        tint = tierColor,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatThemeCostText(theme, isArabic),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = tierColor,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 9.5.sp
                        )
                    )
                }
            }
        }
    }
}

private fun formatThemeCostText(theme: StoreTheme, isArabic: Boolean): String {
    val tier = theme.costTier ?: return if (isArabic) "مجاني" else "Free"
    val tierLabel = when (tier) {
        ForgeKeyTier.BRONZE -> if (isArabic) "برونزي" else "Bronze"
        ForgeKeyTier.SILVER -> if (isArabic) "فضي" else "Silver"
        ForgeKeyTier.GOLD -> if (isArabic) "ذهبي" else "Gold"
        ForgeKeyTier.DIAMOND -> if (isArabic) "ماسي" else "Diamond"
    }
    return "${theme.costAmount} $tierLabel"
}

private fun keyTierColor(tier: ForgeKeyTier?, isDark: Boolean): Color = when (tier) {
    ForgeKeyTier.BRONZE -> if (isDark) Color(0xFFE59A54) else Color(0xFFB45309)
    ForgeKeyTier.SILVER -> if (isDark) Color(0xFFE2E8F0) else Color(0xFF475569)
    ForgeKeyTier.GOLD -> if (isDark) Color(0xFFFFD700) else Color(0xFFA16207)
    ForgeKeyTier.DIAMOND -> if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7)
    null -> if (isDark) Color(0xFF34D399) else Color(0xFF059669)
}

@Composable
internal fun ThemeLiveMiniPreview(
    theme: StoreTheme,
    previewAura: AuraPalette,
    isArabic: Boolean,
    isCompact: Boolean = false,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(if (isCompact) 14.dp else 16.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(previewAura.background)
            .border(
                width = if (previewAura.isGlassStyle) 1.3.dp else 1.dp,
                brush = auraCardBorderBrush(previewAura),
                shape = shape
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            if (previewAura.isNeumorphic) {
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(previewAura.ambientPrimary.copy(alpha = 0.55f), Color.Transparent),
                        radius = w * 0.65f
                    ),
                    center = Offset(w * 0.15f, h * 0.15f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(previewAura.ambientSecondary.copy(alpha = 0.55f), Color.Transparent),
                        radius = w * 0.65f
                    ),
                    center = Offset(w * 0.85f, h * 0.88f)
                )
            } else {
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(previewAura.ambientPrimary.copy(alpha = 0.42f), Color.Transparent),
                        radius = w * 0.5f
                    ),
                    center = Offset(w * 0.18f, h * 0.25f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(previewAura.ambientSecondary.copy(alpha = 0.36f), Color.Transparent),
                        radius = w * 0.48f
                    ),
                    center = Offset(w * 0.82f, h * 0.72f)
                )
            }

            if (theme == StoreTheme.SYNTHWAVE) {
                val horizonY = h * 0.62f
                drawLine(
                    color = previewAura.accentViolet.copy(alpha = 0.65f),
                    start = Offset(0f, horizonY),
                    end = Offset(w, horizonY),
                    strokeWidth = 1.2.dp.toPx()
                )
                for (i in -3..3) {
                    drawLine(
                        color = previewAura.accentCyan.copy(alpha = 0.32f),
                        start = Offset(w * 0.5f, horizonY),
                        end = Offset(w * 0.5f + i * (w * 0.24f), h),
                        strokeWidth = 0.8.dp.toPx()
                    )
                }
            } else if (theme == StoreTheme.GLASS_MORPHISM_PSD_MOCKUP) {
                val step = 18.dp.toPx()
                var x = step
                while (x < w) {
                    drawLine(previewAura.textTertiary.copy(alpha = 0.18f), Offset(x, 0f), Offset(x, h), 0.5.dp.toPx())
                    x += step
                }
                var y = step
                while (y < h) {
                    drawLine(previewAura.textTertiary.copy(alpha = 0.18f), Offset(0f, y), Offset(w, y), 0.5.dp.toPx())
                    y += step
                }
            }
        }

        if (isCompact) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = themeIconFor(theme),
                        contentDescription = null,
                        tint = previewAura.accentCyan,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = theme.badgeLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = previewAura.accentCyan,
                            fontWeight = FontWeight.Black,
                            fontSize = 8.sp,
                            letterSpacing = 0.5.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    shape = RoundedCornerShape(9.dp),
                    color = previewAura.surfaceCard,
                    border = BorderStroke(0.9.dp, auraCardBorderBrush(previewAura)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 7.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(CircleShape)
                                .background(previewAura.progressTrack)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.68f)
                                    .height(4.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(previewAura.accentCyan, previewAura.accentViolet)
                                        )
                                    )
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(previewAura.primaryCtaBg)
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(previewAura.accentCyan)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "VIDEO PULSE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = previewAura.textPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 9.5.sp,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = previewAura.accentCyanSoft,
                        border = BorderStroke(0.6.dp, previewAura.accentCyan.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = theme.badgeLabel,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = previewAura.accentCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(11.dp),
                    color = previewAura.surfaceCard,
                    border = BorderStroke(1.dp, auraCardBorderBrush(previewAura)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isArabic) "معاينة الواجهة والبطاقات" else "Live UI & Card Contrast",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = previewAura.textPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .height(4.dp)
                                    .clip(CircleShape)
                                    .background(previewAura.progressTrack)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.68f)
                                        .height(4.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(previewAura.accentCyan, previewAura.accentViolet)
                                            )
                                        )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            shape = RoundedCornerShape(7.dp),
                            color = previewAura.primaryCtaBg
                        ) {
                            Text(
                                text = if (isArabic) "تشغيل" else "Play",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = previewAura.primaryCtaContent,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreCategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val aura = MaterialTheme.aura
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) aura.accentCyanSoft else aura.unselectedChipBg,
        border = BorderStroke(
            width = if (isSelected) 1.2.dp else 0.9.dp,
            color = if (isSelected) aura.accentCyan else aura.borderSubtle
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium.copy(
                color = if (isSelected) aura.accentCyan else aura.textSecondary,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                fontSize = 11.5.sp
            )
        )
    }
}

@Composable
private fun StoreKeyBalanceChip(
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
        color = aura.surfaceElevated,
        border = BorderStroke(0.9.dp, accent.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 3.dp, vertical = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = accent,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "$count",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = aura.textPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.5.sp
                ),
                maxLines = 1
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = accent.copy(alpha = 0.9f),
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

internal fun themeIconFor(theme: StoreTheme): ImageVector = when (theme) {
    StoreTheme.DEFAULT -> Icons.Default.AutoAwesome
    StoreTheme.LIQUID_FROSTED_GLASS -> Icons.Default.WaterDrop
    StoreTheme.TOKYO_NIGHT -> Icons.Default.ColorLens
    StoreTheme.CATPPUCCIN_MOCHA -> Icons.Default.Palette
    StoreTheme.NORD_ARCTIC -> Icons.Default.BlurOn
    StoreTheme.FROSTED_GLASSMORPHISM -> Icons.Default.BlurOn
    StoreTheme.NEUMORPHISM -> Icons.Default.TouchApp
    StoreTheme.SYNTHWAVE -> Icons.Default.AutoAwesome
    StoreTheme.RETRO_GLOW -> Icons.Default.WbTwilight
    StoreTheme.DRACULA_PRO -> Icons.Default.Diamond
    StoreTheme.HOLOGRAPHIC_LIQUID_GLASS -> Icons.Default.AutoAwesome
    StoreTheme.GLASS_MORPHISM_PSD_MOCKUP -> Icons.Default.Layers
}

private enum class ShopTileEffectType {
    THEMES,
    AVATAR,
    SEEKBARE,
    MIMIC
}

@Composable
private fun ShopBalanceSummaryCard(
    totalKeys: Int,
    shieldsCount: Int,
    forgeState: ForgeUiState,
    isArabic: Boolean
) {
    val aura = MaterialTheme.aura
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = aura.surfaceCard,
        border = BorderStroke(
            1.2.dp,
            Brush.linearGradient(
                listOf(aura.accentGold.copy(alpha = 0.55f), aura.accentCyan.copy(alpha = 0.45f))
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
                        tint = aura.accentGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "رصيدي من الدروع والمفاتيح" else "My Shields & Keys",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = aura.textPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = aura.accentGoldBg,
                    border = BorderStroke(1.dp, aura.accentGold.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = if (isArabic) "المفاتيح: $totalKeys" else "Keys: $totalKeys",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = aura.accentGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StoreKeyBalanceChip(
                    label = if (isArabic) "دروع" else "Shields",
                    count = shieldsCount,
                    accent = aura.accentEmerald,
                    icon = Icons.Default.Shield,
                    modifier = Modifier.weight(1f)
                )
                StoreKeyBalanceChip(
                    label = if (isArabic) "برونزي" else "Bronze",
                    count = forgeState.bronzeKeys,
                    accent = if (aura.isDark) Color(0xFFE59A54) else Color(0xFFB45309),
                    icon = Icons.Default.VpnKey,
                    modifier = Modifier.weight(1f)
                )
                StoreKeyBalanceChip(
                    label = if (isArabic) "فضي" else "Silver",
                    count = forgeState.silverKeys,
                    accent = if (aura.isDark) Color(0xFFE2E8F0) else Color(0xFF475569),
                    icon = Icons.Default.VpnKey,
                    modifier = Modifier.weight(1f)
                )
                StoreKeyBalanceChip(
                    label = if (isArabic) "ذهبي" else "Gold",
                    count = forgeState.goldenKeys,
                    accent = aura.accentGold,
                    icon = Icons.Default.VpnKey,
                    modifier = Modifier.weight(1f)
                )
                StoreKeyBalanceChip(
                    label = if (isArabic) "ماسي" else "Diamond",
                    count = forgeState.diamondKeys,
                    accent = if (aura.isDark) Color(0xFF38BDF8) else Color(0xFF0284C7),
                    icon = Icons.Default.Diamond,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ActiveTrialBannerCard(
    activeThemeTrial: ThemeTrialState,
    isArabic: Boolean
) {
    val aura = MaterialTheme.aura
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = aura.accentFlameSoft,
        border = BorderStroke(
            1.2.dp,
            Brush.horizontalGradient(listOf(aura.accentFlame, aura.accentGold))
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_theme_trial_banner")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = aura.accentFlame,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isArabic) {
                            "تجربة مجانية نشطة: ${activeThemeTrial.theme.displayName}"
                        } else {
                            "1-Min Trial Active: ${activeThemeTrial.theme.displayName}"
                        },
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = aura.textPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (isArabic) {
                            "يعود الثيم تلقائياً بعد انتهاء الدقيقة"
                        } else {
                            "Auto-reverts when timer reaches 00:00"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = aura.textSecondary,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = aura.surfaceElevated,
                border = BorderStroke(1.dp, aura.accentFlame.copy(alpha = 0.6f))
            ) {
                Text(
                    text = String.format("00:%02d", activeThemeTrial.remainingSeconds.coerceIn(0, 60)),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = aura.accentFlame,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun ShopCategoryEffectTile(
    title: String,
    subtitle: String,
    badgeText: String,
    badgeColor: Color,
    accentPrimary: Color,
    accentSecondary: Color,
    effectType: ShopTileEffectType,
    animPhase: Float,
    pulsePhase: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val aura = MaterialTheme.aura
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = aura.surfaceCard,
        border = BorderStroke(
            1.3.dp,
            Brush.linearGradient(
                listOf(accentPrimary.copy(alpha = 0.72f), accentSecondary.copy(alpha = 0.55f))
            )
        ),
        shadowElevation = 6.dp,
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(11.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            // Realistic Category-Specific Animated Effect Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(106.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (aura.isDark) Color(0xFF0A101E) else aura.surfaceSubtle)
                    .border(0.9.dp, accentPrimary.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            ) {
                ShopCategoryEffectCanvas(
                    effectType = effectType,
                    accentPrimary = accentPrimary,
                    accentSecondary = accentSecondary,
                    accentGold = aura.accentGold,
                    animPhase = animPhase,
                    pulsePhase = pulsePhase,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor.copy(alpha = 0.22f),
                    border = BorderStroke(0.8.dp, badgeColor.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(7.dp)
                ) {
                    Text(
                        text = badgeText,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = badgeColor,
                            fontWeight = FontWeight.Black,
                            fontSize = 8.5.sp
                        )
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = aura.textPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = aura.textSecondary,
                        fontSize = 10.5.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ShopCategoryEffectCanvas(
    effectType: ShopTileEffectType,
    accentPrimary: Color,
    accentSecondary: Color,
    accentGold: Color,
    animPhase: Float,
    pulsePhase: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val rad = animPhase * 2f * PI.toFloat()

        when (effectType) {
            ShopTileEffectType.THEMES -> {
                // Chromatic drifting aura orbs + layered frosted-glass theme cards
                val orbX1 = w * (0.28f + 0.14f * cos(rad))
                val orbY1 = h * (0.34f + 0.12f * sin(rad))
                val orbX2 = w * (0.72f - 0.14f * cos(rad))
                val orbY2 = h * (0.68f - 0.12f * sin(rad))

                drawCircle(
                    brush = Brush.radialGradient(listOf(accentPrimary.copy(alpha = 0.52f), Color.Transparent), radius = w * 0.45f),
                    center = Offset(orbX1, orbY1)
                )
                drawCircle(
                    brush = Brush.radialGradient(listOf(accentSecondary.copy(alpha = 0.48f), Color.Transparent), radius = w * 0.42f),
                    center = Offset(orbX2, orbY2)
                )
                // Layered glass theme swatch cards
                val cardW = w * 0.48f
                val cardH = h * 0.48f
                val shift = 4.dp.toPx() * pulsePhase
                drawRoundRect(
                    color = accentSecondary.copy(alpha = 0.25f),
                    topLeft = Offset(cx - cardW * 0.65f - shift, cy - cardH * 0.58f),
                    size = Size(cardW, cardH),
                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                )
                drawRoundRect(
                    color = accentPrimary.copy(alpha = 0.22f),
                    topLeft = Offset(cx - cardW * 0.28f + shift, cy - cardH * 0.34f),
                    size = Size(cardW, cardH),
                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                )
                drawRoundRect(
                    brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.75f), accentPrimary)),
                    topLeft = Offset(cx - cardW * 0.28f + shift, cy - cardH * 0.34f),
                    size = Size(cardW, cardH),
                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
                    style = Stroke(width = 1.4.dp.toPx())
                )
                // Mini spectrum dots
                val dotColors = listOf(accentPrimary, accentSecondary, accentGold, Color(0xFF34D399))
                dotColors.forEachIndexed { idx, col ->
                    drawCircle(
                        color = col,
                        radius = 4.2.dp.toPx(),
                        center = Offset(
                            x = (cx - cardW * 0.14f + shift) + idx * 12.dp.toPx(),
                            y = cy + cardH * 0.02f
                        )
                    )
                }
            }

            ShopTileEffectType.AVATAR -> {
                // Rotating cosmic avatar frame halo + biometric silhouette + orbiting stars
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(accentPrimary.copy(alpha = 0.34f + 0.16f * pulsePhase), Color.Transparent),
                        radius = h * 0.56f
                    ),
                    center = Offset(cx, cy)
                )
                val ringRadius = minOf(w, h) * 0.33f
                rotate(degrees = animPhase * 360f, pivot = Offset(cx, cy)) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(accentPrimary, accentGold, Color.Transparent, accentSecondary, accentPrimary),
                            center = Offset(cx, cy)
                        ),
                        radius = ringRadius,
                        center = Offset(cx, cy),
                        style = Stroke(
                            width = 2.6.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 10f), 0f)
                        )
                    )
                }
                // Inner glowing portrait badge
                drawCircle(
                    color = accentPrimary.copy(alpha = 0.24f),
                    radius = ringRadius * 0.76f,
                    center = Offset(cx, cy)
                )
                // Head circle & shoulders arc
                drawCircle(
                    color = Color.White.copy(alpha = 0.92f),
                    radius = ringRadius * 0.24f,
                    center = Offset(cx, cy - ringRadius * 0.16f)
                )
                drawArc(
                    color = accentGold.copy(alpha = 0.95f),
                    startAngle = 200f,
                    sweepAngle = 140f,
                    useCenter = true,
                    topLeft = Offset(cx - ringRadius * 0.44f, cy + ringRadius * 0.10f),
                    size = Size(ringRadius * 0.88f, ringRadius * 0.75f)
                )
                // Orbiting sparkle nodes
                for (i in 0 until 3) {
                    val angle = rad + i * (2f * PI.toFloat() / 3f)
                    val px = cx + cos(angle) * (ringRadius + 7.dp.toPx())
                    val py = cy + sin(angle) * (ringRadius + 7.dp.toPx())
                    drawCircle(color = accentGold, radius = 2.6.dp.toPx(), center = Offset(px, py))
                }
            }

            ShopTileEffectType.SEEKBARE -> {
                // Pulsing audio/video spectrum bars + glowing animated neon seekbar & thumb
                val padX = w * 0.12f
                val trackW = w - 2f * padX
                val trackY = h * 0.70f
                val progress = 0.20f + 0.65f * pulsePhase
                val thumbX = padX + trackW * progress

                val barCount = 12
                val barSpacing = trackW / barCount
                for (i in 0 until barCount) {
                    val barFrac = (i + 0.5f) / barCount
                    val wave = 0.35f + 0.65f * ((sin(rad * 1.5f + i * 0.7f) + 1f) / 2f)
                    val barH = (h * 0.34f) * wave
                    val barActive = barFrac <= progress
                    drawRoundRect(
                        color = if (barActive) accentPrimary.copy(alpha = 0.9f) else accentSecondary.copy(alpha = 0.26f),
                        topLeft = Offset(padX + i * barSpacing + 2.dp.toPx(), trackY - 12.dp.toPx() - barH),
                        size = Size((barSpacing - 4.dp.toPx()).coerceAtLeast(2.dp.toPx()), barH),
                        cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                    )
                }

                // Track base
                drawLine(
                    color = Color.White.copy(alpha = 0.18f),
                    start = Offset(padX, trackY),
                    end = Offset(padX + trackW, trackY),
                    strokeWidth = 5.dp.toPx(),
                    cap = StrokeCap.Round
                )
                // Active glowing progress track
                drawLine(
                    brush = Brush.horizontalGradient(listOf(accentSecondary, accentPrimary)),
                    start = Offset(padX, trackY),
                    end = Offset(thumbX, trackY),
                    strokeWidth = 5.dp.toPx(),
                    cap = StrokeCap.Round
                )
                // Comet thumb ripple + bright core
                drawCircle(
                    color = accentPrimary.copy(alpha = 0.30f + 0.25f * pulsePhase),
                    radius = 12.dp.toPx(),
                    center = Offset(thumbX, trackY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 6.dp.toPx(),
                    center = Offset(thumbX, trackY)
                )
            }

            ShopTileEffectType.MIMIC -> {
                // Synchronized holographic kinetic mirror echoes radiating from core diamond
                for (echo in 2 downTo 0) {
                    val echoShift = echo * 15.dp.toPx() * (0.4f + 0.6f * pulsePhase)
                    val alpha = when (echo) {
                        2 -> 0.22f
                        1 -> 0.45f
                        else -> 0.92f
                    }
                    val color = if (echo % 2 == 0) accentPrimary else accentSecondary
                    val half = 16.dp.toPx()

                    // Left & Right mirrored kinetic echo brackets
                    drawRoundRect(
                        color = color.copy(alpha = alpha * 0.45f),
                        topLeft = Offset(cx - half - echoShift, cy - half),
                        size = Size(half * 2f, half * 2f),
                        cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
                        style = Stroke(width = 1.8.dp.toPx())
                    )
                    if (echo > 0) {
                        drawRoundRect(
                            color = accentGold.copy(alpha = alpha * 0.5f),
                            topLeft = Offset(cx - half + echoShift, cy - half),
                            size = Size(half * 2f, half * 2f),
                            cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
                            style = Stroke(width = 1.6.dp.toPx())
                        )
                    }
                }
                // Central radiant kinetic pulse
                drawCircle(
                    brush = Brush.radialGradient(listOf(Color.White, accentPrimary, Color.Transparent), radius = 16.dp.toPx()),
                    center = Offset(cx, cy),
                    radius = 12.dp.toPx()
                )
            }
        }
    }
}

@Composable
private fun ShopFeatureSoonPage(
    title: String,
    subtitleAr: String,
    subtitleEn: String,
    effectType: ShopTileEffectType,
    accentPrimary: Color,
    accentSecondary: Color,
    animPhase: Float,
    pulsePhase: Float,
    isArabic: Boolean
) {
    val aura = MaterialTheme.aura
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = aura.surfaceCard,
            border = BorderStroke(1.3.dp, Brush.linearGradient(listOf(accentPrimary, accentSecondary))),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (aura.isDark) Color(0xFF0A101E) else aura.surfaceSubtle)
                        .border(1.dp, accentPrimary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                ) {
                    ShopCategoryEffectCanvas(
                        effectType = effectType,
                        accentPrimary = accentPrimary,
                        accentSecondary = accentSecondary,
                        accentGold = aura.accentGold,
                        animPhase = animPhase,
                        pulsePhase = pulsePhase,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = aura.textPrimary,
                        fontWeight = FontWeight.Black
                    )
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = accentPrimary.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, accentPrimary.copy(alpha = 0.55f))
                ) {
                    Text(
                        text = if (isArabic) "قريباً" else "Soon",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 7.dp),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = accentPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }

                Text(
                    text = if (isArabic) subtitleAr else subtitleEn,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = aura.textSecondary,
                        lineHeight = 19.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
