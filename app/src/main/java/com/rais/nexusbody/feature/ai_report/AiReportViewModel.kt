package com.rais.nexusbody.feature.ai_report

import androidx.lifecycle.ViewModel // Framework ViewModel
import androidx.lifecycle.viewModelScope // Coroutine scope terikat lifecycle VM
import com.rais.nexusbody.data.local.entity.AiReportEntity // Room: Entitas data report AI
import com.rais.nexusbody.domain.repository.AiReportRepository // Domain: Kontrak data report
import com.rais.nexusbody.domain.repository.HealthAssessmentRepository // Domain: Akses data kesehatan (Context)
import com.rais.nexusbody.domain.repository.NutritionRepository // Domain: Akses data gizi (Context)
import com.rais.nexusbody.domain.repository.WorkoutRepository // Domain: Akses data latihan (Context)
import com.rais.nexusbody.core.network.AiManager // Advanced AI: Pustaka multi-model Gemini & Groq
import dagger.hilt.android.lifecycle.HiltViewModel // DI: Injeksi otomatis oleh Hilt
import kotlinx.coroutines.flow.* // Reactive: Aliran data stream
import kotlinx.coroutines.launch // Coroutines: Menjalankan eksekusi background
import io.github.jan.supabase.SupabaseClient // Backend: Klien cloud Supabase
import io.github.jan.supabase.gotrue.auth // Auth: Sistem manajemen user
import java.util.UUID // ID unik
import javax.inject.Inject // DI: Constructor injection

/**
 * AI REPORT VIEWMODEL (INTELLIGENCE HUB)
 * Peran: Menghubungkan seluruh departemen data (Health, Nutrition, Workout) ke "Otak" AI (Gemini/Groq).
 * Strategi: Menggunakan pola Professional Clinical Prompting untuk inferensi variabel silang.
 */
@HiltViewModel
class AiReportViewModel @Inject constructor(
    private val healthRepo: HealthAssessmentRepository, // Konteks: Data biomarker
    private val nutritionRepo: NutritionRepository, // Konteks: Data akumulasi kalori
    private val workoutRepo: WorkoutRepository, // Konteks: Data volume latihan
    private val aiRepo: AiReportRepository, // Penyimpanan: Riwayat analisa AI
    private val aiManager: AiManager, // Mesin AI: Switcher Gemini vs Groq
    private val supabase: SupabaseClient // Auth: Session management
) : ViewModel() {

    // Identitas user login
    private val userId: String
        get() = supabase.auth.currentUserOrNull()?.id ?: "anonymous_user"

    private val _actionState = MutableStateFlow(AiReportUiState())

    // Aliran data state UTAMA
    val state: StateFlow<AiReportUiState> = combine(
        aiRepo.getReports(userId),
        aiManager.selectedModel,
        _actionState
    ) { reports, model, action ->
        action.copy(reports = reports, selectedModel = model)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AiReportUiState(isLoading = true))

    fun setAiModel(model: String) {
        aiManager.setModel(model)
    }

    /**
     * LOGIKA UTAMA: PROFESSIONAL CLINICAL SYNTHESIS (BACKEND FLOW)
     * Strategi: Cross-referencing antar variabel medis untuk mencapai standar Clinical Grade.
     */
    fun generateSynthesis(customPrompt: String, targetAudience: String) {
        viewModelScope.launch {
            _actionState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // 1. COLLECT CONTEXT: Ambil data terakhir dari seluruh modul
                val health = healthRepo.getAssessments(userId).first().firstOrNull()
                val nutrition = nutritionRepo.getLogsByTimeframe(userId, 0, System.currentTimeMillis()).first()
                val workout = workoutRepo.getSessionsWithExercises(userId).first().firstOrNull()

                // 2. PERSONA & SYSTEM PROMPT SELECTION (Clinical Inference Layer)
                val systemRolePrompt = when(targetAudience.lowercase()) {
                    "holistic" -> """
                        Bertindaklah sebagai Konsultan Kesehatan Holistik & Longevity Coach.
                        Tugas: Analisis profil data kesehatan dengan fokus pada tren jangka panjang dan efisiensi metabolik.
                        Berikan laporan terstruktur: The Big Picture, Metabolic Efficiency, System Status (Sleep/Gut), Blueprint for the week, dan Next Diagnostic window.
                    """.trimIndent()
                    "sports doctor" -> """
                        Bertindaklah sebagai Konsultan Senior Kedokteran Olahraga (Sports Medicine) dan Ahli Endokrinologi.
                        Tugas: Lakukan audit klinis komprehensif. Lakukan korelasi silang antara variabel (misal: Ht/Hb dengan Tekanan Darah, atau SGOT dengan penggunaan PED).
                        Gaya bahasa: Teknismedis, objektif, Red Flags & Pathology identification.
                    """.trimIndent()
                    "clinical nutritionist" -> """
                        Bertindaklah sebagai Ahli Gizi Klinis untuk atlet profesional.
                        Tugas: Korelasikan GDP dan profil lipid dengan asupan kalori hari ini. Evaluasi kesehatan pencernaan terhadap beban latihan.
                        Berikan Nutritional Protocol: Preskripsi Makro, Timing & Hidrasi, dan Supplementation.
                    """.trimIndent()
                    "hypertrophy pt" -> """
                        Bertindaklah sebagai Lead Strength & Conditioning Coach elite.
                        Tugas: Physique Audit (LBM/BF), Stress Markers Analysis (RHR vs Liver enzymes/Renal stress), dan Anabolic Index (Testo/E2 ratio).
                        Desain strategi periodisasi 4-8 minggu ke depan.
                    """.trimIndent()
                    else -> "Bertindaklah sebagai pakar kesehatan medis binaraga profesional."
                }

                // 3. VARIABLE MAPPING (Clinical Data Context)
                val contextData = """
                    $systemRolePrompt
                    
                    USER DATA SET FOR CROSS-INFERENCE:
                    [PHYSICAL STATS]
                    - Weight: ${health?.weightKg ?: "--"} kg | BF: ${health?.bodyFatPercentage ?: "--"} % | Visceral: Lvl ${health?.visceralFatLevel ?: "--"}
                    - Waist: ${health?.waistCircumferenceCm ?: "--"} cm | LILA: ${health?.lilaCm ?: "--"} cm
                    
                    [CLINICAL/VITALS]
                    - BP: ${health?.systolicBp ?: "--"}/${health?.diastolicBp ?: "--"} mmHg | HR: ${health?.restingHeartRate ?: "--"} bpm
                    - Temp: ${health?.bodyTemp ?: "--"} °C | Sleep: ${health?.sleepDurationHours ?: "--"}h
                    
                    [BIOCHEMICAL/LAB RESULTS]
                    - Blood: Hb ${health?.hb ?: "--"} g/dl, Ht ${health?.hematocrit ?: "--"} %
                    - Metabolic: Sugar(GDP) ${health?.fastingBloodSugar ?: "--"} mg/dl, Chol ${health?.cholesterolTotal ?: "--"} mg/dl
                    - Liver(Hepatic): SGOT ${health?.sgotAst ?: "--"} U/L, SGPT ${health?.sgptAlt ?: "--"} U/L
                    - Renal(Kidney): BUN ${health?.bun ?: "--"}, Crea ${health?.creatinine ?: "--"}, eGFR ${health?.egfr ?: "--"}
                    - Hormonal: TotalTesto ${health?.totalTestosterone ?: "--"}, FreeTesto ${health?.freeTestosterone ?: "--"}, E2 ${health?.estradiolE2 ?: "--"}
                    
                    [NUTRITION INTAKE TODAY]
                    - Total Calories: ${nutrition.sumOf { it.calories }} kcal
                    - Protein: ${nutrition.sumOf { it.proteinGrams.toDouble() }}g | Carbs: ${nutrition.sumOf { it.carbsGrams.toDouble() }}g | Fats: ${nutrition.sumOf { it.fatGrams.toDouble() }}g
                    
                    [WORKOUT ENGINE]
                    - Last Routine: '${workout?.session?.routineName ?: "N/A"}'
                    - Specific Muscles Targeted: ${workout?.exercises?.flatMap { it.targetMuscles }?.distinct()?.joinToString(", ") ?: "None"}

                    [USER SPECIFIC CONCERN]
                    $customPrompt
                    
                    INSTRUCTIONS:
                    - WAJIB lakukan inferensi silang (misal: korelasi BUN tinggi dengan asupan Protein tinggi).
                    - Gunakan standar pelaporan klinis binaraga elite.
                    - Identifikasi anomali/Red Flags segera.
                """.trimIndent()

                // 4. BRAIN EXECUTION: Panggil AI Manager
                val aiResponse = aiManager.generateContent(contextData)
                
                // 5. CLOUD PERSISTENCE
                val newReport = AiReportEntity(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    reportType = targetAudience,
                    summary = aiResponse
                )
                aiRepo.insertReport(newReport)
                _actionState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _actionState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }
    
    fun deleteReport(id: String) {
        viewModelScope.launch {
            aiRepo.deleteReport(id)
        }
    }
}

// Data class pembungkus status di layar AI
data class AiReportUiState(
    val isLoading: Boolean = false,
    val reports: List<AiReportEntity> = emptyList(),
    val analysisResult: String? = null,
    val error: String? = null,
    val selectedModel: String = "gemini-1.5-flash"
)
