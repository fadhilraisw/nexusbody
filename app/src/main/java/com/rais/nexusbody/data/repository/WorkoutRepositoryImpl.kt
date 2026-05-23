package com.rais.nexusbody.data.repository

import com.rais.nexusbody.data.local.dao.WorkoutDao
import com.rais.nexusbody.data.local.dao.SessionWithExercises
import com.rais.nexusbody.data.local.entity.WorkoutExerciseEntity
import com.rais.nexusbody.data.local.entity.WorkoutSessionEntity
import com.rais.nexusbody.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao
) : WorkoutRepository {

    override fun getSessionsWithExercises(userId: String): Flow<List<SessionWithExercises>> {
        return workoutDao.getSessionsWithExercises(userId)
    }

    override suspend fun insertSession(session: WorkoutSessionEntity, exercises: List<WorkoutExerciseEntity>) {
        workoutDao.insertSession(session)
        workoutDao.insertExercises(exercises)
    }

    override suspend fun deleteSession(sessionId: String) {
        workoutDao.deleteExercisesBySession(sessionId) // hapus anak dulu
        workoutDao.deleteSession(sessionId) // baru hapus induk
    }
}