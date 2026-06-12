package com.rais.nexusbody.domain.repository

import com.rais.nexusbody.data.local.entity.GamificationProfileEntity // Entity profil XP
import kotlinx.coroutines.flow.Flow // Stream data

/**
 * GAMIFICATION REPOSITORY INTERFACE
 * Peran: Kontrak data untuk sistem pencapaian, level, dan muscle mastery.
 * Departemen: Back-End (Logic Contract).
 */
interface GamificationRepository {
    // Mendapatkan profil kemahiran user secara reaktif
    fun getProfile(userId: String): Flow<GamificationProfileEntity?>
    
    // Memperbarui data XP atau Rank user
    suspend fun updateProfile(profile: GamificationProfileEntity)
}
