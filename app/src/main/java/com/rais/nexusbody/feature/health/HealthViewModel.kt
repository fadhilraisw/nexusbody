package com.rais.nexusbody.feature.health

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rais.nexusbody.data.local.entity.HealthAssessmentEntity
import com.rais.nexusbody.data.local.entity.MedicationEntity
import com.rais.nexusbody.domain.model.Medication
import com.rais.nexusbody.domain.repository.HealthAssessmentRepository
import com.rais.nexusbody.domain.repository.MedicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import java.util.UUID
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HealthViewModel @Inject constructor(
    private val repository: HealthAssessmentRepository,
    private val medicationRepo: MedicationRepository,
    private val supabase: SupabaseClient
) : ViewModel() {

    private val userId: String
        get() = supabase.auth.currentUserOrNull()?.id ?: "anonymous_user"

    private fun getCurrentDateStr(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    val assessments: StateFlow<List<HealthAssessmentEntity>> = repository.getAssessments(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val medications: StateFlow<List<MedicationEntity>> = medicationRepo.getAllMedications(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveAssessment(
        weight: Float? = null,
        height: Float? = null,
        lila: Float? = null,
        waist: Float? = null,
        bodyFat: Float? = null,
        visceralFat: Int? = null,
        bloodSugar: Float? = null,
        cholesterol: Float? = null,
        systolic: Int? = null,
        diastolic: Int? = null,
        hr: Int? = null,
        sleep: Float? = null,
        sgot: Float? = null,
        sgpt: Float? = null,
        bilirubin: Float? = null,
        bun: Float? = null,
        creatinine: Float? = null,
        egfr: Float? = null,
        hematocrit: Float? = null,
        hb: Float? = null,
        testoTotal: Float? = null,
        testoFree: Float? = null,
        estradiol: Float? = null,
        prolactin: Float? = null,
        temp: Float? = null,
        insulin: Float? = null
    ) {
        viewModelScope.launch {
            val dateStr = getCurrentDateStr()
            val existing = repository.getAssessmentByDate(userId, dateStr)

            val entity = HealthAssessmentEntity(
                id = existing?.id ?: UUID.randomUUID().toString(),
                userId = userId,
                dateStr = dateStr,
                timestamp = System.currentTimeMillis(),
                
                // Jika input baru ada (tidak null), gunakan yang baru. 
                // Jika input baru null, tetap gunakan data lama dari database.
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
                fastingInsulin = insulin ?: existing?.fastingInsulin
            )
            repository.insertAssessment(entity)
        }
    }

    fun deleteAssessment(id: String) {
        viewModelScope.launch {
            repository.deleteAssessment(id)
        }
    }

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

    fun deleteMedication(id: String) {
        viewModelScope.launch {
            medicationRepo.deleteMedication(id)
        }
    }

    fun updateMedication(med: MedicationEntity) {
        viewModelScope.launch {
            medicationRepo.insertMedication(med)
        }
    }
}
