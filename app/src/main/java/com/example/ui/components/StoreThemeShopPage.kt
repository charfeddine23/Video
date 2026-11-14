package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ForgeUiState
import com.example.ui.theme.AuraPalette
import com.example.ui.theme.StoreTheme
import com.example.ui.theme.StoreThemeCategory
import com.example.ui.theme.aura
import com.example.ui.theme.auraCardBorderBrush
import com.example.ui.theme.auraPaletteFor

@Composable
fun StoreThemeShopPage(
    forgeState: ForgeUiState,
    currentStoreTheme: StoreTheme,
    onSelectStoreTheme: (StoreTheme) -> Unit,
    isArabic: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val aura = MaterialTheme.aura
    val haptic = LocalHapticFeedback.current
    val totalKeys = forgeState.bronzeKeys + forgeState.silverKeys + forgeState.goldenKeys + forgeState.diamondKeys

    var selectedCategory by remember { mutableStateOf<StoreThemeCategory?>(null) }
    var inspectedTheme by remember { mutableStateOf<StoreTheme?>(null) }

    val displayedThemes = remember(selectedCategory) {
        if (selectedCategory == null) {
            StoreTheme.storeThemes
        } else {
            StoreTheme.storeThemes.filter { it.category == selectedCategory }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("store_themes_shop_screen")
    ) {
        // Top Navigation Bar
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
                            onBack()
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(aura.iconButtonBg)
                            .testTag("shop_exit_button")
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
                            text = "Shop",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = aura.accentGold,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = if (isArabic) "متجر الثيمات والمفاتيح" else "Themes & Keys Store",
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
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = aura.accentGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isArabic) "10 ثيمات" else "10 Themes",
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

        // Scrollable Shop Content: Keys Balance + Active Theme Banner + Category Chips + 10 Theme Showcase Cards
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("store_themes_list"),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top Keys Balance Card (رصيدي من المفاتيح)
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
                                    text = if (isArabic) "رصيدي من المفاتيح" else "My Keys Balance",
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
                                    text = if (isArabic) "الإجمالي: $totalKeys" else "Total: $totalKeys",
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
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

            // Currently Applied Theme Banner + Reset to Default
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
                                        onSelectStoreTheme(StoreTheme.DEFAULT)
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

            // Category Filter Chips
            item(key = "theme_category_filters") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StoreCategoryChip(
                        label = if (isArabic) "الكل (10)" else "All (10)",
                        isSelected = selectedCategory == null,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedCategory = null
                        }
                    )
                    StoreThemeCategory.entries.forEach { category ->
                        val count = StoreTheme.storeThemes.count { it.category == category }
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

            // 10 Store Theme Showcase Cards
            items(displayedThemes, key = { it.id }) { theme ->
                val isApplied = currentStoreTheme == theme
                StoreThemeShowcaseCard(
                    theme = theme,
                    isApplied = isApplied,
                    parentIsDark = aura.isDark,
                    isArabic = isArabic,
                    onInspect = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        inspectedTheme = theme
                    },
                    onApply = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onSelectStoreTheme(theme)
                    }
                )
            }
        }
    }

    // Detailed Preview & Apply Dialog when user taps a theme card
    val activeInspection = inspectedTheme
    if (activeInspection != null) {
        val previewPalette = auraPaletteFor(activeInspection, aura.isDark)
        val isAlreadyApplied = currentStoreTheme == activeInspection
        AlertDialog(
            onDismissRequest = { inspectedTheme = null },
            containerColor = aura.dialogBg,
            modifier = Modifier.testTag("store_theme_preview_dialog"),
            title = {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = themeIconFor(activeInspection),
                            contentDescription = null,
                            tint = previewPalette.accentCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = activeInspection.displayName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = aura.textPrimary,
                                fontWeight = FontWeight.Black
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = previewPalette.accentViolet.copy(alpha = 0.16f),
                        border = BorderStroke(0.9.dp, previewPalette.accentViolet.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = activeInspection.badgeLabel,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = previewPalette.accentViolet,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ThemeLiveMiniPreview(
                        theme = activeInspection,
                        previewAura = previewPalette,
                        isArabic = isArabic,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(132.dp)
                    )
                    Text(
                        text = if (isArabic) activeInspection.descriptionAr else activeInspection.descriptionEn,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = aura.textSecondary,
                            lineHeight = 19.sp
                        )
                    )
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
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onSelectStoreTheme(activeInspection)
                        inspectedTheme = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAlreadyApplied) aura.accentEmerald else previewPalette.accentCyan,
                        contentColor = if (isAlreadyApplied) Color.White else previewPalette.primaryCtaContent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("dialog_apply_theme_button")
                ) {
                    Text(
                        text = if (isAlreadyApplied) {
                            if (isArabic) "مُطبّق حالياً ✓" else "Currently Active ✓"
                        } else {
                            if (isArabic) "تطبيق على التطبيق" else "Apply to App"
                        },
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { inspectedTheme = null }) {
                    Text(
                        text = if (isArabic) "إغلاق" else "Close",
                        color = aura.textSecondary
                    )
                }
            }
        )
    }
}

@Composable
private fun StoreThemeShowcaseCard(
    theme: StoreTheme,
    isApplied: Boolean,
    parentIsDark: Boolean,
    isArabic: Boolean,
    onInspect: () -> Unit,
    onApply: () -> Unit
) {
    val aura = MaterialTheme.aura
    val previewAura = remember(theme, parentIsDark) { auraPaletteFor(theme, parentIsDark) }

    val cardBorderColor by animateColorAsState(
        targetValue = if (isApplied) previewAura.accentCyan else aura.borderSubtle,
        animationSpec = tween(250),
        label = "theme_card_border"
    )

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = aura.surfaceCard,
        border = BorderStroke(
            width = if (isApplied) 1.8.dp else 1.dp,
            brush = if (isApplied) {
                Brush.linearGradient(listOf(previewAura.accentCyan, previewAura.accentViolet))
            } else {
                Brush.linearGradient(listOf(cardBorderColor, aura.borderMuted))
            }
        ),
        shadowElevation = if (isApplied) 8.dp else 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable { onInspect() }
            .testTag("store_theme_card_${theme.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Card Header: Icon + Exact Name + Style Badge
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
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        previewAura.accentCyan.copy(alpha = 0.24f),
                                        previewAura.accentViolet.copy(alpha = 0.24f)
                                    )
                                )
                            )
                            .border(
                                1.1.dp,
                                Brush.linearGradient(listOf(previewAura.accentCyan, previewAura.accentViolet)),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = themeIconFor(theme),
                            contentDescription = theme.displayName,
                            tint = previewAura.accentCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = theme.displayName,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = aura.textPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (isArabic) theme.descriptionAr else theme.descriptionEn,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = aura.textSecondary,
                                fontSize = 11.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = previewAura.accentViolet.copy(alpha = 0.16f),
                    border = BorderStroke(0.8.dp, previewAura.accentViolet.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = theme.badgeLabel,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (aura.isDark) previewAura.accentViolet else aura.textPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 9.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }

            // Live Interactive Mini-App UI Preview
            ThemeLiveMiniPreview(
                theme = theme,
                previewAura = previewAura,
                isArabic = isArabic,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(106.dp)
            )

            // Footer: Swatch Dots + Apply Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    listOf(
                        previewAura.background,
                        previewAura.surfaceElevated,
                        previewAura.accentCyan,
                        previewAura.accentViolet,
                        previewAura.accentGold
                    ).forEach { colorDot ->
                        Box(
                            modifier = Modifier
                                .size(15.dp)
                                .clip(CircleShape)
                                .background(colorDot)
                                .border(1.dp, aura.borderSubtle, CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = aura.accentGoldBg,
                        border = BorderStroke(0.7.dp, aura.accentGold.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = if (isArabic) "مجاني • 0 🗝️" else "Free • 0 🗝️",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = aura.accentGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .height(38.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(
                            if (isApplied) {
                                Brush.horizontalGradient(
                                    listOf(aura.accentEmeraldSoft, aura.accentEmeraldSoft)
                                )
                            } else {
                                Brush.horizontalGradient(
                                    listOf(previewAura.accentCyan, previewAura.accentViolet)
                                )
                            }
                        )
                        .then(
                            if (isApplied) {
                                Modifier.border(1.dp, aura.accentEmerald, RoundedCornerShape(11.dp))
                            } else {
                                Modifier
                            }
                        )
                        .clickable { onApply() }
                        .padding(horizontal = 14.dp)
                        .testTag("apply_theme_button_${theme.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isApplied) Icons.Default.CheckCircle else Icons.Default.Palette,
                            contentDescription = null,
                            tint = if (isApplied) aura.accentEmerald else Color(0xFF07090D),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isApplied) {
                                if (isArabic) "مُطبّق ✓" else "Applied ✓"
                            } else {
                                if (isArabic) "تطبيق" else "Apply"
                            },
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isApplied) aura.accentEmerald else Color(0xFF07090D),
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Renders a faithful mini preview of how the app looks with that specific theme applied,
 * including theme-specific effects (SynthWave horizon grid, Holographic prism rim, Liquid spheres,
 * Frosted blur glass, PSD Mockup artboard frames, Neumorphism dual bevels).
 */
@Composable
private fun ThemeLiveMiniPreview(
    theme: StoreTheme,
    previewAura: AuraPalette,
    isArabic: Boolean,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
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
        // Signature Theme Backdrop inside the mini preview
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

            if (theme == StoreTheme.SYNTHWAVE_RETRO) {
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

        // Miniature App Interface inside the Preview
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Mini Top Bar
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

            // Mini Floating Card (demonstrating surfaceCard + borderBrush + CTA + Progress)
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
                        // Mini progress track + active bar
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

private fun themeIconFor(theme: StoreTheme): ImageVector = when (theme) {
    StoreTheme.DEFAULT -> Icons.Default.AutoAwesome
    StoreTheme.TOKYO_NIGHT -> Icons.Default.ColorLens
    StoreTheme.CATPPUCCIN_MOCHA -> Icons.Default.Palette
    StoreTheme.SYNTHWAVE_RETRO -> Icons.Default.AutoAwesome
    StoreTheme.NORD_ARCTIC -> Icons.Default.BlurOn
    StoreTheme.DRACULA_PRO -> Icons.Default.Diamond
    StoreTheme.LIQUID_FROSTED_GLASS -> Icons.Default.WaterDrop
    StoreTheme.HOLOGRAPHIC_LIQUID_GLASS -> Icons.Default.AutoAwesome
    StoreTheme.FROSTED_GLASSMORPHISM -> Icons.Default.BlurOn
    StoreTheme.GLASS_MORPHISM_PSD_MOCKUP -> Icons.Default.Layers
    StoreTheme.NEUMORPHISM -> Icons.Default.TouchApp
}
