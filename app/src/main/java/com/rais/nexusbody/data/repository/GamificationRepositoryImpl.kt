package com.rais.nexusbody.data.repository

import com.rais.nexusbody.data.local.dao.GamificationDao
import com.rais.nexusbody.data.local.entity.GamificationProfileEntity
import com.rais.nexusbody.domain.repository.GamificationRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GamificationRepositoryImpl @Inject constructor(
    private val gamificationDao: GamificationDao,
    private val supabase: SupabaseClient
) : GamificationRepository {
    override fun getProfile(userId: String): Flow<GamificationProfileEntity?> = flow {
        emitAll(gamificationDao.getProfileFlow(userId))
        
        try {
            val remoteData = supabase.from("gamification_profiles")
                .select { filter { eq("userId", userId) } }
                .decodeSingleOrNull<GamificationProfileEntity>()
            if (remoteData != null) {
                gamificationDao.insertProfile(remoteData)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updateProfile(profile: GamificationProfileEntity) {
        gamificationDao.insertProfile(profile)
        try {
            supabase.from("gamification_profiles").upsert(profile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
