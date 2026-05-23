// File: app/src/main/java/com/rais/nexusbody/domain/model/GamificationProfile.kt
package com.rais.nexusbody.domain.model

import java.util.Date

data class MuscleLevel(
    val muscle: com.rais.nexusbody.domain.model.TargetMuscle,
    val level: Int,
    val currentXp: Int,
    val xpToNextLevel: Int,
    val totalXpEarned: Int,
    val lastTrainedAt: Date?
) {
    val progressPercent: Float
        get() = (currentXp.toFloat() / xpToNextLevel.toFloat()).coerceIn(0f, 1f)
}

data class GamificationProfile(
    val id: String,
    val userId: String,
    val totalNutritionXp: Int,
    val dailyDietStreak: Int,
    val longestDietStreak: Int,
    val muscleLevels: Map<com.rais.nexusbody.domain.model.TargetMuscle, com.rais.nexusbody.domain.model.MuscleLevel>,
    val agilityLevel: Int,
    val agilityXp: Int,
    val strengthLevel: Int,
    val strengthXp: Int,
    val enduranceLevel: Int,
    val enduranceXp: Int,
    val overallRank: String,
    val totalWorkoutsCompleted: Int,
    val totalNutritionLogsSubmitted: Int,
    val createdAt: Date,
    val updatedAt: Date
)