package com.rais.nexusbody.feature.health

// --- TECH STACK IMPORT EXPLANATION ---
import androidx.lifecycle.ViewModel // Architecture Component: Dasar logika UI
import androidx.lifecycle.viewModelScope // Coroutines: Mengelola thread latar belakang agar UI tidak freeze
import com.rais.nexusbody.data.local.entity.HealthAssessmentEntity // Room Entity: Definisi skema tabel lokal
import com.rais.nexusbody.data.local.entity.MedicationEntity // Room Entity: Skema data obat
import com.rais.nexusbody.domain.model.Medication // Domain Model: Data murni untuk logika bisnis
import com.rais.nexusbody.domain.repository.HealthAssessmentRepository // Repository Pattern: Kontrak akses data
import com.rais.nexusbody.domain.repository.MedicationRepository // Repository Pattern: Kontrak akses obat
import dagger.hilt.android.lifecycle.HiltViewModel // Dependency Injection: Injeksi otomatis lifecycle
import kotlinx.coroutines.flow.* // Reactive Programming: Aliran data real-time (SSOT)
import kotlinx.coroutines.launch // Coroutines: Eksekusi asinkron
import io.github.jan.supabase.SupabaseClient // Cloud Backend: Klien cloud Supabase
import io.github.jan.supabase.gotrue.auth // Supabase Auth: Manajemen user login
import java.text.SimpleDateFormat // Utilities: Format tanggal
import java.util.Date // Utilities: Objek waktu
import java.util.Locale // Lokalisasi wilayah
import java.util.UUID // Generator ID unik universal
import javax.inject.Inject // Dependency Injection: Anotasi untuk constructor injection

/**
 * HEALTH VIEWMODEL (LOGIC LAYER - BACKEND)
 * Peran: Jantung logika kesehatan yang mengelola alur data dari Input User ke Database (Room & Supabase).
 * Pola: Menggunakan pola MVI (Model-View-Intent) sederhana untuk mengelola status data klinis.
 */
@HiltViewModel
class HealthViewModel @Inject constructor(
    private val repository: HealthAssessmentRepository, // Disuntikkan oleh Hilt: Akses data kesehatan
    private val medicationRepo: MedicationRepository, // Disuntikkan oleh Hilt: Akses data obat
    private val supabase: SupabaseClient // Disuntikkan oleh Hilt: Akses cloud session
) : ViewModel() {

    // --- USER CONTEXT LOGIC (BACKEND FLOW) ---
    // Mengambil ID user yang sedang aktif dari Supabase Auth Session secara real-time.
    private val userId: String
        get() = supabase.auth.currentUserOrNull()?.id ?: "anonymous_user"

    // Helper untuk identifikasi data harian (Format: 2024-05-14)
    private fun getCurrentDateStr(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    // --- REAKTIVITAS UI (FRONTEND FLOW) ---
    // State internal untuk menyimpan filter waktu (Default: Daily)
    private val _timeframe = MutableStateFlow("daily")
    val selectedTimeframe: StateFlow<String> = _timeframe.asStateFlow()

    // SSOT (Single Source of Truth): UI hanya mendengarkan aliran data (Flow) ini.
    // Setiap ada perubahan di Room atau Supabase, UI akan otomatis terupdate.
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val assessments: StateFlow<List<HealthAssessmentEntity>> = _timeframe.flatMapLatest { timeframe ->
        // Backend Flow: Repository mengambil data -> Room memancarkan Flow -> ViewModel merubah jadi State
        repository.getAssessments(userId)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // Optimasi: Berhenti jika UI tidak dibuka selama 5 detik
        initialValue = emptyList()
    )

    // Aliran data reaktif untuk daftar obat harian
    val medications: StateFlow<List<MedicationEntity>> = medicationRepo.getAllMedications(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Fungsi pemicu dari UI (UX Intent) untuk ganti filter waktu
    fun updateTimeframe(timeframe: String) {
        _timeframe.value = timeframe
    }

    /**
     * LOGIKA SIMPAN SMART-UPSERT (BACKEND PERSISTENCE)
     * Strategi: Mendukung penggabungan data (Merge) dari form yang berbeda di hari yang sama.
     */
    fun saveAssessment(
        weight: Float? = null, height: Float? = null, lila: Float? = null, waist: Float? = null,
        bodyFat: Float? = null, visceralFat: Int? = null, bloodSugar: Float? = null,
        cholesterol: Float? = null, systolic: Int? = null, diastolic: Int? = null,
        hr: Int? = null, sleep: Float? = null, sgot: Float? = null, sgpt: Float? = null,
        bilirubin: Float? = null, bun: Float? = null, creatinine: Float? = null,
        egfr: Float? = null, hematocrit: Float? = null, hb: Float? = null,
        testoTotal: Float? = null, testoFree: Float? = null, estradiol: Float? = null,
        prolactin: Float? = null, temp: Float? = null, insulin: Float? = null
    ) {
        viewModelScope.launch {
            val dateStr = getCurrentDateStr()
            // 1. Cek database apakah user sudah mengisi data kesehatan hari ini
            val existing = repository.getAssessmentByDate(userId, dateStr)

            // 2. Logika Merge (Penting!): Jika input baru ada, pakai yang baru. Jika null, pakai data lama.
            // Ini mencegah data "terhapus" saat user mengisi form yang berbeda.
            val entity = HealthAssessmentEntity(
                id = existing?.id ?: UUID.randomUUID().toString(), // Update baris yang sama jika sudah ada
                userId = userId,
                dateStr = dateStr, // Identifier unik harian
                timestamp = System.currentTimeMillis(),
                weightKg = weight ?: existing?.weightKg,
                heightCm = height ?: existing?.heightCm,
                bodyFatPercentage = bodyFat ?: existing?.bodyFatPercentage,
                visceralFatLevel = visceralFat ?: existing?.visceralFatLevel,
                lilaCm = lila ?: existing?.lilaCm,
                waistCircumferenceCm = waist ?: existing?.waistCircumferenceCm,
                sleepDurationHours = sleep ?: existing?.sleepDurationHours,
                systolicBp = systolic ?: existing?.systolicBp,
                diastolicBp = diastolic ?: existing?.diastolicBp,
                restingHeartRate = hr ?: existing?.restingHeartRate,
                bodyTemp = temp ?: existing?.bodyTemp,
                fastingBloodSugar = bloodSugar ?: existing?.fastingBloodSugar,
                cholesterolTotal = cholesterol ?: existing?.cholesterolTotal,
                hb = hb ?: existing?.hb,
                hematocrit = hematocrit ?: existing?.hematocrit,
                sgotAst = sgot ?: existing?.sgotAst,
                sgptAlt = sgpt ?: existing?.sgptAlt,
                bilirubinTotal = bilirubin ?: existing?.bilirubinTotal,
                bun = bun ?: existing?.bun,
                creatinine = creatinine ?: existing?.creatinine,
                egfr = egfr ?: existing?.egfr,
                totalTestosterone = testoTotal ?: existing?.totalTestosterone,
                freeTestosterone = testoFree ?: existing?.freeTestosterone,
                estradiolE2 = estradiol ?: existing?.estradiolE2,
                prolactin = prolactin ?: existing?.prolactin,
                fastingInsulin = insulin ?: existing?.fastingInsulin,
                conditions = existing?.conditions ?: emptyList() // Pertahankan riwayat medis
            )
            // 3. Simpan ke Repository (Logic: Save to Room -> Sync to Supabase)
            repository.insertAssessment(entity)
        }
    }

    // Memperbarui list riwayat penyakit tanpa merusak data klinis lain
    fun updateConditions(newConditions: List<String>) {
        viewModelScope.launch {
            val dateStr = getCurrentDateStr()
            val existing = repository.getAssessmentByDate(userId, dateStr)
            val entity = HealthAssessmentEntity(
                id = existing?.id ?: UUID.randomUUID().toString(),
                userId = userId,
                dateStr = dateStr,
                timestamp = System.currentTimeMillis(),
                weightKg = existing?.weightKg,
                heightCm = existing?.heightCm,
                bodyFatPercentage = existing?.bodyFatPercentage,
                visceralFatLevel = existing?.visceralFatLevel,
                lilaCm = existing?.lilaCm,
                waistCircumferenceCm = existing?.waistCircumferenceCm,
                sleepDurationHours = existing?.sleepDurationHours,
                systolicBp = existing?.systolicBp,
                diastolicBp = existing?.diastolicBp,
                restingHeartRate = existing?.restingHeartRate,
                bodyTemp = existing?.bodyTemp,
                fastingBloodSugar = existing?.fastingBloodSugar,
                cholesterolTotal = existing?.cholesterolTotal,
                hb = existing?.hb,
                hematocrit = existing?.hematocrit,
                sgotAst = existing?.sgotAst,
                sgptAlt = existing?.sgptAlt,
                bilirubinTotal = existing?.bilirubinTotal,
                bun = existing?.bun,
                creatinine = existing?.creatinine,
                egfr = existing?.egfr,
                totalTestosterone = existing?.totalTestosterone,
                freeTestosterone = existing?.freeTestosterone,
                estradiolE2 = existing?.estradiolE2,
                prolactin = existing?.prolactin,
                fastingInsulin = existing?.fastingInsulin,
                conditions = newConditions // Masukkan data baru hasil input user
            )
            repository.insertAssessment(entity)
        }
    }

    // Fungsi hapus permanen data kesehatan
    fun deleteAssessment(id: String) {
        viewModelScope.launch {
            repository.deleteAssessment(id)
        }
    }

    // Fungsi menyimpan jadwal obat baru ke database
    fun saveMedication(med: Medication) {
        viewModelScope.launch {
            val entity = MedicationEntity(
                id = med.id,
                userId = userId,
                name = med.name,
                dosage = med.dosage,
                frequency = med.frequency,
                scheduledTimes = med.scheduledTimes,
                startDate = med.startDate,
                endDate = med.endDate,
                isActive = med.isActive,
                notes = med.notes
            )
            medicationRepo.insertMedication(entity)
        }
    }

    // Fungsi hapus jadwal obat
    fun deleteMedication(id: String) {
        viewModelScope.launch {
            medicationRepo.deleteMedication(id)
        }
    }

    // Fungsi update status aktifitas obat
    fun updateMedication(med: MedicationEntity) {
        viewModelScope.launch {
            medicationRepo.insertMedication(med)
        }
    }
}
