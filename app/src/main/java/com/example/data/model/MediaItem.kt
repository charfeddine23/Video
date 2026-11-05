package com.example.data.model

import com.example.R

data class MediaItem(
    val id: String,
    val title: String,
    val titleArabic: String? = null,
    val subtitleBadge: String = "Top 01 in TV Shows",
    val episodesFreeBadge: String? = "4 episodes Free for you",
    val year: String = "2026",
    val seasonsOrDuration: String = "2 Seasons",
    val maturityRating: String = "16+",
    val qualityTag: String = "4K HDR",
    val genresText: String = "Social | Drama | Romance",
    val category: String = "TV Shows",
    val matchScore: Int = 98,
    val description: String = "",
    val cast: String = "",
    val posterRes: Int = R.drawable.img_hero_poster,
    val backdropRes: Int = R.drawable.img_hero_poster,
    val progress: Float = 0f, // For "Continue Watching"
    val isFeatured: Boolean = false,
    val uriString: String? = null,
    val episodes: List<Episode> = emptyList()
)

data class Episode(
    val episodeNumber: Int,
    val title: String,
    val duration: String,
    val description: String,
    val isFree: Boolean = false
)

data class LiveChannel(
    val id: String,
    val channelNumber: String,
    val channelName: String,
    val currentShow: String,
    val nextShow: String,
    val viewersCount: String,
    val category: String,
    val isLive: Boolean = true
)

data class ShortVideo(
    val id: String,
    val title: String,
    val creatorName: String,
    val handle: String,
    val soundTrack: String,
    val likesCount: String,
    val commentsCount: String,
    val sharesCount: String,
    val tag: String,
    val posterRes: Int
)

object MediaRepository {
    val heroItem = MediaItem(
        id = "hero_1",
        title = "El Sitt Oumour",
        titleArabic = "الست أمور",
        subtitleBadge = "Top 01 in TV Shows",
        episodesFreeBadge = "4 episodes Free for you",
        year = "2026",
        seasonsOrDuration = "2 Seasons",
        maturityRating = "16+",
        qualityTag = "4K HDR",
        genresText = "Social | Drama | Romance",
        category = "TV Shows",
        matchScore = 99,
        description = "An intricate web of family heritage, fierce social rivalries, and forbidden romances across Cairo and Alexandria. As old secrets unravel, the matriarch must choose between family honor and true devotion.",
        cast = "Mona Zaki, Eyad Nassar, Amina Khalil, Ahmed Malek",
        posterRes = R.drawable.img_hero_poster,
        backdropRes = R.drawable.img_hero_poster,
        isFeatured = true,
        episodes = listOf(
            Episode(1, "S1:E1 The Arrival", "48m", "A high-society wedding in Alexandria is shattered when an estranged daughter returns with devastating proof.", true),
            Episode(2, "S1:E2 Whispers in Zamalek", "52m", "Tensions flare as rival factions compete for control of the family empire.", true),
            Episode(3, "S1:E3 Fragile Alliances", "46m", "A secret pact is forged under the cover of night, risking everyone involved.", true),
            Episode(4, "S1:E4 Point of No Return", "55m", "An explosive confrontation at the annual charity gala alters the dynasty's destiny.", true),
            Episode(5, "S1:E5 The Shadow Heir", "50m", "Secrets hidden for two decades are revealed.", false)
        )
    )

    val continueWatchingList = listOf(
        heroItem.copy(id = "cw_1", progress = 0.65f),
        MediaItem(
            id = "cw_2",
            title = "Cyber Pulse 2088",
            titleArabic = "نبض السايبر",
            subtitleBadge = "Trending Worldwide",
            episodesFreeBadge = "Episode 1 Free",
            year = "2026",
            seasonsOrDuration = "1 Season",
            maturityRating = "18+",
            qualityTag = "IMAX Enhanced",
            genresText = "Sci-Fi | Thriller | Action",
            category = "TV Shows",
            matchScore = 96,
            description = "In a rain-slicked mega-city powered by neural interfaces, a rogue detective uncovers a syndicate controlling human memories.",
            cast = "Alexander Skarsgård, Bae Doona",
            posterRes = R.drawable.img_poster_cyberpunk,
            progress = 0.38f
        ),
        MediaItem(
            id = "cw_3",
            title = "Shadow Protocol",
            titleArabic = "بروتوكول الظل",
            subtitleBadge = "Popular This Week",
            episodesFreeBadge = "Full Movie Included",
            year = "2025",
            seasonsOrDuration = "2h 14m",
            maturityRating = "16+",
            qualityTag = "4K Dolby Vision",
            genresText = "Action | Espionage | Suspense",
            category = "Movies",
            matchScore = 94,
            description = "An undercover agent marked for termination goes off-grid across Istanbul and Vienna to protect a lethal decryption cipher.",
            cast = "Idris Elba, Rebecca Ferguson",
            posterRes = R.drawable.img_poster_action,
            progress = 0.82f
        )
    )

    val pulseOriginals = listOf(
        heroItem,
        MediaItem(
            id = "orig_2",
            title = "Neon Mirage",
            titleArabic = "سراب النيون",
            subtitleBadge = "Pulse Original",
            episodesFreeBadge = "3 episodes Free",
            year = "2026",
            seasonsOrDuration = "Miniseries",
            maturityRating = "16+",
            qualityTag = "4K HDR",
            genresText = "Mystery | Cyberpunk | Drama",
            category = "TV Shows",
            matchScore = 97,
            description = "A clandestine investigation into vanishing virtual reality designers leads to a city beneath the dunes.",
            cast = "Asser Yassin, Tara Emad",
            posterRes = R.drawable.img_poster_cyberpunk
        ),
        MediaItem(
            id = "orig_3",
            title = "Redline Beirut",
            titleArabic = "بيروت الحمراء",
            subtitleBadge = "Top 05 in Movies",
            episodesFreeBadge = "VIP Premier",
            year = "2026",
            seasonsOrDuration = "1h 58m",
            maturityRating = "18+",
            qualityTag = "Dolby Atmos",
            genresText = "Heist | Crime | High Octane",
            category = "Movies",
            matchScore = 95,
            description = "An elite crew attempts a high-stakes maritime vault heist along the Mediterranean coast.",
            cast = "Karim Abdel Aziz, Nelly Karim",
            posterRes = R.drawable.img_poster_action
        )
    )

    val trendingNow = listOf(
        MediaItem(
            id = "trend_1",
            title = "Midnight Odyssey",
            titleArabic = "رحلة منتصف الليل",
            subtitleBadge = "#2 in Trending",
            episodesFreeBadge = "Watch for Free",
            year = "2026",
            seasonsOrDuration = "3 Seasons",
            maturityRating = "16+",
            qualityTag = "4K Ultra HD",
            genresText = "Drama | Supernatural | Fantasy",
            category = "TV Shows",
            matchScore = 93,
            description = "An ancient artifact discovered in Upper Egypt unlocks nocturnal visions in seven strangers.",
            cast = "Ahmed Dawood, Mayan El Sayed",
            posterRes = R.drawable.img_hero_poster
        ),
        MediaItem(
            id = "trend_2",
            title = "Echoes of Syndicate",
            titleArabic = "أصداء النقابة",
            subtitleBadge = "#3 in Movies",
            episodesFreeBadge = "4K HDR Free",
            year = "2026",
            seasonsOrDuration = "2h 05m",
            maturityRating = "16+",
            qualityTag = "4K HDR",
            genresText = "Action | Thriller | Crime",
            category = "Movies",
            matchScore = 91,
            description = "A tactical operator defends a high-value whistleblower against ruthless global syndicates.",
            cast = "Amr Youssef, Dhaffer L'Abidine",
            posterRes = R.drawable.img_poster_action
        ),
        MediaItem(
            id = "trend_3",
            title = "Nexus Horizon",
            titleArabic = "أفق نيكسوس",
            subtitleBadge = "Global Hit",
            episodesFreeBadge = "All Episodes Available",
            year = "2025",
            seasonsOrDuration = "2 Seasons",
            maturityRating = "13+",
            qualityTag = "HDR10+",
            genresText = "Sci-Fi | Adventure | Family",
            category = "TV Shows",
            matchScore = 98,
            description = "A generation starship encounters a sentient celestial gateway on the edge of the galaxy.",
            cast = "Salma Abu Deif, Nour",
            posterRes = R.drawable.img_poster_cyberpunk
        )
    )

    val sportsAndLiveList = listOf(
        MediaItem(
            id = "sport_1",
            title = "Champions League: Semi-Final",
            titleArabic = "دوري أبطال أوروبا",
            subtitleBadge = "LIVE in 4K",
            episodesFreeBadge = "Live Event",
            year = "2026",
            seasonsOrDuration = "LIVE NOW",
            maturityRating = "All Ages",
            qualityTag = "Ultra HD 60fps",
            genresText = "Sports | Football | Championship",
            category = "Sports",
            matchScore = 99,
            description = "Live high-octane football clash with multi-angle cameras, real-time statistics, and Dolby surround commentary.",
            cast = "Commentary: Issam Chaouali",
            posterRes = R.drawable.img_poster_action
        ),
        MediaItem(
            id = "sport_2",
            title = "Pulse Sound Sessions",
            titleArabic = "جلسات نبض الموسيقى",
            subtitleBadge = "Hi-Res Lossless Audio",
            episodesFreeBadge = "Full Album Free",
            year = "2026",
            seasonsOrDuration = "45 Min",
            maturityRating = "All Ages",
            qualityTag = "Spatial Audio",
            genresText = "Listen | Acoustic | Live Concert",
            category = "Listen",
            matchScore = 97,
            description = "Intimate studio acoustic sessions featuring legendary artists performing reimagined orchestral arrangements.",
            cast = "Orchestra of Cairo & Special Guests",
            posterRes = R.drawable.img_hero_poster
        )
    )

    val liveChannels = listOf(
        LiveChannel("ch_1", "01", "Pulse Cinema HD", "El Sitt Oumour (S2 E04)", "Midnight Odyssey", "342K Watching", "Cinema"),
        LiveChannel("ch_2", "02", "Pulse Sports 1 UHD", "Live: UEFA Champions League", "Sports Pulse Post-Match", "1.2M Watching", "Sports"),
        LiveChannel("ch_3", "03", "Pulse Documentaries", "Wild Nile: Secret Predators", "Cosmic Horizons 4K", "185K Watching", "Documentary"),
        LiveChannel("ch_4", "04", "Pulse Music Hits", "Top 20 Arabian Pop Countdown", "Late Night Vinyl Sessions", "220K Watching", "Music"),
        LiveChannel("ch_5", "05", "Pulse Action One", "Shadow Protocol: Extended Cut", "Cyber Matrix Reloaded", "510K Watching", "Action")
    )

    val shortsFeed = listOf(
        ShortVideo(
            id = "sh_1",
            title = "Behind the Scenes: That jaw-dropping palace climax in El Sitt Oumour! 🎬✨",
            creatorName = "Video Pulse Studios",
            handle = "@videopulse_official",
            soundTrack = "Original Score • El Sitt Oumour Theme",
            likesCount = "142.8K",
            commentsCount = "3,210",
            sharesCount = "18.4K",
            tag = "BehindTheScenes",
            posterRes = R.drawable.img_hero_poster
        ),
        ShortVideo(
            id = "sh_2",
            title = "Unreal stunt choreography with zero CGI in Shadow Protocol 🔥⚡",
            creatorName = "Pulse Action Hub",
            handle = "@pulse_action",
            soundTrack = "High Adrenaline Synth • Pulse Records",
            likesCount = "289.4K",
            commentsCount = "5,420",
            sharesCount = "41.2K",
            tag = "ActionStunts",
            posterRes = R.drawable.img_poster_action
        ),
        ShortVideo(
            id = "sh_3",
            title = "Cyber Pulse 2088: Building holographic Cairo 🌆 Future vision!",
            creatorName = "VFX Masterclass",
            handle = "@vfxpulse",
            soundTrack = "Synthwave Neon Dream • Cairo 2088",
            likesCount = "95.1K",
            commentsCount = "1,880",
            sharesCount = "12.3K",
            tag = "SciFiArt",
            posterRes = R.drawable.img_poster_cyberpunk
        )
    )

    val categories = listOf("Videos", "Movies", "Listen", "Sports", "Watch for Free")

    val defaultLocalVideos = listOf(
        LocalDeviceVideo(
            id = "loc_1",
            title = "El_Sitt_Oumour_S01E01_4K.mp4",
            durationText = "48:12",
            sizeText = "1.24 GB",
            resolutionText = "4K Ultra HD",
            dateAddedText = "Downloaded Today",
            thumbnailRes = R.drawable.img_hero_poster
        ),
        LocalDeviceVideo(
            id = "loc_2",
            title = "Cyber_Pulse_Teaser_Final.mp4",
            durationText = "03:45",
            sizeText = "182 MB",
            resolutionText = "1080p 60fps",
            dateAddedText = "Yesterday",
            thumbnailRes = R.drawable.img_poster_cyberpunk
        ),
        LocalDeviceVideo(
            id = "loc_3",
            title = "Shadow_Protocol_Action_Scene.mp4",
            durationText = "12:30",
            sizeText = "420 MB",
            resolutionText = "4K HDR",
            dateAddedText = "2 days ago",
            thumbnailRes = R.drawable.img_poster_action
        ),
        LocalDeviceVideo(
            id = "loc_4",
            title = "Camera_Video_20261104.mp4",
            durationText = "01:15",
            sizeText = "68 MB",
            resolutionText = "1080p FHD",
            dateAddedText = "Device Storage",
            thumbnailRes = R.drawable.img_hero_poster
        )
    )
}

data class LocalDeviceVideo(
    val id: String,
    val title: String,
    val durationText: String,
    val sizeText: String,
    val resolutionText: String = "1080p FHD",
    val dateAddedText: String = "Today",
    val uriString: String? = null,
    val thumbnailRes: Int = R.drawable.img_hero_poster,
    val isFromGallery: Boolean = false
)
