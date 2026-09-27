package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.AppDao
import com.example.data.local.DailyQuest
import com.example.data.local.DevFeedback
import com.example.data.local.GameHighScore
import com.example.data.local.TransactionRecord
import com.example.data.local.UserWallet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Calendar

class RewardRepository(
    private val appDao: AppDao,
    private val context: Context? = null
) {
    private val prefs: SharedPreferences? = context?.getSharedPreferences("muhur_wallet_backup", Context.MODE_PRIVATE)

    val walletFlow: Flow<UserWallet?> = appDao.getUserWalletFlow()
    val transactionsFlow: Flow<List<TransactionRecord>> = appDao.getAllTransactions()
    val questsFlow: Flow<List<DailyQuest>> = appDao.getAllQuests()
    val highScoresFlow: Flow<List<GameHighScore>> = appDao.getAllHighScores()
    val feedbackFlow: Flow<List<DevFeedback>> = appDao.getAllFeedback()

    fun syncToPrefs(wallet: UserWallet) {
        prefs?.edit()?.apply {
            putLong("saved_coins", wallet.coins)
            putLong("saved_total_earned", wallet.totalEarnedCoins)
            putInt("saved_gems", wallet.gems)
            putInt("saved_level", wallet.level)
            putInt("saved_xp", wallet.xp)
            putInt("saved_games_played", wallet.gamesPlayed)
            commit() // Synchronous flush to disk so points are never lost on process death
        }
    }

    suspend fun initializeIfEmpty() = withContext(Dispatchers.IO) {
        var wallet = appDao.getUserWallet()
        val backedUpCoins = prefs?.getLong("saved_coins", -1L) ?: -1L

        if (wallet == null) {
            val initialCoins = if (backedUpCoins > 0L) backedUpCoins else 350L
            val initialTotal = prefs?.getLong("saved_total_earned", initialCoins) ?: initialCoins
            val initialGems = prefs?.getInt("saved_gems", 15) ?: 15
            val initialLevel = prefs?.getInt("saved_level", 1) ?: 1
            val initialXp = prefs?.getInt("saved_xp", 80) ?: 80
            val initialGamesPlayed = prefs?.getInt("saved_games_played", 0) ?: 0

            wallet = UserWallet(
                id = 1,
                coins = initialCoins,
                gems = initialGems,
                level = initialLevel,
                xp = initialXp,
                totalEarnedCoins = initialTotal,
                gamesPlayed = initialGamesPlayed,
                consecutiveCheckins = 1,
                lastCheckinDate = 0L,
                referralCode = "MUHUR-GAME-77"
            )
            appDao.insertOrUpdateWallet(wallet)
            syncToPrefs(wallet)
            appDao.insertTransaction(
                TransactionRecord(
                    type = "WELCOME",
                    title = "Welcome Starter Gift",
                    amount = initialCoins,
                    status = "COMPLETED",
                    subtitle = "Muhur Games New Player Bonus"
                )
            )
        } else if (backedUpCoins > wallet.coins) {
            // Restore backed up points if process restarted before SQLite WAL checkpoint
            val updated = wallet.copy(
                coins = backedUpCoins,
                totalEarnedCoins = maxOf(wallet.totalEarnedCoins, prefs?.getLong("saved_total_earned", backedUpCoins) ?: backedUpCoins)
            )
            appDao.insertOrUpdateWallet(updated)
            syncToPrefs(updated)
        } else {
            syncToPrefs(wallet)
        }

        // Initialize Tasks if not present & sync milestones
        val defaultTasks = listOf(
            DailyQuest(
                id = "DAILY_CHECKIN",
                title = "Daily Check-in",
                description = "Claim your daily login rewards",
                targetCount = 1,
                currentCount = if (wallet.lastCheckinDate > 0) 1 else 0,
                rewardCoins = 100,
                category = "DAILY"
            ),
            DailyQuest(
                id = "PLAY_SPIN",
                title = "Lucky Wheel Master",
                description = "Spin the Lucky Wheel 2 times",
                targetCount = 2,
                currentCount = 0,
                rewardCoins = 100,
                category = "DAILY"
            ),
            DailyQuest(
                id = "PLAY_TAP",
                title = "Speed Demon",
                description = "Score 40+ points in Speed Tapper",
                targetCount = 1,
                currentCount = 0,
                rewardCoins = 120,
                category = "DAILY"
            ),
            DailyQuest(
                id = "PLAY_MEMORY",
                title = "Mind Matrix Challenge",
                description = "Complete 1 Memory Match game",
                targetCount = 1,
                currentCount = 0,
                rewardCoins = 150,
                category = "DAILY"
            ),
            DailyQuest(
                id = "PLAY_SCRATCH",
                title = "Golden Scraper",
                description = "Scratch 2 Lucky Cards",
                targetCount = 2,
                currentCount = 0,
                rewardCoins = 100,
                category = "DAILY"
            ),
            DailyQuest(
                id = "PLAY_MATH",
                title = "Math Rush Genius",
                description = "Solve 5 quick math puzzles in Math Rush",
                targetCount = 5,
                currentCount = 0,
                rewardCoins = 130,
                category = "DAILY"
            ),
            DailyQuest(
                id = "SPECIAL_GPAY",
                title = "Google Pay Address Connected",
                description = "Linked muhur2040@gmail.com for instant cashout",
                targetCount = 1,
                currentCount = 1,
                rewardCoins = 250,
                category = "SPECIAL"
            ),
            DailyQuest(
                id = "SPECIAL_PLAY_STORE",
                title = "Rate Muhur Games on Play Store",
                description = "Give 5 stars & support independent game dev",
                targetCount = 1,
                currentCount = 0,
                rewardCoins = 300,
                category = "SPECIAL"
            ),
            DailyQuest(
                id = "SPECIAL_COMMUNITY",
                title = "Join Player Community",
                description = "Connect with fellow players & get bonus news",
                targetCount = 1,
                currentCount = 0,
                rewardCoins = 150,
                category = "SPECIAL"
            ),
            DailyQuest(
                id = "DEV_FEEDBACK",
                title = "Send Dev Feedback",
                description = "Send feedback or game idea to Muhur in Dev Zone",
                targetCount = 1,
                currentCount = 0,
                rewardCoins = 200,
                category = "SPECIAL"
            ),
            DailyQuest(
                id = "MILESTONE_1K",
                title = "$1.00 USD Milestone (1,000 Coins)",
                description = "Reach 1,000 Coins in your wallet balance",
                targetCount = 1000,
                currentCount = wallet.coins.toInt().coerceAtMost(1000),
                rewardCoins = 250,
                category = "MILESTONE"
            ),
            DailyQuest(
                id = "MILESTONE_5K",
                title = "$5.00 USD Earner (5,000 Lifetime)",
                description = "Accumulate 5,000 Coins total earned points",
                targetCount = 5000,
                currentCount = wallet.totalEarnedCoins.toInt().coerceAtMost(5000),
                rewardCoins = 500,
                category = "MILESTONE"
            ),
            DailyQuest(
                id = "MILESTONE_GAMES_10",
                title = "Arcade Veteran (10 Games)",
                description = "Play 10 total games in the arcade library",
                targetCount = 10,
                currentCount = wallet.gamesPlayed.coerceAtMost(10),
                rewardCoins = 300,
                category = "MILESTONE"
            ),
            DailyQuest(
                id = "MILESTONE_STREAK_3",
                title = "3-Day Streak Legend",
                description = "Maintain a 3-day consecutive login streak",
                targetCount = 3,
                currentCount = wallet.consecutiveCheckins.coerceAtMost(3),
                rewardCoins = 200,
                category = "MILESTONE"
            )
        )

        for (task in defaultTasks) {
            val existing = appDao.getQuestById(task.id)
            if (existing == null) {
                appDao.insertQuest(task)
            } else if (task.category == "MILESTONE" && !existing.isClaimed) {
                appDao.setQuestProgress(task.id, task.currentCount)
            } else if (task.id == "SPECIAL_GPAY" && !existing.isClaimed) {
                appDao.setQuestProgress("SPECIAL_GPAY", 1)
            }
        }
    }

    suspend fun addCoins(
        amount: Long,
        title: String,
        type: String,
        subtitle: String = "",
        bonusXp: Int = (amount / 2).toInt().coerceAtLeast(10)
    ): Boolean = withContext(Dispatchers.IO) {
        val currentWallet = appDao.getUserWallet() ?: UserWallet()
        val newCoins = (currentWallet.coins + amount).coerceAtLeast(0L)
        val newTotal = if (amount > 0) currentWallet.totalEarnedCoins + amount else currentWallet.totalEarnedCoins
        val totalXp = currentWallet.xp + bonusXp
        val newLevel = (totalXp / 200) + 1

        val updatedWallet = currentWallet.copy(
            coins = newCoins,
            totalEarnedCoins = newTotal,
            xp = totalXp,
            level = newLevel
        )
        appDao.insertOrUpdateWallet(updatedWallet)
        syncToPrefs(updatedWallet)

        appDao.insertTransaction(
            TransactionRecord(
                type = type,
                title = title,
                amount = amount,
                status = "COMPLETED",
                subtitle = subtitle
            )
        )
        true
    }

    suspend fun recordGamePlayed(
        gameId: String,
        gameTitle: String,
        score: Int,
        coinsEarned: Int
    ) = withContext(Dispatchers.IO) {
        val currentWallet = appDao.getUserWallet() ?: UserWallet()
        val updatedWallet = currentWallet.copy(
            gamesPlayed = currentWallet.gamesPlayed + 1
        )
        appDao.insertOrUpdateWallet(updatedWallet)
        syncToPrefs(updatedWallet)

        // Update highscore
        val existingHighScore = appDao.getHighScore(gameId)
        val newHigh = if (existingHighScore != null) {
            existingHighScore.copy(
                highestScore = maxOf(existingHighScore.highestScore, score),
                totalCoinsWon = existingHighScore.totalCoinsWon + coinsEarned,
                timesPlayed = existingHighScore.timesPlayed + 1
            )
        } else {
            GameHighScore(
                gameId = gameId,
                gameTitle = gameTitle,
                highestScore = score,
                totalCoinsWon = coinsEarned.toLong(),
                timesPlayed = 1
            )
        }
        appDao.insertHighScore(newHigh)

        // Advance specific tasks
        when (gameId) {
            "SPIN_WHEEL" -> appDao.incrementQuestProgress("PLAY_SPIN")
            "SPEED_TAP" -> {
                if (score >= 40) {
                    appDao.incrementQuestProgress("PLAY_TAP")
                }
            }
            "MEMORY_MATCH" -> appDao.incrementQuestProgress("PLAY_MEMORY")
            "SCRATCH_CARD" -> appDao.incrementQuestProgress("PLAY_SCRATCH")
            "MATH_RUSH" -> appDao.incrementQuestProgress("PLAY_MATH", score.coerceAtLeast(1))
        }
        appDao.incrementQuestProgress("MILESTONE_GAMES_10", 1)

        if (coinsEarned > 0) {
            addCoins(
                amount = coinsEarned.toLong(),
                title = "$gameTitle Reward",
                type = "GAME_WIN",
                subtitle = "Score: $score"
            )
        }
    }

    suspend fun claimDailyCheckin(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val currentWallet = appDao.getUserWallet() ?: UserWallet()
        val now = System.currentTimeMillis()

        val calNow = Calendar.getInstance().apply { timeInMillis = now }
        val calLast = Calendar.getInstance().apply { timeInMillis = currentWallet.lastCheckinDate }

        val isSameDay = currentWallet.lastCheckinDate > 0 &&
                calNow.get(Calendar.YEAR) == calLast.get(Calendar.YEAR) &&
                calNow.get(Calendar.DAY_OF_YEAR) == calLast.get(Calendar.DAY_OF_YEAR)

        if (isSameDay) {
            return@withContext Pair(false, "You already claimed today's check-in! Come back tomorrow.")
        }

        calLast.add(Calendar.DAY_OF_YEAR, 1)
        val isConsecutive = currentWallet.lastCheckinDate > 0 &&
                calNow.get(Calendar.YEAR) == calLast.get(Calendar.YEAR) &&
                calNow.get(Calendar.DAY_OF_YEAR) == calLast.get(Calendar.DAY_OF_YEAR)

        val nextStreak = if (isConsecutive) {
            (currentWallet.consecutiveCheckins % 7) + 1
        } else {
            1
        }

        val rewardCoins = when (nextStreak) {
            1 -> 50L
            2 -> 100L
            3 -> 150L
            4 -> 200L
            5 -> 300L
            6 -> 450L
            7 -> 1000L
            else -> 100L
        }

        val bonusGems = if (nextStreak == 7) 5 else 1

        val updatedWallet = currentWallet.copy(
            coins = currentWallet.coins + rewardCoins,
            gems = currentWallet.gems + bonusGems,
            totalEarnedCoins = currentWallet.totalEarnedCoins + rewardCoins,
            consecutiveCheckins = nextStreak,
            lastCheckinDate = now,
            xp = currentWallet.xp + 50
        )
        appDao.insertOrUpdateWallet(updatedWallet)
        syncToPrefs(updatedWallet)

        appDao.insertTransaction(
            TransactionRecord(
                type = "DAILY_CHECKIN",
                title = "Day $nextStreak Check-in Bonus",
                amount = rewardCoins,
                status = "COMPLETED",
                subtitle = if (bonusGems > 0) "+$bonusGems Gems included!" else ""
            )
        )

        appDao.incrementQuestProgress("DAILY_CHECKIN")

        Pair(true, "Claimed $rewardCoins Coins & +$bonusGems Gem for Day $nextStreak streak!")
    }

    suspend fun claimQuest(questId: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val quest = appDao.getQuestById(questId)
            ?: return@withContext Pair(false, "Quest not found")

        if (quest.isClaimed) {
            return@withContext Pair(false, "Quest already claimed")
        }

        if (quest.currentCount < quest.targetCount) {
            return@withContext Pair(false, "Quest requirement not yet met (${quest.currentCount}/${quest.targetCount})")
        }

        val updatedQuest = quest.copy(isClaimed = true)
        appDao.updateQuest(updatedQuest)

        addCoins(
            amount = quest.rewardCoins.toLong(),
            title = "Quest: ${quest.title}",
            type = "QUEST_REWARD",
            subtitle = "Completed daily quest challenge"
        )

        Pair(true, "Claimed ${quest.rewardCoins} Coins!")
    }

    suspend fun requestRedemption(
        optionName: String,
        coinCost: Long,
        targetAccount: String
    ): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val currentWallet = appDao.getUserWallet() ?: UserWallet()
        if (currentWallet.coins < coinCost) {
            return@withContext Pair(false, "Insufficient balance! Need $coinCost coins (You have ${currentWallet.coins}).")
        }

        val updatedWallet = currentWallet.copy(
            coins = currentWallet.coins - coinCost
        )
        appDao.insertOrUpdateWallet(updatedWallet)
        syncToPrefs(updatedWallet)

        appDao.insertTransaction(
            TransactionRecord(
                type = "REDEEM",
                title = "Redeem $optionName",
                amount = -coinCost,
                status = "PROCESSING",
                subtitle = "Sent to: $targetAccount (Est. 12-24 hrs)"
            )
        )

        Pair(true, "Redemption requested successfully! Processing for $targetAccount.")
    }

    suspend fun submitFeedback(
        gameName: String,
        rating: Int,
        feedbackText: String,
        category: String
    ): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val feedback = DevFeedback(
            gameName = gameName,
            rating = rating,
            feedbackText = feedbackText,
            category = category
        )
        appDao.insertFeedback(feedback)
        appDao.incrementQuestProgress("DEV_FEEDBACK")

        // Award 200 coins for beta feedback
        addCoins(
            amount = 200L,
            title = "Beta Feedback Reward",
            type = "DEV_FEEDBACK",
            subtitle = "Feedback for $gameName submitted to Muhur"
        )

        Pair(true, "Thank you! Your feedback reached Muhur. +200 Coins awarded!")
    }

    suspend fun applyReferralCode(code: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val currentWallet = appDao.getUserWallet() ?: UserWallet()
        val trimmed = code.trim().uppercase()

        if (trimmed == currentWallet.referralCode.uppercase()) {
            return@withContext Pair(false, "You cannot enter your own referral code!")
        }

        if (trimmed.length < 4) {
            return@withContext Pair(false, "Please enter a valid referral code.")
        }

        // Grant 500 bonus coins
        addCoins(
            amount = 500L,
            title = "Referral Bonus Code",
            type = "REFERRAL",
            subtitle = "Used code: $trimmed"
        )

        val updatedWallet = currentWallet.copy(
            invitedCount = currentWallet.invitedCount + 1
        )
        appDao.insertOrUpdateWallet(updatedWallet)

        Pair(true, "Success! 500 Coins added to your wallet!")
    }

    suspend fun watchRewardedAd(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val rewardAmount = 100L
        addCoins(
            amount = rewardAmount,
            title = "Rewarded Ad Bonus",
            type = "AD_REWARD",
            subtitle = "Watched sponsor video ad (+100 Coins)"
        )
        appDao.incrementQuestProgress("WATCH_ADS", 1)
        Pair(true, "+$rewardAmount Coins ($0.10 USD) added to your wallet!")
    }

    suspend fun addCustomTask(
        title: String,
        description: String,
        targetCount: Int,
        rewardCoins: Int,
        category: String = "CUSTOM"
    ): Boolean = withContext(Dispatchers.IO) {
        val id = "CUSTOM_${System.currentTimeMillis()}"
        val quest = DailyQuest(
            id = id,
            title = title.trim(),
            description = description.trim(),
            targetCount = targetCount.coerceAtLeast(1),
            currentCount = 0,
            rewardCoins = rewardCoins.coerceIn(20, 1000),
            isClaimed = false,
            category = category
        )
        appDao.insertQuest(quest)
        true
    }

    suspend fun incrementTaskProgress(taskId: String, amount: Int = 1) = withContext(Dispatchers.IO) {
        appDao.incrementQuestProgress(taskId, amount)
    }

    suspend fun setTaskCompleted(taskId: String) = withContext(Dispatchers.IO) {
        val quest = appDao.getQuestById(taskId) ?: return@withContext
        appDao.setQuestProgress(taskId, quest.targetCount)
    }

    suspend fun deleteCustomTask(taskId: String) = withContext(Dispatchers.IO) {
        appDao.deleteQuest(taskId)
    }
}
