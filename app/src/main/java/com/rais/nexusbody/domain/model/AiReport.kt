package com.rais.nexusbody.domain.model

import java.util.Date

data class WorkoutRecommendation(
    val week: Int,
    val focus: String,
    val exercises: List<String>,
    val volumeGuidance: String,
    val intensityNote: String,
    val warningsForInjuries: List<String>
)

data class NutritionRecommendation(
    val dailyCalorieTarget: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatsGrams: Int,
    val mealTimingNotes: String,
    val foodsToAvoid: List<String>,
    val supplementsRecommended: List<String>,
    val hydrationLiters: Float,
    val clinicalNotes: String     // e.g., notes for diabetic diet management
)

data class AiReport(
    val id: String,
    val userId: String,
    val startDate: Date,
    val endDate: Date,
    val diagnosisText: String,
    val clinicalRiskFlags: List<String>,  // e.g., ["pre-diabetic_risk", "hypertension_monitoring"]
    val workoutRecommendations: List<com.rais.nexusbody.domain.model.WorkoutRecommendation>,
    val nutritionRecommendation: com.rais.nexusbody.domain.model.NutritionRecommendation,
    val overallHealthScore: Int,          // 0-100
    val generatedAt: Date,
    val modelVersion: String,
    val disclaimer: String
)
