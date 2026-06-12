package com.rais.nexusbody.domain.repository

import com.rais.nexusbody.data.local.entity.AiReportEntity // Entity rekam medis AI
import kotlinx.coroutines.flow.Flow // Stream data

/**
 * AI REPORT REPOSITORY INTERFACE
 * Peran: Kontrak data untuk menyimpan hasil sintesis kecerdasan buatan (Gemini/Groq).
 * Departemen: Back-End (Logic Contract).
 */
interface AiReportRepository {
    // Mendapatkan seluruh riwayat analisa medis yang pernah dihasilkan AI
    fun getReports(userId: String): Flow<List<AiReportEntity>>
    
    // Menyimpan hasil analisa AI baru ke database lokal dan cloud
    suspend fun insertReport(report: AiReportEntity)
    
    // Menghapus record analisa AI tertentu
    suspend fun deleteReport(id: String)
}
