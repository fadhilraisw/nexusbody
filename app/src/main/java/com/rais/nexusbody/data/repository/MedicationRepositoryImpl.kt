package com.rais.nexusbody.data.repository

import com.rais.nexusbody.data.local.dao.MedicationDao
import com.rais.nexusbody.data.local.entity.MedicationEntity
import com.rais.nexusbody.domain.repository.MedicationRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MedicationRepositoryImpl @Inject constructor(
    private val medicationDao: MedicationDao,
    private val supabase: SupabaseClient
) : MedicationRepository {
    override fun getActiveMedications(userId: String): Flow<List<MedicationEntity>> = flow {
        emitAll(medicationDao.getActiveMedicationsFlow(userId))
        
        try {
            val remoteData = supabase.from("medications")
                .select { 
                    filter { 
                        eq("userId", userId)
                        eq("isActive", true)
                    } 
                }
                .decodeList<MedicationEntity>()
            remoteData.forEach { medicationDao.insertMedication(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getAllMedications(userId: String): Flow<List<MedicationEntity>> = flow {
        emitAll(medicationDao.getAllMedicationsFlow(userId))
        
        try {
            val remoteData = supabase.from("medications")
                .select { filter { eq("userId", userId) } }
                .decodeList<MedicationEntity>()
            remoteData.forEach { medicationDao.insertMedication(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun insertMedication(medication: MedicationEntity) {
        medicationDao.insertMedication(medication)
        try {
            supabase.from("medications").upsert(medication)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteMedication(id: String) {
        medicationDao.deleteMedication(id)
        try {
            supabase.from("medications").delete { filter { eq("id", id) } }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
