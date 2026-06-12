package com.rais.nexusbody.data.repository

import com.rais.nexusbody.data.local.dao.QuestDao // DAO Quest lokal
import com.rais.nexusbody.data.local.entity.QuestEntity // Tabel Quest
import com.rais.nexusbody.domain.repository.QuestRepository // Kontrak Repo
import io.github.jan.supabase.SupabaseClient // SDK Supabase
import io.github.jan.supabase.postgrest.from // SDK Supabase Query
import kotlinx.coroutines.flow.Flow // Flow data
import kotlinx.coroutines.flow.emitAll // Emit data
import kotlinx.coroutines.flow.flow // Flow builder
import javax.inject.Inject // Hilt

/**
 * QUEST REPOSITORY IMPLEMENTATION
 * Peran: Sinkronisasi tantangan gamifikasi (Lokal & Cloud).
 * Alur Data: Room (Instan) -> Supabase (Sync).
 */
class QuestRepositoryImpl @Inject constructor(
    private val questDao: QuestDao, // Injeksi DAO Room
    private val supabase: SupabaseClient // Injeksi Klien Supabase
) : QuestRepository {

    // Ambil quest yang berstatus aktif (Belum Selesai)
    override fun getActiveQuests(userId: String): Flow<List<QuestEntity>> = flow {
        // 1. Pancarkan data lokal segera
        emitAll(questDao.getActiveQuestsFlow(userId))
        
        // 2. Sinkron dengan cloud di latar belakang
        try {
            val remoteData = supabase.from("quests")
                .select { 
                    filter { 
                        eq("userId", userId)
                        eq("isCompleted", false)
                    } 
                }
                .decodeList<QuestEntity>()
            // Update cache lokal
            remoteData.forEach { questDao.insertQuest(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Ambil seluruh riwayat quest (Selesai & Aktif)
    override fun getAllQuests(userId: String): Flow<List<QuestEntity>> = flow {
        emitAll(questDao.getAllQuestsFlow(userId))
        
        try {
            val remoteData = supabase.from("quests")
                .select { filter { eq("userId", userId) } }
                .decodeList<QuestEntity>()
            remoteData.forEach { questDao.insertQuest(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Simpan quest baru ke sistem
    override suspend fun insertQuest(quest: QuestEntity) {
        questDao.insertQuest(quest)
        try {
            supabase.from("quests").upsert(quest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Hapus quest permanen
    override suspend fun deleteQuest(id: String) {
        questDao.deleteQuest(id)
        try {
            supabase.from("quests").delete { filter { eq("id", id) } }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
