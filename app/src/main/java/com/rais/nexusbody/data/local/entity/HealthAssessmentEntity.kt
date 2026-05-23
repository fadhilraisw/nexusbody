package com.rais.nexusbody.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_assessments")
data class HealthAssessmentEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val timestamp: Long,
    val bodyFatPercentage: Float?,
    val visceralFatLevel: Int?,
    val fastingBloodSugar: Float?,
    val cholesterolTotal: Float?,
    val systolicBp: Int?,
    val diastolicBp: Int?,
    val restingHeartRate: Int?,
    val sleepDurationHours: Float?
)