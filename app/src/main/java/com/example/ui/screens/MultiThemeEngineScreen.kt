package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ThemeLiveMiniPreview
import com.example.ui.components.themeIconFor
import com.example.ui.theme.AuraAtmosphericLayer
import com.example.ui.theme.StoreTheme
import com.example.ui.theme.aura
import com.example.ui.theme.auraCardBorderBrush
import com.example.ui.theme.auraPaletteFor
import com.example.ui.theme.stableStatusBarsPadding
import com.example.ui.viewmodel.StreakViewModel

@Composable
fun MultiThemeEngineScreen(
    streakViewModel: StreakViewModel,
    isArabic: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val aura = MaterialTheme.aura
    val haptic = LocalHapticFeedback.current

    BackHandler {
        onBack()
    }

    val activeStoreTheme by streakViewModel.activeStoreTheme.collectAsState()
    val purchasedThemeIds by streakViewModel.purchasedThemeIds.collectAsState()
    val activeThemeTrial by streakViewModel.activeThemeTrial.collectAsState()

    val ownedThemes = remember(purchasedThemeIds) {
        StoreTheme.entries.filter { theme ->
            theme.isUnlockedByDefault || purchasedThemeIds.contains(theme.id)
        }
    }
    val ownedRows = remember(ownedThemes) { ownedThemes.chunked(2) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(aura.background)
            .clickable(enabled = false) {}
            .testTag("multi_theme_engine_screen")
    ) {
        AuraAtmosphericLayer()

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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
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
                            .testTag("multi_theme_engine_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isArabic) "رجوع" else "Back",
                            tint = aura.textPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Multi-Theme Engine",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = aura.textPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (isArabic) "مكتبتك الخاصة بالثيمات المملوكة والافتراضية" else "Your library of default & purchased themes",
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
                    shape = RoundedCornerShape(12.dp),
                    color = aura.accentViolet.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, aura.accentViolet.copy(alpha = 0.45f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = aura.accentViolet,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isArabic) "${ownedThemes.size} ثيم" else "${ownedThemes.size} Owned",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = aura.accentViolet,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .testTag("multi_theme_engine_list"),
                contentPadding = PaddingValues(top = 6.dp, bottom = 36.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Active Theme Banner
                item(key = "engine_active_banner") {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = aura.surfaceElevated,
                        border = BorderStroke(1.1.dp, auraCardBorderBrush(aura)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
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
                                        .background(aura.accentCyanSoft)
                                        .border(1.dp, aura.accentCyan.copy(alpha = 0.55f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = themeIconFor(activeStoreTheme),
                                        contentDescription = null,
                                        tint = aura.accentCyan,
                                        modifier = Modifier.size(19.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (activeThemeTrial != null) {
                                            if (isArabic) "ثيم التجربة المؤقتة نشط" else "Active Trial Theme"
                                        } else {
                                            if (isArabic) "الثيم المفعّل حالياً" else "Currently Applied"
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (activeThemeTrial != null) aura.accentFlame else aura.textTertiary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = activeStoreTheme.displayName,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            color = aura.textPrimary,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.5.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            if (activeStoreTheme != StoreTheme.DEFAULT) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = aura.surfaceSubtle,
                                    border = BorderStroke(1.dp, aura.borderSubtle),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            streakViewModel.equipOwnedStoreTheme(StoreTheme.DEFAULT, isArabic)
                                        }
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
                                            text = if (isArabic) "الافتراضي" else "Default",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = aura.textPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.5.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2-per-row Owned Themes Grid
                items(
                    items = ownedRows,
                    key = { row -> row.joinToString("_") { it.id } }
                ) { rowThemes ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowThemes.forEach { theme ->
                            val isApplied = activeStoreTheme == theme && activeThemeTrial == null
                            OwnedEngineThemeCard(
                                theme = theme,
                                isApplied = isApplied,
                                parentIsDark = aura.isDark,
                                isArabic = isArabic,
                                onSelect = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    streakViewModel.equipOwnedStoreTheme(theme, isArabic)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowThemes.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                // Store hint footer
                item(key = "engine_store_hint") {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = aura.surfaceSubtle,
                        border = BorderStroke(0.9.dp, aura.borderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = aura.accentGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isArabic) {
                                    "أي ثيم تقوم بشرائه من المتجر (Shop ← Themes) ينتقل تلقائياً إلى هنا لتبديله في أي وقت."
                                } else {
                                    "Any theme purchased from Shop → Themes automatically moves here for instant switching anytime."
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = aura.textSecondary,
                                    fontSize = 11.5.sp,
                                    lineHeight = 16.sp
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
private fun OwnedEngineThemeCard(
    theme: StoreTheme,
    isApplied: Boolean,
    parentIsDark: Boolean,
    isArabic: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val aura = MaterialTheme.aura
    val previewAura = remember(theme, parentIsDark) {
        val useDark = when (theme) {
            StoreTheme.NEUMORPHISM -> false
            StoreTheme.DEFAULT, StoreTheme.LIQUID_FROSTED_GLASS, StoreTheme.GLASS_MORPHISM_PSD_MOCKUP -> parentIsDark
            else -> true
        }
        auraPaletteFor(theme, useDark)
    }

    val borderColor by animateColorAsState(
        targetValue = if (isApplied) aura.accentEmerald else aura.borderSubtle,
        animationSpec = tween(240),
        label = "owned_theme_border"
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = aura.surfaceCard,
        border = BorderStroke(
            width = if (isApplied) 1.8.dp else 1.dp,
            brush = if (isApplied) {
                Brush.linearGradient(listOf(aura.accentEmerald, previewAura.accentCyan))
            } else {
                Brush.linearGradient(listOf(borderColor, aura.borderMuted))
            }
        ),
        shadowElevation = if (isApplied) 8.dp else 2.dp,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onSelect() }
            .testTag("engine_theme_card_${theme.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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

                if (isApplied) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = aura.accentEmerald,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF07090D),
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isArabic) "مُطبّق" else "Active",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF07090D),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 8.5.sp
                                )
                            )
                        }
                    }
                }
            }

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

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isApplied) aura.accentEmeraldSoft else aura.surfaceSubtle,
                border = BorderStroke(
                    0.9.dp,
                    if (isApplied) aura.accentEmerald else aura.borderSubtle
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = when {
                        isApplied -> if (isArabic) "مُطبّق حالياً ✓" else "Applied ✓"
                        theme == StoreTheme.DEFAULT -> if (isArabic) "الافتراضي • تطبيق" else "Default • Apply"
                        theme.isUnlockedByDefault -> if (isArabic) "افتراضي مجاني • تطبيق" else "Default Free • Apply"
                        else -> if (isArabic) "مملوك • تطبيق" else "Owned • Apply"
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isApplied) aura.accentEmerald else aura.accentCyan,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
