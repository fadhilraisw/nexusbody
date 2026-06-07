package com.rais.nexusbody.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rais.nexusbody.domain.repository.HealthAssessmentRepository
import com.rais.nexusbody.domain.repository.MedicationRepository
import com.rais.nexusbody.domain.repository.NutritionRepository
import com.rais.nexusbody.domain.repository.WorkoutRepository
import com.rais.nexusbody.data.local.entity.HealthAssessmentEntity
import com.rais.nexusbody.data.local.entity.MedicationEntity
import com.rais.nexusbody.data.local.entity.NutritionLogEntity
import com.rais.nexusbody.data.local.entity.WorkoutSessionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import java.util.Calendar
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = true,
    val userName: String = "Rais",
    val latestHealth: HealthAssessmentEntity? = null,
    val todayNutrition: List<NutritionLogEntity> = emptyList(),
    val latestWorkout: WorkoutSessionEntity? = null,
    val activeMedications: List<MedicationEntity> = emptyList(),
    val totalCalories: Int = 0,
    val totalXp: Int = 0,
    val streak: Int = 14,
    val rank: String = "Platinum I",
    val healthScore: Int = 85
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    healthRepo: HealthAssessmentRepository,
    nutritionRepo: NutritionRepository,
    workoutRepo: WorkoutRepository,
    medicationRepo: MedicationRepository,
    private val supabase: SupabaseClient
) : ViewModel() {

    private val userId: String
        get() = supabase.auth.currentUserOrNull()?.id ?: "anonymous_user"

    val uiState: StateFlow<DashboardUiState> = combine(
        healthRepo.getAssessments(userId),
        workoutRepo.getSessionsWithExercises(userId),
        nutritionRepo.getLogsByTimeframe(userId, getStartOfDay(), getEndOfDay()),
        medicationRepo.getActiveMedications(userId)
    ) { healthList, workoutList, nutritionList, medList ->
        val latestHealth = healthList.firstOrNull()
        val latestWorkout = workoutList.firstOrNull()?.session
        val totalCalories = nutritionList.sumOf { it.calories }

        DashboardUiState(
            isLoading = false,
            latestHealth = latestHealth,
            latestWorkout = latestWorkout,
            activeMedications = medList,
            todayNutrition = nutritionList,
            totalCalories = totalCalories
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(isLoading = true)
    )

    private fun getStartOfDay(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun getEndOfDay(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }
}
