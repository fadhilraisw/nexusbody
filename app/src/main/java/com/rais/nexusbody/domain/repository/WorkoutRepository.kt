package com.rais.nexusbody.domain.repository

import com.rais.nexusbody.data.local.dao.SessionWithExercises
import com.rais.nexusbody.data.local.entity.WorkoutExerciseEntity
import com.rais.nexusbody.data.local.entity.WorkoutSessionEntity
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun getSessionsWithExercises(userId: String): Flow<List<SessionWithExercises>>
    suspend fun insertSession(session: WorkoutSessionEntity, exercises: List<WorkoutExerciseEntity>)
    suspend fun deleteSession(sessionId: String)
}