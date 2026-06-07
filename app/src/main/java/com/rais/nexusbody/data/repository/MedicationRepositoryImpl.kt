package com.rais.nexusbody.data.repository

import com.rais.nexusbody.data.local.dao.MedicationDao
import com.rais.nexusbody.data.local.entity.MedicationEntity
import com.rais.nexusbody.domain.repository.MedicationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MedicationRepositoryImpl @Inject constructor(
    private val medicationDao: MedicationDao
) : MedicationRepository {
    override fun getActiveMedications(userId: String): Flow<List<MedicationEntity>> {
        return medicationDao.getActiveMedicationsFlow(userId)
    }

    override fun getAllMedications(userId: String): Flow<List<MedicationEntity>> {
        return medicationDao.getAllMedicationsFlow(userId)
    }

    override suspend fun insertMedication(medication: MedicationEntity) {
        medicationDao.insertMedication(medication)
    }

    override suspend fun deleteMedication(id: String) {
        medicationDao.deleteMedication(id)
    }
}
