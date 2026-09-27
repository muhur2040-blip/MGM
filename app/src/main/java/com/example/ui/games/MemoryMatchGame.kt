package com.example.ui.games

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import kotlinx.coroutines.delay
import com.example.ui.components.InGameHeader
import com.example.ui.components.RewardSavedDialog

data class MemoryCard(
    val id: Int,
    val iconEmoji: String,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
)

@Composable
fun MemoryMatchGame(
    onBack: () -> Unit,
    onGameFinished: (moves: Int, coinsWon: Int) -> Unit,
    walletCoins: Long = 0L,
    onOpenWallet: () -> Unit = {}
) {
    val icons = remember { listOf("🎮", "💎", "🪙", "🏆", "⚡", "🚀") }

    fun generateDeck(): List<MemoryCard> {
        val list = mutableListOf<MemoryCard>()
        var id = 0
        icons.forEach { icon ->
            list.add(MemoryCard(id++, icon))
            list.add(MemoryCard(id++, icon))
        }
        return list.shuffled()
    }

    val cards = remember { mutableStateListOf<MemoryCard>().apply { addAll(generateDeck()) } }
    var moves by remember { mutableIntStateOf(0) }
    var matches by remember { mutableIntStateOf(0) }
    var firstFlippedIndex by remember { mutableStateOf<Int?>(null) }
    var isBusy by remember { mutableStateOf(false) }
    var isWon by remember { mutableStateOf(false) }
    var showSavedDialog by remember { mutableStateOf(false) }
    var lastSavedCoins by remember { mutableIntStateOf(0) }

    fun resetGame() {
        cards.clear()
        cards.addAll(generateDeck())
        moves = 0
        matches = 0
        firstFlippedIndex = null
        isBusy = false
        isWon = false
    }

    val handleBackWithAutoSave = {
        if (isWon) {
            val coinsWon = (300 - (moves * 10)).coerceAtLeast(80)
            onGameFinished(moves, coinsWon)
            isWon = false
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
            title = "MEMORY MATRIX",
            subtitle = "Pairs: $matches / 6 • Moves: $moves",
            walletCoins = walletCoins,
            onBack = handleBackWithAutoSave,
            onOpenWallet = onOpenWallet,
            trailingBadge = {
                IconButton(
                    onClick = { resetGame() },
                    modifier = Modifier.testTag("memory_reset_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart",
                        tint = SecondaryCyan
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isWon) {
            val coinsWon = (200 - (moves * 3)).coerceIn(80, 250)
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
                        contentDescription = "Win",
                        tint = AccentGold,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "PUZZLE SOLVED!",
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Completed in $moves moves",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "+$coinsWon Coins Won!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = AccentGold
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            showSavedDialog = true
                            resetGame()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("claim_memory_reward_btn")
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
            // Card Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("memory_grid")
            ) {
                itemsIndexed(cards) { index, card ->
                    val isRevealed = card.isFlipped || card.isMatched
                    val rotation by animateFloatAsState(
                        targetValue = if (isRevealed) 180f else 0f,
                        animationSpec = tween(durationMillis = 350),
                        label = "card_flip"
                    )

                    Surface(
                        modifier = Modifier
                            .aspectRatio(0.85f)
                            .graphicsLayer {
                                rotationY = rotation
                                cameraDistance = 8 * density
                            }
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(enabled = !isBusy && !card.isFlipped && !card.isMatched) {
                                if (firstFlippedIndex == null) {
                                    card.isFlipped = true
                                    firstFlippedIndex = index
                                } else if (firstFlippedIndex != index) {
                                    card.isFlipped = true
                                    moves += 1
                                    val firstIdx = firstFlippedIndex!!
                                    firstFlippedIndex = null
                                    isBusy = true

                                    if (cards[firstIdx].iconEmoji == card.iconEmoji) {
                                        cards[firstIdx].isMatched = true
                                        card.isMatched = true
                                        matches += 1
                                        isBusy = false
                                        if (matches == 6) {
                                            isWon = true
                                            val coinsWon = (200 - (moves * 3)).coerceIn(80, 250)
                                            lastSavedCoins = coinsWon
                                            onGameFinished(moves, coinsWon) // Auto-save immediately to DB & Prefs!
                                        }
                                    } else {
                                        // Wait a moment then flip back
                                        // We can launch a coroutine via LaunchedEffect or helper
                                    }
                                }
                            }
                            .testTag("memory_card_$index"),
                        shape = RoundedCornerShape(14.dp),
                        color = if (card.isMatched) AccentEmerald.copy(alpha = 0.25f)
                        else if (card.isFlipped) MaterialTheme.colorScheme.surfaceVariant
                        else PrimaryIndigo,
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (card.isMatched) AccentEmerald
                            else if (card.isFlipped) SecondaryCyan
                            else PrimaryIndigo.copy(alpha = 0.6f)
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (rotation > 90f) {
                                Text(
                                    text = card.iconEmoji,
                                    fontSize = 32.sp,
                                    modifier = Modifier.graphicsLayer { rotationY = 180f }
                                )
                            } else {
                                Text(
                                    text = "❓",
                                    fontSize = 24.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Auto flip back mismatched cards
    LaunchedEffect(isBusy) {
        if (isBusy) {
            delay(800)
            cards.forEach {
                if (!it.isMatched) it.isFlipped = false
            }
            isBusy = false
        }
    }

    // Points Saved in Wallet Confirmation Dialog
    RewardSavedDialog(
        isVisible = showSavedDialog,
        coinsWon = lastSavedCoins,
        gameTitle = "Memory Matrix",
        totalWalletCoins = walletCoins,
        onViewWallet = onOpenWallet,
        onPlayAgain = {
            showSavedDialog = false
            resetGame()
        },
        onDismiss = { showSavedDialog = false }
    )
}
