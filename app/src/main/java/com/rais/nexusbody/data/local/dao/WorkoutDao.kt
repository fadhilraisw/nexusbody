package com.rais.nexusbody.data.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.rais.nexusbody.data.local.entity.WorkoutExerciseEntity
import com.rais.nexusbody.data.local.entity.WorkoutSessionEntity
import kotlinx.coroutines.flow.Flow

// class pembungkus untuk relasi 1-to-many
data class SessionWithExercises(
    @Embedded val session: WorkoutSessionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "sessionId"
    )
    val exercises: List<WorkoutExerciseEntity>
)

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<WorkoutExerciseEntity>)

    // mengambil sesi beserta seluruh gerakannya secara otomatis
    @Transaction
    @Query("SELECT * FROM workout_sessions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getSessionsWithExercises(userId: String): Flow<List<SessionWithExercises>>

    @Query("DELETE FROM workout_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: String)

    @Query("DELETE FROM workout_exercises WHERE sessionId = :sessionId")
    suspend fun deleteExercisesBySession(sessionId: String)
}