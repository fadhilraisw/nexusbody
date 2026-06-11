package com.rais.nexusbody.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rais.nexusbody.data.local.entity.AiReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AiReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: AiReportEntity)

    @Query("SELECT * FROM ai_reports WHERE userId = :userId ORDER BY timestamp DESC")
    fun getReportsFlow(userId: String): Flow<List<AiReportEntity>>

    @Query("DELETE FROM ai_reports WHERE id = :id")
    suspend fun deleteReport(id: String)
}
