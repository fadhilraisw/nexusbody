package com.rais.nexusbody.feature.health.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rais.nexusbody.core.ui.components.PremiumGlassCard
import com.rais.nexusbody.core.ui.components.PremiumTextField
import com.rais.nexusbody.core.ui.theme.*
import com.rais.nexusbody.domain.model.Medication
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HealthAssessmentScreen() {
    var activeSheet by remember { mutableStateOf("") }
    var selectedTimeframe by remember { mutableStateOf("daily") }

    // data murni kosong menunggu database
    var conditions by remember { mutableStateOf(emptyList<String>()) }
    var medsList by remember { mutableStateOf(emptyList<Medication>()) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(20.dp, 20.dp, 20.dp, 120.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text("⚲ clinical tracking", color = textprimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            item { HealthTimeframeSelector(selectedTimeframe) { selectedTimeframe = it } }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("medical conditions", color = textsecondary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    IconButton(onClick = { activeSheet = "conditions" }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Edit, "edit kondisi", tint = premiumaccent, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
                if (conditions.isEmpty()) {
                    Text("belum ada data kondisi medis.", color = textmuted, fontSize = 12.sp)
                } else {
                    FlowRowConditions(conditions)
                }
            }

            item {
                VitalsOverviewCard(onClick = { activeSheet = "vitals" })
            }

            item {
                Text("pagd clinical forms", color = textsecondary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PagdQuickButton("antropometri", Modifier.weight(1f)) { activeSheet = "antro" }
                    PagdQuickButton("biokimia", Modifier.weight(1f)) { activeSheet = "bio" }
                    PagdQuickButton("fisik/klinis", Modifier.weight(1f)) { activeSheet = "fisik" }
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("medication scheduler", color = textsecondary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    TextButton(onClick = { activeSheet = "add_med" }) {
                        Text("+ tambah obat", color = premiumaccent, fontSize = 12.sp)
                    }
                }
            }

            if (medsList.isEmpty()) {
                item { Text("belum ada jadwal obat.", color = textmuted, fontSize = 12.sp) }
            } else {
                items(medsList) { med ->
                    MedicationCard(
                        med = med,
                        onDelete = { medsList = medsList.filter { it.id != med.id } }
                    )
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                Text("rekam medis & kunjungan", color = textsecondary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text("belum ada rekam medis dari ai.", color = textmuted, fontSize = 12.sp)
            }
        }

        if (activeSheet.isNotEmpty()) {
            ModalBottomSheet(
                onDismissRequest = { activeSheet = "" },
                containerColor = Color(0xFF0D0F12),
                modifier = Modifier.fillMaxHeight(0.9f)
            ) {
                when (activeSheet) {
                    "antro" -> AntropometriForm { activeSheet = "" }
                    "bio" -> BiokimiaForm { activeSheet = "" }
                    "fisik" -> FisikKlinisForm { activeSheet = "" }
                    "conditions" -> EditConditionsForm(conditions, onUpdate = { conditions = it }, onDismiss = { activeSheet = "" })
                    "vitals" -> UpdateVitalsContent { activeSheet = "" }
                    "add_med" -> AddMedicationForm { newMed ->
                        medsList = medsList + newMed
                        activeSheet = ""
                    }
                }
            }
        }
    }
}

// komponen ui pendukung lainnya tetap sama seperti sebelumnya (FlowRowConditions, dll)
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
private fun VitalsOverviewCard(onClick: () -> Unit) {
    PremiumGlassCard(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("patient vitals & metrics", color = textmuted, fontSize = 12.sp)
                Icon(Icons.Default.Favorite, null, tint = premiumaccent, modifier = Modifier.size(18.dp))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                VitalMetric("sleep", "--h", statuswarning)
                VitalMetric("weight", "--kg", textprimary)
                VitalMetric("body fat", "--%", statusgood)
                VitalMetric("visceral", "--", statusgood)
            }
            Divider(color = Color.White.copy(0.05f))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("tensi terakhir", color = textmuted, fontSize = 11.sp)
                    Text("--/-- mmhg", color = textprimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("resting hr", color = textmuted, fontSize = 11.sp)
                    Text("-- bpm", color = textprimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
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
private fun MedicationCard(med: Medication, onDelete: () -> Unit) {
    PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(med.name, color = textprimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${med.dosage} · ${med.frequency}", color = textsecondary, fontSize = 13.sp)
                Text("jadwal harian: ${med.scheduledTime}", color = premiumaccent, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, null, tint = statusdanger.copy(alpha = 0.6f))
            }
        }
    }
}

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

@Composable
private fun AddMedicationForm(onAdd: (Medication) -> Unit) {
    var name by remember { mutableStateOf("") }
    var dose by remember { mutableStateOf("") }
    var freq by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("💊 jadwalkan obat baru", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        item { PremiumTextField(value = name, onValueChange = { name = it }, label = "nama obat") }
        item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PremiumTextField(value = dose, onValueChange = { dose = it }, label = "dosis (mg)", modifier = Modifier.weight(1f))
            PremiumTextField(value = freq, onValueChange = { freq = it }, label = "frekuensi", modifier = Modifier.weight(1f))
        }}
        item { PremiumTextField(value = time, onValueChange = { time = it }, label = "waktu (hh:mm)") }
        item { Button(onClick = { if(name.isNotBlank()) onAdd(Medication(UUID.randomUUID().toString(), name, dose, freq, time)) }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = statusgood)) { Text("simpan jadwal", fontWeight = FontWeight.Bold) } }
    }
}

@Composable
private fun AntropometriForm(onDismiss: () -> Unit) {
    LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("📏 antropometri (pengukuran fisik)", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        item { PremiumTextField(value = "", onValueChange = {}, label = "berat badan aktual (kg)") }
        item { PremiumTextField(value = "", onValueChange = {}, label = "tinggi badan (cm)") }
        item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PremiumTextField(value = "", onValueChange = {}, label = "lila (cm)", modifier = Modifier.weight(1f))
            PremiumTextField(value = "", onValueChange = {}, label = "lingkar perut (cm)", modifier = Modifier.weight(1f))
        }}
        item { Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = premiumaccent)) { Text("simpan rekam medis", color = Color.White) } }
    }
}

@Composable
private fun BiokimiaForm(onDismiss: () -> Unit) {
    LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("⚗️ biokimia (hasil lab)", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PremiumTextField(value = "", onValueChange = {}, label = "gula darah puasa (mg/dl)", modifier = Modifier.weight(1f))
            PremiumTextField(value = "", onValueChange = {}, label = "gula darah sewaktu (mg/dl)", modifier = Modifier.weight(1f))
        }}
        item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PremiumTextField(value = "", onValueChange = {}, label = "kolesterol total (mg/dl)", modifier = Modifier.weight(1f))
            PremiumTextField(value = "", onValueChange = {}, label = "trigliserida (mg/dl)", modifier = Modifier.weight(1f))
        }}
        item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PremiumTextField(value = "", onValueChange = {}, label = "asam urat (mg/dl)", modifier = Modifier.weight(1f))
            PremiumTextField(value = "", onValueChange = {}, label = "hemoglobin (g/dl)", modifier = Modifier.weight(1f))
        }}
        item { Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = premiumaccent)) { Text("simpan rekam medis", color = Color.White) } }
    }
}

@Composable
private fun FisikKlinisForm(onDismiss: () -> Unit) {
    LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("🩺 fisik & klinis", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PremiumTextField(value = "", onValueChange = {}, label = "sistolik (mmhg)", modifier = Modifier.weight(1f))
            PremiumTextField(value = "", onValueChange = {}, label = "diastolik (mmhg)", modifier = Modifier.weight(1f))
        }}
        item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PremiumTextField(value = "", onValueChange = {}, label = "heart rate (bpm)", modifier = Modifier.weight(1f))
            PremiumTextField(value = "", onValueChange = {}, label = "suhu tubuh (°c)", modifier = Modifier.weight(1f))
        }}
        item { PremiumTextField(value = "", onValueChange = {}, label = "keluhan gastrointestinal (mual/muntah/gerd)", isMultiline = true) }
        item { Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = premiumaccent)) { Text("simpan rekam medis", color = Color.White) } }
    }
}

@Composable
private fun UpdateVitalsContent(onDismiss: () -> Unit) {
    LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("kalibrasi vitals harian", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        item { PremiumTextField(value = "", onValueChange = {}, label = "jam tidur (jam)") }
        item { Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = premiumaccent)) { Text("simpan", color = Color.White) } }
    }
}