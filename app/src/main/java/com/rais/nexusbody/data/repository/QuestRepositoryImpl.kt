package com.rais.nexusbody.data.repository

import com.rais.nexusbody.data.local.dao.QuestDao
import com.rais.nexusbody.data.local.entity.QuestEntity
import com.rais.nexusbody.domain.repository.QuestRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class QuestRepositoryImpl @Inject constructor(
    private val questDao: QuestDao,
    private val supabase: SupabaseClient
) : QuestRepository {
    override fun getActiveQuests(userId: String): Flow<List<QuestEntity>> = flow {
        emitAll(questDao.getActiveQuestsFlow(userId))
        
        try {
            val remoteData = supabase.from("quests")
                .select { 
                    filter { 
                        eq("userId", userId)
                        eq("isCompleted", false)
                    } 
                }
                .decodeList<QuestEntity>()
            remoteData.forEach { questDao.insertQuest(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun insertQuest(quest: QuestEntity) {
        questDao.insertQuest(quest)
        try {
            supabase.from("quests").upsert(quest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun completeQuest(id: String) {
        questDao.completeQuest(id)
        try {
            supabase.from("quests").update({
                set("isCompleted", true)
            }) {
                filter { eq("id", id) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
