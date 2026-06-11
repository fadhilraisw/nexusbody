package com.rais.nexusbody.domain.repository

import com.rais.nexusbody.data.local.entity.AiReportEntity
import kotlinx.coroutines.flow.Flow

interface AiReportRepository {
    fun getReports(userId: String): Flow<List<AiReportEntity>>
    suspend fun insertReport(report: AiReportEntity)
    suspend fun deleteReport(id: String)
}
