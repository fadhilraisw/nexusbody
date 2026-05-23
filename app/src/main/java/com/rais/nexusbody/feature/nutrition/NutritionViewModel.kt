package com.rais.nexusbody.feature.nutrition

import com.rais.nexusbody.domain.repository.NutritionRepository
import com.rais.nexusbody.data.local.entity.NutritionLogEntity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rais.nexusbody.core.network.FoodApiService
import com.rais.nexusbody.data.remote.dto.FoodProductDto
import com.rais.nexusbody.domain.model.NutritionGoal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


data class NutritionUiState(
    val searchQuery: String = "",
    val searchResults: List<FoodProductDto> = emptyList(),
    val isSearching: Boolean = false,
    val selectedFood: FoodProductDto? = null,
    val servingSizeGrams: String = "100",
    val mealTime: String = "12:00",
    val mealDate: String = "14/05/2026",
    val goalHistory: List<NutritionGoal> = emptyList(),
    val activeGoal: NutritionGoal = NutritionGoal("1", System.currentTimeMillis(), 2400, 160, 250, 70, true)
)

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val foodApi: FoodApiService,
    private val nutritionRepo: NutritionRepository // injeksi repo baru
) : ViewModel() {

    private val _state = MutableStateFlow(NutritionUiState())
    val state: StateFlow<NutritionUiState> = _state.asStateFlow()

    fun updateSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        if (query.length > 2) searchFood(query)
    }

    private fun searchFood(query: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSearching = true)
            try {
                val response = foodApi.searchFood(query)
                _state.value = _state.value.copy(
                    searchResults = response.products.filter { it.productName != null },
                    isSearching = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isSearching = false)
            }
        }
    }

    fun selectFood(food: FoodProductDto) {
        _state.value = _state.value.copy(
            selectedFood = food,
            searchQuery = food.productName ?: "",
            searchResults = emptyList()
        )
    }

    fun updateServingSize(grams: String) {
        _state.value = _state.value.copy(servingSizeGrams = grams)
    }

    fun updateDateTime(time: String, date: String) {
        _state.value = _state.value.copy(mealTime = time, mealDate = date)
    }

    fun saveLog() {
        val currentState = _state.value
        val food = currentState.selectedFood ?: return
        val weight = currentState.servingSizeGrams.toFloatOrNull() ?: 100f
        val multiplier = weight / 100f

        val newLog = NutritionLogEntity(
            id = UUID.randomUUID().toString(),
            userId = "dummy_user_123", // data sementara sebelum modul login dibuat
            timestamp = System.currentTimeMillis(),
            foodName = food.productName ?: "unknown",
            portionGrams = weight,
            calories = ((food.nutriments?.energyKcal ?: 0f) * multiplier).toInt(),
            proteinGrams = (food.nutriments?.proteins ?: 0f) * multiplier,
            carbsGrams = (food.nutriments?.carbs ?: 0f) * multiplier,
            fatGrams = (food.nutriments?.fat ?: 0f) * multiplier,
            isAiEstimated = false
        )

        viewModelScope.launch {
            nutritionRepo.insertLog(newLog)
            // reset ui setelah berhasil disimpan
            _state.value = currentState.copy(selectedFood = null, searchQuery = "", servingSizeGrams = "100")
        }
    }

    fun updateDailyGoal(cal: Int, pro: Int, carb: Int, fat: Int) {
        val newGoal = NutritionGoal(
            id = UUID.randomUUID().toString(),
            dateSet = System.currentTimeMillis(),
            calorieTarget = cal,
            proteinTarget = pro,
            carbTarget = carb,
            fatTarget = fat,
            isActive = true
        )

        // arsipkan goal lama ke history, set yang baru jadi aktif
        val currentActive = _state.value.activeGoal.copy(isActive = false)
        val updatedHistory = _state.value.goalHistory + currentActive

        _state.value = _state.value.copy(
            activeGoal = newGoal,
            goalHistory = updatedHistory
        )
    }

    fun deleteGoalHistory(goalId: String) {
        _state.value = _state.value.copy(
            goalHistory = _state.value.goalHistory.filter { it.id != goalId }
        )
    }
}