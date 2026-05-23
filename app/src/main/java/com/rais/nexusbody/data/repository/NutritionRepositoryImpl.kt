package com.rais.nexusbody.data.repository

import com.rais.nexusbody.data.local.dao.NutritionDao
import com.rais.nexusbody.data.local.entity.NutritionLogEntity
import com.rais.nexusbody.domain.repository.NutritionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NutritionRepositoryImpl @Inject constructor(
    private val nutritionDao: NutritionDao
) : NutritionRepository {

    override fun getLogsByTimeframe(userId: String, startTime: Long, endTime: Long): Flow<List<NutritionLogEntity>> {
        return nutritionDao.getLogsByTimeframe(userId, startTime, endTime)
    }

    override suspend fun insertLog(log: NutritionLogEntity) {
        nutritionDao.insertLog(log)
    }

    override suspend fun deleteLog(id: String) {
        nutritionDao.deleteLog(id)
    }
}