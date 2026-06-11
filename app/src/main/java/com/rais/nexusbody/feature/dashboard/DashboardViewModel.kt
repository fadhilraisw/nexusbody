package com.rais.nexusbody.feature.dashboard

// --- TECH STACK & DATA AGGREGATION IMPORTS ---
import androidx.lifecycle.ViewModel // Architecture Component: Jembatan data ke UI
import androidx.lifecycle.viewModelScope // Coroutines: Manajemen thread (Main vs IO)
import com.rais.nexusbody.domain.repository.HealthAssessmentRepository // Domain: Kontrak data medis
import com.rais.nexusbody.domain.repository.NutritionRepository // Domain: Kontrak data gizi
import com.rais.nexusbody.domain.repository.WorkoutRepository // Domain: Kontrak data latihan
import com.rais.nexusbody.domain.repository.MedicationRepository // Domain: Kontrak data obat
import com.rais.nexusbody.data.local.entity.HealthAssessmentEntity // Entity: Struktur biomarker
import com.rais.nexusbody.data.local.entity.NutritionLogEntity // Entity: Struktur kalori
import com.rais.nexusbody.data.local.entity.WorkoutSessionEntity // Entity: Struktur latihan
import com.rais.nexusbody.data.local.entity.MedicationEntity // Entity: Struktur pengingat
import dagger.hilt.android.lifecycle.HiltViewModel // DI: Injeksi dependensi otomatis
import kotlinx.coroutines.flow.SharingStarted // Reactive: Strategi sharing aliran data
import kotlinx.coroutines.flow.StateFlow // Reactive: State penampung data UI
import kotlinx.coroutines.flow.combine // Reactive: Penggabung banyak aliran data (Aggregation)
import kotlinx.coroutines.flow.stateIn // Reactive: Konversi Flow ke StateFlow yang hot
import io.github.jan.supabase.SupabaseClient // Backend: Session user cloud
import io.github.jan.supabase.gotrue.auth // Auth: Deteksi user aktif
import java.util.Calendar // Utilities: Manipulasi rentang waktu harian
import javax.inject.Inject // DI: Constructor injection

/**
 * DASHBOARD VIEWMODEL (THE REACTIVE BRAIN)
 * Peran: Menjadi agregator data dari 4 departemen berbeda (Health, Nutrition, Workout, Meds).
 * Strategi: Menggunakan pola 'Combine' untuk menciptakan Dashboard yang 100% tersinkronisasi.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val healthRepo: HealthAssessmentRepository, // Disuntikkan Hilt: Departemen Medis
    private val nutritionRepo: NutritionRepository, // Disuntikkan Hilt: Departemen Gizi
    private val workoutRepo: WorkoutRepository, // Disuntikkan Hilt: Departemen Latihan
    private val medicationRepo: MedicationRepository, // Disuntikkan Hilt: Departemen Obat
    private val supabase: SupabaseClient // Disuntikkan Hilt: Cek User Login
) : ViewModel() {

    // --- USER IDENTITY (BACKEND CONTEXT) ---
    // Mengambil user ID yang sedang login agar Dashboard tidak menampilkan data orang lain.
    private val userId: String
        get() = supabase.auth.currentUserOrNull()?.id ?: "anonymous_user"

    /**
     * DASHBOARD STATE AGGREGATOR (BACKEND FLOW)
     * Alur: Pantau 4 tabel database -> Jika salah satu berubah -> Hitung ulang semuanya -> Kirim ke Dashboard.
     * Keunggulan: Dashboard akan otomatis terupdate jika Anda isi makan di page Nutrition tanpa perlu refresh.
     */
    val uiState: StateFlow<DashboardUiState> = combine(
        healthRepo.getAssessments(userId), // Aliran data Biomarker (Tensi, Lemak, dll)
        workoutRepo.getSessionsWithExercises(userId), // Aliran riwayat latihan
        nutritionRepo.getLogsByTimeframe(userId, getStartOfDay(), getEndOfDay()), // Aliran kalori hari ini
        medicationRepo.getActiveMedications(userId) // Aliran jadwal obat yang aktif
    ) { healthList, workoutList, nutritionList, medList ->
        // LOGIKA PEMROSESAN DATA:
        val latestHealth = healthList.firstOrNull() // Ambil biomarker paling baru
        val latestWorkout = workoutList.firstOrNull()?.session // Ambil latihan terakhir
        val totalCalories = nutritionList.sumOf { it.calories } // Akumulasi kalori masuk hari ini

        // Rakit menjadi satu paket data (State) untuk Dashboard
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
        started = SharingStarted.WhileSubscribed(5000), // Hemat baterai: Mati jika user tidak buka Home
        initialValue = DashboardUiState(isLoading = true) // Tampilan awal saat database memuat
    )

    // --- TIME WINDOW LOGIC (ALGORITHM) ---
    // Dashboard hanya fokus pada jendela waktu 24 jam terakhir (Hari ini).
    private fun getStartOfDay(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun getEndOfDay(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59); cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }
}

// Data class pembungkus seluruh variabel Dashboard (Front-End State)
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
