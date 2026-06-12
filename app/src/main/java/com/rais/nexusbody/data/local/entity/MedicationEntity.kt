package com.rais.nexusbody.data.local.entity

import androidx.room.Entity // Room
import androidx.room.PrimaryKey // Room Primary Key

/**
 * MEDICATION ENTITY (LOCAL TABLE)
 * Peran: Menyimpan jadwal pengingat obat user.
 * Tabel: medications
 */
@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey val id: String, // ID Unik
    val userId: String, // Kepemilikan data (Supabase User ID)
    val name: String, // Nama obat
    val dosage: String, // Dosis (misal: 20mg)
    val frequency: String, // Frekuensi (misal: 2x sehari)
    val scheduledTimes: List<String>, // Jam pengingat (Format: ["08:00", "20:00"])
    val startDate: Long, // Waktu mulai konsumsi
    val endDate: Long?, // Waktu berakhir (Opsional)
    val isActive: Boolean = true, // Status apakah masih harus diminum
    val notes: String = "" // Catatan khusus (misal: diminum sesudah makan)
)
