package com.rais.nexusbody.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rais.nexusbody.data.local.entity.GamificationProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GamificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: GamificationProfileEntity)

    @Query("SELECT * FROM gamification_profiles WHERE userId = :userId LIMIT 1")
    fun getProfileFlow(userId: String): Flow<GamificationProfileEntity?>
}
