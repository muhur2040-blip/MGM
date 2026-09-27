package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import kotlinx.coroutines.delay

@Composable
fun RewardedAdDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onAdCompleted: () -> Unit
) {
    if (!isOpen) return

    val totalDurationSeconds = 5
    var secondsLeft by remember { mutableIntStateOf(totalDurationSeconds) }
    var isMuted by remember { mutableStateOf(false) }
    var rewardReady by remember { mutableStateOf(false) }
    var showEarlyExitWarning by remember { mutableStateOf(false) }

    LaunchedEffect(isOpen) {
        secondsLeft = totalDurationSeconds
        rewardReady = false
        showEarlyExitWarning = false
        while (secondsLeft > 0) {
            delay(1000L)
            secondsLeft -= 1
        }
        rewardReady = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = ((totalDurationSeconds - secondsLeft).toFloat() / totalDurationSeconds.toFloat()),
        animationSpec = tween(durationMillis = 300),
        label = "ad_progress"
    )

    Dialog(
        onDismissRequest = {
            if (rewardReady) {
                onDismiss()
            } else {
                showEarlyExitWarning = true
            }
        },
        properties = DialogProperties(dismissOnBackPress = rewardReady, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("rewarded_ad_dialog"),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (rewardReady) AccentGold else SecondaryCyan.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "SPONSORED VIDEO AD",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = SecondaryCyan
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { isMuted = !isMuted },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                contentDescription = "Audio Toggle",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Surface(
                            shape = CircleShape,
                            color = if (rewardReady) AccentGold.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f)
                        ) {
                            IconButton(
                                onClick = {
                                    if (rewardReady) {
                                        onAdCompleted()
                                    } else {
                                        showEarlyExitWarning = true
                                    }
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("close_ad_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Ad",
                                    tint = if (rewardReady) AccentGold else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sponsor Media Showcase Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF2E1065),
                                        Color(0xFF0F172A)
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(SecondaryCyan, PrimaryIndigo))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Video Ad Playing",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Neon Strike 2040: Cyber RPG",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "Next-Gen Tactical Battler by Muhur Studio",
                                fontSize = 12.sp,
                                color = SecondaryCyan,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(5) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Star",
                                        tint = AccentGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "4.9 ★ (120K Gamers)",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Progress & Countdown
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (rewardReady) AccentGold else SecondaryCyan,
                    trackColor = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Coins",
                            tint = AccentGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Reward: +100 Coins ($0.10 USD)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentGold
                        )
                    }

                    Text(
                        text = if (rewardReady) "REWARD READY ✓" else "Reward in ${secondsLeft}s",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = if (rewardReady) AccentEmerald else Color.White.copy(alpha = 0.8f)
                    )
                }

                if (showEarlyExitWarning && !rewardReady) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "⚠️ Watch the full 5 seconds to claim your 100 Coins reward!",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFF87171),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Claim / Action Button
                Button(
                    onClick = {
                        if (rewardReady) {
                            onAdCompleted()
                        }
                    },
                    enabled = rewardReady,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentGold,
                        disabledContainerColor = Color(0xFF1E293B)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("claim_ad_reward_btn")
                ) {
                    Text(
                        text = if (rewardReady) "CLAIM +100 COINS & SAVE" else "WATCHING SPONSOR (${secondsLeft}s)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = if (rewardReady) Color.Black else Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}
