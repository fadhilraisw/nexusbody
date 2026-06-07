package com.rais.nexusbody.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rais.nexusbody.data.local.Converters
import com.rais.nexusbody.data.local.dao.*
import com.rais.nexusbody.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        HealthAssessmentEntity::class,
        NutritionLogEntity::class,
        WorkoutSessionEntity::class,
        WorkoutExerciseEntity::class,
        MedicationEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NexusBodyDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun healthDao(): HealthDao
    abstract fun nutritionDao(): NutritionDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun medicationDao(): MedicationDao
}