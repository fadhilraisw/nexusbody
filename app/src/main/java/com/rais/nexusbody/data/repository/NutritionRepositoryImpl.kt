package com.rais.nexusbody.data.repository

import com.rais.nexusbody.data.local.dao.NutritionDao
import com.rais.nexusbody.data.local.entity.NutritionLogEntity
import com.rais.nexusbody.domain.repository.NutritionRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NutritionRepositoryImpl @Inject constructor(
    private val nutritionDao: NutritionDao,
    private val supabase: SupabaseClient
) : NutritionRepository {

    override fun getLogsByTimeframe(userId: String, startTime: Long, endTime: Long): Flow<List<NutritionLogEntity>> = flow {
        emitAll(nutritionDao.getLogsByTimeframe(userId, startTime, endTime))
        
        try {
            val remoteData = supabase.from("nutrition_logs")
                .select { filter { eq("userId", userId) } }
                .decodeList<NutritionLogEntity>()
            remoteData.forEach { nutritionDao.insertLog(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun insertLog(log: NutritionLogEntity) {
        nutritionDao.insertLog(log)
        try {
            supabase.from("nutrition_logs").upsert(log)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteLog(id: String) {
        nutritionDao.deleteLog(id)
        try {
            supabase.from("nutrition_logs").delete { filter { eq("id", id) } }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
