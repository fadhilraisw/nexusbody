package com.rais.nexusbody.domain.repository

import com.rais.nexusbody.data.local.entity.MedicationEntity
import kotlinx.coroutines.flow.Flow

interface MedicationRepository {
    fun getActiveMedications(userId: String): Flow<List<MedicationEntity>>
    fun getAllMedications(userId: String): Flow<List<MedicationEntity>>
    suspend fun insertMedication(medication: MedicationEntity)
    suspend fun deleteMedication(id: String)
}
