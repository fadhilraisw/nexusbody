package com.rais.nexusbody.domain.model

/**
 * WORKOUT DOMAIN MODELS
 * Peran: Definisi struktur data latihan di layer logika bisnis.
 */

// Sesi Latihan Utama
data class WorkoutSession(
    val id: String,
    val userId: String,
    val timestamp: Long,
    val routineName: String, // Nama Rutinitas (misal: Push Day)
    val totalDurationMinutes: Int,
    val xpEarned: Int, // Hadiah XP yang didapat dari sesi ini
    val clinicalNotes: String? // Catatan kondisi fisik saat latihan
)

// Detail Gerakan dalam satu Sesi
data class WorkoutExercise(
    val id: String,
    val sessionId: String, // Relasi ke Sesi
    val exerciseName: String, // Nama Gerakan (misal: Bench Press)
    val targetMuscles: List<String>, // Kelompok Otot (CHEST, BICEPS, dll)
    val sets: Int,
    val reps: Int,
    val weightKg: Float
)
