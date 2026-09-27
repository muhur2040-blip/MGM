package com.example.ui.games

import com.example.ui.components.InGameHeader
import com.example.ui.components.RewardSavedDialog
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
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
import kotlin.random.Random

data class MathQuestion(
    val questionText: String,
    val correctAnswer: Int,
    val options: List<Int>
)

fun generateMathQuestion(): MathQuestion {
    val type = Random.nextInt(3)
    val a: Int
    val b: Int
    val answer: Int
    val text: String

    when (type) {
        0 -> { // Addition
            a = Random.nextInt(10, 60)
            b = Random.nextInt(5, 50)
            answer = a + b
            text = "$a + $b = ?"
        }
        1 -> { // Subtraction
            a = Random.nextInt(30, 99)
            b = Random.nextInt(5, a)
            answer = a - b
            text = "$a - $b = ?"
        }
        else -> { // Multiplication
            a = Random.nextInt(3, 12)
            b = Random.nextInt(3, 12)
            answer = a * b
            text = "$a × $b = ?"
        }
    }

    val options = mutableSetOf(answer)
    while (options.size < 4) {
        val delta = listOf(-10, -5, -2, -1, 1, 2, 5, 10).random()
        val fake = (answer + delta).coerceAtLeast(1)
        options.add(fake)
    }

    return MathQuestion(text, answer, options.toList().shuffled())
}

@Composable
fun MathRushGame(
    onBack: () -> Unit,
    onGameFinished: (score: Int, coinsWon: Int) -> Unit,
    walletCoins: Long = 0L,
    onOpenWallet: () -> Unit = {}
) {
    var round by remember { mutableIntStateOf(1) }
    var currentQuestion by remember { mutableStateOf(generateMathQuestion()) }
    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(5) }
    var isGameOver by remember { mutableStateOf(false) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var streak by remember { mutableIntStateOf(0) }
    var showSavedDialog by remember { mutableStateOf(false) }
    var lastSavedCoins by remember { mutableIntStateOf(0) }

    val handleBackWithAutoSave = {
        if (isGameOver && score > 0) {
            val coinsWon = (score * 35).coerceAtLeast(30)
            onGameFinished(score, coinsWon)
            isGameOver = false
        }
        onBack()
    }

    LaunchedEffect(round, isGameOver) {
        if (!isGameOver) {
            timeLeft = 5
            selectedAnswer = null
            while (timeLeft > 0 && selectedAnswer == null) {
                delay(1000)
                timeLeft -= 1
            }
            if (selectedAnswer == null) {
                // Time expired for this question
                streak = 0
                if (round < 5) {
                    round += 1
                    currentQuestion = generateMathQuestion()
                } else {
                    isGameOver = true
                }
            }
        }
    }

    LaunchedEffect(isGameOver) {
        if (isGameOver) {
            val coinsWon = ((score * 40) + (streak * 10)).coerceAtLeast(30)
            lastSavedCoins = coinsWon
            onGameFinished(score, coinsWon) // Auto-save immediately to DB & Prefs!
        }
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
            title = "MATH RUSH",
            subtitle = "Round $round / 5 • Streak: $streak 🔥",
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
                        text = "${timeLeft}s",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (timeLeft <= 2) AccentRose else AccentGold
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = { (timeLeft / 5f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (timeLeft <= 2) AccentRose else PrimaryIndigo,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isGameOver) {
            val coinsWon = (score * 35).coerceAtLeast(30)
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
                        text = "MATH CHALLENGE COMPLETE!",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Correct Answers: $score / 5",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryCyan
                    )
                    Text(
                        text = "+$coinsWon Coins Won!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = AccentGold
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            showSavedDialog = true
                            round = 1
                            score = 0
                            streak = 0
                            isGameOver = false
                            currentQuestion = generateMathQuestion()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("claim_math_reward_btn")
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
            // Question Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryIndigo)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Brain",
                            tint = SecondaryCyan,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentQuestion.questionText,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4 Options Grid (2x2)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    currentQuestion.options.take(2).forEach { option ->
                        val isCorrect = option == currentQuestion.correctAnswer
                        val isChosen = selectedAnswer == option
                        val btnColor = if (isChosen) {
                            if (isCorrect) AccentEmerald else AccentRose
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }

                        Button(
                            onClick = {
                                if (selectedAnswer == null) {
                                    selectedAnswer = option
                                    if (isCorrect) {
                                        score += 1
                                        streak += 1
                                    } else {
                                        streak = 0
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp)
                                .testTag("math_option_$option"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = btnColor),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Text(
                                text = "$option",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    currentQuestion.options.drop(2).forEach { option ->
                        val isCorrect = option == currentQuestion.correctAnswer
                        val isChosen = selectedAnswer == option
                        val btnColor = if (isChosen) {
                            if (isCorrect) AccentEmerald else AccentRose
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }

                        Button(
                            onClick = {
                                if (selectedAnswer == null) {
                                    selectedAnswer = option
                                    if (isCorrect) {
                                        score += 1
                                        streak += 1
                                    } else {
                                        streak = 0
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp)
                                .testTag("math_option_$option"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = btnColor),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Text(
                                text = "$option",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Move to next question after small delay when answered
            LaunchedEffect(selectedAnswer) {
                if (selectedAnswer != null) {
                    delay(700)
                    if (round < 5) {
                        round += 1
                        currentQuestion = generateMathQuestion()
                    } else {
                        isGameOver = true
                    }
                }
            }
        }

        // Points Saved in Wallet Confirmation Dialog
        RewardSavedDialog(
            isVisible = showSavedDialog,
            coinsWon = lastSavedCoins,
            gameTitle = "Math Rush",
            totalWalletCoins = walletCoins,
            onViewWallet = onOpenWallet,
            onPlayAgain = {
                showSavedDialog = false
                round = 1
                score = 0
                streak = 0
                isGameOver = false
                currentQuestion = generateMathQuestion()
            },
            onDismiss = { showSavedDialog = false }
        )
    }
}
