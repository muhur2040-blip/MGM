package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.local.DailyQuest
import com.example.data.local.UserWallet
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentRose
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import java.util.Calendar

@Composable
fun EarnScreen(
    wallet: UserWallet,
    quests: List<DailyQuest>,
    onClaimCheckin: () -> Unit,
    onClaimQuest: (String) -> Unit,
    onApplyReferral: (String) -> Unit,
    onWatchAd: () -> Unit = {},
    onLaunchGame: (String) -> Unit = {},
    onNavigateToWallet: () -> Unit = {},
    onNavigateToDev: () -> Unit = {},
    onAddNewTask: (title: String, description: String, targetCount: Int, rewardCoins: Int, category: String) -> Unit = { _, _, _, _, _ -> },
    onCompleteTaskManually: (String) -> Unit = {},
    onDeleteTask: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var inputReferral by remember { mutableStateOf("") }
    var copiedNotification by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") } // ALL, DAILY, ADS, SPECIAL, MILESTONE, CUSTOM
    var showAddTaskDialog by remember { mutableStateOf(false) }

    // Dialog state for new custom task
    var customTaskTitle by remember { mutableStateOf("") }
    var customTaskDesc by remember { mutableStateOf("") }
    var customTaskTarget by remember { mutableIntStateOf(1) }
    var customTaskReward by remember { mutableIntStateOf(100) }
    var customTaskCategory by remember { mutableStateOf("CUSTOM") }

    // Check if checkin claimed today
    val isClaimedToday = remember(wallet.lastCheckinDate) {
        if (wallet.lastCheckinDate == 0L) false
        else {
            val now = Calendar.getInstance()
            val last = Calendar.getInstance().apply { timeInMillis = wallet.lastCheckinDate }
            now.get(Calendar.YEAR) == last.get(Calendar.YEAR) &&
                    now.get(Calendar.DAY_OF_YEAR) == last.get(Calendar.DAY_OF_YEAR)
        }
    }

    val completedTasksCount = quests.count { it.isClaimed }
    val totalTasksCount = quests.size.coerceAtLeast(1)

    val filteredQuests = remember(quests, selectedCategoryFilter) {
        when (selectedCategoryFilter) {
            "DAILY" -> quests.filter { it.category == "DAILY" || it.category == "CHECKIN" || it.category == "GAME" }
            "ADS" -> quests.filter { it.category == "ADS" || it.id.contains("AD", ignoreCase = true) }
            "SPECIAL" -> quests.filter { it.category == "SPECIAL" || it.category == "DEV" }
            "MILESTONE" -> quests.filter { it.category == "MILESTONE" }
            "CUSTOM" -> quests.filter { it.category == "CUSTOM" }
            else -> quests
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("earn_screen_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tasks Progress & Master Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tasks_master_progress_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryIndigo.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF1E1B4B), Color(0xFF0F172A))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.TaskAlt,
                                        contentDescription = "Tasks",
                                        tint = AccentGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "TASKS & MISSIONS HUB",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = "$completedTasksCount of $totalTasksCount Tasks Completed",
                                    fontSize = 12.sp,
                                    color = SecondaryCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = { showAddTaskDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("add_custom_task_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Task",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Add Task",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { (completedTasksCount.toFloat() / totalTasksCount.toFloat()).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = AccentGold,
                            trackColor = Color(0xFF1E293B)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Earn +1,000 Coins ($1.00 USD) by clearing tasks!",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${((completedTasksCount.toFloat() / totalTasksCount.toFloat()) * 100).toInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = AccentGold
                            )
                        }
                    }
                }
            }
        }

        // Rewarded Video Ads Spotlight Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("watch_rewarded_ad_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161F30)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AccentGold.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(AccentGold, AccentRose))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.OndemandVideo,
                            contentDescription = "Watch Ads",
                            tint = Color.Black,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "WATCH VIDEO ADS",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = AccentEmerald.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "+100 COINS",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = AccentEmerald,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "5-sec sponsor video • Direct to Wallet",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onWatchAd,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(42.dp)
                            .testTag("watch_video_ad_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "WATCH",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }

        // Daily Check-in Streak Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_checkin_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold.copy(alpha = 0.5f))
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(AccentRose.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = AccentRose,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "7-DAY LOGIN STREAK",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Current Streak: ${wallet.consecutiveCheckins} Days",
                                    fontSize = 12.sp,
                                    color = AccentRose,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AccentGold.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "Day 7 = +1000",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = AccentGold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 7 Streak Days Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val streakRewards = listOf(50, 100, 150, 200, 300, 450, 1000)
                        streakRewards.forEachIndexed { index, coins ->
                            val dayNum = index + 1
                            val isPast = dayNum < wallet.consecutiveCheckins
                            val isCurrent = dayNum == wallet.consecutiveCheckins

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isPast) AccentEmerald.copy(alpha = 0.25f)
                                            else if (isCurrent) AccentGold.copy(alpha = 0.3f)
                                            else MaterialTheme.colorScheme.surface
                                        )
                                        .border(
                                            width = if (isCurrent) 1.5.dp else 1.dp,
                                            color = if (isCurrent) AccentGold else if (isPast) AccentEmerald else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                            shape = RoundedCornerShape(10.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isPast) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Done",
                                            tint = AccentEmerald,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "+$coins",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (isCurrent) AccentGold else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "D$dayNum",
                                    fontSize = 10.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isCurrent) AccentGold else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onClaimCheckin,
                        enabled = !isClaimedToday,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentGold,
                            disabledContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("claim_checkin_btn")
                    ) {
                        Text(
                            text = if (isClaimedToday) "CLAIMED FOR TODAY (Check in Tomorrow)" else "CLAIM & SAVE TO WALLET",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = if (isClaimedToday) MaterialTheme.colorScheme.onSurfaceVariant else Color.Black
                        )
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TASK CATEGORIES",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${filteredQuests.size} Available",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filterCategories = listOf(
                        "ALL" to "All Tasks",
                        "DAILY" to "Daily Games",
                        "ADS" to "Video Ads",
                        "SPECIAL" to "Special",
                        "MILESTONE" to "Milestones",
                        "CUSTOM" to "My Tasks"
                    )

                    items(filterCategories) { (key, label) ->
                        val isSelected = selectedCategoryFilter == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategoryFilter = key },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryIndigo,
                                selectedLabelColor = Color.White,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) SecondaryCyan else Color.Transparent
                            ),
                            modifier = Modifier.testTag("filter_chip_$key")
                        )
                    }
                }
            }
        }

        // Tasks List
        items(filteredQuests, key = { it.id }) { quest ->
            val canClaim = !quest.isClaimed && quest.currentCount >= quest.targetCount
            val isCustom = quest.category == "CUSTOM" || quest.id.startsWith("CUSTOM_")

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quest_card_${quest.id}"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (quest.isClaimed) AccentEmerald.copy(alpha = 0.3f)
                    else if (canClaim) AccentGold
                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Task Icon
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (quest.isClaimed) AccentEmerald.copy(alpha = 0.2f)
                                else when (quest.category) {
                                    "ADS" -> AccentGold.copy(alpha = 0.2f)
                                    "SPECIAL" -> SecondaryCyan.copy(alpha = 0.2f)
                                    "MILESTONE" -> AccentRose.copy(alpha = 0.2f)
                                    else -> PrimaryIndigo.copy(alpha = 0.2f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                quest.isClaimed -> Icons.Default.CheckCircle
                                quest.category == "ADS" || quest.id.contains("AD") -> Icons.Default.OndemandVideo
                                quest.category == "MILESTONE" -> Icons.Default.EmojiEvents
                                quest.category == "SPECIAL" -> Icons.Default.Star
                                else -> Icons.Default.TaskAlt
                            },
                            contentDescription = quest.title,
                            tint = when {
                                quest.isClaimed -> AccentEmerald
                                quest.category == "ADS" || quest.id.contains("AD") -> AccentGold
                                quest.category == "MILESTONE" -> AccentRose
                                quest.category == "SPECIAL" -> SecondaryCyan
                                else -> PrimaryIndigo
                            },
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = quest.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            if (isCustom) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = PrimaryIndigo.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "CUSTOM",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SecondaryCyan,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = quest.description,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LinearProgressIndicator(
                                progress = { (quest.currentCount.toFloat() / quest.targetCount.toFloat()).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (canClaim || quest.isClaimed) AccentEmerald else SecondaryCyan,
                                trackColor = MaterialTheme.colorScheme.surface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${quest.currentCount}/${quest.targetCount}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Action Button
                    Column(horizontalAlignment = Alignment.End) {
                        if (quest.isClaimed) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = AccentEmerald.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AccentEmerald.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = "CLAIMED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = AccentEmerald,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        } else if (canClaim) {
                            Button(
                                onClick = { onClaimQuest(quest.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("claim_quest_${quest.id}")
                            ) {
                                Text(
                                    text = "+${quest.rewardCoins}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                        } else {
                            // Quick Action Launcher Button
                            when {
                                quest.category == "ADS" || quest.id.contains("AD") -> {
                                    OutlinedButton(
                                        onClick = onWatchAd,
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.testTag("action_ad_${quest.id}")
                                    ) {
                                        Text("WATCH", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                                    }
                                }
                                quest.id == "PLAY_SPIN" -> {
                                    OutlinedButton(
                                        onClick = { onLaunchGame("SPIN_WHEEL") },
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("PLAY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SecondaryCyan)
                                    }
                                }
                                quest.id == "PLAY_TAP" -> {
                                    OutlinedButton(
                                        onClick = { onLaunchGame("SPEED_TAP") },
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("PLAY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SecondaryCyan)
                                    }
                                }
                                quest.id == "PLAY_MEMORY" -> {
                                    OutlinedButton(
                                        onClick = { onLaunchGame("MEMORY_MATCH") },
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("PLAY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SecondaryCyan)
                                    }
                                }
                                quest.id == "PLAY_SCRATCH" -> {
                                    OutlinedButton(
                                        onClick = { onLaunchGame("SCRATCH_CARD") },
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("PLAY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SecondaryCyan)
                                    }
                                }
                                quest.id == "PLAY_MATH" -> {
                                    OutlinedButton(
                                        onClick = { onLaunchGame("MATH_RUSH") },
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("PLAY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SecondaryCyan)
                                    }
                                }
                                quest.id == "DEV_FEEDBACK" -> {
                                    OutlinedButton(
                                        onClick = onNavigateToDev,
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("DEV ZONE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                                    }
                                }
                                quest.id == "SPECIAL_GPAY" -> {
                                    OutlinedButton(
                                        onClick = onNavigateToWallet,
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("GPAY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentEmerald)
                                    }
                                }
                                isCustom -> {
                                    Row {
                                        IconButton(
                                            onClick = { onDeleteTask(quest.id) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        OutlinedButton(
                                            onClick = { onCompleteTaskManually(quest.id) },
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text("FINISH", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                                        }
                                    }
                                }
                                else -> {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surface
                                    ) {
                                        Text(
                                            text = "+${quest.rewardCoins}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Referral & Invite Section with Verified Email
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("referral_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, SecondaryCyan.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SecondaryCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = "Friends",
                                tint = SecondaryCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "INVITE FRIENDS & EARN",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Earn 500 Coins for every gamer who joins",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Account Email Banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SecondaryCyan.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SecondaryCyan.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = SecondaryCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "REGISTERED GAMER & GPAY ACCOUNT",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "muhur2040@gmail.com",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // User Code Box
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "YOUR REFERRAL CODE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = wallet.referralCode,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    color = SecondaryCyan
                                )
                            }

                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Referral Code", wallet.referralCode)
                                    clipboard.setPrimaryClip(clip)
                                    copiedNotification = true
                                },
                                modifier = Modifier.testTag("copy_referral_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = SecondaryCyan
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Enter friend's referral code
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = inputReferral,
                            onValueChange = { inputReferral = it },
                            placeholder = { Text("Enter Friend's Code", fontSize = 12.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SecondaryCyan,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("friend_referral_input")
                        )

                        Button(
                            onClick = {
                                if (inputReferral.isNotBlank()) {
                                    onApplyReferral(inputReferral)
                                    inputReferral = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryCyan),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("apply_referral_btn")
                        ) {
                            Text(
                                text = "APPLY",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.Black
                            )
                        }
                    }

                    if (copiedNotification) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "✓ Referral code copied to clipboard!",
                            fontSize = 11.sp,
                            color = AccentEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Add Custom Task Dialog
    if (showAddTaskDialog) {
        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = {
                Text(
                    text = "Add Custom Task / Goal",
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = Color.White
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Set a custom gaming challenge or personal milestone and earn bonus coins when you finish!",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = customTaskTitle,
                        onValueChange = { customTaskTitle = it },
                        label = { Text("Task Title (e.g. Win 3 Speed Tap games)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_task_title_input")
                    )

                    OutlinedTextField(
                        value = customTaskDesc,
                        onValueChange = { customTaskDesc = it },
                        label = { Text("Description (Optional)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = customTaskTarget.toString(),
                            onValueChange = {
                                customTaskTarget = it.toIntOrNull()?.coerceIn(1, 100) ?: 1
                            },
                            label = { Text("Target Count") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = customTaskReward.toString(),
                            onValueChange = {
                                customTaskReward = it.toIntOrNull()?.coerceIn(20, 1000) ?: 100
                            },
                            label = { Text("Reward Coins") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customTaskTitle.isNotBlank()) {
                            onAddNewTask(
                                customTaskTitle,
                                if (customTaskDesc.isNotBlank()) customTaskDesc else "Custom player challenge",
                                customTaskTarget,
                                customTaskReward,
                                "CUSTOM"
                            )
                            customTaskTitle = ""
                            customTaskDesc = ""
                            showAddTaskDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("save_custom_task_btn")
                ) {
                    Text("ADD TASK", fontWeight = FontWeight.Black, color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) {
                    Text("CANCEL", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = Color(0xFF1E1B4B),
            shape = RoundedCornerShape(22.dp)
        )
    }
}
