package com.rais.nexusbody.data.repository

import com.rais.nexusbody.data.local.dao.SessionWithExercises
import com.rais.nexusbody.data.local.dao.WorkoutDao
import com.rais.nexusbody.data.local.entity.WorkoutExerciseEntity
import com.rais.nexusbody.data.local.entity.WorkoutSessionEntity
import com.rais.nexusbody.domain.repository.WorkoutRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val supabase: SupabaseClient
) : WorkoutRepository {

    override fun getSessionsWithExercises(userId: String): Flow<List<SessionWithExercises>> = flow {
        emitAll(workoutDao.getSessionsWithExercises(userId))
        
        try {
            val remoteSessions = supabase.from("workout_sessions")
                .select { filter { eq("userId", userId) } }
                .decodeList<WorkoutSessionEntity>()
            
            val remoteExercises = supabase.from("workout_exercises")
                .select() // Ideally filter by sessionIds
                .decodeList<WorkoutExerciseEntity>()
                
            remoteSessions.forEach { workoutDao.insertSession(it) }
            remoteExercises.forEach { workoutDao.insertExercise(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun insertSession(session: WorkoutSessionEntity, exercises: List<WorkoutExerciseEntity>) {
        workoutDao.insertSession(session)
        exercises.forEach { workoutDao.insertExercise(it) }
        
        try {
            supabase.from("workout_sessions").upsert(session)
            exercises.forEach { supabase.from("workout_exercises").upsert(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteSession(sessionId: String) {
        workoutDao.deleteSession(sessionId)
        try {
            supabase.from("workout_sessions").delete { filter { eq("id", sessionId) } }
            supabase.from("workout_exercises").delete { filter { eq("sessionId", sessionId) } }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
