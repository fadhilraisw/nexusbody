package com.rais.nexusbody.domain.model

import java.util.Date // Objek waktu Java

/**
 * HEALTH ASSESSMENT DOMAIN MODEL
 * Peran: Model data murni untuk merepresentasikan status kesehatan user di layer logika.
 * Kenapa di Domain? Agar logika bisnis tidak tergantung pada framework database (Room).
 */
data class HealthAssessment(
    val id: String, // Identitas unik universal
    val userId: String, // Kepemilikan data
    val metrics: com.rais.nexusbody.domain.model.BiochemicalMetrics, // Kumpulan hasil lab
    val vitals: com.rais.nexusbody.domain.model.VitalSigns, // Kumpulan tanda vital (tensi/hr)
    val bodyComp: com.rais.nexusbody.domain.model.BodyComposition, // Komposisi tubuh (lemak/berat)
    val assessmentTime: Date, // Waktu pengambilan data
    val healthScore: Int, // Skor kesehatan hasil kalkulasi (0-100)
    val notes: String?, // Catatan klinis tambahan
    val createdAt: Date // Waktu pembuatan record
)

// Sub-model untuk pengorganisasian data yang lebih rapi (Modular)

data class BiochemicalMetrics(
    val bloodSugarMgDl: Float?,
    val cholesterolMgDl: Float?,
    val hdlMgDl: Float?,
    val ldlMgDl: Float?,
    val sgotUl: Float?,
    val sgptUl: Float?,
    val creatinineMgDl: Float?,
    val egfr: Float?
)

data class VitalSigns(
    val systolicBp: Int?,
    val diastolicBp: Int?,
    val heartRateBpm: Int?,
    val bodyTempCelsius: Float?,
    val respiratoryRate: Int?
)

data class BodyComposition(
    val weightKg: Float,
    val heightCm: Float,
    val bodyFatPercentage: Float?,
    val skeletalMuscleMassKg: Float?,
    val visceralFatLevel: Int?
)
