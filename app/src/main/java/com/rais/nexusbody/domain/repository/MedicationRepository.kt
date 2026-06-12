package com.rais.nexusbody.domain.repository

import com.rais.nexusbody.data.local.entity.MedicationEntity // Entity tabel obat
import kotlinx.coroutines.flow.Flow // Stream data

/**
 * MEDICATION REPOSITORY INTERFACE
 * Peran: Kontrak data untuk penjadwalan dan pemantauan obat/suplemen klinis.
 * Departemen: Back-End (Logic Contract).
 */
interface MedicationRepository {
    // Mendapatkan hanya daftar obat yang statusnya masih Aktif
    fun getActiveMedications(userId: String): Flow<List<MedicationEntity>>
    
    // Mendapatkan riwayat lengkap seluruh obat (Aktif & Berhenti)
    fun getAllMedications(userId: String): Flow<List<MedicationEntity>>
    
    // Menyimpan jadwal obat baru atau update status obat lama
    suspend fun insertMedication(medication: MedicationEntity)
    
    // Menghapus record obat dari database
    suspend fun deleteMedication(id: String)
}
