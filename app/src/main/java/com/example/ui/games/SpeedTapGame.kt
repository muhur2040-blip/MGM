package com.example.ui.games

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TouchApp
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentRose
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import kotlinx.coroutines.delay

import com.example.ui.components.InGameHeader
import com.example.ui.components.RewardSavedDialog

@Composable
fun SpeedTapGame(
    onBack: () -> Unit,
    onGameFinished: (score: Int, coinsWon: Int) -> Unit,
    walletCoins: Long = 0L,
    onOpenWallet: () -> Unit = {}
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var tapCount by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(10) }
    var tapScale by remember { mutableFloatStateOf(1f) }
    var showSavedDialog by remember { mutableStateOf(false) }
    var lastSavedCoins by remember { mutableIntStateOf(0) }

    fun triggerTapHaptic() {
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(20)
            }
        } catch (_: Exception) {}
    }

    val handleBackWithAutoSave = {
        if (isGameOver && tapCount > 0) {
            val coinsWon = (tapCount * 1.5).toInt().coerceAtLeast(20)
            onGameFinished(tapCount, coinsWon)
            isGameOver = false
            tapCount = 0
        }
        onBack()
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            timeLeft = 10
            while (timeLeft > 0) {
                delay(1000)
                timeLeft -= 1
            }
            isPlaying = false
            isGameOver = true
            val coinsWon = (tapCount * 1.5).toInt().coerceAtLeast(20)
            lastSavedCoins = coinsWon
            onGameFinished(tapCount, coinsWon) // Auto-save immediately to DB & Prefs!
        }
    }

    val animatedScale by animateFloatAsState(
        targetValue = tapScale,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 600f),
        label = "tap_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // In-Game Header with Live Wallet Balance
        InGameHeader(
            title = "SPEED TAPPER",
            subtitle = "Test your reflexes",
            walletCoins = walletCoins,
            onBack = handleBackWithAutoSave,
            onOpenWallet = onOpenWallet,
            trailingBadge = {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Time",
                            tint = AccentRose,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${timeLeft}s",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = AccentRose
                        )
                    }
                }
            }
        )
        // Progress bar for time
        LinearProgressIndicator(
            progress = { (timeLeft / 10f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (timeLeft <= 3) AccentRose else SecondaryCyan,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isGameOver) {
            val coinsWon = (tapCount * 1.5).toInt().coerceAtLeast(20)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(2.dp, AccentGold)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Trophy",
                        tint = AccentGold,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "TIME'S UP!",
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Total Taps: $tapCount",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryCyan
                    )
                    Text(
                        text = "+$coinsWon Coins Earned!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = AccentGold
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            showSavedDialog = true
                            isGameOver = false
                            tapCount = 0
                            timeLeft = 10
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("claim_speed_tap_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = "Save to Wallet",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SAVE TO WALLET",
                                color = Color.Black,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        } else if (!isPlaying) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Bolt",
                    tint = SecondaryCyan,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Tap as fast as you can in 10 seconds!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "More taps = Higher coins reward multiplier",
                    fontSize = 13.sp,
                    color = AccentGold
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        isPlaying = true
                        tapCount = 0
                        timeLeft = 10
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryCyan),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(56.dp)
                        .testTag("start_speed_tap_btn")
                ) {
                    Text(
                        text = "START RUSH!",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }
        } else {
            // Active tapping area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$tapCount",
                    fontSize = 58.sp,
                    fontWeight = FontWeight.Black,
                    color = if (tapCount > 40) AccentRose else AccentGold
                )
                Text(
                    text = if (tapCount > 50) "ULTRA COMBO! 🔥🔥" else if (tapCount > 30) "FRENZY! 🔥" else "TAP TAP TAP!",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SecondaryCyan
                )

                Spacer(modifier = Modifier.height(28.dp))

                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .scale(animatedScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(SecondaryCyan, PrimaryIndigo, Color(0xFF0F172A))
                            )
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            tapCount += 1
                            tapScale = 0.88f
                            triggerTapHaptic()
                        }
                        .testTag("speed_tap_circle_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = "Tap",
                            tint = Color.White,
                            modifier = Modifier.size(54.dp)
                        )
                        Text(
                            text = "SMASH!",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(tapScale) {
        if (tapScale < 1f) {
            delay(50)
            tapScale = 1f
        }
    }

    // Points Saved in Wallet Confirmation Dialog
    RewardSavedDialog(
        isVisible = showSavedDialog,
        coinsWon = lastSavedCoins,
        gameTitle = "Speed Tapper",
        totalWalletCoins = walletCoins,
        onViewWallet = onOpenWallet,
        onPlayAgain = {
            showSavedDialog = false
            isPlaying = true
            tapCount = 0
            timeLeft = 10
            isGameOver = false
        },
        onDismiss = { showSavedDialog = false }
    )
}
