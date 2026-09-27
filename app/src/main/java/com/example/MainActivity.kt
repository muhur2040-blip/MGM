package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppBottomNav
import com.example.ui.components.CelebrationOverlay
import com.example.ui.components.RewardedAdDialog
import com.example.ui.components.TopBalanceBar
import com.example.ui.games.MathRushGame
import com.example.ui.games.MemoryMatchGame
import com.example.ui.games.ScratchCardGame
import com.example.ui.games.SpeedTapGame
import com.example.ui.games.SpinWheelGame
import com.example.ui.screens.DevStudioScreen
import com.example.ui.screens.EarnScreen
import com.example.ui.screens.GamesHubScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MuhurAppViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MuhurGamesApp()
            }
        }
    }
}

@Composable
fun MuhurGamesApp(
    viewModel: MuhurAppViewModel = viewModel()
) {
    val wallet by viewModel.wallet.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val quests by viewModel.quests.collectAsStateWithLifecycle()
    val highScores by viewModel.highScores.collectAsStateWithLifecycle()
    val feedbacks by viewModel.feedbacks.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val activeGame by viewModel.activeGame.collectAsStateWithLifecycle()
    val showCelebration by viewModel.showCelebration.collectAsStateWithLifecycle()
    val isAdPlaying by viewModel.isAdPlaying.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.userMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (activeGame == null) {
                    AppBottomNav(
                        selectedTab = currentTab,
                        onTabSelected = { viewModel.switchTab(it) },
                        modifier = Modifier.navigationBarsPadding()
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (activeGame != null) {
                    // Active Game View
                    Box(modifier = Modifier.statusBarsPadding()) {
                        when (activeGame) {
                            "SPIN_WHEEL" -> {
                                SpinWheelGame(
                                    onBack = { viewModel.closeGame() },
                                    onRewardEarned = { coins ->
                                        viewModel.onGameCompleted("SPIN_WHEEL", "Lucky Spin", coins, coins)
                                    },
                                    walletCoins = wallet.coins,
                                    onOpenWallet = { viewModel.navigateToWallet() }
                                )
                            }
                            "SPEED_TAP" -> {
                                SpeedTapGame(
                                    onBack = { viewModel.closeGame() },
                                    onGameFinished = { score, coinsWon ->
                                        viewModel.onGameCompleted("SPEED_TAP", "Speed Tapper", score, coinsWon)
                                    },
                                    walletCoins = wallet.coins,
                                    onOpenWallet = { viewModel.navigateToWallet() }
                                )
                            }
                            "MEMORY_MATCH" -> {
                                MemoryMatchGame(
                                    onBack = { viewModel.closeGame() },
                                    onGameFinished = { moves, coinsWon ->
                                        viewModel.onGameCompleted("MEMORY_MATCH", "Memory Matrix", moves, coinsWon)
                                    },
                                    walletCoins = wallet.coins,
                                    onOpenWallet = { viewModel.navigateToWallet() }
                                )
                            }
                            "SCRATCH_CARD" -> {
                                ScratchCardGame(
                                    onBack = { viewModel.closeGame() },
                                    onRewardEarned = { coins ->
                                        viewModel.onGameCompleted("SCRATCH_CARD", "Lucky Scratch", coins, coins)
                                    },
                                    walletCoins = wallet.coins,
                                    onOpenWallet = { viewModel.navigateToWallet() }
                                )
                            }
                            "MATH_RUSH" -> {
                                MathRushGame(
                                    onBack = { viewModel.closeGame() },
                                    onGameFinished = { score, coinsWon ->
                                        viewModel.onGameCompleted("MATH_RUSH", "Math Rush", score, coinsWon)
                                    },
                                    walletCoins = wallet.coins,
                                    onOpenWallet = { viewModel.navigateToWallet() }
                                )
                            }
                        }
                    }
                } else {
                    // Primary Tabs View
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                    ) {
                        TopBalanceBar(
                            wallet = wallet,
                            onWalletClick = { viewModel.switchTab(2) }
                        )

                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "tab_transition",
                            modifier = Modifier.weight(1f)
                        ) { tab ->
                            when (tab) {
                                0 -> GamesHubScreen(
                                    wallet = wallet,
                                    highScores = highScores,
                                    onLaunchGame = { viewModel.launchGame(it) },
                                    onWatchAd = { viewModel.openRewardedAd() }
                                )
                                1 -> EarnScreen(
                                    wallet = wallet,
                                    quests = quests,
                                    onClaimCheckin = { viewModel.claimDailyCheckin() },
                                    onClaimQuest = { viewModel.claimQuest(it) },
                                    onApplyReferral = { viewModel.applyReferralCode(it) },
                                    onWatchAd = { viewModel.openRewardedAd() },
                                    onLaunchGame = { viewModel.launchGame(it) },
                                    onNavigateToWallet = { viewModel.navigateToWallet() },
                                    onNavigateToDev = { viewModel.switchTab(3) },
                                    onAddNewTask = { title, desc, target, reward, cat ->
                                        viewModel.addNewTask(title, desc, target, reward, cat)
                                    },
                                    onCompleteTaskManually = { viewModel.completeTaskManually(it) },
                                    onDeleteTask = { viewModel.deleteTask(it) }
                                )
                                2 -> WalletScreen(
                                    wallet = wallet,
                                    transactions = transactions,
                                    onRequestRedeem = { option, cost, account ->
                                        viewModel.requestRedemption(option, cost, account)
                                    },
                                    onAddBonusCoins = { amount, title ->
                                        viewModel.saveDirectPointsToWallet(amount, title)
                                    }
                                )
                                3 -> DevStudioScreen(
                                    feedbackList = feedbacks,
                                    onSubmitFeedback = { game, rating, text, cat ->
                                        viewModel.submitDevFeedback(game, rating, text, cat)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Celebration Confetti Layer
        CelebrationOverlay(
            isVisible = showCelebration,
            onDismiss = { viewModel.dismissCelebration() }
        )

        // Rewarded Video Ad Modal
        RewardedAdDialog(
            isOpen = isAdPlaying,
            onDismiss = { viewModel.closeRewardedAd() },
            onAdCompleted = { viewModel.onRewardedAdCompleted() }
        )
    }
}
