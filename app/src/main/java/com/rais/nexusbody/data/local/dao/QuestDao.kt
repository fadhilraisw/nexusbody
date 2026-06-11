package com.rais.nexusbody.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rais.nexusbody.data.local.entity.QuestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuest(quest: QuestEntity)

    @Query("SELECT * FROM quests WHERE userId = :userId AND isCompleted = 0")
    fun getActiveQuestsFlow(userId: String): Flow<List<QuestEntity>>

    @Query("UPDATE quests SET isCompleted = 1 WHERE id = :id")
    suspend fun completeQuest(id: String)
}
