package com.rais.nexusbody.domain.model

import java.util.Date

enum class MovementType {
    COMPOUND_PUSH, COMPOUND_PULL, COMPOUND_LEGS,
    ISOLATION_UPPER, ISOLATION_LOWER,
    CARDIO_STEADY_STATE, CARDIO_HIIT,
    MOBILITY, FLEXIBILITY, SPORT_SPECIFIC
}

enum class TargetMuscle {
    CHEST, BACK_UPPER, BACK_LOWER, SHOULDERS,
    BICEPS, TRICEPS, FOREARMS,
    QUADRICEPS, HAMSTRINGS, GLUTES, CALVES,
    CORE_ABS, CORE_OBLIQUES, CORE_LOWER_BACK,
    FULL_BODY, CARDIOVASCULAR, AGILITY
}

data class WorkoutSet(
    val setNumber: Int,
    val reps: Int?,
    val weightKg: Float?,
    val durationSeconds: Int?,     // for timed sets / cardio
    val restSeconds: Int?,
    val rpe: Float?,               // Rate of Perceived Exertion 1-10
    val notes: String?
)

data class WorkoutLog(
    val id: String,
    val userId: String,
    val movementType: com.rais.nexusbody.domain.model.MovementType,
    val targetMuscle: com.rais.nexusbody.domain.model.TargetMuscle,
    val exerciseName: String,
    val durationMinutes: Int,
    val sets: List<com.rais.nexusbody.domain.model.WorkoutSet>,
    val totalVolumeTonnage: Float,  // sum of (weight * reps) across all sets
    val workoutTime: Date,
    val xpEarned: Int,
    val notes: String?,
    val createdAt: Date
)
