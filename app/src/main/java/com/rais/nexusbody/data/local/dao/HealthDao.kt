package com.rais.nexusbody.data.local.dao

import androidx.room.Dao // Framework Room: Penanda interface eksekutor SQL
import androidx.room.Insert // Operasi penulisan data
import androidx.room.OnConflictStrategy // Penentu aksi jika ID data sama
import androidx.room.Query // Operasi pembacaan data manual
import com.rais.nexusbody.data.local.entity.HealthAssessmentEntity // Target entitas
import kotlinx.coroutines.flow.Flow // Aliran data reaktif

/**
 * HEALTH DAO (DATA ACCESS OBJECT)
 * Peran: Definisi perintah SQL untuk departemen kesehatan.
 * Hubungan: Digunakan oleh HealthRepositoryImpl.
 */
@Dao
interface HealthDao {
    
    // Menyimpan data kesehatan. Jika ID sama, timpa data lama (Logika Update)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: HealthAssessmentEntity)

    // Mengambil satu data kesehatan berdasarkan user dan tanggal spesifik
    // Digunakan untuk logika "Smart Merge" harian
    @Query("SELECT * FROM health_assessments WHERE userId = :userId AND dateStr = :dateStr LIMIT 1")
    suspend fun getAssessmentByDate(userId: String, dateStr: String): HealthAssessmentEntity?

    // Mengambil aliran (Stream) seluruh riwayat kesehatan secara real-time
    // Diurutkan dari yang paling baru
    @Query("SELECT * FROM health_assessments WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAssessmentsFlow(userId: String): Flow<List<HealthAssessmentEntity>>

    // Menghapus record data berdasarkan ID unik
    @Query("DELETE FROM health_assessments WHERE id = :id")
    suspend fun deleteAssessment(id: String)
}
