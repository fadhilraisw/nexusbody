package com.rais.nexusbody.domain.repository

import com.rais.nexusbody.data.local.entity.GamificationProfileEntity
import kotlinx.coroutines.flow.Flow

interface GamificationRepository {
    fun getProfile(userId: String): Flow<GamificationProfileEntity?>
    suspend fun updateProfile(profile: GamificationProfileEntity)
}
