package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_wallet")
data class UserWallet(
    @PrimaryKey val id: Int = 1,
    val coins: Long = 350L,
    val gems: Int = 15,
    val level: Int = 1,
    val xp: Int = 80,
    val totalEarnedCoins: Long = 350L,
    val gamesPlayed: Int = 0,
    val consecutiveCheckins: Int = 1,
    val lastCheckinDate: Long = 0L,
    val referralCode: String = "MUHUR-GAME-77",
    val invitedCount: Int = 0,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)

@Entity(tableName = "transaction_records")
data class TransactionRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val type: String, // "WELCOME", "GAME_WIN", "DAILY_CHECKIN", "QUEST_REWARD", "REDEEM", "REFERRAL", "DEV_FEEDBACK"
    val title: String,
    val amount: Long, // positive for earn, negative for redeem
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "COMPLETED", // "COMPLETED", "PROCESSING", "APPROVED"
    val subtitle: String = ""
)

@Entity(tableName = "daily_quests")
data class DailyQuest(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val targetCount: Int,
    val currentCount: Int = 0,
    val rewardCoins: Int,
    val isClaimed: Boolean = false,
    val category: String = "GAME"
)

@Entity(tableName = "game_high_scores")
data class GameHighScore(
    @PrimaryKey val gameId: String,
    val gameTitle: String,
    val highestScore: Int = 0,
    val totalCoinsWon: Long = 0L,
    val timesPlayed: Int = 0
)

@Entity(tableName = "dev_feedback")
data class DevFeedback(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val gameName: String,
    val rating: Int,
    val feedbackText: String,
    val category: String, // "BUG_REPORT", "FEATURE_IDEA", "GENERAL_REVIEW"
    val timestamp: Long = System.currentTimeMillis()
)
