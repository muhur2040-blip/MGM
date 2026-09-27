package com.example.ui.games

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGoldDark
import com.example.ui.theme.AccentRose
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import kotlin.random.Random
import com.example.ui.components.InGameHeader
import com.example.ui.components.RewardSavedDialog

@Composable
fun ScratchCardGame(
    onBack: () -> Unit,
    onRewardEarned: (coins: Int) -> Unit,
    walletCoins: Long = 0L,
    onOpenWallet: () -> Unit = {}
) {
    val context = LocalContext.current
    var cardsLeft by remember { mutableIntStateOf(3) }
    var currentPrize by remember { mutableIntStateOf(listOf(80, 150, 250, 500, 300).random()) }
    var scratchedPercent by remember { mutableFloatStateOf(0f) }
    var isFullyRevealed by remember { mutableStateOf(false) }
    var showSavedDialog by remember { mutableStateOf(false) }
    var lastSavedCoins by remember { mutableIntStateOf(0) }

    val scratchPoints = remember { mutableStateListOf<Offset>() }

    fun triggerVibe() {
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(15)
            }
        } catch (_: Exception) {}
    }

    fun startNextCard() {
        if (cardsLeft > 0) {
            cardsLeft -= 1
            currentPrize = listOf(100, 150, 250, 400, 500).random()
            scratchPoints.clear()
            scratchedPercent = 0f
            isFullyRevealed = false
        }
    }

    val handleBackWithAutoSave = {
        if (isFullyRevealed) {
            onRewardEarned(currentPrize)
            isFullyRevealed = false
        }
        onBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // In-Game Header with Live Wallet Balance
        InGameHeader(
            title = "LUCKY SCRATCH",
            subtitle = "Scratch & Reveal Instant Prize",
            walletCoins = walletCoins,
            onBack = handleBackWithAutoSave,
            onOpenWallet = onOpenWallet,
            trailingBadge = {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(
                        text = "$cardsLeft Cards",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = SecondaryCyan
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Scratch Card Container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = androidx.compose.foundation.BorderStroke(2.dp, AccentGold)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Background Underlying Prize Layer
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Prize",
                        tint = AccentGold,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "CONGRATULATIONS!",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = AccentEmerald
                    )
                    Text(
                        text = "+$currentPrize COINS",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp,
                        color = AccentGold
                    )
                    Text(
                        text = "Muhur Developer Mystery Prize",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Scratch Overlay Surface
                if (!isFullyRevealed) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(22.dp))
                            .pointerInput(Unit) {
                                detectDragGestures { change, _ ->
                                    change.consume()
                                    scratchPoints.add(change.position)
                                    if (scratchPoints.size % 4 == 0) {
                                        triggerVibe()
                                    }
                                    scratchedPercent = (scratchPoints.size / 60f).coerceIn(0f, 1f)
                                    if (scratchedPercent >= 0.5f && !isFullyRevealed) {
                                        isFullyRevealed = true
                                        lastSavedCoins = currentPrize
                                        onRewardEarned(currentPrize) // Auto-save immediately to DB & Prefs!
                                        triggerVibe()
                                    }
                                }
                            }
                            .testTag("scratch_canvas")
                    ) {
                        // Scratch Card Coating (Silver/Gold metallic pattern)
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF64748B), Color(0xFF94A3B8), Color(0xFF475569))
                            )
                        )

                        // Erase scratched paths
                        scratchPoints.forEach { point ->
                            drawCircle(
                                color = Color.Transparent,
                                radius = 45f,
                                center = point,
                                blendMode = BlendMode.Clear
                            )
                        }
                    }

                    // Scratch helper prompt
                    if (scratchPoints.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.55f))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "👆 Rub finger here to scratch!",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        if (isFullyRevealed) {
            Button(
                onClick = {
                    showSavedDialog = true
                    startNextCard()
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(54.dp)
                    .testTag("claim_scratch_btn")
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
                        text = "SAVE +$currentPrize COINS TO WALLET",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            Text(
                text = "Scratch progress: ${(scratchedPercent * 100).toInt()}% / 50% to uncover",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Points Saved in Wallet Confirmation Dialog
        RewardSavedDialog(
            isVisible = showSavedDialog,
            coinsWon = lastSavedCoins,
            gameTitle = "Lucky Scratch",
            totalWalletCoins = walletCoins,
            onViewWallet = onOpenWallet,
            onPlayAgain = {
                showSavedDialog = false
            },
            onDismiss = { showSavedDialog = false }
        )
    }
}
