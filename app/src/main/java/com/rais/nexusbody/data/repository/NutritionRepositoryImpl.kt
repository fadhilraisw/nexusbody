package com.rais.nexusbody.data.repository

import com.rais.nexusbody.data.local.dao.NutritionDao // Room DAO Gizi
import com.rais.nexusbody.data.local.entity.NutritionLogEntity // Entitas Tabel Gizi
import com.rais.nexusbody.domain.repository.NutritionRepository // Interface Kontrak
import io.github.jan.supabase.SupabaseClient // SDK Supabase
import io.github.jan.supabase.postgrest.from // Helper Tabel Supabase
import kotlinx.coroutines.flow.Flow // Aliran data asinkron
import kotlinx.coroutines.flow.emitAll // Mengalirkan data Flow lain
import kotlinx.coroutines.flow.flow // Generator Flow kustom
import javax.inject.Inject // Dependency Injection

/**
 * NUTRITION REPOSITORY IMPLEMENTATION
 * Peran: Manajemen sinkronisasi data asupan makanan antara HP dan Cloud.
 * Strategy: OFFLINE-FIRST (Prioritas data lokal untuk kecepatan UX).
 */
class NutritionRepositoryImpl @Inject constructor(
    private val nutritionDao: NutritionDao, // Database Lokal ponsel
    private val supabase: SupabaseClient // Database Cloud Supabase
) : NutritionRepository {

    // Ambil log makan berdasarkan rentang waktu (misal: Hari ini)
    override fun getLogsByTimeframe(userId: String, startTime: Long, endTime: Long): Flow<List<NutritionLogEntity>> = flow {
        // 1. BACKEND SYNC: Pancarkan data lokal segera agar UI tidak kosong (UX Instan)
        emitAll(nutritionDao.getLogsByTimeframe(userId, startTime, endTime))
        
        // 2. BACKEND SYNC: Di latar belakang, ambil data terbaru dari Supabase Cloud
        try {
            val remoteData = supabase.from("nutrition_logs")
                .select { filter { eq("userId", userId) } }
                .decodeList<NutritionLogEntity>()
            
            // 3. Update data lokal dengan data dari cloud (Sinkronisasi Dua Arah)
            remoteData.forEach { nutritionDao.insertLog(it) }
        } catch (e: Exception) {
            // Jika gagal sync (misal: tidak ada internet), aplikasi tetap aman berjalan dengan data lokal
            e.printStackTrace()
        }
    }

    // CRUD: CREATE/UPDATE
    override suspend fun insertLog(log: NutritionLogEntity) {
        // Simpan ke HP dulu
        nutritionDao.insertLog(log)
        // Baru kirim ke Cloud (Upsert: Update if exists, else Insert)
        try {
            supabase.from("nutrition_logs").upsert(log)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // CRUD: DELETE
    override suspend fun deleteLog(id: String) {
        // Hapus dari HP
        nutritionDao.deleteLog(id)
        // Hapus dari Cloud berdasarkan ID unik (UUID)
        try {
            supabase.from("nutrition_logs").delete { filter { eq("id", id) } }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
