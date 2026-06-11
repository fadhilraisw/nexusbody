package com.rais.nexusbody.feature.workout

import androidx.lifecycle.ViewModel // Framework ViewModel
import androidx.lifecycle.viewModelScope // Scope coroutine terikat lifecycle VM
import com.rais.nexusbody.data.local.dao.SessionWithExercises // Relasi database Sesi & Gerakan
import com.rais.nexusbody.data.local.entity.WorkoutExerciseEntity // Entitas Tabel Gerakan
import com.rais.nexusbody.data.local.entity.WorkoutSessionEntity // Entitas Tabel Sesi Workout
import com.rais.nexusbody.domain.repository.WorkoutRepository // Kontrak data workout
import com.rais.nexusbody.domain.repository.GamificationRepository // Kontrak data leveling
import com.rais.nexusbody.data.local.entity.GamificationProfileEntity // Entitas profil gamifikasi
import dagger.hilt.android.lifecycle.HiltViewModel // Injeksi otomatis Hilt
import kotlinx.coroutines.flow.* // Aliran data asinkron reaktif
import kotlinx.coroutines.launch // Menjalankan coroutine
import io.github.jan.supabase.SupabaseClient // Klien Supabase
import io.github.jan.supabase.gotrue.auth // Sistem login
import java.util.UUID // ID unik
import javax.inject.Inject // Injeksi constructor

// State sederhana untuk menangani status asinkron di layar Workout
data class WorkoutUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val repository: WorkoutRepository, // Injeksi repo latihan
    private val gamificationRepo: GamificationRepository, // Injeksi repo leveling
    private val supabase: SupabaseClient // Injeksi Supabase
) : ViewModel() {

    // Aliran status UI
    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    // Identitas user login
    private val userId: String
        get() = supabase.auth.currentUserOrNull()?.id ?: "anonymous_user"

    // State filter waktu untuk workout (Default: Weekly)
    private val _timeframe = MutableStateFlow("weekly")
    val selectedTimeframe: StateFlow<String> = _timeframe.asStateFlow()

    // Ambil data riwayat sesi latihan secara reaktif berdasarkan filter waktu
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val sessions: StateFlow<List<SessionWithExercises>> = _timeframe.flatMapLatest { timeframe ->
        repository.getSessionsWithExercises(userId)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Update filter waktu dari UI
    fun updateTimeframe(timeframe: String) {
        _timeframe.value = timeframe
    }

    // Fungsi UTAMA: Menyimpan satu sesi latihan lengkap beserta gerakan-gerakannya
    fun saveWorkoutSession(
        routineName: String,
        duration: Int,
        notes: String,
        exercises: List<ExerciseInput>,
        customTimestamp: Long? = null
    ) {
        viewModelScope.launch {
            val sessionId = UUID.randomUUID().toString()
            val timestamp = customTimestamp ?: System.currentTimeMillis()

            // LOGIKA GAMIFIKASI: Hitung bonus XP jika latihan variatif (>4 jenis otot)
            val xpBonus = if (exercises.flatMap { it.muscles }.distinct().size >= 4) 10 else 0

            // 1. Siapkan data header Sesi
            val session = WorkoutSessionEntity(
                id = sessionId,
                userId = userId,
                timestamp = timestamp,
                routineName = routineName,
                totalDurationMinutes = duration,
                clinicalNotes = notes,
                xpEarned = xpBonus
            )

            // 2. Siapkan data detail tiap Gerakan
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

            // 3. Simpan sesi & gerakan ke database (Room + Supabase)
            repository.insertSession(session, exerciseEntities)
            
            // 4. Update level kemahiran otot di sistem Gamifikasi
            updateMuscleMastery(exerciseEntities, xpBonus)
        }
    }

    // Logika Distribusi XP ke masing-masing kelompok otot di profil user
    private suspend fun updateMuscleMastery(exercises: List<WorkoutExerciseEntity>, bonus: Int) {
        // Ambil profil gamifikasi terakhir
        val profile = gamificationRepo.getProfile(userId).first() ?: GamificationProfileEntity(userId = userId)
        val uniqueMuscles = exercises.flatMap { it.targetMuscles }.distinct()
        
        var updatedProfile = profile
        // Tambahkan XP ke variabel otot spesifik
        uniqueMuscles.forEach { muscle ->
            updatedProfile = when(muscle) {
                "CHEST" -> updatedProfile.copy(chestXp = updatedProfile.chestXp + 5)
                "BACK_UPPER" -> updatedProfile.copy(backUpperXp = updatedProfile.backUpperXp + 5)
                "BICEPS" -> updatedProfile.copy(bicepsXp = updatedProfile.bicepsXp + 5)
                "QUADRICEPS" -> updatedProfile.copy(quadricepsXp = updatedProfile.quadricepsXp + 5)
                "CORE_ABS" -> updatedProfile.copy(coreAbsXp = updatedProfile.coreAbsXp + 5)
                else -> updatedProfile
            }
        }
        
        // Simpan update profil (Level Up Logic terjadi di sini secara asumsif)
        gamificationRepo.updateProfile(updatedProfile.copy(
            totalNutritionXp = updatedProfile.totalNutritionXp + bonus
        ))
    }

    // Fungsi hapus riwayat latihan
    fun deleteSession(id: String) {
        viewModelScope.launch {
            repository.deleteSession(id)
        }
    }
}

// Data class input sederhana dari UI
data class ExerciseInput(
    val name: String,
    val muscles: List<String>,
    val sets: Int,
    val reps: Int,
    val weight: Float
)
