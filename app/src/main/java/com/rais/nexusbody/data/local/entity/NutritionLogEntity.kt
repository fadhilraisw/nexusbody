package com.rais.nexusbody.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nutrition_logs")
data class NutritionLogEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val timestamp: Long,
    val foodName: String,
    val portionGrams: Float,
    val calories: Int,
    val proteinGrams: Float,
    val carbsGrams: Float,
    val fatGrams: Float,
    val isAiEstimated: Boolean
)