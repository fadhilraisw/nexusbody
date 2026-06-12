package com.rais.nexusbody.domain.repository

import com.rais.nexusbody.data.local.dao.SessionWithExercises // Relasi database Room
import com.rais.nexusbody.data.local.entity.WorkoutExerciseEntity // Entity gerakan
import com.rais.nexusbody.data.local.entity.WorkoutSessionEntity // Entity sesi latihan
import kotlinx.coroutines.flow.Flow // Stream data

/**
 * WORKOUT REPOSITORY INTERFACE
 * Peran: Kontrak data untuk manajemen latihan, volume, dan pemulihan (recovery).
 * Departemen: Back-End (Logic Contract).
 */
interface WorkoutRepository {
    // Mendapatkan seluruh riwayat sesi latihan beserta rincian gerakannya (1-to-N)
    fun getSessionsWithExercises(userId: String): Flow<List<SessionWithExercises>>
    
    // Menyimpan sesi latihan utuh (Header Sesi + Daftar Gerakan) secara simultan
    suspend fun insertSession(session: WorkoutSessionEntity, exercises: List<WorkoutExerciseEntity>)
    
    // Menghapus data sesi latihan tertentu berdasarkan ID
    suspend fun deleteSession(sessionId: String)
}
