package com.rais.nexusbody.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// state untuk menyimpan data layar
data class DashboardUiState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val totalCalories: Int = 0,
    val totalXp: Int = 0,
    val streak: Int = 0,
    val rank: String = "",
    val healthScore: Int = 0
)

@HiltViewModel
class DashboardViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // simulasi loading data dari database (nanti diganti pakai usecase)
            delay(1000)

            _uiState.value = DashboardUiState(
                isLoading = false,
                userName = "Rais",
                totalCalories = 2150,
                totalXp = 450,
                streak = 14,
                rank = "Platinum I",
                healthScore = 85
            )
        }
    }
}