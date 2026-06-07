package com.rais.nexusbody.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rais.nexusbody.data.local.entity.MedicationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: MedicationEntity)

    @Query("SELECT * FROM medications WHERE userId = :userId AND isActive = 1")
    fun getActiveMedicationsFlow(userId: String): Flow<List<MedicationEntity>>

    @Query("SELECT * FROM medications WHERE userId = :userId ORDER BY startDate DESC")
    fun getAllMedicationsFlow(userId: String): Flow<List<MedicationEntity>>

    @Query("DELETE FROM medications WHERE id = :id")
    suspend fun deleteMedication(id: String)
}
