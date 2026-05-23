package com.rais.nexusbody.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rais.nexusbody.data.local.entity.HealthAssessmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: HealthAssessmentEntity)

    @Query("SELECT * FROM health_assessments WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAssessmentsFlow(userId: String): Flow<List<HealthAssessmentEntity>>

    @Query("DELETE FROM health_assessments WHERE id = :id")
    suspend fun deleteAssessment(id: String)
}