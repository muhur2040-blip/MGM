package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DevFeedback
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentRose
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan

data class DevGameProject(
    val title: String,
    val genre: String,
    val techStack: String,
    val status: String,
    val description: String,
    val badgeColor: Color
)

@Composable
fun DevStudioScreen(
    feedbackList: List<DevFeedback>,
    onSubmitFeedback: (gameName: String, rating: Int, text: String, category: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val devGames = listOf(
        DevGameProject(
            title = "Cyber Dash 2040",
            genre = "Sci-Fi Infinite Runner",
            techStack = "Jetpack Compose & Canvas Physics",
            status = "v1.2 Live Beta",
            description = "High-speed reflex runner across neon skyscrapers with cyber powerups and synthwave soundtrack.",
            badgeColor = AccentEmerald
        ),
        DevGameProject(
            title = "Neon Strike RPG",
            genre = "Tactical Cyber Battler",
            techStack = "Kotlin Coroutines & StateFlow",
            status = "In Development",
            description = "Turn-based tactical squad battler set in futuristic Neo-Tokyo with deep equipment crafting.",
            badgeColor = SecondaryCyan
        ),
        DevGameProject(
            title = "Pixel Dungeon Rush",
            genre = "Roguelike Retro Crawler",
            techStack = "Modern Android & Room Persistence",
            status = "Alpha Playtest",
            description = "Procedural dungeon delve with randomized loot, boss encounters, and permadeath thrill.",
            badgeColor = PrimaryIndigo
        )
    )

    var selectedGame by remember { mutableStateOf("Cyber Dash 2040") }
    var selectedCategory by remember { mutableStateOf("FEATURE_IDEA") }
    var rating by remember { mutableIntStateOf(5) }
    var feedbackText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dev_studio_screen_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Developer Profile Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dev_profile_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryIndigo.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF1E1B4B),
                                    Color(0xFF0F172A)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(PrimaryIndigo, SecondaryCyan)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Terminal,
                                    contentDescription = "Developer",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Muhur Game Studio",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = AccentEmerald.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "VERIFIED DEV",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AccentEmerald,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Lead Developer: Muhur",
                                    fontSize = 13.sp,
                                    color = SecondaryCyan,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = "Email",
                                        tint = AccentGold,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "muhur2040@gmail.com",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentGold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Welcome to my game developer hub! I design and code interactive mobile games, rewarding arcade mini-games, and player-first experiences on Android. Inquiries, partnership proposals, and feedback are always welcome directly at muhur2040@gmail.com.",
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:muhur2040@gmail.com")
                                    putExtra(Intent.EXTRA_SUBJECT, "Muhur Games Inquiry / Collab")
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("contact_muhur_btn")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "EMAIL DEVELOPER (muhur2040@gmail.com)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Developer Portfolio
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "GAMES BY MUHUR",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Studio Portfolio",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(devGames) { game ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = game.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = game.badgeColor.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, game.badgeColor.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = game.status,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = game.badgeColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Genre: ${game.genre}  •  ${game.techStack}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = game.description,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Beta Tester Feedback & Earn Coins Form
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("beta_feedback_form"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "BETA TESTER FEEDBACK",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Help Muhur improve games & earn instant coins",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AccentGold.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold)
                        ) {
                            Text(
                                text = "+200 COINS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = AccentGold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "1. Select Game / Project:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Cyber Dash", "Speed Tapper", "Neon Strike", "Arcade Hub").forEach { name ->
                            FilterChip(
                                selected = selectedGame == name,
                                onClick = { selectedGame = name },
                                label = { Text(name, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryIndigo,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "2. Feedback Category:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Triple("FEATURE_IDEA", "Idea", Icons.Default.Lightbulb),
                            Triple("BUG_REPORT", "Bug", Icons.Default.BugReport),
                            Triple("GENERAL_REVIEW", "Review", Icons.Default.RateReview)
                        ).forEach { (id, label, icon) ->
                            FilterChip(
                                selected = selectedCategory == id,
                                onClick = { selectedCategory = id },
                                leadingIcon = {
                                    Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(14.dp))
                                },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SecondaryCyan,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "3. Rating:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        for (i in 1..5) {
                            IconButton(
                                onClick = { rating = i },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Star $i",
                                    tint = AccentGold,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = feedbackText,
                        onValueChange = { feedbackText = it },
                        placeholder = { Text("What did you enjoy or what should Muhur add to this game?", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("feedback_text_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentGold
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (feedbackText.isNotBlank()) {
                                onSubmitFeedback(selectedGame, rating, feedbackText.trim(), selectedCategory)
                                feedbackText = ""
                            }
                        },
                        enabled = feedbackText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_feedback_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SEND TO MUHUR (+200 COINS)",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }

        // Google Play Store Publishing Guide Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("play_store_guide_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131F33)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AccentGold)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AccentGold.copy(alpha = 0.2f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Shop,
                                    contentDescription = "Google Play",
                                    tint = AccentGold,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "GOOGLE PLAY STORE PUBLISHING",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = AccentGold
                            )
                            Text(
                                text = "Release & Store Upload Checklist",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SecondaryCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Muhur Games is fully compliant with Google Play Developer Program policies (Zero broad storage permissions, Android 15 / API Level 36 Ready, Unique Application ID, and 100% USD-only global rewards). Follow these steps to publish:",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    listOf(
                        "1️⃣ Export Android App Bundle (.aab):" to "Click 'Export' or 'Download APK / AAB' in the AI Studio top bar settings. Google Play requires an Android App Bundle (.aab) for new releases.",
                        "2️⃣ Open Google Play Console:" to "Log in with your Google Developer account (muhur2040@gmail.com) at play.google.com/console.",
                        "3️⃣ Create App:" to "Register 'Muhur Games', set default language to English (US), and select 'Free' game/app.",
                        "4️⃣ Upload AAB Release:" to "In Production or Closed Testing, upload the exported .aab bundle and enter release notes.",
                        "5️⃣ Store Listing & Rollout:" to "Provide the 512x512 icon, screenshots, description, and submit for review.",
                        "6️⃣ Developer Contact Email:" to "Confirm your official support email is set to muhur2040@gmail.com in the store listing settings."
                    ).forEach { (stepTitle, stepDesc) ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(
                                text = stepTitle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = stepDesc,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 15.sp,
                                modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/console"))
                            try {
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("open_play_console_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shop,
                            contentDescription = "Console",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "OPEN GOOGLE PLAY CONSOLE",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        // Studio Roadmap
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = "Roadmap",
                            tint = SecondaryCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MUHUR STUDIO ROADMAP 2026",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    listOf(
                        "Q1: Launch Global Multiplayer Tournaments & Clash Arena",
                        "Q2: Release Cyber Dash 2040 on Google Play with PvP ghost racing",
                        "Q3: Introduce Clan/Guild coin pools & weekly community payouts",
                        "Q4: Cross-platform PC/Steam release of Neon Strike RPG"
                    ).forEach { milestone ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("⚡", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = milestone,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
