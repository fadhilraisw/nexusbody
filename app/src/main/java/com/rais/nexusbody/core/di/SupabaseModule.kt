package com.rais.nexusbody.core.di

import com.rais.nexusbody.BuildConfig // Variabel aman dari local.properties
import dagger.Module // Modul DI
import dagger.Provides // Penyedia instansi
import dagger.hilt.InstallIn // Cakupan DI
import dagger.hilt.components.SingletonComponent // Global Scope
import io.github.jan.supabase.SupabaseClient // SDK Supabase Core
import io.github.jan.supabase.createSupabaseClient // Fungsi builder Supabase
import io.github.jan.supabase.gotrue.Auth // Modul Autentikasi
import io.github.jan.supabase.postgrest.Postgrest // Modul Database Real-time
import javax.inject.Singleton // Objek tunggal

/**
 * SUPABASE INFRASTRUCTURE MODULE
 * Peran: Konfigurasi jembatan utama antara aplikasi Android dan Backend Cloud Supabase.
 * Tech Stack: Supabase Kotlin SDK (Auth & Postgrest).
 */
@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    // Menyediakan Klien Supabase Tunggal untuk seluruh aplikasi
    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            // Data diambil dari local.properties melalui BuildConfig (Production Ready)
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            // Menginstall modul-modul yang diperlukan aplikasi
            install(Auth) // Untuk Login/Register
            install(Postgrest) // Untuk CRUD Database Cloud
        }
    }
}
