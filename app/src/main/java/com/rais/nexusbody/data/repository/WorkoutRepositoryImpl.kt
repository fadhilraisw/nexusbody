package com.rais.nexusbody.data.repository

import com.rais.nexusbody.data.local.dao.SessionWithExercises // Relasi database Room
import com.rais.nexusbody.data.local.dao.WorkoutDao // DAO Latihan
import com.rais.nexusbody.data.local.entity.WorkoutExerciseEntity // Tabel Gerakan
import com.rais.nexusbody.data.local.entity.WorkoutSessionEntity // Tabel Sesi
import com.rais.nexusbody.domain.repository.WorkoutRepository // Kontrak Domain
import io.github.jan.supabase.SupabaseClient // SDK Supabase
import io.github.jan.supabase.postgrest.from // Query helper
import kotlinx.coroutines.flow.Flow // Flow asinkron
import kotlinx.coroutines.flow.emitAll // Stream aggregator
import kotlinx.coroutines.flow.flow // Generator Flow
import javax.inject.Inject // DI

/**
 * WORKOUT REPOSITORY IMPLEMENTATION
 * Peran: Manajemen sinkronisasi latihan (Offline-First).
 * Relasi: Menangani data 1-to-many (1 Sesi punya banyak Gerakan).
 */
class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao, // Injeksi DAO Room
    private val supabase: SupabaseClient // Injeksi Klien Supabase
) : WorkoutRepository {

    // Mengambil seluruh riwayat latihan (Lokal + Cloud Sync)
    override fun getSessionsWithExercises(userId: String): Flow<List<SessionWithExercises>> = flow {
        // 1. Pancarkan data lokal segera (UX Instan)
        emitAll(workoutDao.getSessionsWithExercises(userId))
        
        // 2. Sinkronisasi dengan Cloud
        try {
            // Ambil sesi dari Supabase
            val remoteSessions = supabase.from("workout_sessions")
                .select { filter { eq("userId", userId) } }
                .decodeList<WorkoutSessionEntity>()
            
            // Ambil seluruh gerakan (untuk simplicity, ambil semua dulu)
            val remoteExercises = supabase.from("workout_exercises")
                .select()
                .decodeList<WorkoutExerciseEntity>()
                
            // 3. Update database ponsel agar tetap up-to-date
            remoteSessions.forEach { workoutDao.insertSession(it) }
            remoteExercises.forEach { workoutDao.insertExercise(it) }
        } catch (e: Exception) {
            e.printStackTrace() // Gagal sync jika offline
        }
    }

    // SIMPAN: Simpan Sesi & Gerakan secara paralel ke Lokal dan Cloud
    override suspend fun insertSession(session: WorkoutSessionEntity, exercises: List<WorkoutExerciseEntity>) {
        // Simpan ke Room Lokal
        workoutDao.insertSession(session)
        exercises.forEach { workoutDao.insertExercise(it) }
        
        // Simpan ke Supabase Cloud (Upsert)
        try {
            supabase.from("workout_sessions").upsert(session)
            exercises.forEach { supabase.from("workout_exercises").upsert(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // HAPUS: Hapus data di kedua sisi (Local & Remote)
    override suspend fun deleteSession(sessionId: String) {
        workoutDao.deleteSession(sessionId) // Hapus lokal
        try {
            supabase.from("workout_sessions").delete { filter { eq("id", sessionId) } }
            supabase.from("workout_exercises").delete { filter { eq("sessionId", sessionId) } }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
