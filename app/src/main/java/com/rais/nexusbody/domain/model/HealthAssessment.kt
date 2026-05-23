package com.rais.nexusbody.domain.model

import java.util.Date

enum class SmokingStatus { NEVER, FORMER, CURRENT_LIGHT, CURRENT_HEAVY }

data class BiochemicalData(
    val fastingBloodGlucoseMgDl: Float? = null,
    val hba1cPercent: Float? = null,
    val totalCholesterolMgDl: Float? = null,
    val ldlMgDl: Float? = null,
    val hdlMgDl: Float? = null,
    val triglyceridesMgDl: Float? = null,
    val uricAcidMgDl: Float? = null,
    val systolicBpMmHg: Int? = null,
    val diastolicBpMmHg: Int? = null,
    val hemoglobinGdL: Float? = null
)

data class HealthAssessment(
    val id: String,
    val userId: String,
    val hasHypertension: Boolean,
    val hasDiabetes: Boolean,
    val hasGout: Boolean,
    val hasGerd: Boolean,
    val injuries: List<String>,           // e.g. ["lower_back_pain", "left_knee_injury"]
    val allergies: List<String>,          // e.g. ["shellfish", "peanuts", "lactose"]
    val sleepHoursPerNight: Float,
    val smokingStatus: com.rais.nexusbody.domain.model.SmokingStatus,
    val biochemicalData: com.rais.nexusbody.domain.model.BiochemicalData,
    val notes: String?,
    val timestamp: Date
)
