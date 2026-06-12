package com.rais.nexusbody.data.local.entity

import androidx.room.Entity // Framework Room: Penanda kelas sebagai tabel SQL
import androidx.room.PrimaryKey // Framework Room: Penanda kolom kunci utama

/**
 * HEALTH ASSESSMENT ENTITY (LOCAL TABLE)
 * Peran: Definisi tabel database untuk menyimpan seluruh metrik biomarker medis.
 * Tabel: health_assessments
 */
@Entity(tableName = "health_assessments")
data class HealthAssessmentEntity(
    @PrimaryKey val id: String, // ID Unik (UUID) untuk sinkronisasi cloud
    val userId: String, // Relasi ke ID user di Supabase Auth
    val dateStr: String, // Kunci unik harian (YYYY-MM-DD) untuk logika Smart-Upsert
    val timestamp: Long, // Waktu data dibuat dalam format milidetik
    
    // --- METRIK FISIK & ANTROPOMETRI ---
    val weightKg: Float? = null,
    val heightCm: Float? = null,
    val bodyFatPercentage: Float? = null,
    val visceralFatLevel: Int? = null,
    val lilaCm: Float? = null,
    val waistCircumferenceCm: Float? = null,
    val sleepDurationHours: Float?, // Data durasi tidur harian
    val bodyTemp: Float? = null, // Suhu tubuh (°C)
    
    // --- METRIK KLINIS & VITAL ---
    val systolicBp: Int? = null, // Tekanan darah atas
    val diastolicBp: Int? = null, // Tekanan darah bawah
    val restingHeartRate: Int? = null, // Denyut jantung diam (bpm)
    
    // --- HASIL LAB (BIOKIMIA) ---
    val fastingBloodSugar: Float? = null, // Gula darah puasa (GDP)
    val cholesterolTotal: Float? = null, // Kolesterol total
    val hb: Float? = null, // Hemoglobin
    val hematocrit: Float? = null, // Hematokrit (Ht)
    
    // --- FUNGSI ORGAN ---
    val sgotAst: Float? = null, // Fungsi hati (AST)
    val sgptAlt: Float? = null, // Fungsi hati (ALT)
    val bilirubinTotal: Float? = null,
    val bun: Float? = null, // Fungsi ginjal (BUN)
    val creatinine: Float? = null, // Fungsi ginjal (Crea)
    val egfr: Float? = null, // Kecepatan filtrasi ginjal
    
    // --- PANEL HORMONAL ---
    val totalTestosterone: Float? = null,
    val freeTestosterone: Float? = null,
    val estradiolE2: Float? = null,
    val prolactin: Float? = null,
    val fastingInsulin: Float? = null,
    
    // --- RIWAYAT KONDISI ---
    val conditions: List<String> = emptyList() // Daftar riwayat penyakit (disimpan sebagai JSON)
)
