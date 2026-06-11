package com.rais.nexusbody.di

import com.rais.nexusbody.data.repository.*
import com.rais.nexusbody.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNutritionRepository(
        nutritionRepositoryImpl: NutritionRepositoryImpl
    ): NutritionRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutRepository(
        workoutRepositoryImpl: WorkoutRepositoryImpl
    ): WorkoutRepository

    @Binds
    @Singleton
    abstract fun bindHealthAssessmentRepository(
        healthAssessmentRepositoryImpl: HealthAssessmentRepositoryImpl
    ): HealthAssessmentRepository

    @Binds
    @Singleton
    abstract fun bindMedicationRepository(
        medicationRepositoryImpl: MedicationRepositoryImpl
    ): MedicationRepository

    @Binds
    @Singleton
    abstract fun bindQuestRepository(
        questRepositoryImpl: QuestRepositoryImpl
    ): QuestRepository

    @Binds
    @Singleton
    abstract fun bindGamificationRepository(
        gamificationRepositoryImpl: GamificationRepositoryImpl
    ): GamificationRepository

    @Binds
    @Singleton
    abstract fun bindAiReportRepository(
        aiReportRepositoryImpl: AiReportRepositoryImpl
    ): AiReportRepository
}
