package com.rais.nexusbody.core.database

import androidx.room.Database // Framework Room
import androidx.room.RoomDatabase // Kelas dasar Room
import androidx.room.TypeConverters // Penanda converter data kompleks
import com.rais.nexusbody.data.local.Converters // Logic pengubahan data Map/List ke String
import com.rais.nexusbody.data.local.dao.* // Daftar DAO
import com.rais.nexusbody.data.local.entity.* // Daftar Entitas (Tabel)

/**
 * NEXUS BODY DATABASE CONFIGURATION
 * Peran: Definisi pusat struktur database lokal ponsel (SSOT).
 * Versi: v9 (Mendukung AI Report, Quest, Gamification, dan 20+ Biomarker).
 */
@Database(
    // Daftar seluruh tabel yang ada di dalam database
    entities = [
        UserEntity::class,
        HealthAssessmentEntity::class,
        NutritionLogEntity::class,
        WorkoutSessionEntity::class,
        WorkoutExerciseEntity::class,
        MedicationEntity::class,
        QuestEntity::class,
        GamificationProfileEntity::class,
        AiReportEntity::class
    ],
    version = 9, // Wajib naik setiap ada perubahan struktur tabel
    exportSchema = false // Jangan simpan skema di luar APK
)
@TypeConverters(Converters::class) // Gunakan converter untuk kolom JSONB (seperti daftar otot atau kondisi medis)
abstract class NexusBodyDatabase : RoomDatabase() {
    
    // Abstract functions untuk mendapatkan akses ke masing-masing departemen tabel
    abstract fun userDao(): UserDao
    abstract fun healthDao(): HealthDao
    abstract fun nutritionDao(): NutritionDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun medicationDao(): MedicationDao
    abstract fun questDao(): QuestDao
    abstract fun gamificationDao(): GamificationDao
    abstract fun aiReportDao(): AiReportDao
}
