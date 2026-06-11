package com.rais.nexusbody.data.repository

// --- TECH STACK & SYNC LOGIC IMPORTS ---
import com.rais.nexusbody.data.local.dao.HealthDao // Room DAO: Eksekutor SQL Lokal
import com.rais.nexusbody.data.local.entity.HealthAssessmentEntity // Room Entity: Skema tabel medis
import com.rais.nexusbody.domain.repository.HealthAssessmentRepository // Domain: Interface kontrak data
import io.github.jan.supabase.SupabaseClient // Backend Cloud: Klien Supabase
import io.github.jan.supabase.postgrest.from // SDK Supabase: Helper query PostgREST
import kotlinx.coroutines.flow.Flow // Reactive: Aliran data asinkron
import kotlinx.coroutines.flow.emitAll // Reactive: Menggabungkan aliran data
import kotlinx.coroutines.flow.flow // Reactive: Generator aliran data kustom
import javax.inject.Inject // DI: Injeksi dependensi oleh Hilt

/**
 * HEALTH ASSESSMENT REPOSITORY (DATA SYNC LAYER)
 * Peran: Menjadi mediator antara database lokal (Room) dan database cloud (Supabase).
 * Pola: Mengimplementasikan strategi "OFFLINE-FIRST" untuk performa maksimal.
 */
class HealthAssessmentRepositoryImpl @Inject constructor(
    private val healthDao: HealthDao, // Injeksi otomatis Hilt: Akses database ponsel
    private val supabase: SupabaseClient // Injeksi otomatis Hilt: Akses database awan
) : HealthAssessmentRepository {

    /**
     * FUNGSI AMBIL DATA (REACTIVE SYNC FLOW)
     * Alur Backend: Ambil Lokal (Instan) -> Emit ke UI -> Tarik Cloud (Background) -> Simpan Lokal -> Emit ke UI.
     */
    override fun getAssessments(userId: String): Flow<List<HealthAssessmentEntity>> = flow {
        // 1. BACKEND STEP 1: Pancarkan data dari database lokal dulu agar UI langsung muncul (UX Instan).
        emitAll(healthDao.getAssessmentsFlow(userId))
        
        // 2. BACKEND STEP 2: Coba tarik data terbaru dari Supabase Cloud (Sync).
        try {
            val remoteData = supabase.from("health_assessments")
                .select { filter { eq("userId", userId) } }
                .decodeList<HealthAssessmentEntity>()
            
            // 3. BACKEND STEP 3: Perbarui database lokal (Room) dengan data dari cloud.
            // Jika user baru instal ulang app, data lama di cloud akan otomatis kembali ke ponsel.
            remoteData.forEach { healthDao.insertAssessment(it) }
        } catch (e: Exception) {
            // Jika tidak ada koneksi internet, biarkan saja (App tetap jalan dengan data lokal).
            e.printStackTrace()
        }
    }

    /**
     * FUNGSI CARI BY TANGGAL (SMART MERGE LOGIC)
     * Digunakan untuk mengecek apakah record untuk hari tertentu sudah ada (Update vs Insert).
     */
    override suspend fun getAssessmentByDate(userId: String, dateStr: String): HealthAssessmentEntity? {
        // Cek database internal ponsel dulu.
        val local = healthDao.getAssessmentByDate(userId, dateStr)
        if (local != null) return local
        
        // Jika di ponsel kosong (mungkin baru instal app), coba cari di Cloud.
        return try {
            supabase.from("health_assessments")
                .select { 
                    filter { 
                        eq("userId", userId)
                        eq("dateStr", dateStr)
                    } 
                }.decodeSingleOrNull<HealthAssessmentEntity>()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * FUNGSI SIMPAN (CRUD: CREATE/UPDATE)
     * Alur Backend: Save Local -> Post to Cloud.
     */
    override suspend fun insertAssessment(assessment: HealthAssessmentEntity) {
        // 1. LOCAL PERSISTENCE: Simpan ke Room. Data aman meskipun HP tidak ada sinyal.
        healthDao.insertAssessment(assessment)
        
        // 2. REMOTE PERSISTENCE: Kirim ke Supabase menggunakan fitur UPSERT (Update if exists, else Insert).
        try {
            supabase.from("health_assessments").upsert(assessment)
        } catch (e: Exception) {
            // Log kegagalan sync jika offline.
            e.printStackTrace()
        }
    }

    /**
     * FUNGSI HAPUS (CRUD: DELETE)
     * Menghapus record secara permanen di kedua dunia (Local & Cloud).
     */
    override suspend fun deleteAssessment(id: String) {
        // Hapus dari Room.
        healthDao.deleteAssessment(id)
        
        // Hapus dari Supabase Cloud.
        try {
            supabase.from("health_assessments").delete {
                filter { eq("id", id) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
