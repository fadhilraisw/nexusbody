package com.rais.nexusbody.domain.repository

import com.rais.nexusbody.data.local.entity.HealthAssessmentEntity // Entity biomarker lokal
import kotlinx.coroutines.flow.Flow // Stream data reaktif

/**
 * HEALTH ASSESSMENT REPOSITORY INTERFACE
 * Peran: Kontrak data untuk departemen biomarker medis & klinis.
 * Departemen: Back-End (Logic Contract).
 */
interface HealthAssessmentRepository {
    // Mendapatkan seluruh riwayat rekam medis user secara real-time
    fun getAssessments(userId: String): Flow<List<HealthAssessmentEntity>>
    
    // Mendapatkan data biomarker spesifik pada tanggal tertentu (YYYY-MM-DD)
    suspend fun getAssessmentByDate(userId: String, dateStr: String): HealthAssessmentEntity?
    
    // Operasi penulisan data biomarker baru atau update data lama (Smart Sync)
    suspend fun insertAssessment(assessment: HealthAssessmentEntity)
    
    // Menghapus rekam medis secara permanen berdasarkan ID unik
    suspend fun deleteAssessment(id: String)
}
