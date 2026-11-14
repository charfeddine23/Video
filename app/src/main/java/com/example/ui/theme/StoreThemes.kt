package com.example.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class StoreThemeCategory(val labelAr: String, val labelEn: String) {
    COLOR_PALETTE("ألوان وإضاءة", "Color Themes"),
    GLASS_DEPTH("زجاج وعمق", "Glass & Depth")
}

enum class StoreTheme(
    val id: String,
    val displayName: String,
    val badgeLabel: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val category: StoreThemeCategory?
) {
    DEFAULT(
        id = "default",
        displayName = "Aura Pulse Default",
        badgeLabel = "CORE",
        descriptionAr = "الهوية الأساسية لتطبيق Video Pulse بتوهج السيان والبنفسجي",
        descriptionEn = "Core Video Pulse cyan and violet liquid glow identity",
        category = null
    ),
    TOKYO_NIGHT(
        id = "tokyo_night",
        displayName = "Tokyo Night (CYBER)",
        badgeLabel = "CYBER",
        descriptionAr = "ليالي طوكيو السيبرانية بأزرق نيلي عميق وإضاءة نيون كهربائية",
        descriptionEn = "Shibuya cyber midnight indigo with electric neon accents",
        category = StoreThemeCategory.COLOR_PALETTE
    ),
    CATPPUCCIN_MOCHA(
        id = "catppuccin_mocha",
        displayName = "Catppuccin Mocha (COZY)",
        badgeLabel = "COZY",
        descriptionAr = "ألوان باستيل دافئة مريحة للعين بدرجات الموكا والخزامى والخوخ",
        descriptionEn = "Soothing warm mocha pastels with lavender and peach tones",
        category = StoreThemeCategory.COLOR_PALETTE
    ),
    SYNTHWAVE_RETRO(
        id = "synthwave_retro",
        displayName = "SynthWave (RETRO-GLOW)",
        badgeLabel = "RETRO-GLOW",
        descriptionAr = "أجواء الثمانينات الليزرية بأفق شبكي متوهج وماجينتا ساطعة",
        descriptionEn = "80s outrun horizon grid with laser magenta and sunset gold",
        category = StoreThemeCategory.COLOR_PALETTE
    ),
    NORD_ARCTIC(
        id = "nord_arctic",
        displayName = "Nord (ARCTIC MINIMAL)",
        badgeLabel = "ARCTIC MINIMAL",
        descriptionAr = "بساطة قطبية هادئة بألوان الشمال الثلجية والشفق الهادئ",
        descriptionEn = "Clean Scandinavian polar frost and calm aurora minimalism",
        category = StoreThemeCategory.COLOR_PALETTE
    ),
    DRACULA_PRO(
        id = "dracula_pro",
        displayName = "Dracula Pro (ELITE)",
        badgeLabel = "ELITE",
        descriptionAr = "تباين احترافي فائق بأسلوب دراكولا النخبوي مع بنفسجي ونعناع نيون",
        descriptionEn = "High-contrast obsidian elite palette with violet and neon mint",
        category = StoreThemeCategory.COLOR_PALETTE
    ),
    LIQUID_FROSTED_GLASS(
        id = "liquid_frosted_glass",
        displayName = "Liquid / Frosted Glass",
        badgeLabel = "LIQUID GLASS",
        descriptionAr = "ألواح زجاجية مائية شفافة بلمعة بلورية وانعكاسات ناعمة",
        descriptionEn = "Translucent aquatic crystal glass panels with specular rims",
        category = StoreThemeCategory.GLASS_DEPTH
    ),
    HOLOGRAPHIC_LIQUID_GLASS(
        id = "holographic_liquid_glass",
        displayName = "Holographic Liquid Glass",
        badgeLabel = "HOLOGRAPHIC",
        descriptionAr = "زجاج هولوغرافي قزحي بأطياف متداخلة وإطارات منشورية متلألئة",
        descriptionEn = "Iridescent liquid prism glass with cyan-magenta-gold borders",
        category = StoreThemeCategory.GLASS_DEPTH
    ),
    FROSTED_GLASSMORPHISM(
        id = "frosted_glassmorphism",
        displayName = "Frosted Glassmorphism",
        badgeLabel = "FROSTED",
        descriptionAr = "تأثير الزجاج المصنفر الضبابي فوق كرات ضوئية ناعمة",
        descriptionEn = "Misty frosted milk-glass layers over soft luminous spheres",
        category = StoreThemeCategory.GLASS_DEPTH
    ),
    GLASS_MORPHISM_PSD_MOCKUP(
        id = "glass_morphism_psd_mockup",
        displayName = "Glass morphism PSD Mockup",
        badgeLabel = "PSD MOCKUP",
        descriptionAr = "طبقات تصميم ستوديو احترافية بإطارات زجاجية مزدوجة وشبكة عرض",
        descriptionEn = "Precision studio presentation glass planes on an artboard grid",
        category = StoreThemeCategory.GLASS_DEPTH
    ),
    NEUMORPHISM(
        id = "neumorphism",
        displayName = "Neumorphism",
        badgeLabel = "SOFT UI",
        descriptionAr = "واجهات ناعمة بارزة الملمس بظلال وإضاءات منحوتة ثنائية الاتجاه",
        descriptionEn = "Tactile extruded Soft UI matte cards with sculpted bevels",
        category = StoreThemeCategory.GLASS_DEPTH
    );

    companion object {
        val storeThemes: List<StoreTheme>
            get() = entries.filter { it != DEFAULT }

        fun fromId(id: String?): StoreTheme {
            return entries.firstOrNull { it.id == id } ?: DEFAULT
        }
    }
}

fun auraPaletteFor(storeTheme: StoreTheme, isDark: Boolean): AuraPalette {
    return when (storeTheme) {
        StoreTheme.DEFAULT -> if (isDark) DarkAuraPalette else LightAuraPalette

        StoreTheme.TOKYO_NIGHT -> if (isDark) {
            AuraPalette(
                isDark = true,
                storeTheme = storeTheme,
                background = Color(0xFF1A1B26),
                backgroundAlt = Color(0xFF16161E),
                surfaceCard = Color(0xDF24283B),
                surfaceElevated = Color(0xF0292E42),
                surfaceSubtle = Color(0x551F2335),
                surfaceGlass = Color(0x247AA2F7),
                dialogBg = Color(0xF51F2335),
                bottomBarBg = Color(0xF216161E),
                iconButtonBg = Color(0x287AA2F7),
                borderSubtle = Color(0x557AA2F7),
                borderMuted = Color(0x333B4261),
                progressTrack = Color(0x333B4261),
                textPrimary = Color(0xFFC0CAF5),
                textSecondary = Color(0xFF9AA5CE),
                textTertiary = Color(0xFF565F89),
                accentCyan = Color(0xFF7DCFFF),
                accentCyanSoft = Color(0x287DCFFF),
                accentViolet = Color(0xFFBB9AF7),
                accentEmerald = Color(0xFF9ECE6A),
                accentEmeraldSoft = Color(0x289ECE6A),
                accentGold = Color(0xFFE0AF68),
                accentGoldBg = Color(0x28E0AF68),
                accentFlame = Color(0xFFFF9E64),
                accentFlameSoft = Color(0x28FF9E64),
                accentDanger = Color(0xFFF7768E),
                primaryCtaBg = Color(0xFF7AA2F7),
                primaryCtaContent = Color(0xFF16161E),
                unselectedChipBg = Color(0x33292E42),
                ambientPrimary = Color(0xFF7DCFFF),
                ambientSecondary = Color(0xFFBB9AF7),
                ambientTertiary = Color(0xFF7AA2F7),
                borderBrushStart = Color(0xFF7DCFFF),
                borderBrushEnd = Color(0xFFBB9AF7)
            )
        } else {
            AuraPalette(
                isDark = false,
                storeTheme = storeTheme,
                background = Color(0xFFE1E2E7),
                backgroundAlt = Color(0xFFD5D6DB),
                surfaceCard = Color(0xFAF4F5F8),
                surfaceElevated = Color(0xFFFFFFFF),
                surfaceSubtle = Color(0xFFE9EAEE),
                surfaceGlass = Color(0x1E2E7DE9),
                dialogBg = Color(0xFCFFFFFF),
                bottomBarBg = Color(0xFAF4F5F8),
                iconButtonBg = Color(0x162E7DE9),
                borderSubtle = Color(0xFFB4BBD0),
                borderMuted = Color(0xFFC9CEDF),
                progressTrack = Color(0xFFD0D4E3),
                textPrimary = Color(0xFF1E2D59),
                textSecondary = Color(0xFF4B5C8E),
                textTertiary = Color(0xFF7882A8),
                accentCyan = Color(0xFF007197),
                accentCyanSoft = Color(0x1E007197),
                accentViolet = Color(0xFF7847BD),
                accentEmerald = Color(0xFF4E6B31),
                accentEmeraldSoft = Color(0x1E4E6B31),
                accentGold = Color(0xFF8C6C3E),
                accentGoldBg = Color(0x208C6C3E),
                accentFlame = Color(0xFFB15C00),
                accentFlameSoft = Color(0x1EB15C00),
                accentDanger = Color(0xFFD2255B),
                primaryCtaBg = Color(0xFF2E7DE9),
                primaryCtaContent = Color(0xFFFFFFFF),
                unselectedChipBg = Color(0xFFD5D8E4),
                ambientPrimary = Color(0xFF2E7DE9),
                ambientSecondary = Color(0xFF7847BD),
                ambientTertiary = Color(0xFF007197),
                borderBrushStart = Color(0xFF2E7DE9),
                borderBrushEnd = Color(0xFF7847BD)
            )
        }

        StoreTheme.CATPPUCCIN_MOCHA -> if (isDark) {
            AuraPalette(
                isDark = true,
                storeTheme = storeTheme,
                background = Color(0xFF1E1E2E),
                backgroundAlt = Color(0xFF181825),
                surfaceCard = Color(0xE8313244),
                surfaceElevated = Color(0xF53B3D52),
                surfaceSubtle = Color(0x5545475A),
                surfaceGlass = Color(0x22CBA6F7),
                dialogBg = Color(0xF71E1E2E),
                bottomBarBg = Color(0xF511111B),
                iconButtonBg = Color(0x26B4BEFE),
                borderSubtle = Color(0x4CB4BEFE),
                borderMuted = Color(0x30585B70),
                progressTrack = Color(0x33585B70),
                textPrimary = Color(0xFFCDD6F4),
                textSecondary = Color(0xFFBAC2DE),
                textTertiary = Color(0xFF7F849C),
                accentCyan = Color(0xFF89DCEB),
                accentCyanSoft = Color(0x2689DCEB),
                accentViolet = Color(0xFFCBA6F7),
                accentEmerald = Color(0xFFA6E3A1),
                accentEmeraldSoft = Color(0x26A6E3A1),
                accentGold = Color(0xFFF9E2AF),
                accentGoldBg = Color(0x26F9E2AF),
                accentFlame = Color(0xFFFAB387),
                accentFlameSoft = Color(0x26FAB387),
                accentDanger = Color(0xFFF38BA8),
                primaryCtaBg = Color(0xFFCBA6F7),
                primaryCtaContent = Color(0xFF1E1E2E),
                unselectedChipBg = Color(0x3845475A),
                ambientPrimary = Color(0xFFCBA6F7),
                ambientSecondary = Color(0xFFFAB387),
                ambientTertiary = Color(0xFF89DCEB),
                borderBrushStart = Color(0xFFCBA6F7),
                borderBrushEnd = Color(0xFFFAB387)
            )
        } else {
            AuraPalette(
                isDark = false,
                storeTheme = storeTheme,
                background = Color(0xFFEFF1F5),
                backgroundAlt = Color(0xFFE6E9EF),
                surfaceCard = Color(0xFAFFFFFF),
                surfaceElevated = Color(0xFFFFFFFF),
                surfaceSubtle = Color(0xFFDCE0E8),
                surfaceGlass = Color(0x188839EF),
                dialogBg = Color(0xFCFFFFFF),
                bottomBarBg = Color(0xFAFCFDFE),
                iconButtonBg = Color(0x168839EF),
                borderSubtle = Color(0xFFBCC0CC),
                borderMuted = Color(0xFFCCD0DA),
                progressTrack = Color(0xFFDCE0E8),
                textPrimary = Color(0xFF4C4F69),
                textSecondary = Color(0xFF5C5F77),
                textTertiary = Color(0xFF8C8FA1),
                accentCyan = Color(0xFF04A5E5),
                accentCyanSoft = Color(0x1C04A5E5),
                accentViolet = Color(0xFF8839EF),
                accentEmerald = Color(0xFF40A02B),
                accentEmeraldSoft = Color(0x1C40A02B),
                accentGold = Color(0xFFDF8E1D),
                accentGoldBg = Color(0x1EDF8E1D),
                accentFlame = Color(0xFFFE640B),
                accentFlameSoft = Color(0x1CFE640B),
                accentDanger = Color(0xFFD20F39),
                primaryCtaBg = Color(0xFF8839EF),
                primaryCtaContent = Color(0xFFFFFFFF),
                unselectedChipBg = Color(0xFFE6E9EF),
                ambientPrimary = Color(0xFF8839EF),
                ambientSecondary = Color(0xFFFE640B),
                ambientTertiary = Color(0xFF04A5E5),
                borderBrushStart = Color(0xFF8839EF),
                borderBrushEnd = Color(0xFFFE640B)
            )
        }

        StoreTheme.SYNTHWAVE_RETRO -> if (isDark) {
            AuraPalette(
                isDark = true,
                storeTheme = storeTheme,
                background = Color(0xFF140926),
                backgroundAlt = Color(0xFF0C0518),
                surfaceCard = Color(0xDC241142),
                surfaceElevated = Color(0xED2F1754),
                surfaceSubtle = Color(0x553B1B6B),
                surfaceGlass = Color(0x2AFF2A85),
                dialogBg = Color(0xF51B0B36),
                bottomBarBg = Color(0xF5100620),
                iconButtonBg = Color(0x30FF2A85),
                borderSubtle = Color(0x66FF2A85),
                borderMuted = Color(0x4000F0FF),
                progressTrack = Color(0x383B1B6B),
                textPrimary = Color(0xFFFFF0FF),
                textSecondary = Color(0xFFE5BAF8),
                textTertiary = Color(0xFFA074C2),
                accentCyan = Color(0xFF00F0FF),
                accentCyanSoft = Color(0x2A00F0FF),
                accentViolet = Color(0xFFFF2A85),
                accentEmerald = Color(0xFF36F9F6),
                accentEmeraldSoft = Color(0x2836F9F6),
                accentGold = Color(0xFFFF9E00),
                accentGoldBg = Color(0x28FF9E00),
                accentFlame = Color(0xFFFF5E3A),
                accentFlameSoft = Color(0x2AFF5E3A),
                accentDanger = Color(0xFFFF3864),
                primaryCtaBg = Color(0xFFFF2A85),
                primaryCtaContent = Color(0xFFFFFFFF),
                unselectedChipBg = Color(0x402E1454),
                ambientPrimary = Color(0xFFFF2A85),
                ambientSecondary = Color(0xFF00F0FF),
                ambientTertiary = Color(0xFFFF9E00),
                hasGridBackdrop = true,
                borderBrushStart = Color(0xFFFF2A85),
                borderBrushEnd = Color(0xFF00F0FF)
            )
        } else {
            AuraPalette(
                isDark = false,
                storeTheme = storeTheme,
                background = Color(0xFFFDF2FA),
                backgroundAlt = Color(0xFFF8E1F4),
                surfaceCard = Color(0xFAFFFFFF),
                surfaceElevated = Color(0xFFFFFFFF),
                surfaceSubtle = Color(0xFFFCE8F7),
                surfaceGlass = Color(0x1ADB2777),
                dialogBg = Color(0xFCFFFFFF),
                bottomBarBg = Color(0xFAFFF7FD),
                iconButtonBg = Color(0x18DB2777),
                borderSubtle = Color(0xFFE9A8D8),
                borderMuted = Color(0xFFF3CBE9),
                progressTrack = Color(0xFFF5D0EC),
                textPrimary = Color(0xFF3B0764),
                textSecondary = Color(0xFF6B21A8),
                textTertiary = Color(0xFF9333EA),
                accentCyan = Color(0xFF0284C7),
                accentCyanSoft = Color(0x1C0284C7),
                accentViolet = Color(0xFFDB2777),
                accentEmerald = Color(0xFF0D9488),
                accentEmeraldSoft = Color(0x1C0D9488),
                accentGold = Color(0xFFEA580C),
                accentGoldBg = Color(0x1EEA580C),
                accentFlame = Color(0xFFE11D48),
                accentFlameSoft = Color(0x1CE11D48),
                accentDanger = Color(0xFFDC2626),
                primaryCtaBg = Color(0xFFDB2777),
                primaryCtaContent = Color(0xFFFFFFFF),
                unselectedChipBg = Color(0xFFFCE7F3),
                ambientPrimary = Color(0xFFDB2777),
                ambientSecondary = Color(0xFF0284C7),
                ambientTertiary = Color(0xFFEA580C),
                hasGridBackdrop = true,
                borderBrushStart = Color(0xFFDB2777),
                borderBrushEnd = Color(0xFF0284C7)
            )
        }

        StoreTheme.NORD_ARCTIC -> if (isDark) {
            AuraPalette(
                isDark = true,
                storeTheme = storeTheme,
                background = Color(0xFF2E3440),
                backgroundAlt = Color(0xFF242933),
                surfaceCard = Color(0xEA3B4252),
                surfaceElevated = Color(0xF5434C5E),
                surfaceSubtle = Color(0x554C566A),
                surfaceGlass = Color(0x2288C0D0),
                dialogBg = Color(0xF72E3440),
                bottomBarBg = Color(0xF5272C36),
                iconButtonBg = Color(0x2688C0D0),
                borderSubtle = Color(0x4C81A1C1),
                borderMuted = Color(0x304C566A),
                progressTrack = Color(0x384C566A),
                textPrimary = Color(0xFFECEFF4),
                textSecondary = Color(0xFFD8DEE9),
                textTertiary = Color(0xFF9AA5B8),
                accentCyan = Color(0xFF88C0D0),
                accentCyanSoft = Color(0x2688C0D0),
                accentViolet = Color(0xFFB48EAD),
                accentEmerald = Color(0xFFA3BE8C),
                accentEmeraldSoft = Color(0x26A3BE8C),
                accentGold = Color(0xFFEBCB8B),
                accentGoldBg = Color(0x26EBCB8B),
                accentFlame = Color(0xFFD08770),
                accentFlameSoft = Color(0x26D08770),
                accentDanger = Color(0xFFBF616A),
                primaryCtaBg = Color(0xFF88C0D0),
                primaryCtaContent = Color(0xFF2E3440),
                unselectedChipBg = Color(0x40434C5E),
                ambientPrimary = Color(0xFF88C0D0),
                ambientSecondary = Color(0xFF81A1C1),
                ambientTertiary = Color(0xFFA3BE8C),
                borderBrushStart = Color(0xFF88C0D0),
                borderBrushEnd = Color(0xFF81A1C1)
            )
        } else {
            AuraPalette(
                isDark = false,
                storeTheme = storeTheme,
                background = Color(0xFFECEFF4),
                backgroundAlt = Color(0xFFE5E9F0),
                surfaceCard = Color(0xFAF8FAFC),
                surfaceElevated = Color(0xFFFFFFFF),
                surfaceSubtle = Color(0xFFD8DEE9),
                surfaceGlass = Color(0x185E81AC),
                dialogBg = Color(0xFCFFFFFF),
                bottomBarBg = Color(0xFAF7F9FC),
                iconButtonBg = Color(0x165E81AC),
                borderSubtle = Color(0xFFB8C4D6),
                borderMuted = Color(0xFFCCD5E3),
                progressTrack = Color(0xFFD8DEE9),
                textPrimary = Color(0xFF2E3440),
                textSecondary = Color(0xFF4C566A),
                textTertiary = Color(0xFF6B768C),
                accentCyan = Color(0xFF5E81AC),
                accentCyanSoft = Color(0x1C5E81AC),
                accentViolet = Color(0xFF8E6C88),
                accentEmerald = Color(0xFF5A8A5E),
                accentEmeraldSoft = Color(0x1C5A8A5E),
                accentGold = Color(0xFFB38632),
                accentGoldBg = Color(0x1EB38632),
                accentFlame = Color(0xFFBE6445),
                accentFlameSoft = Color(0x1CBE6445),
                accentDanger = Color(0xFFBF616A),
                primaryCtaBg = Color(0xFF5E81AC),
                primaryCtaContent = Color(0xFFFFFFFF),
                unselectedChipBg = Color(0xFFE5E9F0),
                ambientPrimary = Color(0xFF5E81AC),
                ambientSecondary = Color(0xFF88C0D0),
                ambientTertiary = Color(0xFF5A8A5E),
                borderBrushStart = Color(0xFF5E81AC),
                borderBrushEnd = Color(0xFF88C0D0)
            )
        }

        StoreTheme.DRACULA_PRO -> if (isDark) {
            AuraPalette(
                isDark = true,
                storeTheme = storeTheme,
                background = Color(0xFF22212C),
                backgroundAlt = Color(0xFF1B1A23),
                surfaceCard = Color(0xE82B2A38),
                surfaceElevated = Color(0xF5343245),
                surfaceSubtle = Color(0x55454158),
                surfaceGlass = Color(0x249580FF),
                dialogBg = Color(0xF722212C),
                bottomBarBg = Color(0xF5191821),
                iconButtonBg = Color(0x289580FF),
                borderSubtle = Color(0x559580FF),
                borderMuted = Color(0x38504C67),
                progressTrack = Color(0x38454158),
                textPrimary = Color(0xFFF8F8F2),
                textSecondary = Color(0xFFCFCBE6),
                textTertiary = Color(0xFF847BB8),
                accentCyan = Color(0xFF80FFEA),
                accentCyanSoft = Color(0x2680FFEA),
                accentViolet = Color(0xFF9580FF),
                accentEmerald = Color(0xFF8AFF80),
                accentEmeraldSoft = Color(0x268AFF80),
                accentGold = Color(0xFFFFFF80),
                accentGoldBg = Color(0x24FFFF80),
                accentFlame = Color(0xFFFF80BF),
                accentFlameSoft = Color(0x26FF80BF),
                accentDanger = Color(0xFFFF9580),
                primaryCtaBg = Color(0xFF9580FF),
                primaryCtaContent = Color(0xFF1B1A23),
                unselectedChipBg = Color(0x40383549),
                ambientPrimary = Color(0xFF9580FF),
                ambientSecondary = Color(0xFFFF80BF),
                ambientTertiary = Color(0xFF80FFEA),
                borderBrushStart = Color(0xFF9580FF),
                borderBrushEnd = Color(0xFF80FFEA)
            )
        } else {
            AuraPalette(
                isDark = false,
                storeTheme = storeTheme,
                background = Color(0xFFF6F4FF),
                backgroundAlt = Color(0xFFECE8FF),
                surfaceCard = Color(0xFAFFFFFF),
                surfaceElevated = Color(0xFFFFFFFF),
                surfaceSubtle = Color(0xFFEBE5FF),
                surfaceGlass = Color(0x186D28D9),
                dialogBg = Color(0xFCFFFFFF),
                bottomBarBg = Color(0xFAFBF9FF),
                iconButtonBg = Color(0x166D28D9),
                borderSubtle = Color(0xFFCBBDF9),
                borderMuted = Color(0xFFDFD5FC),
                progressTrack = Color(0xFFE4DCFF),
                textPrimary = Color(0xFF22212C),
                textSecondary = Color(0xFF4E4A67),
                textTertiary = Color(0xFF7970A9),
                accentCyan = Color(0xFF0891B2),
                accentCyanSoft = Color(0x1C0891B2),
                accentViolet = Color(0xFF6D28D9),
                accentEmerald = Color(0xFF15803D),
                accentEmeraldSoft = Color(0x1C15803D),
                accentGold = Color(0xFFB45309),
                accentGoldBg = Color(0x1CB45309),
                accentFlame = Color(0xFFDB2777),
                accentFlameSoft = Color(0x1CDB2777),
                accentDanger = Color(0xFFDC2626),
                primaryCtaBg = Color(0xFF6D28D9),
                primaryCtaContent = Color(0xFFFFFFFF),
                unselectedChipBg = Color(0xFFEDE9FE),
                ambientPrimary = Color(0xFF6D28D9),
                ambientSecondary = Color(0xFFDB2777),
                ambientTertiary = Color(0xFF0891B2),
                borderBrushStart = Color(0xFF6D28D9),
                borderBrushEnd = Color(0xFFDB2777)
            )
        }

        StoreTheme.LIQUID_FROSTED_GLASS -> if (isDark) {
            AuraPalette(
                isDark = true,
                storeTheme = storeTheme,
                background = Color(0xFF071322),
                backgroundAlt = Color(0xFF040B16),
                surfaceCard = Color(0x8E10243D),
                surfaceElevated = Color(0xB3163050),
                surfaceSubtle = Color(0x521C3C63),
                surfaceGlass = Color(0x36FFFFFF),
                dialogBg = Color(0xDF0D1E33),
                bottomBarBg = Color(0xD4081526),
                iconButtonBg = Color(0x2EBAE6FD),
                borderSubtle = Color(0x6EBAE6FD),
                borderMuted = Color(0x40FFFFFF),
                progressTrack = Color(0x36BAE6FD),
                textPrimary = Color(0xFFF0F9FF),
                textSecondary = Color(0xFFBAE6FD),
                textTertiary = Color(0xFF7DD3FC),
                accentCyan = Color(0xFF38BDF8),
                accentCyanSoft = Color(0x2C38BDF8),
                accentViolet = Color(0xFF818CF8),
                accentEmerald = Color(0xFF2DD4BF),
                accentEmeraldSoft = Color(0x2C2DD4BF),
                accentGold = Color(0xFFFDE047),
                accentGoldBg = Color(0x28FDE047),
                accentFlame = Color(0xFFFB923C),
                accentFlameSoft = Color(0x28FB923C),
                accentDanger = Color(0xFFFB7185),
                primaryCtaBg = Color(0xFF38BDF8),
                primaryCtaContent = Color(0xFF061322),
                unselectedChipBg = Color(0x3D173252),
                ambientPrimary = Color(0xFF38BDF8),
                ambientSecondary = Color(0xFF2DD4BF),
                ambientTertiary = Color(0xFF818CF8),
                isGlassStyle = true,
                borderBrushStart = Color(0xCCFFFFFF),
                borderBrushEnd = Color(0x6638BDF8)
            )
        } else {
            AuraPalette(
                isDark = false,
                storeTheme = storeTheme,
                background = Color(0xFFE6F3FF),
                backgroundAlt = Color(0xFFD5EBFF),
                surfaceCard = Color(0xB8FFFFFF),
                surfaceElevated = Color(0xDDFFFFFF),
                surfaceSubtle = Color(0x99E0F2FE),
                surfaceGlass = Color(0x55FFFFFF),
                dialogBg = Color(0xEEFFFFFF),
                bottomBarBg = Color(0xDDF4FAFF),
                iconButtonBg = Color(0x1E0284C7),
                borderSubtle = Color(0xFFFFFFFF),
                borderMuted = Color(0x887DD3FC),
                progressTrack = Color(0x88BAE6FD),
                textPrimary = Color(0xFF082F49),
                textSecondary = Color(0xFF0C4A6E),
                textTertiary = Color(0xFF0369A1),
                accentCyan = Color(0xFF0284C7),
                accentCyanSoft = Color(0x220284C7),
                accentViolet = Color(0xFF4F46E5),
                accentEmerald = Color(0xFF0D9488),
                accentEmeraldSoft = Color(0x220D9488),
                accentGold = Color(0xFFB45309),
                accentGoldBg = Color(0x22D97706),
                accentFlame = Color(0xFFEA580C),
                accentFlameSoft = Color(0x22EA580C),
                accentDanger = Color(0xFFE11D48),
                primaryCtaBg = Color(0xFF0284C7),
                primaryCtaContent = Color(0xFFFFFFFF),
                unselectedChipBg = Color(0xA6FFFFFF),
                ambientPrimary = Color(0xFF38BDF8),
                ambientSecondary = Color(0xFF2DD4BF),
                ambientTertiary = Color(0xFF6366F1),
                isGlassStyle = true,
                borderBrushStart = Color(0xFFFFFFFF),
                borderBrushEnd = Color(0x8838BDF8)
            )
        }

        StoreTheme.HOLOGRAPHIC_LIQUID_GLASS -> if (isDark) {
            AuraPalette(
                isDark = true,
                storeTheme = storeTheme,
                background = Color(0xFF0C0A1C),
                backgroundAlt = Color(0xFF070512),
                surfaceCard = Color(0x8F181533),
                surfaceElevated = Color(0xB5221E47),
                surfaceSubtle = Color(0x542E2559),
                surfaceGlass = Color(0x33E879F9),
                dialogBg = Color(0xE615132B),
                bottomBarBg = Color(0xDB0C0A1C),
                iconButtonBg = Color(0x2EE879F9),
                borderSubtle = Color(0x88E879F9),
                borderMuted = Color(0x5522D3EE),
                progressTrack = Color(0x36E879F9),
                textPrimary = Color(0xFFFAF5FF),
                textSecondary = Color(0xFFE9D5FF),
                textTertiary = Color(0xFFA5B4FC),
                accentCyan = Color(0xFF22D3EE),
                accentCyanSoft = Color(0x2E22D3EE),
                accentViolet = Color(0xFFE879F9),
                accentEmerald = Color(0xFF34D399),
                accentEmeraldSoft = Color(0x2B34D399),
                accentGold = Color(0xFFFCD34D),
                accentGoldBg = Color(0x2BFCD34D),
                accentFlame = Color(0xFFF472B6),
                accentFlameSoft = Color(0x2CF472B6),
                accentDanger = Color(0xFFFB7185),
                primaryCtaBg = Color(0xFFE879F9),
                primaryCtaContent = Color(0xFF0E081F),
                unselectedChipBg = Color(0x3D261F4D),
                ambientPrimary = Color(0xFFE879F9),
                ambientSecondary = Color(0xFF22D3EE),
                ambientTertiary = Color(0xFFFCD34D),
                isGlassStyle = true,
                borderBrushStart = Color(0xFF22D3EE),
                borderBrushEnd = Color(0xFFE879F9)
            )
        } else {
            AuraPalette(
                isDark = false,
                storeTheme = storeTheme,
                background = Color(0xFFF4F1FF),
                backgroundAlt = Color(0xFFEAE3FF),
                surfaceCard = Color(0xBCFFFFFF),
                surfaceElevated = Color(0xE0FFFFFF),
                surfaceSubtle = Color(0xA6F5E8FF),
                surfaceGlass = Color(0x59FFFFFF),
                dialogBg = Color(0xF0FFFFFF),
                bottomBarBg = Color(0xE0FAF7FF),
                iconButtonBg = Color(0x1CC026D3),
                borderSubtle = Color(0x99C084FC),
                borderMuted = Color(0x7022D3EE),
                progressTrack = Color(0x88E9D5FF),
                textPrimary = Color(0xFF1E1B4B),
                textSecondary = Color(0xFF4C1D95),
                textTertiary = Color(0xFF6D28D9),
                accentCyan = Color(0xFF0891B2),
                accentCyanSoft = Color(0x220891B2),
                accentViolet = Color(0xFFC026D3),
                accentEmerald = Color(0xFF059669),
                accentEmeraldSoft = Color(0x22059669),
                accentGold = Color(0xFFD97706),
                accentGoldBg = Color(0x22D97706),
                accentFlame = Color(0xFFDB2777),
                accentFlameSoft = Color(0x22DB2777),
                accentDanger = Color(0xFFE11D48),
                primaryCtaBg = Color(0xFF9333EA),
                primaryCtaContent = Color(0xFFFFFFFF),
                unselectedChipBg = Color(0xA8FFFFFF),
                ambientPrimary = Color(0xFFC084FC),
                ambientSecondary = Color(0xFF22D3EE),
                ambientTertiary = Color(0xFFF472B6),
                isGlassStyle = true,
                borderBrushStart = Color(0xFF0891B2),
                borderBrushEnd = Color(0xFFC026D3)
            )
        }

        StoreTheme.FROSTED_GLASSMORPHISM -> if (isDark) {
            AuraPalette(
                isDark = true,
                storeTheme = storeTheme,
                background = Color(0xFF0F172A),
                backgroundAlt = Color(0xFF090E1B),
                surfaceCard = Color(0x851E293B),
                surfaceElevated = Color(0xAD27344A),
                surfaceSubtle = Color(0x52334155),
                surfaceGlass = Color(0x2EFFFFFF),
                dialogBg = Color(0xDF162032),
                bottomBarBg = Color(0xD40F172A),
                iconButtonBg = Color(0x2BFFFFFF),
                borderSubtle = Color(0x59FFFFFF),
                borderMuted = Color(0x33CBD5E1),
                progressTrack = Color(0x33FFFFFF),
                textPrimary = Color(0xFFF8FAFC),
                textSecondary = Color(0xFFCBD5E1),
                textTertiary = Color(0xFF94A3B8),
                accentCyan = Color(0xFF60A5FA),
                accentCyanSoft = Color(0x2B60A5FA),
                accentViolet = Color(0xFFA78BFA),
                accentEmerald = Color(0xFF4ADE80),
                accentEmeraldSoft = Color(0x2B4ADE80),
                accentGold = Color(0xFFFBBF24),
                accentGoldBg = Color(0x2BFBBF24),
                accentFlame = Color(0xFFF97316),
                accentFlameSoft = Color(0x2BF97316),
                accentDanger = Color(0xFFF87171),
                primaryCtaBg = Color(0xFFF8FAFC),
                primaryCtaContent = Color(0xFF0F172A),
                unselectedChipBg = Color(0x40334155),
                ambientPrimary = Color(0xFF60A5FA),
                ambientSecondary = Color(0xFFA78BFA),
                ambientTertiary = Color(0xFF38BDF8),
                isGlassStyle = true,
                borderBrushStart = Color(0x99FFFFFF),
                borderBrushEnd = Color(0x4494A3B8)
            )
        } else {
            AuraPalette(
                isDark = false,
                storeTheme = storeTheme,
                background = Color(0xFFE8EEF7),
                backgroundAlt = Color(0xFFDCE4F2),
                surfaceCard = Color(0xAEFFFFFF),
                surfaceElevated = Color(0xD6FFFFFF),
                surfaceSubtle = Color(0x94F1F5F9),
                surfaceGlass = Color(0x66FFFFFF),
                dialogBg = Color(0xEBFFFFFF),
                bottomBarBg = Color(0xD8F8FAFC),
                iconButtonBg = Color(0x181E293B),
                borderSubtle = Color(0xEBFFFFFF),
                borderMuted = Color(0x88CBD5E1),
                progressTrack = Color(0x99CBD5E1),
                textPrimary = Color(0xFF0F172A),
                textSecondary = Color(0xFF334155),
                textTertiary = Color(0xFF64748B),
                accentCyan = Color(0xFF2563EB),
                accentCyanSoft = Color(0x1E2563EB),
                accentViolet = Color(0xFF7C3AED),
                accentEmerald = Color(0xFF16A34A),
                accentEmeraldSoft = Color(0x1E16A34A),
                accentGold = Color(0xFFD97706),
                accentGoldBg = Color(0x1ED97706),
                accentFlame = Color(0xFFEA580C),
                accentFlameSoft = Color(0x1EEA580C),
                accentDanger = Color(0xFFDC2626),
                primaryCtaBg = Color(0xFF1E293B),
                primaryCtaContent = Color(0xFFFFFFFF),
                unselectedChipBg = Color(0x99FFFFFF),
                ambientPrimary = Color(0xFF60A5FA),
                ambientSecondary = Color(0xFFA78BFA),
                ambientTertiary = Color(0xFF34D399),
                isGlassStyle = true,
                borderBrushStart = Color(0xFFFFFFFF),
                borderBrushEnd = Color(0x7094A3B8)
            )
        }

        StoreTheme.GLASS_MORPHISM_PSD_MOCKUP -> if (isDark) {
            AuraPalette(
                isDark = true,
                storeTheme = storeTheme,
                background = Color(0xFF0B101B),
                backgroundAlt = Color(0xFF060910),
                surfaceCard = Color(0xAA162033),
                surfaceElevated = Color(0xCC1E2B42),
                surfaceSubtle = Color(0x6624324D),
                surfaceGlass = Color(0x2AE2E8F0),
                dialogBg = Color(0xEB131C2E),
                bottomBarBg = Color(0xE60A101B),
                iconButtonBg = Color(0x2894A3B8),
                borderSubtle = Color(0x7094A3B8),
                borderMuted = Color(0x4564748B),
                progressTrack = Color(0x3B475569),
                textPrimary = Color(0xFFF1F5F9),
                textSecondary = Color(0xFFB8C7DC),
                textTertiary = Color(0xFF7B8FA8),
                accentCyan = Color(0xFF22D3EE),
                accentCyanSoft = Color(0x2822D3EE),
                accentViolet = Color(0xFF818CF8),
                accentEmerald = Color(0xFF34D399),
                accentEmeraldSoft = Color(0x2834D399),
                accentGold = Color(0xFFFACC15),
                accentGoldBg = Color(0x26FACC15),
                accentFlame = Color(0xFFFB923C),
                accentFlameSoft = Color(0x26FB923C),
                accentDanger = Color(0xFFF43F5E),
                primaryCtaBg = Color(0xFF22D3EE),
                primaryCtaContent = Color(0xFF060C16),
                unselectedChipBg = Color(0x401E293B),
                ambientPrimary = Color(0xFF22D3EE),
                ambientSecondary = Color(0xFF34D399),
                ambientTertiary = Color(0xFF818CF8),
                isGlassStyle = true,
                hasGridBackdrop = true,
                borderBrushStart = Color(0xAAE2E8F0),
                borderBrushEnd = Color(0x6622D3EE)
            )
        } else {
            AuraPalette(
                isDark = false,
                storeTheme = storeTheme,
                background = Color(0xFFEDF2F7),
                backgroundAlt = Color(0xFFE2E8F0),
                surfaceCard = Color(0xD6FFFFFF),
                surfaceElevated = Color(0xF5FFFFFF),
                surfaceSubtle = Color(0xBFE2E8F0),
                surfaceGlass = Color(0x70FFFFFF),
                dialogBg = Color(0xFAFFFFFF),
                bottomBarBg = Color(0xEBFFFFFF),
                iconButtonBg = Color(0x18334155),
                borderSubtle = Color(0xFFB8C5D6),
                borderMuted = Color(0xFFCBD5E1),
                progressTrack = Color(0xFFCBD5E1),
                textPrimary = Color(0xFF0F172A),
                textSecondary = Color(0xFF334155),
                textTertiary = Color(0xFF64748B),
                accentCyan = Color(0xFF0284C7),
                accentCyanSoft = Color(0x1C0284C7),
                accentViolet = Color(0xFF4F46E5),
                accentEmerald = Color(0xFF059669),
                accentEmeraldSoft = Color(0x1C059669),
                accentGold = Color(0xFFCA8A04),
                accentGoldBg = Color(0x1CCA8A04),
                accentFlame = Color(0xFFEA580C),
                accentFlameSoft = Color(0x1CEA580C),
                accentDanger = Color(0xFFE11D48),
                primaryCtaBg = Color(0xFF0F172A),
                primaryCtaContent = Color(0xFFFFFFFF),
                unselectedChipBg = Color(0xFFE2E8F0),
                ambientPrimary = Color(0xFF0284C7),
                ambientSecondary = Color(0xFF059669),
                ambientTertiary = Color(0xFF4F46E5),
                isGlassStyle = true,
                hasGridBackdrop = true,
                borderBrushStart = Color(0xFFFFFFFF),
                borderBrushEnd = Color(0xFF94A3B8)
            )
        }

        StoreTheme.NEUMORPHISM -> if (isDark) {
            AuraPalette(
                isDark = true,
                storeTheme = storeTheme,
                background = Color(0xFF242832),
                backgroundAlt = Color(0xFF1E212B),
                surfaceCard = Color(0xFF2A2F3B),
                surfaceElevated = Color(0xFF303644),
                surfaceSubtle = Color(0xFF20242E),
                surfaceGlass = Color(0xFF2E3442),
                dialogBg = Color(0xFF282D39),
                bottomBarBg = Color(0xFF242832),
                iconButtonBg = Color(0xFF303644),
                borderSubtle = Color(0x3DFFFFFF),
                borderMuted = Color(0x700D1017),
                progressTrack = Color(0xFF1A1E26),
                textPrimary = Color(0xFFEDF2F7),
                textSecondary = Color(0xFFA0AEC0),
                textTertiary = Color(0xFF718096),
                accentCyan = Color(0xFF4FD1C5),
                accentCyanSoft = Color(0x264FD1C5),
                accentViolet = Color(0xFF9F7AEA),
                accentEmerald = Color(0xFF68D391),
                accentEmeraldSoft = Color(0x2668D391),
                accentGold = Color(0xFFF6E05E),
                accentGoldBg = Color(0x24F6E05E),
                accentFlame = Color(0xFFF6AD55),
                accentFlameSoft = Color(0x26F6AD55),
                accentDanger = Color(0xFFFC8181),
                primaryCtaBg = Color(0xFF4FD1C5),
                primaryCtaContent = Color(0xFF1A202C),
                unselectedChipBg = Color(0xFF1E222B),
                ambientPrimary = Color(0xFF353B4A),
                ambientSecondary = Color(0xFF1B1F28),
                ambientTertiary = Color(0xFF4FD1C5),
                isNeumorphic = true,
                borderBrushStart = Color(0x40FFFFFF),
                borderBrushEnd = Color(0x900B0D13)
            )
        } else {
            AuraPalette(
                isDark = false,
                storeTheme = storeTheme,
                background = Color(0xFFE6EBF2),
                backgroundAlt = Color(0xFFDCE3EC),
                surfaceCard = Color(0xFFECF0F7),
                surfaceElevated = Color(0xFFF2F5FA),
                surfaceSubtle = Color(0xFFDCE3EE),
                surfaceGlass = Color(0xFFE2E8F0),
                dialogBg = Color(0xFFF0F4FA),
                bottomBarBg = Color(0xFFE8EDF5),
                iconButtonBg = Color(0xFFF0F4FA),
                borderSubtle = Color(0xFFFFFFFF),
                borderMuted = Color(0xFFBDC7D6),
                progressTrack = Color(0xFFD1D9E6),
                textPrimary = Color(0xFF1E293B),
                textSecondary = Color(0xFF475569),
                textTertiary = Color(0xFF64748B),
                accentCyan = Color(0xFF4F46E5),
                accentCyanSoft = Color(0x1C4F46E5),
                accentViolet = Color(0xFF7C3AED),
                accentEmerald = Color(0xFF0D9488),
                accentEmeraldSoft = Color(0x1C0D9488),
                accentGold = Color(0xFFD97706),
                accentGoldBg = Color(0x1CD97706),
                accentFlame = Color(0xFFEA580C),
                accentFlameSoft = Color(0x1CEA580C),
                accentDanger = Color(0xFFDC2626),
                primaryCtaBg = Color(0xFF4F46E5),
                primaryCtaContent = Color(0xFFFFFFFF),
                unselectedChipBg = Color(0xFFDDE4EE),
                ambientPrimary = Color(0xFFFFFFFF),
                ambientSecondary = Color(0xFFC2CBD9),
                ambientTertiary = Color(0xFF4F46E5),
                isNeumorphic = true,
                borderBrushStart = Color(0xFFFFFFFF),
                borderBrushEnd = Color(0xFFB5C0D0)
            )
        }
    }
}

fun materialColorSchemeFor(
    storeTheme: StoreTheme,
    isDark: Boolean,
    palette: AuraPalette
): ColorScheme {
    return if (isDark) {
        darkColorScheme(
            primary = palette.accentCyan,
            onPrimary = palette.primaryCtaContent,
            primaryContainer = palette.surfaceElevated,
            onPrimaryContainer = palette.textPrimary,
            secondary = palette.accentViolet,
            onSecondary = palette.textPrimary,
            secondaryContainer = palette.surfaceSubtle,
            onSecondaryContainer = palette.textPrimary,
            tertiary = palette.accentGold,
            onTertiary = palette.background,
            background = palette.background,
            onBackground = palette.textPrimary,
            surface = palette.surfaceCard,
            onSurface = palette.textPrimary,
            surfaceVariant = palette.surfaceElevated,
            onSurfaceVariant = palette.textSecondary,
            outline = palette.borderSubtle,
            outlineVariant = palette.borderMuted
        )
    } else {
        lightColorScheme(
            primary = palette.accentCyan,
            onPrimary = Color.White,
            primaryContainer = palette.accentCyanSoft,
            onPrimaryContainer = palette.textPrimary,
            secondary = palette.accentViolet,
            onSecondary = Color.White,
            secondaryContainer = palette.surfaceSubtle,
            onSecondaryContainer = palette.textPrimary,
            tertiary = palette.accentGold,
            onTertiary = Color.White,
            background = palette.background,
            onBackground = palette.textPrimary,
            surface = palette.surfaceCard,
            onSurface = palette.textPrimary,
            surfaceVariant = palette.backgroundAlt,
            onSurfaceVariant = palette.textSecondary,
            outline = palette.borderSubtle,
            outlineVariant = palette.borderMuted
        )
    }
}

/**
 * Draws the active StoreTheme's atmospheric visual layer (orbs, horizon grids, artboard lines, etc.)
 * behind any screen using MaterialTheme.aura.
 */
@Composable
fun AuraAtmosphericLayer(
    modifier: Modifier = Modifier
) {
    val aura = MaterialTheme.aura
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        if (aura.isNeumorphic) {
            // Soft sculpted top highlight & bottom subtle depth for Soft UI Neumorphism
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        aura.ambientPrimary.copy(alpha = if (aura.isDark) 0.35f else 0.70f),
                        Color.Transparent
                    ),
                    radius = w * 0.9f
                ),
                center = Offset(w * 0.15f, h * 0.08f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        aura.ambientSecondary.copy(alpha = if (aura.isDark) 0.45f else 0.35f),
                        Color.Transparent
                    ),
                    radius = w * 0.95f
                ),
                center = Offset(w * 0.88f, h * 0.88f)
            )
            return@Canvas
        }

        val primaryAlpha = when {
            aura.isGlassStyle && aura.isDark -> 0.28f
            aura.isGlassStyle -> 0.22f
            aura.isDark -> 0.18f
            else -> 0.12f
        }
        val secondaryAlpha = when {
            aura.isGlassStyle && aura.isDark -> 0.24f
            aura.isGlassStyle -> 0.18f
            aura.isDark -> 0.14f
            else -> 0.09f
        }

        // Top-left / top-center ambient bloom
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    aura.ambientPrimary.copy(alpha = primaryAlpha),
                    aura.ambientSecondary.copy(alpha = secondaryAlpha * 0.6f),
                    Color.Transparent
                ),
                radius = w * 0.92f
            ),
            center = Offset(w * 0.22f, h * 0.14f)
        )

        // Mid-right / lower-right ambient bloom
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    aura.ambientSecondary.copy(alpha = secondaryAlpha),
                    aura.ambientTertiary.copy(alpha = secondaryAlpha * 0.5f),
                    Color.Transparent
                ),
                radius = w * 0.85f
            ),
            center = Offset(w * 0.85f, h * 0.55f)
        )

        // Lower accent glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    aura.ambientTertiary.copy(alpha = secondaryAlpha * 0.85f),
                    Color.Transparent
                ),
                radius = w * 0.78f
            ),
            center = Offset(w * 0.35f, h * 0.92f)
        )

        // SynthWave Retro-Glow Horizon Sun & Perspective Grid OR PSD Mockup Artboard Grid
        if (aura.hasGridBackdrop) {
            if (aura.storeTheme == StoreTheme.SYNTHWAVE_RETRO) {
                val horizonY = h * 0.74f
                // Retro sunset glow above horizon
                drawCircle(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            aura.accentGold.copy(alpha = if (aura.isDark) 0.20f else 0.12f),
                            aura.accentViolet.copy(alpha = if (aura.isDark) 0.14f else 0.08f),
                            Color.Transparent
                        )
                    ),
                    radius = w * 0.34f,
                    center = Offset(w * 0.5f, horizonY - w * 0.06f)
                )
                // Horizon line
                val gridColor = aura.accentCyan.copy(alpha = if (aura.isDark) 0.16f else 0.12f)
                drawLine(
                    color = aura.accentViolet.copy(alpha = if (aura.isDark) 0.28f else 0.18f),
                    start = Offset(0f, horizonY),
                    end = Offset(w, horizonY),
                    strokeWidth = 1.4.dp.toPx()
                )
                // Perspective radiating grid lines below horizon
                val vanishX = w * 0.5f
                for (i in -5..5) {
                    val bottomX = vanishX + i * (w * 0.22f)
                    drawLine(
                        color = gridColor,
                        start = Offset(vanishX, horizonY),
                        end = Offset(bottomX, h),
                        strokeWidth = 0.9.dp.toPx()
                    )
                }
                // Horizontal perspective rungs
                val rows = 5
                for (r in 1..rows) {
                    val t = (r.toFloat() / rows.toFloat())
                    val y = horizonY + (h - horizonY) * (t * t)
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 0.8.dp.toPx()
                    )
                }
            } else {
                // Studio PSD Mockup architectural grid & artboard crosshairs
                val lineColor = aura.textTertiary.copy(alpha = if (aura.isDark) 0.08f else 0.10f)
                val step = 36.dp.toPx()
                var x = step
                while (x < w) {
                    drawLine(lineColor, Offset(x, 0f), Offset(x, h), strokeWidth = 0.6.dp.toPx())
                    x += step
                }
                var y = step
                while (y < h) {
                    drawLine(lineColor, Offset(0f, y), Offset(w, y), strokeWidth = 0.6.dp.toPx())
                    y += step
                }
                // Subtle corner studio frame
                drawRect(
                    color = aura.accentCyan.copy(alpha = 0.08f),
                    topLeft = Offset(12.dp.toPx(), 12.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(w - 24.dp.toPx(), h - 24.dp.toPx()),
                    style = Stroke(width = 0.8.dp.toPx())
                )
            }
        }
    }
}

fun auraCardBorderBrush(aura: AuraPalette): Brush {
    return when {
        aura.isNeumorphic -> Brush.linearGradient(
            listOf(aura.borderBrushStart, aura.borderBrushEnd)
        )
        aura.storeTheme == StoreTheme.HOLOGRAPHIC_LIQUID_GLASS -> Brush.linearGradient(
            listOf(
                aura.accentCyan.copy(alpha = 0.75f),
                aura.accentViolet.copy(alpha = 0.75f),
                aura.accentGold.copy(alpha = 0.65f),
                aura.accentEmerald.copy(alpha = 0.70f)
            )
        )
        aura.isGlassStyle -> Brush.verticalGradient(
            listOf(aura.borderBrushStart, aura.borderBrushEnd)
        )
        else -> Brush.linearGradient(
            listOf(aura.borderBrushStart, aura.borderBrushEnd)
        )
    }
}

fun Modifier.auraSurfaceContainer(
    aura: AuraPalette,
    cornerRadius: Dp = 20.dp,
    elevated: Boolean = false
): Modifier {
    val shape = RoundedCornerShape(cornerRadius)
    val bgColor = if (elevated) aura.surfaceElevated else aura.surfaceCard
    val borderWidth = if (aura.isGlassStyle || aura.isNeumorphic) 1.25.dp else 1.dp
    return this
        .clip(shape)
        .background(bgColor, shape)
        .border(borderWidth, auraCardBorderBrush(aura), shape)
}
