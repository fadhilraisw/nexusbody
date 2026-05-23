// File: app/src/main/java/com/rais/nexusbody/domain/model/User.kt
package com.rais.nexusbody.domain.model

import java.util.Date

enum class Gender { MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY }
enum class DailyActivityLevel { SEDENTARY, LIGHTLY_ACTIVE, MODERATELY_ACTIVE, VERY_ACTIVE, EXTRA_ACTIVE }
enum class ProgramGoal { WEIGHT_LOSS, MUSCLE_GAIN, MAINTENANCE, ATHLETIC_PERFORMANCE, CLINICAL_RECOVERY }
enum class BmiPreference { METRIC, IMPERIAL }

data class User(
    val id: String,
    val name: String,
    val age: Int,
    val gender: com.rais.nexusbody.domain.model.Gender,
    val heightCm: Float,
    val weightKg: Float,
    val dailyActivityLevel: com.rais.nexusbody.domain.model.DailyActivityLevel,
    val programGoal: com.rais.nexusbody.domain.model.ProgramGoal,
    val bmiPreference: com.rais.nexusbody.domain.model.BmiPreference,
    val createdAt: Date,
    val updatedAt: Date
) {
    val bmi: Float get() = weightKg / ((heightCm / 100f) * (heightCm / 100f))
    val bmiCategory: String get() = when {
        bmi < 18.5f -> "Underweight"
        bmi < 25.0f -> "Normal"
        bmi < 30.0f -> "Overweight"
        else -> "Obese"
    }
}