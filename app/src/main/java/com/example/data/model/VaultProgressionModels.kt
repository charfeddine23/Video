package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class VaultTier(
    val titleAr: String,
    val titleEn: String,
    val rangeText: String,
    val startLevel: Int,
    val endLevel: Int
) {
    FOUNDATION("المستوى التأسيسي", "Foundation Tier", "1 - 30", 1, 30),
    PRO("مستوى المحترفين", "Pro Tier", "31 - 100", 31, 100),
    EXPERT("مستوى الخبراء", "Expert Tier", "101 - 200", 101, 200),
    ELITE("القمة الأسطورية", "Elite Pinnacle", "201 - 365", 201, 365)
}

data class VaultLevelMilestone(
    val level: Int,
    val titleAr: String,
    val titleEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val shieldsReward: Int,
    val tier: VaultTier,
    val isMajor: Boolean = false,
    val badgeName: String? = null,
    val iconType: String = "shield"
)

object VaultProgressionRegistry {

    fun getMilestone(level: Int): VaultLevelMilestone {
        val clampedLevel = level.coerceIn(1, 365)

        // Tier 1: Levels 1 to 30 (Foundation Milestones & Core Unlocks)
        if (clampedLevel in 1..30) {
            return when (clampedLevel) {
                1 -> VaultLevelMilestone(
                    level = 1,
                    titleAr = "المتصفح المحلي الذكي للوسائط",
                    titleEn = "Base Local Media Browser",
                    descriptionAr = "فحص وقراءة سريعة لجميع الفيديوهات والملفات المخزنة في جهازك دون إنترنت.",
                    descriptionEn = "Lightning-fast zero-permission offline scanner for all local device media.",
                    shieldsReward = 1,
                    tier = VaultTier.FOUNDATION,
                    iconType = "browser"
                )
                2 -> VaultLevelMilestone(
                    level = 2,
                    titleAr = "منظف الذاكرة المؤقتة للصور المصغرة",
                    titleEn = "Cache & Thumbnail Cleaner",
                    descriptionAr = "تفريغ الذاكرة المؤقتة واستعادة مساحة التخزين فوراً مع حماية الأداء.",
                    descriptionEn = "One-tap thumbnail cache purger freeing valuable device storage.",
                    shieldsReward = 1,
                    tier = VaultTier.FOUNDATION,
                    iconType = "cleaner"
                )
                3 -> VaultLevelMilestone(
                    level = 3,
                    titleAr = "التحكم في سرعة التشغيل الافتراضية",
                    titleEn = "Playback Speed Multiplier Defaults",
                    descriptionAr = "تخصيص سرعة التشغيل الافتراضية للفيديوهات (1.0x, 1.25x, 1.5x) بسلاسة.",
                    descriptionEn = "Set your persistent default video playback speed preference.",
                    shieldsReward = 1,
                    tier = VaultTier.FOUNDATION,
                    iconType = "speed"
                )
                4 -> VaultLevelMilestone(
                    level = 4,
                    titleAr = "مرشح حماية العين الليلي للزجاج السائل",
                    titleEn = "Visual Eye Protection Scrim",
                    descriptionAr = "تفعيل طبقة تعتيم سينمائية ناعمة مريحة للعين أثناء المشاهدة في الظلام.",
                    descriptionEn = "Softer ambient display scrim protecting vision during night playback.",
                    shieldsReward = 1,
                    tier = VaultTier.FOUNDATION,
                    iconType = "eye"
                )
                5 -> VaultLevelMilestone(
                    level = 5,
                    titleAr = "مبدل المظهر (ليلي / نهاري زجاجي)",
                    titleEn = "Theme Mode Switcher (Light/Dark Glass)",
                    descriptionAr = "التحكم اليدوي الفوري بين الوضع الليلي والنهاري ومظهر الزجاج السائل M5.",
                    descriptionEn = "Manual instant toggling between Liquid Glass Dark and Light modes.",
                    shieldsReward = 2,
                    tier = VaultTier.FOUNDATION,
                    isMajor = true,
                    iconType = "theme"
                )
                in 6..9 -> VaultLevelMilestone(
                    level = clampedLevel,
                    titleAr = "تأثيرات الزجاج السائل M${clampedLevel} المتوهجة",
                    titleEn = "Glass UI Accent FX Lvl $clampedLevel",
                    descriptionAr = "إبراز حدود البطاقات الزجاجية بتوهج نيون أزرق وبنفسجي ديناميكي.",
                    descriptionEn = "Enhanced frosted glass border glow with pulsating neon accents.",
                    shieldsReward = 1,
                    tier = VaultTier.FOUNDATION,
                    iconType = "sparkle"
                )
                10 -> VaultLevelMilestone(
                    level = 10,
                    titleAr = "قفل التطبيق وخزنة الوسائط السرية",
                    titleEn = "App Lock & Biometric Vault (PIN / Fingerprint)",
                    descriptionAr = "حماية الخصوصية المطلقة للملفات والمجلدات برمز PIN وبصمة الإصبع دون اتصال.",
                    descriptionEn = "Secure PIN & biometric local vault keeping your private videos encrypted.",
                    shieldsReward = 3,
                    tier = VaultTier.FOUNDATION,
                    isMajor = true,
                    iconType = "lock"
                )
                in 11..14 -> VaultLevelMilestone(
                    level = clampedLevel,
                    titleAr = "أدوات تنظيم وفرز المجلدات M${clampedLevel - 10}",
                    titleEn = "Folder Organization Utilities Lvl ${clampedLevel - 10}",
                    descriptionAr = "حظر المجلدات غير المرغوب فيها وتصنيف الوسائط حسب الحجم والمدة والتاريخ.",
                    descriptionEn = "Folder blacklisting, smart sorting, and media catalog management.",
                    shieldsReward = 1,
                    tier = VaultTier.FOUNDATION,
                    iconType = "folder"
                )
                15 -> VaultLevelMilestone(
                    level = 15,
                    titleAr = "اختيار اللغة يدوياً (العربية / English)",
                    titleEn = "Manual Language Selection (Arabic / English)",
                    descriptionAr = "تبديل فوري للغة التطبيق واتجاه الواجهة (RTL/LTR) مع الحفاظ على كل البيانات.",
                    descriptionEn = "Instant multilingual toggle between Arabic and English with full RTL.",
                    shieldsReward = 3,
                    tier = VaultTier.FOUNDATION,
                    isMajor = true,
                    iconType = "language"
                )
                in 16..19 -> VaultLevelMilestone(
                    level = clampedLevel,
                    titleAr = "تخصيص تنبيهات الحماية المحلية M${clampedLevel - 15}",
                    titleEn = "Local Notification Customization Lvl ${clampedLevel - 15}",
                    descriptionAr = "تذكيرات ذكية بموعد تجديد السترايك قبل نهاية اليوم لحماية دروعك.",
                    descriptionEn = "Customizable offline streak preservation alerts.",
                    shieldsReward = 1,
                    tier = VaultTier.FOUNDATION,
                    iconType = "notification"
                )
                20 -> VaultLevelMilestone(
                    level = 20,
                    titleAr = "المشاركة المحلية فائقة السرعة للوسائط",
                    titleEn = "High-Speed Offline Media Sharing (Wi-Fi Direct)",
                    descriptionAr = "مشاركة ونقل مقاطع الفيديو الكبيرة بين الأجهزة بسرعة فائقة بدون إنترنت.",
                    descriptionEn = "Blazing-fast offline Wi-Fi Direct file sharing for videos.",
                    shieldsReward = 5,
                    tier = VaultTier.FOUNDATION,
                    isMajor = true,
                    iconType = "share"
                )
                in 21..24 -> VaultLevelMilestone(
                    level = clampedLevel,
                    titleAr = "تحسينات عتادية لمسرع الفيديو GPU M${clampedLevel - 20}",
                    titleEn = "GPU Decoder Optimization Toggles Lvl ${clampedLevel - 20}",
                    descriptionAr = "استهلاك أدنى للبطارية وتشغيل فائق النعومة بمعدل 120 إطاراً في الثانية.",
                    descriptionEn = "Hardware-accelerated rendering profiles for butter-smooth 120 FPS playback.",
                    shieldsReward = 1,
                    tier = VaultTier.FOUNDATION,
                    iconType = "gpu"
                )
                25 -> VaultLevelMilestone(
                    level = 25,
                    titleAr = "حزمة الثيمات الزجاجية الحصرية (3 ليلي / 3 نهاري)",
                    titleEn = "Exclusive Glass Themes Pack (3 Dark / 3 Light)",
                    descriptionAr = "فتح 6 أنماط لونية مميزة: النيون السيان، والأمبير الشمسي، والزمرد الملكي.",
                    descriptionEn = "Unlock 6 handcrafted liquid glass aesthetic palettes.",
                    shieldsReward = 5,
                    tier = VaultTier.FOUNDATION,
                    isMajor = true,
                    iconType = "palette"
                )
                in 26..29 -> VaultLevelMilestone(
                    level = clampedLevel,
                    titleAr = "توهج أورا النيوني المتطور M${clampedLevel - 25}",
                    titleEn = "Advanced Neon Aura Glow Effects Lvl ${clampedLevel - 25}",
                    descriptionAr = "توهج محيطي أعمق يتفاعل مع ألوان الفيديو والصوتيات في المشغل.",
                    descriptionEn = "Dynamic ambient backlight aura that smoothly reflects video colors.",
                    shieldsReward = 2,
                    tier = VaultTier.FOUNDATION,
                    iconType = "aura"
                )
                else -> VaultLevelMilestone(
                    level = 30,
                    titleAr = "تنبيهات السترايك الذكية ومكافأة الشهر الأول",
                    titleEn = "Smart Streak Protection Reminders + Month-1 Bonus",
                    descriptionAr = "اكتمال 30 يوماً من الحماسة! تنبيهات ذكية متقدمة مع مكافأة كبرى 10 دروع.",
                    descriptionEn = "30-day mastery unlocked! Smart streak reminders and massive +10 shields drop.",
                    shieldsReward = 10,
                    tier = VaultTier.FOUNDATION,
                    isMajor = true,
                    badgeName = "Month-1 Guardian",
                    iconType = "trophy"
                )
            }
        }

        // Tier 2: Levels 31 to 100 (Pro Tier Progress)
        if (clampedLevel in 31..100) {
            return when (clampedLevel) {
                50 -> VaultLevelMilestone(
                    level = 50,
                    titleAr = "معادل الصوت الاحترافي والصوت المكاني ثلاثي الأبعاد",
                    titleEn = "Advanced Audio Equalizer Integration",
                    descriptionAr = "دمج 5 نطاقات ترددية مع تضخيم الباس Bass Boost والصوت المحيطي المكاني.",
                    descriptionEn = "Full 5-band studio equalizer with dynamic bass boost & 3D spatial stage.",
                    shieldsReward = 10,
                    tier = VaultTier.PRO,
                    isMajor = true,
                    badgeName = "Acoustic Master",
                    iconType = "equalizer"
                )
                75 -> VaultLevelMilestone(
                    level = 75,
                    titleAr = "حزمة إدارة العلامات الزمنية والفصول الذكية",
                    titleEn = "Smart Bookmarks Management Suite",
                    descriptionAr = "إدارة كاملة للنقاط المرجعية الفورية على شريط التقدم لكل فيديو محلي.",
                    descriptionEn = "Deep bookmark cataloging and chapter jumping on the video track.",
                    shieldsReward = 12,
                    tier = VaultTier.PRO,
                    isMajor = true,
                    badgeName = "Timeline Navigator",
                    iconType = "bookmark"
                )
                100 -> VaultLevelMilestone(
                    level = 100,
                    titleAr = "وسام الشرف الماسي 'Aura Pro' الملكي",
                    titleEn = "'Aura Pro' Glass Title Badge & Shield Drop",
                    descriptionAr = "شعار النخبة الزجاجي على الملف الشخصي مع إسقاط أضخم للدروع (+20 درعاً).",
                    descriptionEn = "Prestigious 'Aura Pro' profile badge plus monumental +20 shields drop.",
                    shieldsReward = 20,
                    tier = VaultTier.PRO,
                    isMajor = true,
                    badgeName = "Aura Pro",
                    iconType = "crown"
                )
                else -> {
                    val isOdd = clampedLevel % 2 != 0
                    if (isOdd) {
                        VaultLevelMilestone(
                            level = clampedLevel,
                            titleAr = "مكافأة الصمود الاحترافي للمستوى $clampedLevel",
                            titleEn = "Pro Resilience Level $clampedLevel",
                            descriptionAr = "منح درع حماية إضافي أوتوماتيكياً لدعم السلسلة المستمرة.",
                            descriptionEn = "Automatic +1 Shield award for unwavering daily engagement.",
                            shieldsReward = 1,
                            tier = VaultTier.PRO,
                            iconType = "shield"
                        )
                    } else {
                        VaultLevelMilestone(
                            level = clampedLevel,
                            titleAr = "شارة زجاجية وجزيئات لهب للمستوى $clampedLevel",
                            titleEn = "Glass UI Badge & Flame Particles Lvl $clampedLevel",
                            descriptionAr = "فتح تأثير جزيئات النار المتصاعدة وشارة تميز زجاجية للواجهة.",
                            descriptionEn = "Dynamic flame particle animation & holographic glass UI badge.",
                            shieldsReward = 1,
                            tier = VaultTier.PRO,
                            badgeName = "Pro Lvl $clampedLevel",
                            iconType = "flame"
                        )
                    }
                }
            }
        }

        // Tier 3: Levels 101 to 200 (Expert Tier)
        if (clampedLevel in 101..200) {
            return when (clampedLevel) {
                150 -> VaultLevelMilestone(
                    level = 150,
                    titleAr = "طبقة التشفير المتقدمة لخزنة الوسائط السرية",
                    titleEn = "Enhanced Vault Encryption Layer",
                    descriptionAr = "تعزيز خوارزمية التشفير المحلي للفيديوهات والصوتيات مع مكافأة 15 درعاً.",
                    descriptionEn = "Military-grade local cryptographic obfuscation for vault files + 15 Shields.",
                    shieldsReward = 15,
                    tier = VaultTier.EXPERT,
                    isMajor = true,
                    badgeName = "Cipher Sentinel",
                    iconType = "encryption"
                )
                200 -> VaultLevelMilestone(
                    level = 200,
                    titleAr = "وسام خبير النظام المحلي 'Local System Master'",
                    titleEn = "'Local System Master' Badge + Max Shield Refill",
                    descriptionAr = "تعبئة مخزون الدروع بالكامل حتى الحد الأقصى 30/30 ووسام الخبير الملكي.",
                    descriptionEn = "Max Shield inventory refill (+25 shields) and master system badge.",
                    shieldsReward = 25,
                    tier = VaultTier.EXPERT,
                    isMajor = true,
                    badgeName = "Local System Master",
                    iconType = "master"
                )
                else -> {
                    val isEvery10 = clampedLevel % 10 == 0
                    if (isEvery10) {
                        VaultLevelMilestone(
                            level = clampedLevel,
                            titleAr = "تدرج RGB مميز للمستوى $clampedLevel ومكافأة ثلاثية",
                            titleEn = "Custom RGB Aura Preset & 3 Shields Lvl $clampedLevel",
                            descriptionAr = "فتح تدرج لوني جديد للمحيط النيوني مع مكافأة +3 دروع حماية.",
                            descriptionEn = "Bespoke RGB accent tone unlocked alongside +3 Shields drop.",
                            shieldsReward = 3,
                            tier = VaultTier.EXPERT,
                            iconType = "rgb"
                        )
                    } else {
                        VaultLevelMilestone(
                            level = clampedLevel,
                            titleAr = "محطة الخبير للمستوى $clampedLevel",
                            titleEn = "Expert Milestone Level $clampedLevel",
                            descriptionAr = "تعزيز استقرار السلسلة وكسب درع حماية إضافي للمخزون.",
                            descriptionEn = "Expert streak progression reward with +1 Shield allocation.",
                            shieldsReward = 1,
                            tier = VaultTier.EXPERT,
                            iconType = "shield"
                        )
                    }
                }
            }
        }

        // Tier 4: Levels 201 to 365 (Elite Pinnacle)
        return when (clampedLevel) {
            365 -> VaultLevelMilestone(
                level = 365,
                titleAr = "الأسطورة السنوية الذهبية 'Yearly Aura Legend'",
                titleEn = "Golden 'Yearly Aura Legend' Trophy Badge",
                descriptionAr = "إنجاز 365 يوماً من الحماسة الكاملة! نقش درع الأسطورة الذهبي وتعبئة كاملة للمخزون 30/30 وفتح جميع الميزات.",
                descriptionEn = "Ultimate mastery unlocked! Golden engraved trophy badge, instant 30/30 max shield refill, and lifetime glass styling.",
                shieldsReward = 30,
                tier = VaultTier.ELITE,
                isMajor = true,
                badgeName = "Yearly Aura Legend 👑",
                iconType = "legend"
            )
            else -> {
                val isEvery15 = clampedLevel % 15 == 0
                if (isEvery15) {
                    VaultLevelMilestone(
                        level = clampedLevel,
                        titleAr = "إسقاط دروع النخبة للمستوى $clampedLevel (+5 دروع)",
                        titleEn = "Elite Shield Drop Level $clampedLevel (+5 Shields)",
                        descriptionAr = "مكافأة كبرى لرواد النخبة: 5 دروع حماية إضافية فورية.",
                        descriptionEn = "Prestigious elite drop: +5 Shields added straight to your inventory.",
                        shieldsReward = 5,
                        tier = VaultTier.ELITE,
                        isMajor = true,
                        iconType = "shield_pack"
                    )
                } else {
                    VaultLevelMilestone(
                        level = clampedLevel,
                        titleAr = "محطة النخبة للمستوى $clampedLevel",
                        titleEn = "Elite Milestone Level $clampedLevel",
                        descriptionAr = "خطوة نحو عرش أسطورة الـ 365 يوماً مع درع إضافي.",
                        descriptionEn = "One step closer to the 365-day peak with +1 Shield protection.",
                        shieldsReward = 1,
                        tier = VaultTier.ELITE,
                        iconType = "shield"
                    )
                }
            }
        }
    }

    /**
     * Returns the curated list of milestone cards to display for a selected tier tab.
     */
    fun getTierMilestones(tier: VaultTier): List<VaultLevelMilestone> {
        return when (tier) {
            VaultTier.FOUNDATION -> (1..30).map { getMilestone(it) }
            VaultTier.PRO -> {
                val proList = mutableListOf<VaultLevelMilestone>()
                proList.add(getMilestone(31))
                proList.add(getMilestone(32))
                proList.add(getMilestone(35))
                proList.add(getMilestone(40))
                proList.add(getMilestone(45))
                proList.add(getMilestone(50)) // Major
                proList.add(getMilestone(60))
                proList.add(getMilestone(70))
                proList.add(getMilestone(75)) // Major
                proList.add(getMilestone(80))
                proList.add(getMilestone(90))
                proList.add(getMilestone(100)) // Major
                proList
            }
            VaultTier.EXPERT -> {
                val expertList = mutableListOf<VaultLevelMilestone>()
                for (lvl in 110..200 step 10) {
                    expertList.add(getMilestone(lvl))
                }
                if (!expertList.any { it.level == 150 }) {
                    expertList.add(getMilestone(150))
                }
                expertList.sortedBy { it.level }
            }
            VaultTier.ELITE -> {
                val eliteList = mutableListOf<VaultLevelMilestone>()
                for (lvl in 215..350 step 15) {
                    eliteList.add(getMilestone(lvl))
                }
                eliteList.add(getMilestone(365)) // Pinnacle
                eliteList.sortedBy { it.level }
            }
        }
    }
}
