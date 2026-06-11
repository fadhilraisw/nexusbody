package com.rais.nexusbody.di

import android.content.Context
import androidx.room.Room
import com.rais.nexusbody.core.database.NexusBodyDatabase
import com.rais.nexusbody.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NexusBodyDatabase {
        return Room.databaseBuilder(
            context,
            NexusBodyDatabase::class.java,
            "nexusbody_local_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideUserDao(database: NexusBodyDatabase): UserDao = database.userDao()

    @Provides
    fun provideHealthDao(database: NexusBodyDatabase): HealthDao = database.healthDao()

    @Provides
    fun provideNutritionDao(database: NexusBodyDatabase): NutritionDao = database.nutritionDao()

    @Provides
    fun provideWorkoutDao(database: NexusBodyDatabase): WorkoutDao = database.workoutDao()

    @Provides
    fun provideMedicationDao(database: NexusBodyDatabase): MedicationDao = database.medicationDao()

    @Provides
    fun provideQuestDao(database: NexusBodyDatabase): QuestDao = database.questDao()

    @Provides
    fun provideGamificationDao(database: NexusBodyDatabase): GamificationDao = database.gamificationDao()

    @Provides
    fun provideAiReportDao(database: NexusBodyDatabase): AiReportDao = database.aiReportDao()
}
