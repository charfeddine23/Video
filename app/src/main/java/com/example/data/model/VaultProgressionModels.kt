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

        // Tier 1: Levels 1 to 30 (Foundation Milestones & Shield Rewards)
        if (clampedLevel in 1..30) {
            return when (clampedLevel) {
                1 -> VaultLevelMilestone(
                    level = 1,
                    titleAr = "بداية رحلة الشعلة",
                    titleEn = "Flame Journey Kickoff",
                    descriptionAr = "انطلاقة السلسلة اليومية الأولى مع درع حماية ترحيبي.",
                    descriptionEn = "Begin your daily watch streak with a starter protection shield.",
                    shieldsReward = 1,
                    tier = VaultTier.FOUNDATION,
                    iconType = "flame"
                )
                2 -> VaultLevelMilestone(
                    level = 2,
                    titleAr = "شرارة الاستمرارية الأولى",
                    titleEn = "First Persistence Spark",
                    descriptionAr = "إثبات التزام اليوم الثاني مع مكافأة درع إضافي.",
                    descriptionEn = "Day-2 consistency milestone awarding +1 Streak Shield.",
                    shieldsReward = 1,
                    tier = VaultTier.FOUNDATION,
                    iconType = "shield"
                )
                3 -> VaultLevelMilestone(
                    level = 3,
                    titleAr = "تعزيز الشعلة الثلاثي",
                    titleEn = "Triple Flame Momentum",
                    descriptionAr = "ثلاثة أيام متواصلة من التغذية المستمرة للشعلة.",
                    descriptionEn = "Three consecutive days keeping the daily watch flame alive.",
                    shieldsReward = 1,
                    tier = VaultTier.FOUNDATION,
                    iconType = "flame"
                )
                4 -> VaultLevelMilestone(
                    level = 4,
                    titleAr = "حارس البوابة الأول",
                    titleEn = "Gatekeeper Rank I",
                    descriptionAr = "تعزيز مخزون الحماية بدرع إضافي قبل محطة اليوم الخامس.",
                    descriptionEn = "Reinforce your inventory with +1 Shield ahead of Day 5.",
                    shieldsReward = 1,
                    tier = VaultTier.FOUNDATION,
                    iconType = "shield"
                )
                5 -> VaultLevelMilestone(
                    level = 5,
                    titleAr = "درع البداية المتوهج (محطة كبرى)",
                    titleEn = "Glowing Starter Shield Milestone",
                    descriptionAr = "إنجاز الأيام الخمسة الأولى مع منحة مضاعفة (+2 دروع حماية).",
                    descriptionEn = "First 5-day milestone completed with a +2 Shields boost.",
                    shieldsReward = 2,
                    tier = VaultTier.FOUNDATION,
                    isMajor = true,
                    badgeName = "Starter Flame",
                    iconType = "trophy"
                )
                in 6..9 -> VaultLevelMilestone(
                    level = clampedLevel,
                    titleAr = "توهج النيون المتصاعد للمستوى $clampedLevel",
                    titleEn = "Rising Neon Aura Lvl $clampedLevel",
                    descriptionAr = "شارة تقدم مستمرة مع +1 درع حماية يضاف إلى رصيدك.",
                    descriptionEn = "Continuous streak progression awarding +1 Shield.",
                    shieldsReward = 1,
                    tier = VaultTier.FOUNDATION,
                    iconType = "sparkle"
                )
                10 -> VaultLevelMilestone(
                    level = 10,
                    titleAr = "حارس الشعلة الفضي (محطة كبرى)",
                    titleEn = "Silver Flame Guardian Milestone",
                    descriptionAr = "إتمام العشرة أيام الأولى بنجاح مع جائزة 3 دروع حماية.",
                    descriptionEn = "10-day streak milestone awarding Silver Guardian badge + 3 Shields.",
                    shieldsReward = 3,
                    tier = VaultTier.FOUNDATION,
                    isMajor = true,
                    badgeName = "Silver Guardian",
                    iconType = "shield_pack"
                )
                in 11..14 -> VaultLevelMilestone(
                    level = clampedLevel,
                    titleAr = "صمود السلسلة للمستوى $clampedLevel",
                    titleEn = "Streak Resilience Lvl $clampedLevel",
                    descriptionAr = "تواصل يومي يحفظ السلسلة ويمنحك درع حماية إضافي.",
                    descriptionEn = "Daily consistency milestone granting +1 Protection Shield.",
                    shieldsReward = 1,
                    tier = VaultTier.FOUNDATION,
                    iconType = "shield"
                )
                15 -> VaultLevelMilestone(
                    level = 15,
                    titleAr = "حزمة دروع منتصف الشهر",
                    titleEn = "Mid-Month Shield Pack",
                    descriptionAr = "نصف شهر من الالتزام المتواصل ومكافأة 3 دروع حماية.",
                    descriptionEn = "Half-month milestone unlocked with a +3 Shields drop.",
                    shieldsReward = 3,
                    tier = VaultTier.FOUNDATION,
                    isMajor = true,
                    badgeName = "Half-Month Vanguard",
                    iconType = "trophy"
                )
                in 16..19 -> VaultLevelMilestone(
                    level = clampedLevel,
                    titleAr = "وهج المثابرة للمستوى $clampedLevel",
                    titleEn = "Persistence Glow Lvl $clampedLevel",
                    descriptionAr = "خطوة إضافية نحو الدرع الذهبي مع مكافأة درع حماية.",
                    descriptionEn = "One step closer to the Golden Shield with +1 Shield.",
                    shieldsReward = 1,
                    tier = VaultTier.FOUNDATION,
                    iconType = "sparkle"
                )
                20 -> VaultLevelMilestone(
                    level = 20,
                    titleAr = "الدرع الذهبي الملكي (محطة كبرى)",
                    titleEn = "Golden Royal Shield Milestone",
                    descriptionAr = "إتمام 20 يوماً واستحقاق وسام الدرع الذهبي مع +5 دروع.",
                    descriptionEn = "20-day mastery unlocking the Golden Shield crest + 5 Shields.",
                    shieldsReward = 5,
                    tier = VaultTier.FOUNDATION,
                    isMajor = true,
                    badgeName = "Golden Guardian",
                    iconType = "crown"
                )
                in 21..24 -> VaultLevelMilestone(
                    level = clampedLevel,
                    titleAr = "طاقة النخبة للمستوى $clampedLevel",
                    titleEn = "Elite Momentum Lvl $clampedLevel",
                    descriptionAr = "تعزيز إضافي لرصيد الدروع مع الحفاظ على اشتعال الشعلة.",
                    descriptionEn = "Keep your daily flame burning to collect +1 Shield.",
                    shieldsReward = 1,
                    tier = VaultTier.FOUNDATION,
                    iconType = "shield"
                )
                25 -> VaultLevelMilestone(
                    level = 25,
                    titleAr = "وسام الهالة المشعّة (محطة كبرى)",
                    titleEn = "Radiant Aura Crest Milestone",
                    descriptionAr = "إنجاز الـ 25 يوماً مع منحة 5 دروع وشارة الشرف الزجاجية.",
                    descriptionEn = "25-day achievement granting Radiant Crest + 5 Shields.",
                    shieldsReward = 5,
                    tier = VaultTier.FOUNDATION,
                    isMajor = true,
                    badgeName = "Radiant Aura",
                    iconType = "aura"
                )
                in 26..29 -> VaultLevelMilestone(
                    level = clampedLevel,
                    titleAr = "توهج أورا النيوني المتطور M${clampedLevel - 25}",
                    titleEn = "Neon Aura Flame Boost Lvl ${clampedLevel - 25}",
                    descriptionAr = "تعزيز الشعلة اليومية مع مكافأة +2 دروع حماية للمخزون.",
                    descriptionEn = "Vibrant neon aura flair awarding +2 Streak Shields.",
                    shieldsReward = 2,
                    tier = VaultTier.FOUNDATION,
                    iconType = "aura"
                )
                else -> VaultLevelMilestone(
                    level = 30,
                    titleAr = "مكافأة الشهر الأول الكبرى (+10 دروع)",
                    titleEn = "Month-1 Grand Milestone (+10 Shields)",
                    descriptionAr = "اكتمال 30 يوماً من الحماسة! لقب حارس الشهر الأول مع مكافأة كبرى 10 دروع.",
                    descriptionEn = "30-day mastery unlocked! Month-1 Guardian title and massive +10 shields drop.",
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
