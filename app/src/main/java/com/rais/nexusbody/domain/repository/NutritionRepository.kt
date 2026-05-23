package com.rais.nexusbody.domain.repository

import com.rais.nexusbody.data.local.entity.NutritionLogEntity
import kotlinx.coroutines.flow.Flow

interface NutritionRepository {
    fun getLogsByTimeframe(userId: String, startTime: Long, endTime: Long): Flow<List<NutritionLogEntity>>
    suspend fun insertLog(log: NutritionLogEntity)
    suspend fun deleteLog(id: String)
}