package com.example.ui.games

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.EmojiEvents
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.InGameHeader
import com.example.ui.components.RewardSavedDialog
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGoldDark
import com.example.ui.theme.AccentRose
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.PrimaryIndigoDark
import com.example.ui.theme.SecondaryCyan
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

data class WheelSlice(
    val prize: Int,
    val label: String,
    val color: Color
)

@Composable
fun SpinWheelGame(
    onBack: () -> Unit,
    onRewardEarned: (coins: Int) -> Unit,
    walletCoins: Long = 0L,
    onOpenWallet: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val slices = remember {
        listOf(
            WheelSlice(50, "50", Color(0xFF4F46E5)),
            WheelSlice(100, "100", Color(0xFF06B6D4)),
            WheelSlice(25, "25", Color(0xFF10B981)),
            WheelSlice(500, "500 🔥", Color(0xFFF59E0B)),
            WheelSlice(75, "75", Color(0xFF8B5CF6)),
            WheelSlice(1000, "1K 💎", Color(0xFFEC4899)),
            WheelSlice(150, "150", Color(0xFF0EA5E9)),
            WheelSlice(200, "200 ⭐", Color(0xFF14B8A6))
        )
    }

    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var wonPrize by remember { mutableStateOf<WheelSlice?>(null) }
    var spinsToday by remember { mutableIntStateOf(5) }
    var showSavedDialog by remember { mutableStateOf(false) }
    var lastSavedCoins by remember { mutableIntStateOf(0) }

    fun triggerVibration() {
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(50)
            }
        } catch (_: Exception) {}
    }

    val handleBackWithAutoSave = {
        if (wonPrize != null) {
            val prize = wonPrize?.prize ?: 0
            onRewardEarned(prize)
            wonPrize = null
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
        // In-Game Header with Live Wallet Balance & Spins Badge
        InGameHeader(
            title = "LUCKY SPIN WHEEL",
            subtitle = "Muhur Studio Mini-Game",
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
                            imageVector = Icons.Default.Casino,
                            contentDescription = "Spins Left",
                            tint = SecondaryCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$spinsToday Left",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = SecondaryCyan
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Wheel Container
        Box(
            modifier = Modifier
                .size(320.dp),
            contentAlignment = Alignment.Center
        ) {
            // Animated Wheel Canvas
            Canvas(
                modifier = Modifier
                    .size(300.dp)
                    .rotate(rotationAngle)
                    .testTag("wheel_canvas")
            ) {
                val canvasSize = size.minDimension
                val radius = canvasSize / 2f
                val sliceSweep = 360f / slices.size

                slices.forEachIndexed { i, slice ->
                    val startAngle = i * sliceSweep - 90f
                    drawArc(
                        color = slice.color,
                        startAngle = startAngle,
                        sweepAngle = sliceSweep,
                        useCenter = true,
                        size = Size(canvasSize, canvasSize)
                    )

                    // Draw inner border slice
                    drawArc(
                        color = Color.White.copy(alpha = 0.25f),
                        startAngle = startAngle,
                        sweepAngle = sliceSweep,
                        useCenter = true,
                        style = Stroke(width = 2f),
                        size = Size(canvasSize, canvasSize)
                    )

                    // Native text drawing
                    val angleMidRad = Math.toRadians((startAngle + sliceSweep / 2f).toDouble())
                    val textDistance = radius * 0.65f
                    val textX = (center.x + textDistance * Math.cos(angleMidRad)).toFloat()
                    val textY = (center.y + textDistance * Math.sin(angleMidRad)).toFloat() + 10f

                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 34f
                            isFakeBoldText = true
                            textAlign = android.graphics.Paint.Align.CENTER
                            setShadowLayer(4f, 2f, 2f, android.graphics.Color.BLACK)
                        }
                        drawText(slice.label, textX, textY, paint)
                    }
                }

                // Outer Ring
                drawCircle(
                    color = AccentGoldDark,
                    radius = radius,
                    style = Stroke(width = 10f)
                )
                // Center Peg
                drawCircle(
                    color = Color(0xFF1E293B),
                    radius = 28f
                )
                drawCircle(
                    color = AccentGold,
                    radius = 18f
                )
            }

            // Top Pointer Arrow
            Canvas(
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.TopCenter)
            ) {
                val path = Path().apply {
                    moveTo(size.width / 2f, size.height)
                    lineTo(0f, 0f)
                    lineTo(size.width, 0f)
                    close()
                }
                drawPath(path = path, color = AccentRose)
                drawPath(path = path, color = Color.White, style = Stroke(width = 3f))
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Spin Button or Result Card
        if (wonPrize != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(2.dp, AccentGold)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Win",
                        tint = AccentGold,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "YOU WON!",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = AccentGold
                    )
                    Text(
                        text = "+${wonPrize?.prize} Coins",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            wonPrize = null
                            showSavedDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("claim_spin_reward_btn")
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
        } else {
            Button(
                onClick = {
                    if (!isSpinning && spinsToday > 0) {
                        isSpinning = true
                        spinsToday -= 1
                        val chosenIndex = Random.nextInt(slices.size)
                        val extraRounds = 5 + Random.nextInt(4)
                        val sliceSweep = 360f / slices.size
                        // The pointer is at top (-90 degrees / 270 degrees)
                        // A slice `i` has angle [i*sliceSweep - 90, (i+1)*sliceSweep - 90]
                        // To land on index `chosenIndex` at top:
                        val targetFinalAngle = 360f - (chosenIndex * sliceSweep + sliceSweep / 2f)
                        val targetTotalAngle = rotationAngle + (extraRounds * 360f) + (targetFinalAngle - (rotationAngle % 360f))

                        scope.launch {
                            val startTime = System.currentTimeMillis()
                            val duration = 4000L
                            val startAngle = rotationAngle
                            val deltaAngle = targetTotalAngle - startAngle

                            while (System.currentTimeMillis() - startTime < duration) {
                                val elapsed = (System.currentTimeMillis() - startTime).toFloat() / duration
                                // Ease-out cubic curve
                                val factor = 1f - Math.pow((1f - elapsed).toDouble(), 3.0).toFloat()
                                rotationAngle = startAngle + deltaAngle * factor
                                triggerVibration()
                                delay(32)
                            }
                            rotationAngle = targetTotalAngle
                            isSpinning = false
                            val landedSlice = slices[chosenIndex]
                            wonPrize = landedSlice
                            lastSavedCoins = landedSlice.prize
                            onRewardEarned(landedSlice.prize) // Auto-save immediately to DB & Prefs!
                            triggerVibration()
                        }
                    }
                },
                enabled = !isSpinning && spinsToday > 0,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(56.dp)
                    .testTag("spin_wheel_action_btn"),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentGold,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Spin",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSpinning) "SPINNING..." else "SPIN FOR COINS!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "⚡ Real physics deceleration • Instant coin rewards",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Points Saved in Wallet Confirmation Dialog
        RewardSavedDialog(
            isVisible = showSavedDialog,
            coinsWon = lastSavedCoins,
            gameTitle = "Lucky Spin Wheel",
            totalWalletCoins = walletCoins,
            onViewWallet = onOpenWallet,
            onPlayAgain = { showSavedDialog = false },
            onDismiss = { showSavedDialog = false }
        )
    }
}
