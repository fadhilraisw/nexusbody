package com.rais.nexusbody.domain.repository

import com.rais.nexusbody.data.local.entity.QuestEntity
import kotlinx.coroutines.flow.Flow

interface QuestRepository {
    fun getActiveQuests(userId: String): Flow<List<QuestEntity>>
    suspend fun insertQuest(quest: QuestEntity)
    suspend fun completeQuest(id: String)
}
