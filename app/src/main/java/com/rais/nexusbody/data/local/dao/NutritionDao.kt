package com.rais.nexusbody.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rais.nexusbody.data.local.entity.NutritionLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NutritionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: NutritionLogEntity)

    // mengambil log berdasarkan rentang waktu tertentu (untuk filter daily/weekly/monthly)
    @Query("SELECT * FROM nutrition_logs WHERE userId = :userId AND timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getLogsByTimeframe(userId: String, startTime: Long, endTime: Long): Flow<List<NutritionLogEntity>>

    @Query("DELETE FROM nutrition_logs WHERE id = :id")
    suspend fun deleteLog(id: String)
}