package com.rais.nexusbody.data.repository

import com.rais.nexusbody.data.local.dao.AiReportDao
import com.rais.nexusbody.data.local.entity.AiReportEntity
import com.rais.nexusbody.domain.repository.AiReportRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AiReportRepositoryImpl @Inject constructor(
    private val aiReportDao: AiReportDao,
    private val supabase: SupabaseClient
) : AiReportRepository {
    override fun getReports(userId: String): Flow<List<AiReportEntity>> = flow {
        emitAll(aiReportDao.getReportsFlow(userId))
        try {
            val remote = supabase.from("ai_reports")
                .select { filter { eq("userId", userId) } }
                .decodeList<AiReportEntity>()
            remote.forEach { aiReportDao.insertReport(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun insertReport(report: AiReportEntity) {
        aiReportDao.insertReport(report)
        try {
            supabase.from("ai_reports").upsert(report)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteReport(id: String) {
        aiReportDao.deleteReport(id)
        try {
            supabase.from("ai_reports").delete { filter { eq("id", id) } }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
