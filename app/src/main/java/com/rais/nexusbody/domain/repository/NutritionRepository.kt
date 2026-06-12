package com.rais.nexusbody.domain.repository

import com.rais.nexusbody.data.local.entity.NutritionLogEntity // Entitas tabel log makan
import kotlinx.coroutines.flow.Flow // Stream data

/**
 * NUTRITION REPOSITORY INTERFACE
 * Peran: Kontrak data untuk pelacakan asupan gizi harian.
 * Departemen: Back-End (Logic Contract).
 */
interface NutritionRepository {
    // Mendapatkan daftar log makanan user dalam rentang waktu milidetik tertentu
    fun getLogsByTimeframe(userId: String, startTime: Long, endTime: Long): Flow<List<NutritionLogEntity>>
    
    // Menyimpan log makanan baru ke database lokal dan sinkron ke cloud
    suspend fun insertLog(log: NutritionLogEntity)
    
    // Menghapus log makanan tertentu berdasarkan ID unik (UUID)
    suspend fun deleteLog(id: String)
}
