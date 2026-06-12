package com.rais.nexusbody.di

import com.rais.nexusbody.core.network.FoodApiService // Service pencarian makanan
import com.rais.nexusbody.core.network.GroqApiService // Service AI Groq
import dagger.Module // Penanda modul DI
import dagger.Provides // Penyedia instansi objek
import dagger.hilt.InstallIn // Penentu cakupan modul
import dagger.hilt.components.SingletonComponent // Cakupan global (selama app hidup)
import retrofit2.Retrofit // Framework networking
import retrofit2.converter.gson.GsonConverterFactory // Parser JSON ke Objek Kotlin
import javax.inject.Named // Pembeda jika ada dua instansi tipe yang sama
import javax.inject.Singleton // Menjamin objek hanya dibuat satu kali

/**
 * NETWORK MODULE (INFRASTRUCTURE LAYER)
 * Peran: Menyediakan koneksi HTTP ke API eksternal (Food Database & Groq AI).
 * Tech Stack: Retrofit & GSON.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Menyediakan konfigurasi Retrofit khusus untuk Database Makanan (OpenFoodFacts)
    @Provides
    @Singleton
    @Named("FoodRetrofit")
    fun provideFoodRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/") // URL dasar API makanan
            .addConverterFactory(GsonConverterFactory.create()) // Otomatis ubah JSON ke Class
            .build()
    }

    // Menyediakan konfigurasi Retrofit khusus untuk API AI Groq
    @Provides
    @Singleton
    @Named("GroqRetrofit")
    fun provideGroqRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.groq.com/openai/") // URL dasar Groq
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Menghubungkan Interface FoodApiService dengan engine Retrofit
    @Provides
    @Singleton
    fun provideFoodApiService(@Named("FoodRetrofit") retrofit: Retrofit): FoodApiService {
        return retrofit.create(FoodApiService::class.java)
    }

    // Menghubungkan Interface GroqApiService dengan engine Retrofit
    @Provides
    @Singleton
    fun provideGroqApiService(@Named("GroqRetrofit") retrofit: Retrofit): GroqApiService {
        return retrofit.create(GroqApiService::class.java)
    }
}
