package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Wallet
    @Query("SELECT * FROM user_wallet WHERE id = 1 LIMIT 1")
    fun getUserWalletFlow(): Flow<UserWallet?>

    @Query("SELECT * FROM user_wallet WHERE id = 1 LIMIT 1")
    suspend fun getUserWallet(): UserWallet?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWallet(wallet: UserWallet)

    // Transactions
    @Query("SELECT * FROM transaction_records ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionRecord)

    // Daily Quests
    @Query("SELECT * FROM daily_quests")
    fun getAllQuests(): Flow<List<DailyQuest>>

    @Query("SELECT * FROM daily_quests WHERE id = :id LIMIT 1")
    suspend fun getQuestById(id: String): DailyQuest?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuest(quest: DailyQuest)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllQuests(quests: List<DailyQuest>)

    @Update
    suspend fun updateQuest(quest: DailyQuest)

    @Query("UPDATE daily_quests SET currentCount = MIN(targetCount, currentCount + :increment) WHERE id = :questId AND isClaimed = 0")
    suspend fun incrementQuestProgress(questId: String, increment: Int = 1)

    @Query("UPDATE daily_quests SET currentCount = :count WHERE id = :questId AND isClaimed = 0")
    suspend fun setQuestProgress(questId: String, count: Int)

    @Query("DELETE FROM daily_quests WHERE id = :id")
    suspend fun deleteQuest(id: String)

    // High Scores
    @Query("SELECT * FROM game_high_scores")
    fun getAllHighScores(): Flow<List<GameHighScore>>

    @Query("SELECT * FROM game_high_scores WHERE gameId = :gameId LIMIT 1")
    suspend fun getHighScore(gameId: String): GameHighScore?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHighScore(highScore: GameHighScore)

    // Feedback
    @Query("SELECT * FROM dev_feedback ORDER BY timestamp DESC")
    fun getAllFeedback(): Flow<List<DevFeedback>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: DevFeedback)
}
