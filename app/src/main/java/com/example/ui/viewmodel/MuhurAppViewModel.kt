package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.DailyQuest
import com.example.data.local.DevFeedback
import com.example.data.local.GameHighScore
import com.example.data.local.TransactionRecord
import com.example.data.local.UserWallet
import com.example.data.repository.RewardRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MuhurAppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RewardRepository

    init {
        val database = AppDatabase.getInstance(application)
        repository = RewardRepository(database.appDao(), application.applicationContext)
        viewModelScope.launch {
            repository.initializeIfEmpty()
        }
    }

    val wallet: StateFlow<UserWallet> = repository.walletFlow
        .map { it ?: UserWallet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserWallet())

    val transactions: StateFlow<List<TransactionRecord>> = repository.transactionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quests: StateFlow<List<DailyQuest>> = repository.questsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val highScores: StateFlow<List<GameHighScore>> = repository.highScoresFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val feedbacks: StateFlow<List<DevFeedback>> = repository.feedbackFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentTab = MutableStateFlow(0) // 0: Games, 1: Earn, 2: Wallet, 3: Dev Studio
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _activeGame = MutableStateFlow<String?>(null)
    val activeGame: StateFlow<String?> = _activeGame.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    private val _showCelebration = MutableStateFlow(false)
    val showCelebration: StateFlow<Boolean> = _showCelebration.asStateFlow()

    private val _isAdPlaying = MutableStateFlow(false)
    val isAdPlaying: StateFlow<Boolean> = _isAdPlaying.asStateFlow()

    fun switchTab(index: Int) {
        _currentTab.value = index
        _activeGame.value = null
    }

    fun launchGame(gameId: String) {
        _activeGame.value = gameId
    }

    fun closeGame() {
        _activeGame.value = null
    }

    fun triggerCelebration() {
        _showCelebration.value = true
    }

    fun dismissCelebration() {
        _showCelebration.value = false
    }

    fun navigateToWallet() {
        _activeGame.value = null
        _currentTab.value = 2
    }

    fun saveDirectPointsToWallet(amount: Long, title: String, subtitle: String = "Manual Bonus") {
        viewModelScope.launch {
            repository.addCoins(amount, title, "WALLET_BONUS", subtitle)
            triggerCelebration()
            val usd = amount / 1000.0
            _userMessage.emit("+$amount Coins ($%.2f USD) saved to your wallet!".format(usd))
        }
    }

    fun onGameCompleted(gameId: String, title: String, score: Int, coinsWon: Int) {
        viewModelScope.launch {
            repository.recordGamePlayed(gameId, title, score, coinsWon)
            if (coinsWon > 0) {
                triggerCelebration()
                val usd = coinsWon / 1000.0
                _userMessage.emit("+$coinsWon Coins ($%.2f USD) saved to your wallet!".format(usd))
            }
        }
    }

    fun claimDailyCheckin() {
        viewModelScope.launch {
            val (success, msg) = repository.claimDailyCheckin()
            if (success) {
                triggerCelebration()
            }
            _userMessage.emit(msg)
        }
    }

    fun claimQuest(questId: String) {
        viewModelScope.launch {
            val (success, msg) = repository.claimQuest(questId)
            if (success) {
                triggerCelebration()
            }
            _userMessage.emit(msg)
        }
    }

    fun requestRedemption(optionName: String, coinCost: Long, targetAccount: String) {
        viewModelScope.launch {
            val (success, msg) = repository.requestRedemption(optionName, coinCost, targetAccount)
            _userMessage.emit(msg)
        }
    }

    fun submitDevFeedback(gameName: String, rating: Int, feedbackText: String, category: String) {
        viewModelScope.launch {
            val (success, msg) = repository.submitFeedback(gameName, rating, feedbackText, category)
            if (success) {
                triggerCelebration()
            }
            _userMessage.emit(msg)
        }
    }

    fun applyReferralCode(code: String) {
        viewModelScope.launch {
            val (success, msg) = repository.applyReferralCode(code)
            if (success) {
                triggerCelebration()
            }
            _userMessage.emit(msg)
        }
    }

    fun openRewardedAd() {
        _isAdPlaying.value = true
    }

    fun closeRewardedAd() {
        _isAdPlaying.value = false
    }

    fun onRewardedAdCompleted() {
        _isAdPlaying.value = false
        viewModelScope.launch {
            val (success, msg) = repository.watchRewardedAd()
            if (success) {
                triggerCelebration()
            }
            _userMessage.emit(msg)
        }
    }

    fun addNewTask(
        title: String,
        description: String,
        targetCount: Int,
        rewardCoins: Int,
        category: String
    ) {
        viewModelScope.launch {
            repository.addCustomTask(title, description, targetCount, rewardCoins, category)
            _userMessage.emit("New task added: $title (+$rewardCoins Coins)")
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteCustomTask(taskId)
            _userMessage.emit("Task removed")
        }
    }

    fun completeTaskManually(taskId: String) {
        viewModelScope.launch {
            repository.setTaskCompleted(taskId)
            claimQuest(taskId)
        }
    }
}
