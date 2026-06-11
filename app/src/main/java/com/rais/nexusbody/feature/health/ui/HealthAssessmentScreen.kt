package com.rais.nexusbody.feature.health.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rais.nexusbody.core.ui.components.PremiumGlassCard
import com.rais.nexusbody.core.ui.components.PremiumTextField
import com.rais.nexusbody.core.ui.theme.premiumaccent
import com.rais.nexusbody.core.ui.theme.statusdanger
import com.rais.nexusbody.core.ui.theme.statusgood
import com.rais.nexusbody.core.ui.theme.statuswarning
import com.rais.nexusbody.core.ui.theme.textmuted
import com.rais.nexusbody.core.ui.theme.textprimary
import com.rais.nexusbody.core.ui.theme.textsecondary
import com.rais.nexusbody.domain.model.Medication
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.rais.nexusbody.feature.health.HealthViewModel
import com.rais.nexusbody.data.local.entity.HealthAssessmentEntity
import com.rais.nexusbody.data.local.entity.MedicationEntity

// Model Data Sementara untuk CRUD Rekam Medis
data class MedicalRecord(
    val id: String,
    val date: String,
    val doctor: String,
    val diagnosis: String,
    val notes: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HealthAssessmentScreen(
    viewModel: HealthViewModel = hiltViewModel()
) {
    val assessments by viewModel.assessments.collectAsState()
    val medications by viewModel.medications.collectAsState()
    val latestAssessment = assessments.firstOrNull()
    val conditions = latestAssessment?.conditions ?: emptyList()

    var activeSheet by remember { mutableStateOf("") }
    var selectedTimeframe by remember { mutableStateOf("daily") }

    // State Dinamis (Sekarang diambil dari ViewModel/Repo)
    var medicalRecords by remember { mutableStateOf(emptyList<MedicalRecord>()) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(20.dp, 20.dp, 20.dp, 120.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text("⚲ clinical tracking", color = textprimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            item { HealthTimeframeSelector(selectedTimeframe) { 
                selectedTimeframe = it 
                viewModel.updateTimeframe(it)
            } }

            // 1. Section: Active Conditions
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("medical conditions", color = textsecondary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    IconButton(onClick = { activeSheet = "conditions" }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Edit, "edit kondisi", tint = premiumaccent, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
                if (conditions.isEmpty()) {
                    Text("belum ada data kondisi medis kustom.", color = textmuted, fontSize = 12.sp)
                } else {
                    FlowRowConditions(conditions)
                }
            }

            // 2. Section: Vitals Overview
            item {
                VitalsOverviewCard(
                    latestAssessment = latestAssessment,
                    onClick = { activeSheet = "vitals" }
                )
            }

            // 3. Section: PAGD Clinical Forms Navigation
            item {
                Text("pagd clinical forms", color = textsecondary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PagdQuickButton("antropometri", Modifier.weight(1f)) { activeSheet = "antro" }
                    PagdQuickButton("biokimia", Modifier.weight(1f)) { activeSheet = "bio" }
                    PagdQuickButton("fisik/klinis", Modifier.weight(1f)) { activeSheet = "fisik" }
                }
            }

            // 4. Section: Medication Scheduler
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("medication scheduler", color = textsecondary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    TextButton(onClick = { activeSheet = "add_med" }) {
                        Text("+ tambah obat", color = premiumaccent, fontSize = 12.sp)
                    }
                }
            }

            if (medications.isEmpty()) {
                item { Text("belum ada jadwal konsumsi obat.", color = textmuted, fontSize = 12.sp) }
            } else {
                items(medications) { medEntity ->
                    // Konversi Entity ke Model untuk UI
                    val med = Medication(
                        id = medEntity.id,
                        name = medEntity.name,
                        dosage = medEntity.dosage,
                        frequency = medEntity.frequency,
                        scheduledTimes = medEntity.scheduledTimes,
                        isActive = medEntity.isActive,
                        startDate = medEntity.startDate,
                        endDate = medEntity.endDate,
                        notes = medEntity.notes
                    )
                    MedicationCard(
                        med = med,
                        onDelete = { viewModel.deleteMedication(med.id) },
                        onClick = { activeSheet = "med_detail_${med.id}" }
                    )
                }
            }

            // 5. Section: Medical Records (CRUD Kunjungan Dokter)
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("rekam medis & kunjungan", color = textsecondary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    TextButton(onClick = { activeSheet = "add_record" }) {
                        Text("+ tambah kunjungan", color = premiumaccent, fontSize = 12.sp)
                    }
                }
            }

            if (medicalRecords.isEmpty()) {
                item { Text("belum ada rekam medis kunjungan rumah sakit.", color = textmuted, fontSize = 12.sp) }
            } else {
                items(medicalRecords) { record ->
                    MedicalRecordCard(
                        record = record,
                        onDelete = { medicalRecords = medicalRecords.filter { it.id != record.id } },
                        onClick = { activeSheet = "view_record_${record.id}" }
                    )
                }
            }
        }

        // Dynamic Bottom Sheet Router
        if (activeSheet.isNotEmpty()) {
            ModalBottomSheet(
                onDismissRequest = { activeSheet = "" },
                containerColor = Color(0xFF0D0F12),
                modifier = Modifier.fillMaxHeight(0.9f)
            ) {
                when (activeSheet) {
                    "antro" -> AntropometriForm(viewModel) { activeSheet = "" }
                    "bio" -> BiokimiaForm(viewModel) { activeSheet = "" }
                    "fisik" -> FisikKlinisForm(viewModel) { activeSheet = "" }
                    "conditions" -> EditConditionsForm(
                        currentConditions = conditions, 
                        onUpdate = { viewModel.updateConditions(it) }, 
                        onDismiss = { activeSheet = "" }
                    )
                    "vitals" -> UpdateVitalsContent(viewModel) { activeSheet = "" }
                    "add_med" -> AddMedicationForm { newMed ->
                        viewModel.saveMedication(newMed)
                        activeSheet = "med_detail_${newMed.id}"
                    }
                    "add_record" -> AddMedicalRecordForm { newRecord ->
                        medicalRecords = medicalRecords + newRecord
                        activeSheet = ""
                    }
                    else -> {
                        if (activeSheet.startsWith("med_detail_")) {
                            val medId = activeSheet.removePrefix("med_detail_")
                            val medEntity = medications.find { it.id == medId }
                            if (medEntity != null) {
                                // Konversi Entity ke Model untuk UI Detail
                                val med = Medication(
                                    id = medEntity.id,
                                    name = medEntity.name,
                                    dosage = medEntity.dosage,
                                    frequency = medEntity.frequency,
                                    scheduledTimes = medEntity.scheduledTimes,
                                    isActive = medEntity.isActive,
                                    startDate = medEntity.startDate,
                                    endDate = medEntity.endDate,
                                    notes = medEntity.notes
                                )
                                MedicationDetailSheet(
                                    med = med,
                                    onUpdate = { updatedMed ->
                                        // Update via ViewModel
                                        val updatedEntity = MedicationEntity(
                                            id = updatedMed.id,
                                            userId = medEntity.userId, // Tetap gunakan user ID asli
                                            name = updatedMed.name,
                                            dosage = updatedMed.dosage,
                                            frequency = updatedMed.frequency,
                                            scheduledTimes = updatedMed.scheduledTimes,
                                            startDate = updatedMed.startDate,
                                            endDate = updatedMed.endDate,
                                            isActive = updatedMed.isActive,
                                            notes = updatedMed.notes
                                        )
                                        viewModel.updateMedication(updatedEntity)
                                    },
                                    onDismiss = { activeSheet = "" }
                                )
                            }
                        } else if (activeSheet.startsWith("view_record_")) {
                            val recordId = activeSheet.removePrefix("view_record_")
                            val record = medicalRecords.find { it.id == recordId }
                            if (record != null) {
                                ViewMedicalRecordDetailSheet(record) { activeSheet = "" }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- KOMPONEN UI ELEMEN UTAMA ---

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRowConditions(conditions: List<String>) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        conditions.forEach { condition ->
            val isDanger = condition.contains("alergi") || condition.contains("cedera")
            val color = if (isDanger) statusdanger else statuswarning
            Box(modifier = Modifier.background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp)).border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                Text(condition, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun HealthTimeframeSelector(selected: String, onSelect: (String) -> Unit) {
    val frames = listOf("daily", "weekly", "monthly", "yearly", "custom")
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(frames) { frame ->
            val isSelected = selected == frame
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) premiumaccent else Color.White.copy(0.05f))
                    .clickable { onSelect(frame) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(frame, color = if (isSelected) Color.White else textsecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun VitalsOverviewCard(latestAssessment: HealthAssessmentEntity?, onClick: () -> Unit) {
    PremiumGlassCard(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("patient vitals & clinical metrics", color = textmuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Icon(Icons.Default.Favorite, null, tint = premiumaccent, modifier = Modifier.size(18.dp))
            }

            // --- SECTION 1: PHYSICAL (ANTROPOMETRI) ---
            Text("physical & anthropometry", color = premiumaccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                VitalMetric("weight", latestAssessment?.weightKg?.let { "${it}kg" } ?: "--", textprimary)
                VitalMetric("height", latestAssessment?.heightCm?.let { "${it}cm" } ?: "--", textprimary)
                VitalMetric("body fat", latestAssessment?.bodyFatPercentage?.let { "${it}%" } ?: "--", statusgood)
                VitalMetric("visceral", latestAssessment?.visceralFatLevel?.toString() ?: "--", statusgood)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                VitalMetric("lila", latestAssessment?.lilaCm?.let { "${it}cm" } ?: "--", textsecondary)
                VitalMetric("waist", latestAssessment?.waistCircumferenceCm?.let { "${it}cm" } ?: "--", textsecondary)
                VitalMetric("sleep", latestAssessment?.sleepDurationHours?.let { "${it}h" } ?: "--", statuswarning)
                Spacer(Modifier.width(40.dp)) // balancing
            }

            Divider(color = Color.White.copy(0.05f))

            // --- SECTION 2: CLINICAL (FISIK/KLINIS) ---
            Text("clinical & vitals", color = premiumaccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("blood pressure", color = textmuted, fontSize = 10.sp)
                    val systolic = latestAssessment?.systolicBp
                    val diastolic = latestAssessment?.diastolicBp
                    val bpText = if(systolic != null && diastolic != null) "$systolic/$diastolic" else "--/--"
                    Text(bpText, color = textprimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("mmhg", color = textmuted, fontSize = 9.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("resting hr", color = textmuted, fontSize = 10.sp)
                    Text(latestAssessment?.restingHeartRate?.let { "$it" } ?: "--", color = textprimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("bpm", color = textmuted, fontSize = 9.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("body temp", color = textmuted, fontSize = 10.sp)
                    Text(latestAssessment?.bodyTemp?.let { "$it" } ?: "--", color = textprimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("°c", color = textmuted, fontSize = 9.sp)
                }
            }

            Divider(color = Color.White.copy(0.05f))

            // --- SECTION 3: LAB RESULTS (BIOKIMIA) ---
            Text("biochemical (lab results)", color = premiumaccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            
            // Sub: Metabolik & Darah
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                VitalMetric("blood sugar", latestAssessment?.fastingBloodSugar?.let { "$it" } ?: "--", statuswarning)
                VitalMetric("cholesterol", latestAssessment?.cholesterolTotal?.let { "$it" } ?: "--", statusdanger)
                VitalMetric("hb", latestAssessment?.hb?.let { "$it" } ?: "--", textprimary)
                VitalMetric("hematocrit", latestAssessment?.hematocrit?.let { "$it%" } ?: "--", textprimary)
            }

            // Sub: Hepatic & Renal
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("liver (ast/alt)", color = textmuted, fontSize = 10.sp)
                    val ast = latestAssessment?.sgotAst
                    val alt = latestAssessment?.sgptAlt
                    Text(if(ast != null || alt != null) "${ast ?: "--"}/${alt ?: "--"}" else "--/--", color = textprimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("kidney (crea/egfr)", color = textmuted, fontSize = 10.sp)
                    val crea = latestAssessment?.creatinine
                    val egfr = latestAssessment?.egfr
                    Text(if(crea != null || egfr != null) "${crea ?: "--"}/${egfr ?: "--"}" else "--/--", color = textprimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("hormonal (testo)", color = textmuted, fontSize = 10.sp)
                    Text(latestAssessment?.totalTestosterone?.let { "$it" } ?: "--", color = premiumaccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun VitalMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(label, color = textsecondary, fontSize = 10.sp)
    }
}

@Composable
private fun PagdQuickButton(label: String, modifier: Modifier, onClick: () -> Unit) {
    Box(modifier = modifier.clip(RoundedCornerShape(12.dp)).background(Color.White.copy(0.05f)).clickable { onClick() }.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
        Text(label, color = textprimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun MedicalRecordCard(record: MedicalRecord, onDelete: () -> Unit, onClick: () -> Unit) {
    PremiumGlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp).clickable { onClick() }) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(record.doctor, color = premiumaccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(record.date, color = textmuted, fontSize = 11.sp)
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.Delete, null, tint = statusdanger.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                    }
                }
            }
            Text(record.diagnosis, color = textprimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Divider(color = Color.White.copy(0.05f))
            Text(record.notes, color = textsecondary, fontSize = 12.sp, lineHeight = 18.sp)
        }
    }
}

@Composable
private fun MedicationCard(med: Medication, onDelete: () -> Unit, onClick: () -> Unit) {
    val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
    val rangeText = if (med.endDate != null) {
        "${sdf.format(Date(med.startDate))} - ${sdf.format(Date(med.endDate))}"
    } else {
        "Mulai: ${sdf.format(Date(med.startDate))}"
    }

    PremiumGlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { onClick() }) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(med.name, color = textprimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(rangeText, color = textmuted, fontSize = 10.sp)
                }
                Text("${med.dosage} · ${med.frequency}", color = textsecondary, fontSize = 13.sp)
                if (med.scheduledTimes.isNotEmpty()) {
                    Text(
                        "jadwal: ${med.scheduledTimes.joinToString(", ")}", 
                        color = premiumaccent, 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text("belum ada jadwal jam", color = textmuted, fontSize = 11.sp)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, null, tint = statusdanger.copy(alpha = 0.6f))
            }
        }
    }
}

// --- JALUR DATA CRUD & PENCATATAN FORM SHEET ---

@Composable
private fun EditConditionsForm(currentConditions: List<String>, onUpdate: (List<String>) -> Unit, onDismiss: () -> Unit) {
    var newCondition by remember { mutableStateOf("") }
    LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("update medical conditions", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PremiumTextField(value = newCondition, onValueChange = { newCondition = it }, label = "kondisi medis baru...", modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        if(newCondition.isNotBlank()) { onUpdate(currentConditions + newCondition); newCondition = "" }
                    },
                    modifier = Modifier.background(statusgood, RoundedCornerShape(12.dp))
                ) { Icon(Icons.Default.Add, null, tint = Color.White) }
            }
        }
        items(currentConditions) { condition ->
            Row(modifier = Modifier.fillMaxWidth().background(Color.White.copy(0.05f), RoundedCornerShape(12.dp)).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(condition, color = textprimary, fontSize = 14.sp)
                IconButton(onClick = { onUpdate(currentConditions.filter { it != condition }) }, modifier = Modifier.size(20.dp)) {
                    Icon(Icons.Default.Delete, null, tint = statusdanger)
                }
            }
        }
        item { Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f))) { Text("selesai", color = textprimary) } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMedicationForm(onAdd: (Medication) -> Unit) {
    var name by remember { mutableStateOf("") }
    var dose by remember { mutableStateOf("") }
    var freq by remember { mutableStateOf("") }
    var showRangePicker by remember { mutableStateOf(false) }
    var dateRange by remember { mutableStateOf<Pair<Long?, Long?>>(null to null) }

    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val dateLabel = if (dateRange.first != null && dateRange.second != null) {
        "${sdf.format(Date(dateRange.first!!))} - ${sdf.format(Date(dateRange.second!!))}"
    } else {
        "pilih rentang tanggal konsumsi"
    }

    LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("💊 tambah obat baru", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        item { PremiumTextField(value = name, onValueChange = { name = it }, label = "nama obat") }
        item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PremiumTextField(value = dose, onValueChange = { dose = it }, label = "dosis (mg)", modifier = Modifier.weight(1f))
            PremiumTextField(value = freq, onValueChange = { freq = it }, label = "frekuensi (ex: 2x sehari)", modifier = Modifier.weight(1f))
        }}

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(0.05f))
                    .clickable { showRangePicker = true }
                    .padding(16.dp)
            ) {
                Text(dateLabel, color = if(dateRange.first != null) textprimary else textmuted, fontSize = 14.sp)
            }
        }

        item {
            Button(
                onClick = { 
                    if(name.isNotBlank() && dateRange.first != null) {
                        onAdd(Medication(
                            id = UUID.randomUUID().toString(), 
                            name = name, 
                            dosage = dose, 
                            frequency = freq, 
                            scheduledTimes = emptyList(),
                            startDate = dateRange.first!!,
                            endDate = dateRange.second
                        )) 
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = statusgood)
            ) { Text("buat jadwal obat", fontWeight = FontWeight.Bold) }
        }
    }

    if (showRangePicker) {
        val state = rememberDateRangePickerState()
        DatePickerDialog(
            onDismissRequest = { showRangePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dateRange = state.selectedStartDateMillis to state.selectedEndDateMillis
                        showRangePicker = false
                    },
                    enabled = state.selectedStartDateMillis != null
                ) {
                    Text("SIMPAN TANGGAL", color = premiumaccent, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRangePicker = false }) {
                    Text("BATAL", color = textmuted)
                }
            },
            colors = androidx.compose.material3.DatePickerDefaults.colors(
                containerColor = Color(0xFF0D0F12)
            )
        ) {
            DateRangePicker(
                state = state,
                title = { Text("Pilih Durasi Konsumsi Obat", modifier = Modifier.padding(16.dp), color = textprimary) },
                headline = { Text("Rentang Tanggal", modifier = Modifier.padding(horizontal = 16.dp), color = textprimary) },
                showModeToggle = false,
                modifier = Modifier.fillMaxWidth().height(500.dp),
                colors = androidx.compose.material3.DatePickerDefaults.colors(
                    containerColor = Color(0xFF0D0F12),
                    titleContentColor = textprimary,
                    headlineContentColor = textprimary,
                    dayContentColor = textprimary,
                    selectedDayContainerColor = premiumaccent,
                    selectedDayContentColor = Color.White,
                    todayContentColor = premiumaccent,
                    todayDateBorderColor = premiumaccent
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MedicationDetailSheet(med: Medication, onUpdate: (Medication) -> Unit, onDismiss: () -> Unit) {
    var showTimePicker by remember { mutableStateOf(false) }
    
    LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        item {
            Column {
                Text(med.name, color = textprimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("${med.dosage} · ${med.frequency}", color = textsecondary, fontSize = 14.sp)
            }
        }
        
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("jadwal konsumsi harian", color = textprimary, fontWeight = FontWeight.Bold)
                TextButton(onClick = { showTimePicker = true }) {
                    Icon(Icons.Default.Add, null, tint = premiumaccent, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("tambah jam", color = premiumaccent)
                }
            }
        }

        if (med.scheduledTimes.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().background(Color.White.copy(0.05f), RoundedCornerShape(12.dp)).padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("belum ada jam pengingat", color = textmuted, fontSize = 12.sp)
                }
            }
        } else {
            items(med.scheduledTimes) { time ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(0.05f), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, null, tint = premiumaccent, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(time, color = textprimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = { 
                        onUpdate(med.copy(scheduledTimes = med.scheduledTimes.filter { it != time }))
                    }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, null, tint = statusdanger.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        item {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f))
            ) {
                Text("selesai", color = textprimary)
            }
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", timePickerState.hour, timePickerState.minute)
                    if (!med.scheduledTimes.contains(formattedTime)) {
                        onUpdate(med.copy(scheduledTimes = (med.scheduledTimes + formattedTime).sorted()))
                    }
                    showTimePicker = false
                }) { Text("tambah", color = premiumaccent) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("batal", color = textmuted) }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }
}

@Composable
private fun AntropometriForm(viewModel: HealthViewModel, onDismiss: () -> Unit) {
    var berat by remember { mutableStateOf("") }
    var tinggi by remember { mutableStateOf("") }
    var bodyFat by remember { mutableStateOf("") }
    var visceral by remember { mutableStateOf("") }
    var lila by remember { mutableStateOf("") }
    var perut by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("📏 antropometri (pengukuran fisik)", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PremiumTextField(value = berat, onValueChange = { berat = it }, label = "berat (kg)", modifier = Modifier.weight(1f))
                PremiumTextField(value = tinggi, onValueChange = { tinggi = it }, label = "tinggi (cm)", modifier = Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PremiumTextField(value = bodyFat, onValueChange = { bodyFat = it }, label = "body fat (%)", modifier = Modifier.weight(1f))
                PremiumTextField(value = visceral, onValueChange = { visceral = it }, label = "visceral lvl", modifier = Modifier.weight(1f))
            }
        }
        item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PremiumTextField(value = lila, onValueChange = { lila = it }, label = "lila (cm)", modifier = Modifier.weight(1f))
            PremiumTextField(value = perut, onValueChange = { perut = it }, label = "lingkar perut (cm)", modifier = Modifier.weight(1f))
        }}
        item { 
            Button(
                onClick = { 
                    viewModel.saveAssessment(
                        weight = berat.toFloatOrNull(),
                        height = tinggi.toFloatOrNull(),
                        bodyFat = bodyFat.toFloatOrNull(),
                        visceralFat = visceral.toIntOrNull(),
                        lila = lila.toFloatOrNull(),
                        waist = perut.toFloatOrNull()
                    )
                    onDismiss() 
                }, 
                modifier = Modifier.fillMaxWidth(), 
                colors = ButtonDefaults.buttonColors(containerColor = premiumaccent)
            ) { Text("simpan rekam medis", color = Color.White) } 
        }
    }
}

@Composable
private fun BiokimiaForm(viewModel: HealthViewModel, onDismiss: () -> Unit) {
    var gdp by remember { mutableStateOf("") }
    var kolesterol by remember { mutableStateOf("") }
    var hb by remember { mutableStateOf("") }
    var hematokrit by remember { mutableStateOf("") }
    
    // Liver
    var sgot by remember { mutableStateOf("") }
    var sgpt by remember { mutableStateOf("") }
    var bilirubin by remember { mutableStateOf("") }
    
    // Renal
    var bun by remember { mutableStateOf("") }
    var creatinine by remember { mutableStateOf("") }
    var egfr by remember { mutableStateOf("") }

    // Hormonal
    var testoTotal by remember { mutableStateOf("") }
    var testoFree by remember { mutableStateOf("") }
    var e2 by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("⚗️ biokimia (hasil lab)", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        
        item { Text("metabolik & darah", color = premiumaccent, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PremiumTextField(value = gdp, onValueChange = { gdp = it }, label = "gdp (mg/dl)", modifier = Modifier.weight(1f))
            PremiumTextField(value = kolesterol, onValueChange = { kolesterol = it }, label = "kolesterol (mg/dl)", modifier = Modifier.weight(1f))
        }}
        item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PremiumTextField(value = hb, onValueChange = { hb = it }, label = "hb (g/dl)", modifier = Modifier.weight(1f))
            PremiumTextField(value = hematokrit, onValueChange = { hematokrit = it }, label = "ht (%)", modifier = Modifier.weight(1f))
        }}

        item { Text("fungsi hati (hepatic)", color = premiumaccent, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PremiumTextField(value = sgot, onValueChange = { sgot = it }, label = "sgot (u/l)", modifier = Modifier.weight(1f))
            PremiumTextField(value = sgpt, onValueChange = { sgpt = it }, label = "sgpt (u/l)", modifier = Modifier.weight(1f))
        }}
        item { PremiumTextField(value = bilirubin, onValueChange = { bilirubin = it }, label = "bilirubin total (mg/dl)") }

        item { Text("fungsi ginjal (renal)", color = premiumaccent, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PremiumTextField(value = bun, onValueChange = { bun = it }, label = "bun (mg/dl)", modifier = Modifier.weight(1f))
            PremiumTextField(value = creatinine, onValueChange = { creatinine = it }, label = "creatinine (mg/dl)", modifier = Modifier.weight(1f))
        }}
        item { PremiumTextField(value = egfr, onValueChange = { egfr = it }, label = "egfr (ml/min/1.73m2)") }

        item { Text("panel hormonal", color = premiumaccent, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PremiumTextField(value = testoTotal, onValueChange = { testoTotal = it }, label = "t.total (ng/dl)", modifier = Modifier.weight(1f))
            PremiumTextField(value = testoFree, onValueChange = { testoFree = it }, label = "t.free (pg/ml)", modifier = Modifier.weight(1f))
        }}
        item { PremiumTextField(value = e2, onValueChange = { e2 = it }, label = "estradiol e2 (pg/ml)") }

        item { 
            Button(
                onClick = { 
                    viewModel.saveAssessment(
                        bloodSugar = gdp.toFloatOrNull(),
                        cholesterol = kolesterol.toFloatOrNull(),
                        hb = hb.toFloatOrNull(),
                        hematocrit = hematokrit.toFloatOrNull(),
                        sgot = sgot.toFloatOrNull(),
                        sgpt = sgpt.toFloatOrNull(),
                        bilirubin = bilirubin.toFloatOrNull(),
                        bun = bun.toFloatOrNull(),
                        creatinine = creatinine.toFloatOrNull(),
                        egfr = egfr.toFloatOrNull(),
                        testoTotal = testoTotal.toFloatOrNull(),
                        testoFree = testoFree.toFloatOrNull(),
                        estradiol = e2.toFloatOrNull()
                    )
                    onDismiss() 
                }, 
                modifier = Modifier.fillMaxWidth().height(50.dp), 
                colors = ButtonDefaults.buttonColors(containerColor = premiumaccent)
            ) { Text("simpan data lab", color = Color.White, fontWeight = FontWeight.Bold) } 
        }
        item { Spacer(Modifier.height(40.dp)) }
    }
}

@Composable
private fun FisikKlinisForm(viewModel: HealthViewModel, onDismiss: () -> Unit) {
    var sistolik by remember { mutableStateOf("") }
    var diastolik by remember { mutableStateOf("") }
    var hr by remember { mutableStateOf("") }
    var suhu by remember { mutableStateOf("") }
    var keluhan by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("🩺 fisik & klinis", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PremiumTextField(value = sistolik, onValueChange = { sistolik = it }, label = "sistolik (mmhg)", modifier = Modifier.weight(1f))
            PremiumTextField(value = diastolik, onValueChange = { diastolik = it }, label = "diastolik (mmhg)", modifier = Modifier.weight(1f))
        }}
        item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PremiumTextField(value = hr, onValueChange = { hr = it }, label = "heart rate (bpm)", modifier = Modifier.weight(1f))
            PremiumTextField(value = suhu, onValueChange = { suhu = it }, label = "suhu tubuh (°c)", modifier = Modifier.weight(1f))
        }}
        item { PremiumTextField(value = keluhan, onValueChange = { keluhan = it }, label = "keluhan gastrointestinal (mual/muntah/gerd)", isMultiline = true) }
        item { 
            Button(
                onClick = { 
                    viewModel.saveAssessment(
                        systolic = sistolik.toIntOrNull(),
                        diastolic = diastolik.toIntOrNull(),
                        hr = hr.toIntOrNull(),
                        temp = suhu.toFloatOrNull()
                    )
                    onDismiss() 
                }, 
                modifier = Modifier.fillMaxWidth().height(50.dp), 
                colors = ButtonDefaults.buttonColors(containerColor = premiumaccent)
            ) { Text("simpan rekam medis", color = Color.White, fontWeight = FontWeight.Bold) } 
        }
        item { Spacer(Modifier.height(40.dp)) }
    }
}

@Composable
private fun UpdateVitalsContent(viewModel: HealthViewModel, onDismiss: () -> Unit) {
    var tidur by remember { mutableStateOf("") }
    LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("kalibrasi vitals harian", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        item { PremiumTextField(value = tidur, onValueChange = { tidur = it }, label = "jam tidur (jam)") }
        item { 
            Button(
                onClick = { 
                    viewModel.saveAssessment(sleep = tidur.toFloatOrNull())
                    onDismiss() 
                }, 
                modifier = Modifier.fillMaxWidth(), 
                colors = ButtonDefaults.buttonColors(containerColor = premiumaccent)
            ) { Text("simpan", color = Color.White) } 
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMedicalRecordForm(onAdd: (MedicalRecord) -> Unit) {
    var doctor by remember { mutableStateOf("") }
    var diagnosis by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val selectedDateText = selectedDateMillis?.let { sdf.format(Date(it)) } ?: "pilih tanggal kunjungan"

    LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("🏥 tambah rekam kunjungan rs", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        item { PremiumTextField(value = doctor, onValueChange = { doctor = it }, label = "nama dokter / spesialis") }
        item { PremiumTextField(value = diagnosis, onValueChange = { diagnosis = it }, label = "diagnosa utama klinis") }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(0.05f))
                    .clickable { showDatePicker = true }
                    .padding(16.dp)
            ) {
                Text(selectedDateText, color = if(selectedDateMillis != null) textprimary else textmuted, fontSize = 14.sp)
            }
        }

        item { PremiumTextField(value = notes, onValueChange = { notes = it }, label = "catatan resep / intervensi medis", isMultiline = true) }
        item {
            Button(
                onClick = { 
                    if(doctor.isNotBlank() && diagnosis.isNotBlank() && selectedDateMillis != null) {
                        onAdd(MedicalRecord(UUID.randomUUID().toString(), selectedDateText, doctor, diagnosis, notes)) 
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = statusgood)
            ) { Text("simpan rekam medis", fontWeight = FontWeight.Bold) }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) { Text("SIMPAN", color = premiumaccent, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("BATAL") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun ViewMedicalRecordDetailSheet(record: MedicalRecord, onDismiss: () -> Unit) {
    LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Column {
                Text(record.doctor, color = premiumaccent, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(record.date, color = textsecondary, fontSize = 14.sp)
            }
        }
        item { Divider(color = Color.White.copy(0.05f)) }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Diagnosa:", color = textmuted, fontSize = 12.sp)
                Text(record.diagnosis, color = textprimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Catatan/Resep:", color = textmuted, fontSize = 12.sp)
                Text(record.notes, color = textsecondary, fontSize = 14.sp, lineHeight = 22.sp)
            }
        }
        item {
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f))) {
                Text("Tutup", color = textprimary)
            }
        }
        item { Spacer(Modifier.height(40.dp)) }
    }
}
