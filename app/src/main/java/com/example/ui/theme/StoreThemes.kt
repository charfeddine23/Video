package com.example.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.data.model.ForgeKeyTier

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
    val category: StoreThemeCategory?,
    val costTier: ForgeKeyTier?,
    val costAmount: Int,
    val isUnlockedByDefault: Boolean = false
) {
    DEFAULT(
        id = "default",
        displayName = "Aura Pulse Default",
        badgeLabel = "CORE",
        descriptionAr = "الهوية الأساسية لتطبيق Video Pulse بتوهج السيان والبنفسجي",
        descriptionEn = "Core Video Pulse cyan and violet liquid glow identity",
        category = null,
        costTier = null,
        costAmount = 0,
        isUnlockedByDefault = true
    ),
    LIQUID_FROSTED_GLASS(
        id = "liquid_frosted_glass",
        displayName = "Liquid / Frosted Glass",
        badgeLabel = "DEFAULT • FREE",
        descriptionAr = "ألواح زجاجية مائية شفافة بلمعة بلورية وانعكاسات ناعمة",
        descriptionEn = "Default translucent aquatic crystal glass panels with specular rims",
        category = StoreThemeCategory.GLASS_DEPTH,
        costTier = null,
        costAmount = 0,
        isUnlockedByDefault = true
    ),
    TOKYO_NIGHT(
        id = "tokyo_night",
        displayName = "Tokyo Night (CYBER)",
        badgeLabel = "CYBER",
        descriptionAr = "ليالي طوكيو السيبرانية بخلفية #1A1B26 وتوهج أزرق نيون #7AA2F7",
        descriptionEn = "Shibuya cyber midnight #1A1B26 with electric neon #7AA2F7 accents",
        category = StoreThemeCategory.COLOR_PALETTE,
        costTier = ForgeKeyTier.BRONZE,
        costAmount = 2
    ),
    CATPPUCCIN_MOCHA(
        id = "catppuccin_mocha",
        displayName = "Catppuccin Mocha (COZY)",
        badgeLabel = "COZY",
        descriptionAr = "ألوان موكا دافئة مريحة للعين #1E1E2E مع لمسات زرقاء هادئة #89B4FA",
        descriptionEn = "Cozy mocha #1E1E2E palette with smooth #89B4FA sapphire accents",
        category = StoreThemeCategory.COLOR_PALETTE,
        costTier = ForgeKeyTier.BRONZE,
        costAmount = 2
    ),
    NORD_ARCTIC(
        id = "nord_arctic",
        displayName = "Nord (ARCTIC MINIMAL)",
        badgeLabel = "ARCTIC MINIMAL",
        descriptionAr = "بساطة قطبية هادئة #2E3440 مع سيان جليدي نقي #88C0D0",
        descriptionEn = "Arctic minimal #2E3440 polar night with icy #88C0D0 cyan",
        category = StoreThemeCategory.COLOR_PALETTE,
        costTier = ForgeKeyTier.SILVER,
        costAmount = 2
    ),
    FROSTED_GLASSMORPHISM(
        id = "frosted_glassmorphism",
        displayName = "Frosted Glassmorphism",
        badgeLabel = "FROSTED",
        descriptionAr = "زجاج مصنفر شفاف فوق خلفية #121629 مع توهج نعناعي نيون #00FF88",
        descriptionEn = "Frosted glass cards over #121629 backdrop with #00FF88 mint neon",
        category = StoreThemeCategory.GLASS_DEPTH,
        costTier = ForgeKeyTier.SILVER,
        costAmount = 3
    ),
    NEUMORPHISM(
        id = "neumorphism",
        displayName = "Neumorphism",
        badgeLabel = "SOFT UI",
        descriptionAr = "واجهات ناعمة بارزة #E4EBF5 وبطاقات منحوتة #EEF2F7 بلون #6D5DFC",
        descriptionEn = "Light Soft-UI #E4EBF5 canvas, extruded #EEF2F7 cards & #6D5DFC accent",
        category = StoreThemeCategory.GLASS_DEPTH,
        costTier = ForgeKeyTier.SILVER,
        costAmount = 3
    ),
    SYNTHWAVE(
        id = "synthwave",
        displayName = "SynthWave",
        badgeLabel = "RETRO NEON",
        descriptionAr = "ريتروايف ليلي #262335 بأفق شبكي متوهج #FF71CE و #01CDFE",
        descriptionEn = "Outrun #262335 horizon grid with #FF71CE & #01CDFE retro neon",
        category = StoreThemeCategory.COLOR_PALETTE,
        costTier = ForgeKeyTier.GOLD,
        costAmount = 2
    ),
    RETRO_GLOW(
        id = "retro_glow",
        displayName = "RETRO-GLOW",
        badgeLabel = "WARM AMBER",
        descriptionAr = "دفء كلاسيكي متوهج #1C1816 مع بطاقات #2D2623 وعنبر دافئ #FFB86C",
        descriptionEn = "Warm #1C1816 retro glow with #2D2623 cards & #FFB86C amber",
        category = StoreThemeCategory.COLOR_PALETTE,
        costTier = ForgeKeyTier.GOLD,
        costAmount = 2
    ),
    DRACULA_PRO(
        id = "dracula_pro",
        displayName = "Dracula Pro (ELITE)",
        badgeLabel = "ELITE",
        descriptionAr = "تباين نخبوي احترافي #282A36 مع نيون وردي وبنفسجي #FF79C6",
        descriptionEn = "Elite #282A36 Dracula Pro palette with #FF79C6 pink/purple neon",
        category = StoreThemeCategory.COLOR_PALETTE,
        costTier = ForgeKeyTier.GOLD,
        costAmount = 3
    ),
    HOLOGRAPHIC_LIQUID_GLASS(
        id = "holographic_liquid_glass",
        displayName = "Holographic Liquid Glass",
        badgeLabel = "HOLOGRAPHIC",
        descriptionAr = "زجاج هولوغرافي قزحي #0F0C1B بحواف متلألئة #FF007F و #00F0FF",
        descriptionEn = "Iridescent #0F0C1B glass with #FF007F & #00F0FF holographic rims",
        category = StoreThemeCategory.GLASS_DEPTH,
        costTier = ForgeKeyTier.DIAMOND,
        costAmount = 2
    ),
    GLASS_MORPHISM_PSD_MOCKUP(
        id = "glass_morphism_psd_mockup",
        displayName = "Glass morphism PSD Mockup",
        badgeLabel = "PSD MOCKUP",
        descriptionAr = "طبقات زجاجية فائقة النقاء بأسلوب عرض الستوديو وشبكة التصميم",
        descriptionEn = "Ultra-clean layered studio presentation glass on an artboard grid",
        category = StoreThemeCategory.GLASS_DEPTH,
        costTier = ForgeKeyTier.DIAMOND,
        costAmount = 2
    );

    companion object {
        val storeCatalog: List<StoreTheme>
            get() = entries.filter { it != DEFAULT }

        fun fromId(id: String?): StoreTheme {
            if (id == "synthwave_retro") return SYNTHWAVE
            return entries.firstOrNull { it.id == id } ?: DEFAULT
        }
    }
}

fun auraPaletteFor(storeTheme: StoreTheme, isDark: Boolean): AuraPalette {
    return when (storeTheme) {
        StoreTheme.DEFAULT -> if (isDark) DarkAuraPalette else LightAuraPalette

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

        StoreTheme.TOKYO_NIGHT -> if (isDark) {
            AuraPalette(
                isDark = true,
                storeTheme = storeTheme,
                background = Color(0xFF1A1B26),
                backgroundAlt = Color(0xFF16161E),
                surfaceCard = Color(0xEC24283B),
                surfaceElevated = Color(0xF5292E42),
                surfaceSubtle = Color(0x661F2335),
                surfaceGlass = Color(0x267AA2F7),
                dialogBg = Color(0xF71F2335),
                bottomBarBg = Color(0xF516161E),
                iconButtonBg = Color(0x2A7AA2F7),
                borderSubtle = Color(0x667AA2F7),
                borderMuted = Color(0x383B4261),
                progressTrack = Color(0x383B4261),
                textPrimary = Color(0xFFC0CAF5),
                textSecondary = Color(0xFF9AA5CE),
                textTertiary = Color(0xFF565F89),
                accentCyan = Color(0xFF7AA2F7),
                accentCyanSoft = Color(0x2B7AA2F7),
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
                unselectedChipBg = Color(0x3D292E42),
                ambientPrimary = Color(0xFF7AA2F7),
                ambientSecondary = Color(0xFFBB9AF7),
                ambientTertiary = Color(0xFF7DCFFF),
                borderBrushStart = Color(0xFF7AA2F7),
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
                accentCyan = Color(0xFF2E7DE9),
                accentCyanSoft = Color(0x1E2E7DE9),
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
                surfaceCard = Color(0xEE313244),
                surfaceElevated = Color(0xF53B3D52),
                surfaceSubtle = Color(0x5545475A),
                surfaceGlass = Color(0x2489B4FA),
                dialogBg = Color(0xF71E1E2E),
                bottomBarBg = Color(0xF511111B),
                iconButtonBg = Color(0x2889B4FA),
                borderSubtle = Color(0x5589B4FA),
                borderMuted = Color(0x33585B70),
                progressTrack = Color(0x38585B70),
                textPrimary = Color(0xFFCDD6F4),
                textSecondary = Color(0xFFBAC2DE),
                textTertiary = Color(0xFF7F849C),
                accentCyan = Color(0xFF89B4FA),
                accentCyanSoft = Color(0x2B89B4FA),
                accentViolet = Color(0xFFCBA6F7),
                accentEmerald = Color(0xFFA6E3A1),
                accentEmeraldSoft = Color(0x26A6E3A1),
                accentGold = Color(0xFFF9E2AF),
                accentGoldBg = Color(0x26F9E2AF),
                accentFlame = Color(0xFFFAB387),
                accentFlameSoft = Color(0x26FAB387),
                accentDanger = Color(0xFFF38BA8),
                primaryCtaBg = Color(0xFF89B4FA),
                primaryCtaContent = Color(0xFF1E1E2E),
                unselectedChipBg = Color(0x3845475A),
                ambientPrimary = Color(0xFF89B4FA),
                ambientSecondary = Color(0xFFCBA6F7),
                ambientTertiary = Color(0xFFFAB387),
                borderBrushStart = Color(0xFF89B4FA),
                borderBrushEnd = Color(0xFFCBA6F7)
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
                surfaceGlass = Color(0x181E66F5),
                dialogBg = Color(0xFCFFFFFF),
                bottomBarBg = Color(0xFAFCFDFE),
                iconButtonBg = Color(0x161E66F5),
                borderSubtle = Color(0xFFBCC0CC),
                borderMuted = Color(0xFFCCD0DA),
                progressTrack = Color(0xFFDCE0E8),
                textPrimary = Color(0xFF4C4F69),
                textSecondary = Color(0xFF5C5F77),
                textTertiary = Color(0xFF8C8FA1),
                accentCyan = Color(0xFF1E66F5),
                accentCyanSoft = Color(0x1C1E66F5),
                accentViolet = Color(0xFF8839EF),
                accentEmerald = Color(0xFF40A02B),
                accentEmeraldSoft = Color(0x1C40A02B),
                accentGold = Color(0xFFDF8E1D),
                accentGoldBg = Color(0x1EDF8E1D),
                accentFlame = Color(0xFFFE640B),
                accentFlameSoft = Color(0x1CFE640B),
                accentDanger = Color(0xFFD20F39),
                primaryCtaBg = Color(0xFF1E66F5),
                primaryCtaContent = Color(0xFFFFFFFF),
                unselectedChipBg = Color(0xFFE6E9EF),
                ambientPrimary = Color(0xFF1E66F5),
                ambientSecondary = Color(0xFF8839EF),
                ambientTertiary = Color(0xFFFE640B),
                borderBrushStart = Color(0xFF1E66F5),
                borderBrushEnd = Color(0xFF8839EF)
            )
        }

        StoreTheme.NORD_ARCTIC -> if (isDark) {
            AuraPalette(
                isDark = true,
                storeTheme = storeTheme,
                background = Color(0xFF2E3440),
                backgroundAlt = Color(0xFF242933),
                surfaceCard = Color(0xEE3B4252),
                surfaceElevated = Color(0xF5434C5E),
                surfaceSubtle = Color(0x554C566A),
                surfaceGlass = Color(0x2488C0D0),
                dialogBg = Color(0xF72E3440),
                bottomBarBg = Color(0xF5272C36),
                iconButtonBg = Color(0x2688C0D0),
                borderSubtle = Color(0x5588C0D0),
                borderMuted = Color(0x384C566A),
                progressTrack = Color(0x384C566A),
                textPrimary = Color(0xFFECEFF4),
                textSecondary = Color(0xFFD8DEE9),
                textTertiary = Color(0xFF9AA5B8),
                accentCyan = Color(0xFF88C0D0),
                accentCyanSoft = Color(0x2888C0D0),
                accentViolet = Color(0xFF81A1C1),
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

        StoreTheme.FROSTED_GLASSMORPHISM -> if (isDark) {
            AuraPalette(
                isDark = true,
                storeTheme = storeTheme,
                background = Color(0xFF121629),
                backgroundAlt = Color(0xFF0C0F1E),
                surfaceCard = Color(0x8E1D2442),
                surfaceElevated = Color(0xB5252E52),
                surfaceSubtle = Color(0x552E3861),
                surfaceGlass = Color(0x2E00FF88),
                dialogBg = Color(0xE3161B33),
                bottomBarBg = Color(0xD9101426),
                iconButtonBg = Color(0x2600FF88),
                borderSubtle = Color(0x5EFFFFFF),
                borderMuted = Color(0x4500FF88),
                progressTrack = Color(0x36FFFFFF),
                textPrimary = Color(0xFFF8FAFC),
                textSecondary = Color(0xFFD0D9EE),
                textTertiary = Color(0xFF8E9BBF),
                accentCyan = Color(0xFF00FF88),
                accentCyanSoft = Color(0x2800FF88),
                accentViolet = Color(0xFF60A5FA),
                accentEmerald = Color(0xFF00FF88),
                accentEmeraldSoft = Color(0x2800FF88),
                accentGold = Color(0xFFFBBF24),
                accentGoldBg = Color(0x28FBBF24),
                accentFlame = Color(0xFF34D399),
                accentFlameSoft = Color(0x2834D399),
                accentDanger = Color(0xFFF87171),
                primaryCtaBg = Color(0xFF00FF88),
                primaryCtaContent = Color(0xFF0A121C),
                unselectedChipBg = Color(0x40242C4F),
                ambientPrimary = Color(0xFF00FF88),
                ambientSecondary = Color(0xFF38BDF8),
                ambientTertiary = Color(0xFF818CF8),
                isGlassStyle = true,
                borderBrushStart = Color(0xAAFFFFFF),
                borderBrushEnd = Color(0x5500FF88)
            )
        } else {
            AuraPalette(
                isDark = false,
                storeTheme = storeTheme,
                background = Color(0xFFEBF3F5),
                backgroundAlt = Color(0xFFDDEAEF),
                surfaceCard = Color(0xB8FFFFFF),
                surfaceElevated = Color(0xDDFFFFFF),
                surfaceSubtle = Color(0x99E6F7F1),
                surfaceGlass = Color(0x66FFFFFF),
                dialogBg = Color(0xEEFFFFFF),
                bottomBarBg = Color(0xDDF4FAFB),
                iconButtonBg = Color(0x18059669),
                borderSubtle = Color(0xEBFFFFFF),
                borderMuted = Color(0x886EE7B7),
                progressTrack = Color(0x99CBD5E1),
                textPrimary = Color(0xFF0F172A),
                textSecondary = Color(0xFF334155),
                textTertiary = Color(0xFF64748B),
                accentCyan = Color(0xFF059669),
                accentCyanSoft = Color(0x1E059669),
                accentViolet = Color(0xFF2563EB),
                accentEmerald = Color(0xFF059669),
                accentEmeraldSoft = Color(0x1E059669),
                accentGold = Color(0xFFD97706),
                accentGoldBg = Color(0x1ED97706),
                accentFlame = Color(0xFF0D9488),
                accentFlameSoft = Color(0x1E0D9488),
                accentDanger = Color(0xFFDC2626),
                primaryCtaBg = Color(0xFF059669),
                primaryCtaContent = Color(0xFFFFFFFF),
                unselectedChipBg = Color(0x99FFFFFF),
                ambientPrimary = Color(0xFF10B981),
                ambientSecondary = Color(0xFF38BDF8),
                ambientTertiary = Color(0xFF6366F1),
                isGlassStyle = true,
                borderBrushStart = Color(0xFFFFFFFF),
                borderBrushEnd = Color(0x70059669)
            )
        }

        // Neumorphism uses authentic Soft-UI Light palette (#E4EBF5 bg, #EEF2F7 extruded cards, #6D5DFC accent)
        StoreTheme.NEUMORPHISM -> AuraPalette(
            isDark = false,
            storeTheme = storeTheme,
            background = Color(0xFFE4EBF5),
            backgroundAlt = Color(0xFFD7E0EE),
            surfaceCard = Color(0xFFEEF2F7),
            surfaceElevated = Color(0xFFF5F8FC),
            surfaceSubtle = Color(0xFFDCE4F0),
            surfaceGlass = Color(0xFFE7EDF6),
            dialogBg = Color(0xFFF3F6FB),
            bottomBarBg = Color(0xFFEAF0F8),
            iconButtonBg = Color(0xFFF2F5FA),
            borderSubtle = Color(0xFFFFFFFF),
            borderMuted = Color(0xFFC3CEE0),
            progressTrack = Color(0xFFD1DAE8),
            textPrimary = Color(0xFF1E2438),
            textSecondary = Color(0xFF4B5572),
            textTertiary = Color(0xFF6E7A99),
            accentCyan = Color(0xFF6D5DFC),
            accentCyanSoft = Color(0x1E6D5DFC),
            accentViolet = Color(0xFF8B7DFF),
            accentEmerald = Color(0xFF0D9488),
            accentEmeraldSoft = Color(0x1C0D9488),
            accentGold = Color(0xFFD97706),
            accentGoldBg = Color(0x1CD97706),
            accentFlame = Color(0xFFEA580C),
            accentFlameSoft = Color(0x1CEA580C),
            accentDanger = Color(0xFFDC2626),
            primaryCtaBg = Color(0xFF6D5DFC),
            primaryCtaContent = Color(0xFFFFFFFF),
            unselectedChipBg = Color(0xFFDCE4F0),
            ambientPrimary = Color(0xFFFFFFFF),
            ambientSecondary = Color(0xFFC0CCE0),
            ambientTertiary = Color(0xFF6D5DFC),
            isNeumorphic = true,
            borderBrushStart = Color(0xFFFFFFFF),
            borderBrushEnd = Color(0xFFB8C4D8)
        )

        StoreTheme.SYNTHWAVE -> if (isDark) {
            AuraPalette(
                isDark = true,
                storeTheme = storeTheme,
                background = Color(0xFF262335),
                backgroundAlt = Color(0xFF1D1A2B),
                surfaceCard = Color(0xE82E2B40),
                surfaceElevated = Color(0xF537334D),
                surfaceSubtle = Color(0x55423B5C),
                surfaceGlass = Color(0x28FF71CE),
                dialogBg = Color(0xF7262335),
                bottomBarBg = Color(0xF51F1C2C),
                iconButtonBg = Color(0x2CFF71CE),
                borderSubtle = Color(0x66FF71CE),
                borderMuted = Color(0x4801CDFE),
                progressTrack = Color(0x38423B5C),
                textPrimary = Color(0xFFFFF2FC),
                textSecondary = Color(0xFFE3C8EC),
                textTertiary = Color(0xFFA088B8),
                accentCyan = Color(0xFF01CDFE),
                accentCyanSoft = Color(0x2801CDFE),
                accentViolet = Color(0xFFFF71CE),
                accentEmerald = Color(0xFF05FFA1),
                accentEmeraldSoft = Color(0x2805FFA1),
                accentGold = Color(0xFFFFFB96),
                accentGoldBg = Color(0x24FFFB96),
                accentFlame = Color(0xFFFF71CE),
                accentFlameSoft = Color(0x28FF71CE),
                accentDanger = Color(0xFFFF3864),
                primaryCtaBg = Color(0xFFFF71CE),
                primaryCtaContent = Color(0xFF1A1424),
                unselectedChipBg = Color(0x4037334D),
                ambientPrimary = Color(0xFFFF71CE),
                ambientSecondary = Color(0xFF01CDFE),
                ambientTertiary = Color(0xFFB967FF),
                hasGridBackdrop = true,
                borderBrushStart = Color(0xFFFF71CE),
                borderBrushEnd = Color(0xFF01CDFE)
            )
        } else {
            AuraPalette(
                isDark = false,
                storeTheme = storeTheme,
                background = Color(0xFFFDF2FA),
                backgroundAlt = Color(0xFFF7E1F3),
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

        StoreTheme.RETRO_GLOW -> if (isDark) {
            AuraPalette(
                isDark = true,
                storeTheme = storeTheme,
                background = Color(0xFF1C1816),
                backgroundAlt = Color(0xFF141110),
                surfaceCard = Color(0xEC2D2623),
                surfaceElevated = Color(0xF5382F2B),
                surfaceSubtle = Color(0x55423630),
                surfaceGlass = Color(0x28FFB86C),
                dialogBg = Color(0xF7241E1B),
                bottomBarBg = Color(0xF5171312),
                iconButtonBg = Color(0x28FFB86C),
                borderSubtle = Color(0x60FFB86C),
                borderMuted = Color(0x3852433B),
                progressTrack = Color(0x38423630),
                textPrimary = Color(0xFFFFF5EB),
                textSecondary = Color(0xFFE3C8B2),
                textTertiary = Color(0xFF9C8474),
                accentCyan = Color(0xFFFFB86C),
                accentCyanSoft = Color(0x28FFB86C),
                accentViolet = Color(0xFFFF7954),
                accentEmerald = Color(0xFFA3E635),
                accentEmeraldSoft = Color(0x26A3E635),
                accentGold = Color(0xFFFFD166),
                accentGoldBg = Color(0x28FFD166),
                accentFlame = Color(0xFFFF8C42),
                accentFlameSoft = Color(0x28FF8C42),
                accentDanger = Color(0xFFFF5E5B),
                primaryCtaBg = Color(0xFFFFB86C),
                primaryCtaContent = Color(0xFF1C1816),
                unselectedChipBg = Color(0x40382F2B),
                ambientPrimary = Color(0xFFFFB86C),
                ambientSecondary = Color(0xFFFF7954),
                ambientTertiary = Color(0xFFFFD166),
                borderBrushStart = Color(0xFFFFB86C),
                borderBrushEnd = Color(0xFFFF7954)
            )
        } else {
            AuraPalette(
                isDark = false,
                storeTheme = storeTheme,
                background = Color(0xFFFFF7EE),
                backgroundAlt = Color(0xFFFCEBD8),
                surfaceCard = Color(0xFAFFFFFF),
                surfaceElevated = Color(0xFFFFFFFF),
                surfaceSubtle = Color(0xFFFAE8D4),
                surfaceGlass = Color(0x1CD97706),
                dialogBg = Color(0xFCFFFFFF),
                bottomBarBg = Color(0xFAFFFDF9),
                iconButtonBg = Color(0x1AD97706),
                borderSubtle = Color(0xFFF3C99F),
                borderMuted = Color(0xFFF8DEC2),
                progressTrack = Color(0xFFF7DFC5),
                textPrimary = Color(0xFF2B1810),
                textSecondary = Color(0xFF6B422D),
                textTertiary = Color(0xFF9A6D56),
                accentCyan = Color(0xFFD97706),
                accentCyanSoft = Color(0x1ED97706),
                accentViolet = Color(0xFFEA580C),
                accentEmerald = Color(0xFF15803D),
                accentEmeraldSoft = Color(0x1C15803D),
                accentGold = Color(0xFFB45309),
                accentGoldBg = Color(0x1EB45309),
                accentFlame = Color(0xFFC2410C),
                accentFlameSoft = Color(0x1EC2410C),
                accentDanger = Color(0xFFDC2626),
                primaryCtaBg = Color(0xFFD97706),
                primaryCtaContent = Color(0xFFFFFFFF),
                unselectedChipBg = Color(0xFFFCEAD7),
                ambientPrimary = Color(0xFFF59E0B),
                ambientSecondary = Color(0xFFEA580C),
                ambientTertiary = Color(0xFFD97706),
                borderBrushStart = Color(0xFFF59E0B),
                borderBrushEnd = Color(0xFFEA580C)
            )
        }

        StoreTheme.DRACULA_PRO -> if (isDark) {
            AuraPalette(
                isDark = true,
                storeTheme = storeTheme,
                background = Color(0xFF282A36),
                backgroundAlt = Color(0xFF21222C),
                surfaceCard = Color(0xEC44475A),
                surfaceElevated = Color(0xF54E5268),
                surfaceSubtle = Color(0x66383A4A),
                surfaceGlass = Color(0x28FF79C6),
                dialogBg = Color(0xF7282A36),
                bottomBarBg = Color(0xF51E1F29),
                iconButtonBg = Color(0x2AFF79C6),
                borderSubtle = Color(0x66FF79C6),
                borderMuted = Color(0x456272A4),
                progressTrack = Color(0x406272A4),
                textPrimary = Color(0xFFF8F8F2),
                textSecondary = Color(0xFFD6D8EB),
                textTertiary = Color(0xFF9BA8D0),
                accentCyan = Color(0xFF8BE9FD),
                accentCyanSoft = Color(0x268BE9FD),
                accentViolet = Color(0xFFFF79C6),
                accentEmerald = Color(0xFF50FA7B),
                accentEmeraldSoft = Color(0x2650FA7B),
                accentGold = Color(0xFFF1FA8C),
                accentGoldBg = Color(0x24F1FA8C),
                accentFlame = Color(0xFFFFB86C),
                accentFlameSoft = Color(0x26FFB86C),
                accentDanger = Color(0xFFFF5555),
                primaryCtaBg = Color(0xFFFF79C6),
                primaryCtaContent = Color(0xFF21222C),
                unselectedChipBg = Color(0x4D343746),
                ambientPrimary = Color(0xFFFF79C6),
                ambientSecondary = Color(0xFFBD93F9),
                ambientTertiary = Color(0xFF8BE9FD),
                borderBrushStart = Color(0xFFFF79C6),
                borderBrushEnd = Color(0xFFBD93F9)
            )
        } else {
            AuraPalette(
                isDark = false,
                storeTheme = storeTheme,
                background = Color(0xFFF7F4FF),
                backgroundAlt = Color(0xFFEDE7FE),
                surfaceCard = Color(0xFAFFFFFF),
                surfaceElevated = Color(0xFFFFFFFF),
                surfaceSubtle = Color(0xFFEBE4FF),
                surfaceGlass = Color(0x186D28D9),
                dialogBg = Color(0xFCFFFFFF),
                bottomBarBg = Color(0xFAFBF9FF),
                iconButtonBg = Color(0x166D28D9),
                borderSubtle = Color(0xFFCBBDF9),
                borderMuted = Color(0xFFDFD5FC),
                progressTrack = Color(0xFFE4DCFF),
                textPrimary = Color(0xFF282A36),
                textSecondary = Color(0xFF4E4A67),
                textTertiary = Color(0xFF7970A9),
                accentCyan = Color(0xFF0891B2),
                accentCyanSoft = Color(0x1C0891B2),
                accentViolet = Color(0xFFDB2777),
                accentEmerald = Color(0xFF15803D),
                accentEmeraldSoft = Color(0x1C15803D),
                accentGold = Color(0xFFB45309),
                accentGoldBg = Color(0x1CB45309),
                accentFlame = Color(0xFF9333EA),
                accentFlameSoft = Color(0x1C9333EA),
                accentDanger = Color(0xFFDC2626),
                primaryCtaBg = Color(0xFFDB2777),
                primaryCtaContent = Color(0xFFFFFFFF),
                unselectedChipBg = Color(0xFFEDE9FE),
                ambientPrimary = Color(0xFFDB2777),
                ambientSecondary = Color(0xFF6D28D9),
                ambientTertiary = Color(0xFF0891B2),
                borderBrushStart = Color(0xFFDB2777),
                borderBrushEnd = Color(0xFF6D28D9)
            )
        }

        StoreTheme.HOLOGRAPHIC_LIQUID_GLASS -> if (isDark) {
            AuraPalette(
                isDark = true,
                storeTheme = storeTheme,
                background = Color(0xFF0F0C1B),
                backgroundAlt = Color(0xFF090712),
                surfaceCard = Color(0x941A1530),
                surfaceElevated = Color(0xBA241D42),
                surfaceSubtle = Color(0x562F2454),
                surfaceGlass = Color(0x30FF007F),
                dialogBg = Color(0xE8161129),
                bottomBarBg = Color(0xDE0F0C1B),
                iconButtonBg = Color(0x2EFF007F),
                borderSubtle = Color(0x88FF007F),
                borderMuted = Color(0x6600F0FF),
                progressTrack = Color(0x36FFFFFF),
                textPrimary = Color(0xFFFAF5FF),
                textSecondary = Color(0xFFE5D8FF),
                textTertiary = Color(0xFFA79CE0),
                accentCyan = Color(0xFF00F0FF),
                accentCyanSoft = Color(0x2E00F0FF),
                accentViolet = Color(0xFFFF007F),
                accentEmerald = Color(0xFF34D399),
                accentEmeraldSoft = Color(0x2B34D399),
                accentGold = Color(0xFFFCD34D),
                accentGoldBg = Color(0x2BFCD34D),
                accentFlame = Color(0xFFFF007F),
                accentFlameSoft = Color(0x2CFF007F),
                accentDanger = Color(0xFFFB7185),
                primaryCtaBg = Color(0xFF00F0FF),
                primaryCtaContent = Color(0xFF0F0C1B),
                unselectedChipBg = Color(0x40261E45),
                ambientPrimary = Color(0xFFFF007F),
                ambientSecondary = Color(0xFF00F0FF),
                ambientTertiary = Color(0xFFA855F7),
                isGlassStyle = true,
                borderBrushStart = Color(0xFFFF007F),
                borderBrushEnd = Color(0xFF00F0FF)
            )
        } else {
            AuraPalette(
                isDark = false,
                storeTheme = storeTheme,
                background = Color(0xFFF5F1FF),
                backgroundAlt = Color(0xFFECE3FF),
                surfaceCard = Color(0xBDFFFFFF),
                surfaceElevated = Color(0xE0FFFFFF),
                surfaceSubtle = Color(0xA6F7E8FF),
                surfaceGlass = Color(0x59FFFFFF),
                dialogBg = Color(0xF0FFFFFF),
                bottomBarBg = Color(0xE0FAF7FF),
                iconButtonBg = Color(0x1CFF007F),
                borderSubtle = Color(0x99FF007F),
                borderMuted = Color(0x7000B4D8),
                progressTrack = Color(0x88E9D5FF),
                textPrimary = Color(0xFF1E1B4B),
                textSecondary = Color(0xFF4C1D95),
                textTertiary = Color(0xFF6D28D9),
                accentCyan = Color(0xFF0891B2),
                accentCyanSoft = Color(0x220891B2),
                accentViolet = Color(0xFFDB2777),
                accentEmerald = Color(0xFF059669),
                accentEmeraldSoft = Color(0x22059669),
                accentGold = Color(0xFFD97706),
                accentGoldBg = Color(0x22D97706),
                accentFlame = Color(0xFFEC4899),
                accentFlameSoft = Color(0x22EC4899),
                accentDanger = Color(0xFFE11D48),
                primaryCtaBg = Color(0xFFDB2777),
                primaryCtaContent = Color(0xFFFFFFFF),
                unselectedChipBg = Color(0xA8FFFFFF),
                ambientPrimary = Color(0xFFFF007F),
                ambientSecondary = Color(0xFF00F0FF),
                ambientTertiary = Color(0xFF8B5CF6),
                isGlassStyle = true,
                borderBrushStart = Color(0xFFDB2777),
                borderBrushEnd = Color(0xFF0891B2)
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
    }
}

fun materialColorSchemeFor(
    storeTheme: StoreTheme,
    isDark: Boolean,
    palette: AuraPalette
): ColorScheme {
    return if (palette.isDark) {
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
                        aura.ambientPrimary.copy(alpha = 0.72f),
                        Color.Transparent
                    ),
                    radius = w * 0.9f
                ),
                center = Offset(w * 0.15f, h * 0.08f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        aura.ambientSecondary.copy(alpha = 0.38f),
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

        if (aura.hasGridBackdrop) {
            if (aura.storeTheme == StoreTheme.SYNTHWAVE) {
                val horizonY = h * 0.74f
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
                val gridColor = aura.accentCyan.copy(alpha = if (aura.isDark) 0.16f else 0.12f)
                drawLine(
                    color = aura.accentViolet.copy(alpha = if (aura.isDark) 0.28f else 0.18f),
                    start = Offset(0f, horizonY),
                    end = Offset(w, horizonY),
                    strokeWidth = 1.4.dp.toPx()
                )
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
                Color(0xFFFF007F).copy(alpha = 0.82f),
                Color(0xFF00F0FF).copy(alpha = 0.82f),
                aura.accentGold.copy(alpha = 0.72f),
                Color(0xFF00F0FF).copy(alpha = 0.80f)
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
