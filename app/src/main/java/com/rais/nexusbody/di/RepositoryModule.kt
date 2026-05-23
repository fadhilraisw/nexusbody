package com.rais.nexusbody.di

import com.rais.nexusbody.data.repository.NutritionRepositoryImpl
import com.rais.nexusbody.data.repository.WorkoutRepositoryImpl
import com.rais.nexusbody.domain.repository.NutritionRepository
import com.rais.nexusbody.domain.repository.WorkoutRepository
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
}