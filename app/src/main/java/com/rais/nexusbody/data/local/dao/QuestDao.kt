package com.rais.nexusbody.data.local.dao

import androidx.room.* // Room
import com.rais.nexusbody.data.local.entity.QuestEntity // Tabel Quest
import kotlinx.coroutines.flow.Flow // Flow data

/**
 * QUEST DAO (DATA ACCESS OBJECT)
 * Peran: Operasi SQL lokal untuk sistem tantangan.
 */
@Dao
interface QuestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuest(quest: QuestEntity)

    // Ambil tantangan yang masih harus diselesaikan
    @Query("SELECT * FROM quests WHERE userId = :userId AND isCompleted = 0")
    fun getActiveQuestsFlow(userId: String): Flow<List<QuestEntity>>

    // Ambil seluruh riwayat tantangan
    @Query("SELECT * FROM quests WHERE userId = :userId")
    fun getAllQuestsFlow(userId: String): Flow<List<QuestEntity>>

    // Hapus satu tantangan
    @Query("DELETE FROM quests WHERE id = :id")
    suspend fun deleteQuest(id: String)
}
