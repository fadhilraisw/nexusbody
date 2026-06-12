package com.rais.nexusbody.data.repository

import com.rais.nexusbody.data.local.dao.GamificationDao // DAO XP & Profil
import com.rais.nexusbody.data.local.entity.GamificationProfileEntity // Tabel Profil
import com.rais.nexusbody.domain.repository.GamificationRepository // Kontrak Domain
import io.github.jan.supabase.SupabaseClient // SDK Supabase
import io.github.jan.supabase.postgrest.from // Helper Tabel
import kotlinx.coroutines.flow.Flow // Flow
import kotlinx.coroutines.flow.emitAll // Stream
import kotlinx.coroutines.flow.flow // Generator
import javax.inject.Inject // DI

/**
 * GAMIFICATION REPOSITORY IMPLEMENTATION
 * Peran: Menangani progres leveling, XP, dan rank user secara cloud-synced.
 */
class GamificationRepositoryImpl @Inject constructor(
    private val gamificationDao: GamificationDao, // Database Lokal
    private val supabase: SupabaseClient // Database Cloud
) : GamificationRepository {

    // Ambil profil leveling user (XP per otot, Rank global)
    override fun getProfile(userId: String): Flow<GamificationProfileEntity?> = flow {
        // 1. Tampilkan XP lokal segera
        emitAll(gamificationDao.getProfileFlow(userId))
        
        // 2. Sinkron dengan Cloud untuk memastikan XP tidak hilang saat ganti perangkat
        try {
            val remoteData = supabase.from("gamification_profiles")
                .select { filter { eq("userId", userId) } }
                .decodeSingleOrNull<GamificationProfileEntity>()
            
            if (remoteData != null) {
                // Simpan update cloud ke lokal
                gamificationDao.insertProfile(remoteData)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // UPDATE: Simpan progres XP terbaru
    override suspend fun updateProfile(profile: GamificationProfileEntity) {
        gamificationDao.insertProfile(profile)
        try {
            supabase.from("gamification_profiles").upsert(profile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
