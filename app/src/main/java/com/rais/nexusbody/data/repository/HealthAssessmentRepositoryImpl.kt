package com.rais.nexusbody.data.repository

import com.rais.nexusbody.data.local.dao.HealthDao
import com.rais.nexusbody.data.local.entity.HealthAssessmentEntity
import com.rais.nexusbody.domain.repository.HealthAssessmentRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HealthAssessmentRepositoryImpl @Inject constructor(
    private val healthDao: HealthDao,
    private val supabase: SupabaseClient
) : HealthAssessmentRepository {
    override fun getAssessments(userId: String): Flow<List<HealthAssessmentEntity>> {
        return healthDao.getAssessmentsFlow(userId)
    }

    override suspend fun getAssessmentByDate(userId: String, dateStr: String): HealthAssessmentEntity? {
        return healthDao.getAssessmentByDate(userId, dateStr)
    }

    override suspend fun insertAssessment(assessment: HealthAssessmentEntity) {
        // 1. Simpan ke Lokal (Offline First)
        healthDao.insertAssessment(assessment)
        
        // 2. Simpan ke Supabase (Production Ready)
        try {
            supabase.postgrest["health_assessments"].insert(assessment)
        } catch (e: Exception) {
            // Silently fail if offline, data is still in local
            e.printStackTrace()
        }
    }

    override suspend fun deleteAssessment(id: String) {
        healthDao.deleteAssessment(id)
        try {
            supabase.postgrest["health_assessments"].delete {
                filter { eq("id", id) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
