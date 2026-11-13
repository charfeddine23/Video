package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import com.example.ui.theme.stableStatusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PulseCyan
import com.example.ui.theme.PulseViolet
import com.example.ui.theme.TextDark
import com.example.ui.viewmodel.StreakViewModel

data class AvatarPreset(
    val id: Int,
    val nameEn: String,
    val nameAr: String,
    val icon: ImageVector,
    val gradient: List<Color>
)

val defaultAvatarPresets = listOf(
    AvatarPreset(
        id = 0,
        nameEn = "Neon Pulse",
        nameAr = "نبض النيون",
        icon = Icons.Default.Person,
        gradient = listOf(PulseCyan, PulseViolet)
    ),
    AvatarPreset(
        id = 1,
        nameEn = "Liquid Flame",
        nameAr = "اللهب المتوهج",
        icon = Icons.Default.LocalFireDepartment,
        gradient = listOf(Color(0xFFFF5722), Color(0xFFFF9800))
    ),
    AvatarPreset(
        id = 2,
        nameEn = "Cosmic Star",
        nameAr = "النجم الكوني",
        icon = Icons.Default.AutoAwesome,
        gradient = listOf(Color(0xFF7928CA), Color(0xFFFF0080))
    ),
    AvatarPreset(
        id = 3,
        nameEn = "Shield Guardian",
        nameAr = "حارس الدرع",
        icon = Icons.Default.Security,
        gradient = listOf(Color(0xFF00E5FF), Color(0xFF10B981))
    ),
    AvatarPreset(
        id = 4,
        nameEn = "VIP Prestige",
        nameAr = "عضوية VIP",
        icon = Icons.Default.WorkspacePremium,
        gradient = listOf(Color(0xFFFFD700), Color(0xFFFF8C00))
    )
)

@Composable
fun EditProfileScreen(
    streakViewModel: StreakViewModel,
    onNavigateBack: () -> Unit,
    isArabic: Boolean = false,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val currentSavedName by streakViewModel.userName.collectAsState()
    val currentSavedAvatarUri by streakViewModel.userAvatarUri.collectAsState()
    val currentSavedPreset by streakViewModel.userAvatarPreset.collectAsState()
    val uiState by streakViewModel.uiState.collectAsState()

    var nameInput by remember(currentSavedName) { mutableStateOf(currentSavedName) }
    var selectedAvatarUri by remember(currentSavedAvatarUri) { mutableStateOf(currentSavedAvatarUri) }
    var selectedPresetId by remember(currentSavedPreset) { mutableIntStateOf(currentSavedPreset) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            selectedAvatarUri = uri.toString()
        }
    }

    val currentTierBadge = streakViewModel.getUserTierBadge(uiState.streakDays, isArabic)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
            .testTag("edit_profile_screen")
    ) {
        // Ambient glass background glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color(0x2000F2FE), Color(0x107928CA), Color.Transparent),
                            radius = size.width * 0.9f
                        ),
                        center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.15f)
                    )
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .stableStatusBarsPadding()
        ) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x26FFFFFF))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = if (isArabic) "تعديل الملف الشخصي" else "Edit Profile",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Main Avatar Preview Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .border(1.dp, Color(0x2EFFFFFF), RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xD9121826))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Avatar Circle
                            val preset = defaultAvatarPresets.find { it.id == selectedPresetId }
                                ?: defaultAvatarPresets[0]

                            Box(
                                modifier = Modifier
                                    .size(108.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(preset.gradient))
                                    .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = preset.icon,
                                    contentDescription = "Profile Avatar",
                                    tint = TextDark,
                                    modifier = Modifier.size(54.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = nameInput.ifBlank { "Charfeddine Kahlessenane" },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Tier Badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0x3300F2FE),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PulseCyan.copy(alpha = 0.6f))
                            ) {
                                Text(
                                    text = currentTierBadge,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = PulseCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Pick Photo from Device Button
                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0x2600F2FE),
                                    contentColor = PulseCyan
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PulseCyan.copy(alpha = 0.5f))
                            ) {
                                Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isArabic) "إضافة صورة من الهاتف" else "Add Photo from Phone",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                // Choose Default Preset Avatar
                item {
                    Column {
                        Text(
                            text = if (isArabic) "أو اختر صورة رمزية افتراضية:" else "Or Select a Preset Avatar:",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color(0xFF94A3B8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsIndexed(defaultAvatarPresets) { _, item ->
                                val isSelected = selectedPresetId == item.id && selectedAvatarUri == null
                                Box(
                                    modifier = Modifier
                                        .size(62.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(item.gradient))
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) PulseCyan else Color(0x33FFFFFF),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            selectedPresetId = item.id
                                            selectedAvatarUri = null
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.nameEn,
                                        tint = TextDark,
                                        modifier = Modifier.size(30.dp)
                                    )
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color(0x33000000), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // User Name Input Section
                item {
                    Column {
                        Text(
                            text = if (isArabic) "اسم المستخدم" else "User Name",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color(0xFFCBD5E1),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp
                            ),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("edit_username_input"),
                            shape = RoundedCornerShape(14.dp),
                            placeholder = {
                                Text("Charfeddine Kahlessenane", color = Color(0xFF64748B))
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PulseCyan,
                                unfocusedBorderColor = Color(0x2AFFFFFF),
                                focusedContainerColor = Color(0x22121826),
                                unfocusedContainerColor = Color(0x22121826),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }
                }

                // Tier Levels Information Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .border(1.dp, Color(0x1FFFFFFF), RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xB3121826))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (isArabic) "نظام رتب الأعضاء والسترايك" else "Member Tier System",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            TierRow(
                                title = if (isArabic) "مستخدم عادي" else "Standard User",
                                requirement = if (isArabic) "السترايك حتى 20 يوماً" else "Streak up to 20 days",
                                isActive = uiState.streakDays <= 20
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            TierRow(
                                title = if (isArabic) "مستخدم مميز" else "Special User",
                                requirement = if (isArabic) "السترايك من 21 حتى 60 يوماً" else "Streak 21 to 60 days",
                                isActive = uiState.streakDays in 21..60
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            TierRow(
                                title = if (isArabic) "مستخدم مميز (VIP)" else "VIP Member",
                                requirement = if (isArabic) "السترايك من 61 حتى 180 يوماً" else "Streak 61 to 180 days",
                                isActive = uiState.streakDays in 61..180
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            TierRow(
                                title = "Vip II Member",
                                requirement = if (isArabic) "أكثر من 180 يوماً متواصلاً" else "Streak over 180 days",
                                isActive = uiState.streakDays > 180,
                                isTopTier = true
                            )
                        }
                    }
                }

                // Save Changes Button
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            streakViewModel.updateProfile(
                                name = nameInput,
                                avatarUri = selectedAvatarUri,
                                presetId = selectedPresetId
                            )
                            onNavigateBack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("save_profile_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PulseCyan,
                            contentColor = TextDark
                        )
                    ) {
                        Text(
                            text = if (isArabic) "حفظ التغييرات" else "Save Changes",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TierRow(
    title: String,
    requirement: String,
    isActive: Boolean,
    isTopTier: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isActive) (if (isTopTier) Color(0xFFFFD700) else PulseCyan) else Color(0xFF64748B))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                    color = if (isActive) (if (isTopTier) Color(0xFFFFD700) else PulseCyan) else Color(0xFFCBD5E1),
                    fontSize = 12.sp
                )
            )
        }
        Text(
            text = requirement,
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (isActive) Color.White else Color(0xFF64748B),
                fontSize = 11.sp
            )
        )
    }
}
