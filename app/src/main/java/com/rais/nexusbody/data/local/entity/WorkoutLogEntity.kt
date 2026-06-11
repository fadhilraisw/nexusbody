package com.rais.nexusbody.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val timestamp: Long,
    val routineName: String,
    val totalDurationMinutes: Int,
    val clinicalNotes: String,
    val xpEarned: Int = 0 // Track XP earned per session
)

@Entity(tableName = "workout_exercises")
data class WorkoutExerciseEntity(
    @PrimaryKey val id: String,
    val sessionId: String, // relasi ke WorkoutSessionEntity
    val exerciseName: String,
    val targetMuscles: List<String>, // menggunakan converter
    val sets: Int,
    val reps: Int,
    val weightKg: Float
)
