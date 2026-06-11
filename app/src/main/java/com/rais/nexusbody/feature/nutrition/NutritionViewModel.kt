package com.rais.nexusbody.feature.nutrition

// --- TECH STACK & NETWORK IMPORTS ---
import com.rais.nexusbody.domain.repository.NutritionRepository // Domain: Kontrak data gizi
import com.rais.nexusbody.data.local.entity.NutritionLogEntity // Room Entity: Skema tabel log makanan
import androidx.lifecycle.ViewModel // Architecture Component: Dasar logika reaktif
import androidx.lifecycle.viewModelScope // Coroutines: Manajemen thread asinkron
import com.rais.nexusbody.core.network.FoodApiService // Retrofit Service: Koneksi ke OpenFoodFacts API
import com.rais.nexusbody.data.remote.dto.FoodProductDto // GSON DTO: Objek hasil fetch API global
import com.rais.nexusbody.domain.model.NutritionGoal // Domain Model: Target makro harian
import dagger.hilt.android.lifecycle.HiltViewModel // DI: Injeksi otomatis lifecycle oleh Hilt
import kotlinx.coroutines.flow.* // Reactive: Aliran data reaktif (combine, flatMapLatest)
import kotlinx.coroutines.launch // Coroutines: Eksekusi proses background
import java.util.UUID // Utilities: Generator ID unik
import java.util.Calendar // Utilities: Manipulasi waktu (Start/End Day)
import java.util.Date // Utilities: Objek tanggal
import java.util.Locale // Utilities: Lokalisasi bahasa
import java.text.SimpleDateFormat // Utilities: Format tampilan tanggal
import io.github.jan.supabase.SupabaseClient // Backend: Klien cloud Supabase
import io.github.jan.supabase.gotrue.auth // Auth: Manajemen identitas user
import javax.inject.Inject // DI: Constructor injection

/**
 * NUTRITION VIEWMODEL (ANALYTICS ENGINE)
 * Peran: Menghitung budget makro secara real-time dan mencari data makanan global.
 * Pola: Menggunakan SSOT (Single Source of Truth) dari database lokal Room.
 */
@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val foodApi: FoodApiService, // Disuntikkan Hilt: Untuk mencari makanan di database global
    private val nutritionRepo: NutritionRepository, // Disuntikkan Hilt: Akses database log makan
    private val supabase: SupabaseClient // Disuntikkan Hilt: Akses session user
) : ViewModel() {

    // --- USER IDENTIFICATION (BACKEND FLOW) ---
    // Mengambil user ID aktif untuk memastikan log makanan tersimpan di akun yang benar.
    private val userId: String
        get() = supabase.auth.currentUserOrNull()?.id ?: "anonymous_user"

    // State internal mutable untuk menampung input sementara user (Search query, porsi, dll)
    private val _state = MutableStateFlow(NutritionUiState())
    
    /**
     * REAKTIVITAS DATA UTAMA (FRONTEND FLOW)
     * Alur: Update UI State -> Trigger Filter Waktu -> Ambil data Room -> Kalkulasi Makro -> Pancarkan ke UI.
     */
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val state: StateFlow<NutritionUiState> = _state.flatMapLatest { currentState ->
        // 1. Dapatkan rentang waktu (Start/End Timestamp) berdasarkan pilihan filter user (Daily/Weekly/dll)
        val timeframe = getStartAndEndTimestamps(currentState.mealDate, currentState.selectedTimeframe)
        
        // 2. Hubungkan aliran data UI dengan aliran data dari database (Room Flow)
        combine(
            MutableStateFlow(currentState),
            nutritionRepo.getLogsByTimeframe(userId, timeframe.first, timeframe.second)
        ) { state, logs ->
            // 3. AUTO-CALCULATION LOGIC: Hitung total Kalori, Protein, Karbo, Lemak secara otomatis.
            // Ini memastikan progress bar di UI selalu akurat tanpa tombol 'Refresh'.
            state.copy(
                todaysLogs = logs,
                currentCalories = logs.sumOf { it.calories },
                currentProtein = logs.sumOf { it.proteinGrams.toDouble() }.toFloat(),
                currentCarbs = logs.sumOf { it.carbsGrams.toDouble() }.toFloat(),
                currentFat = logs.sumOf { it.fatGrams.toDouble() }.toFloat()
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NutritionUiState()
    )

    // --- TIME FILTER ALGORITHM (BACKEND LOGIC) ---
    // Menghasilkan rentang milidetik yang presisi untuk filter database (Daily, Weekly, Monthly, Yearly).
    private fun getStartAndEndTimestamps(anchorDate: String, timeframe: String): Pair<Long, Long> {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = try { sdf.parse(anchorDate) } catch (e: Exception) { Date() }
        val cal = Calendar.getInstance()
        cal.time = date ?: Date()
        
        return when (timeframe) {
            "daily" -> {
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0)
                val start = cal.timeInMillis
                cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59)
                start to cal.timeInMillis
            }
            "weekly" -> {
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
                val start = cal.timeInMillis
                cal.add(Calendar.DAY_OF_WEEK, 6)
                cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59)
                start to cal.timeInMillis
            }
            "monthly" -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                val start = cal.timeInMillis
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                start to cal.timeInMillis
            }
            "yearly" -> {
                cal.set(Calendar.DAY_OF_YEAR, 1)
                val start = cal.timeInMillis
                cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR))
                start to cal.timeInMillis
            }
            else -> { 
                cal.set(Calendar.HOUR_OF_DAY, 0)
                val start = cal.timeInMillis
                cal.set(Calendar.HOUR_OF_DAY, 23)
                start to cal.timeInMillis
            }
        }
    }

    // Mengubah filter waktu (UX Intent)
    fun updateTimeframe(timeframe: String) {
        _state.value = _state.value.copy(selectedTimeframe = timeframe)
    }

    // --- SEARCH LOGIC (NETWORK FLOW) ---
    // Memanggil API OpenFoodFacts menggunakan Retrofit untuk mencari data gizi makanan.
    fun updateSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query, selectedFood = null)
        if (query.length > 2) searchFood(query) // Debouncing sederhana: cari jika > 2 karakter
    }

    private fun searchFood(query: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSearching = true)
            try {
                // Fetch data dari server (Global Database)
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

    // Memilih satu makanan dari hasil pencarian (Frontend State)
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

    // Menghapus log makan (CRUD: Delete)
    fun deleteLog(id: String) {
        viewModelScope.launch {
            nutritionRepo.deleteLog(id)
        }
    }

    /**
     * FUNGSI SIMPAN (CRUD: Create/Update)
     * Alur: Hitung Porsi -> Rakit Entitas -> Simpan Lokal (Room) -> Sinkron Cloud (Supabase).
     */
    fun saveLog() {
        val currentState = _state.value
        val food = currentState.selectedFood ?: return
        val weight = currentState.servingSizeGrams.toFloatOrNull() ?: 100f
        val multiplier = weight / 100f // Menghitung rasio porsi terhadap standar 100g

        // Membangun data objek log makanan dengan variabel presisi medis
        val newLog = NutritionLogEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            timestamp = combineDateAndTime(currentState.mealDate, currentState.mealTime),
            foodName = food.productName ?: "unknown",
            portionGrams = weight,
            calories = ((food.nutriments?.energyKcal ?: 0f) * multiplier).toInt(),
            proteinGrams = (food.nutriments?.proteins ?: 0f) * multiplier,
            carbsGrams = (food.nutriments?.carbs ?: 0f) * multiplier,
            fatGrams = (food.nutriments?.fat ?: 0f) * multiplier,
            isAiEstimated = false // Flag untuk membedakan data API vs Estimasi AI
        )

        viewModelScope.launch {
            // Backend Flow: Repository akan mengirim ke Room dulu baru ke Supabase
            nutritionRepo.insertLog(newLog)
            // Reset status form di UI
            _state.value = currentState.copy(selectedFood = null, searchQuery = "", servingSizeGrams = "100")
        }
    }

    // Helper: Merakit string tanggal/jam menjadi milidetik untuk sorting database
    private fun combineDateAndTime(dateStr: String, timeStr: String): Long {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return try {
            sdf.parse("$dateStr $timeStr")?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    // Update target gizi (Goal Setting)
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

// Model status UI tunggal
data class NutritionUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<FoodProductDto> = emptyList(),
    val isSearching: Boolean = false,
    val selectedFood: FoodProductDto? = null,
    val servingSizeGrams: String = "100",
    val mealTime: String = "12:00",
    val mealDate: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
    val selectedTimeframe: String = "daily",
    val todaysLogs: List<NutritionLogEntity> = emptyList(),
    val currentCalories: Int = 0,
    val currentProtein: Float = 0f,
    val currentCarbs: Float = 0f,
    val currentFat: Float = 0f,
    val goalHistory: List<NutritionGoal> = emptyList(),
    val activeGoal: NutritionGoal = NutritionGoal("1", System.currentTimeMillis(), 2400, 160, 250, 70, true)
)
