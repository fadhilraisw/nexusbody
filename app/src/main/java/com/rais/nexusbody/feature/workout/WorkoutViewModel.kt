package com.rais.nexusbody.feature.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rais.nexusbody.data.local.dao.SessionWithExercises
import com.rais.nexusbody.data.local.entity.WorkoutExerciseEntity
import com.rais.nexusbody.data.local.entity.WorkoutSessionEntity
import com.rais.nexusbody.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import java.util.UUID
import javax.inject.Inject

data class WorkoutUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val repository: WorkoutRepository,
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    private val userId: String
        get() = supabase.auth.currentUserOrNull()?.id ?: "anonymous_user"

    val sessions: StateFlow<List<SessionWithExercises>> = repository.getSessionsWithExercises(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveWorkoutSession(
        routineName: String,
        duration: Int,
        notes: String,
        exercises: List<ExerciseInput>
    ) {
        viewModelScope.launch {
            val sessionId = UUID.randomUUID().toString()
            val session = WorkoutSessionEntity(
                id = sessionId,
                userId = userId,
                timestamp = System.currentTimeMillis(),
                routineName = routineName,
                totalDurationMinutes = duration,
                clinicalNotes = notes
            )

            val exerciseEntities = exercises.map { input ->
                WorkoutExerciseEntity(
                    id = UUID.randomUUID().toString(),
                    sessionId = sessionId,
                    exerciseName = input.name,
                    targetMuscles = input.muscles,
                    sets = input.sets,
                    reps = input.reps,
                    weightKg = input.weight
                )
            }

            repository.insertSession(session, exerciseEntities)
        }
    }

    fun deleteSession(id: String) {
        viewModelScope.launch {
            repository.deleteSession(id)
        }
    }
}

data class ExerciseInput(
    val name: String,
    val muscles: List<String>,
    val sets: Int,
    val reps: Int,
    val weight: Float
)
