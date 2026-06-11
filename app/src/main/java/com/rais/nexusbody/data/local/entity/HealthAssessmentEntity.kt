package com.rais.nexusbody.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_assessments")
data class HealthAssessmentEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val dateStr: String, // format: YYYY-MM-DD untuk identifikasi hari unik
    val timestamp: Long,
    val bodyFatPercentage: Float?,
    val visceralFatLevel: Int?,
    val fastingBloodSugar: Float?,
    val cholesterolTotal: Float?,
    val systolicBp: Int?,
    val diastolicBp: Int?,
    val restingHeartRate: Int?,
    val sleepDurationHours: Float?,
    val conditions: List<String> = emptyList(), // Tambahkan kolom conditions
    val weightKg: Float? = null,
    val heightCm: Float? = null,
    val lilaCm: Float? = null,
    val waistCircumferenceCm: Float? = null,
    // --- LIVER FUNCTION ---
    val sgotAst: Float? = null,
    val sgptAlt: Float? = null,
    val bilirubinTotal: Float? = null,
    // --- RENAL FUNCTION ---
    val bun: Float? = null,
    val creatinine: Float? = null,
    val egfr: Float? = null,
    // --- BLOOD PANEL ---
    val hematocrit: Float? = null,
    val hb: Float? = null,
    // --- HORMONAL PANEL ---
    val totalTestosterone: Float? = null,
    val freeTestosterone: Float? = null,
    val estradiolE2: Float? = null,
    val prolactin: Float? = null,
    // --- MISC ---
    val bodyTemp: Float? = null,
    val fastingInsulin: Float? = null
)