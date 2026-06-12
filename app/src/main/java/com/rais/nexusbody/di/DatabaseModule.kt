package com.rais.nexusbody.di

import android.content.Context // Context Android untuk akses sistem file
import androidx.room.Room // Framework Database Lokal
import com.rais.nexusbody.core.database.NexusBodyDatabase // Kelas database utama
import com.rais.nexusbody.data.local.dao.* // Import seluruh DAO (Data Access Objects)
import dagger.Module // Modul Hilt
import dagger.Provides // Penyedia instansi
import dagger.hilt.InstallIn // Cakupan instalasi
import dagger.hilt.android.qualifiers.ApplicationContext // Penanda context global
import dagger.hilt.components.SingletonComponent // Global scope
import javax.inject.Singleton // Objek tunggal

/**
 * DATABASE MODULE (INFRASTRUCTURE LAYER)
 * Peran: Menyediakan akses ke penyimpanan lokal ponsel menggunakan Room Database.
 * Tech Stack: Room SQLite.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // Menyediakan instansi utama database lokal (NexusBodyDatabase)
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NexusBodyDatabase {
        return Room.databaseBuilder(
            context,
            NexusBodyDatabase::class.java,
            "nexusbody_local_db" // Nama file database di memori HP
        ).fallbackToDestructiveMigration() // Reset data otomatis jika versi database naik (Development mode)
        .build()
    }

    // --- PENYEDIA DAO (DATA ACCESS OBJECTS) ---
    // DAO adalah jembatan antara kodingan Kotlin dan perintah SQL mentah

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
