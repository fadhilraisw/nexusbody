package com.rais.nexusbody.data.repository

import com.rais.nexusbody.data.local.dao.MedicationDao // Interface database lokal
import com.rais.nexusbody.data.local.entity.MedicationEntity // Struktur tabel obat
import com.rais.nexusbody.domain.repository.MedicationRepository // Kontrak bisnis
import io.github.jan.supabase.SupabaseClient // Klien Cloud
import io.github.jan.supabase.postgrest.from // SDK Supabase
import kotlinx.coroutines.flow.Flow // Stream data
import kotlinx.coroutines.flow.emitAll // Helper aliran flow
import kotlinx.coroutines.flow.flow // Pembangkit flow asinkron
import javax.inject.Inject // Hilt

/**
 * MEDICATION REPOSITORY IMPLEMENTATION
 * Peran: Menangani sinkronisasi jadwal obat antara database ponsel dan cloud.
 * Tech: Menggunakan Supabase Postgrest untuk penyimpanan cloud.
 */
class MedicationRepositoryImpl @Inject constructor(
    private val medicationDao: MedicationDao, // Injeksi DAO Room
    private val supabase: SupabaseClient // Injeksi Supabase
) : MedicationRepository {

    // Mengambil jadwal obat yang berstatus 'Aktif' saja
    override fun getActiveMedications(userId: String): Flow<List<MedicationEntity>> = flow {
        // 1. Kirim data dari HP segera
        emitAll(medicationDao.getActiveMedicationsFlow(userId))
        
        // 2. Tarik data terbaru dari Cloud (Background Sync)
        try {
            val remoteData = supabase.from("medications")
                .select { 
                    filter { 
                        eq("userId", userId)
                        eq("isActive", true)
                    } 
                }
                .decodeList<MedicationEntity>()
            // 3. Masukkan ke database lokal
            remoteData.forEach { medicationDao.insertMedication(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Mengambil SELURUH riwayat obat (Aktif & Selesai)
    override fun getAllMedications(userId: String): Flow<List<MedicationEntity>> = flow {
        emitAll(medicationDao.getAllMedicationsFlow(userId))
        
        try {
            val remoteData = supabase.from("medications")
                .select { filter { eq("userId", userId) } }
                .decodeList<MedicationEntity>()
            remoteData.forEach { medicationDao.insertMedication(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // SIMPAN: Save to Room, then Upsert to Supabase
    override suspend fun insertMedication(medication: MedicationEntity) {
        medicationDao.insertMedication(medication)
        try {
            supabase.from("medications").upsert(medication)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // HAPUS: Delete from Room, then Delete from Supabase
    override suspend fun deleteMedication(id: String) {
        medicationDao.deleteMedication(id)
        try {
            supabase.from("medications").delete { filter { eq("id", id) } }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
