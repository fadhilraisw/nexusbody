package com.rais.nexusbody.domain.repository

import com.rais.nexusbody.data.local.entity.HealthAssessmentEntity
import kotlinx.coroutines.flow.Flow

interface HealthAssessmentRepository {
    fun getAssessments(userId: String): Flow<List<HealthAssessmentEntity>>
    suspend fun getAssessmentByDate(userId: String, dateStr: String): HealthAssessmentEntity?
    suspend fun insertAssessment(assessment: HealthAssessmentEntity)
    suspend fun deleteAssessment(id: String)
}
