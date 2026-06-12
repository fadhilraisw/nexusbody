package com.rais.nexusbody.domain.repository

import com.rais.nexusbody.data.local.entity.QuestEntity // Entity tabel tantangan
import kotlinx.coroutines.flow.Flow // Stream data

/**
 * QUEST REPOSITORY INTERFACE
 * Peran: Kontrak data untuk sistem tantangan harian (Gamifikasi).
 * Departemen: Back-End (Logic Contract).
 */
interface QuestRepository {
    // Mendapatkan daftar tantangan yang masih berjalan (belum selesai)
    fun getActiveQuests(userId: String): Flow<List<QuestEntity>>
    
    // Mendapatkan seluruh daftar tantangan yang pernah dibuat
    fun getAllQuests(userId: String): Flow<List<QuestEntity>>
    
    // Menyimpan tantangan baru ke sistem
    suspend fun insertQuest(quest: QuestEntity)
    
    // Menghapus tantangan tertentu
    suspend fun deleteQuest(id: String)
}
