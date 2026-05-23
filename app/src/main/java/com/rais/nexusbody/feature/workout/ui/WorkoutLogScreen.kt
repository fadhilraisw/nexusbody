package com.rais.nexusbody.feature.workout.ui

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
import androidx.compose.material.icons.filled.Star
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
import java.util.UUID

data class ExerciseLog(val id: String, val name: String, val muscles: Set<String>, val sets: String, val reps: String, val weight: String, val duration: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutLogScreen() {
    var activeSheet by remember { mutableStateOf("") }
    var selectedTimeframe by remember { mutableStateOf("weekly") }

    // data dikosongkan
    var exercises by remember { mutableStateOf(emptyList<ExerciseLog>()) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(20.dp, 20.dp, 20.dp, 120.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item { Text("⚡ training engine", color = textprimary, fontSize = 24.sp, fontWeight = FontWeight.Bold) }
            item { WorkoutTimeframeSelector(selectedTimeframe) { selectedTimeframe = it } }
            item { MuscleMasteryCard() }

            item { Text("jadwal & riwayat", color = textsecondary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold) }

            item { Text("belum ada jadwal atau log latihan.", color = textmuted, fontSize = 12.sp) }
        }

        FloatingActionButton(
            onClick = { activeSheet = "add_session" },
            containerColor = premiumaccent,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        ) {
            Icon(Icons.Default.Add, "tambah log workout", tint = Color.White)
        }

        if (activeSheet.isNotEmpty()) {
            ModalBottomSheet(
                onDismissRequest = { activeSheet = "" },
                containerColor = Color(0xFF0D0F12),
                modifier = Modifier.fillMaxHeight(0.95f)
            ) {
                when (activeSheet) {
                    "add_session" -> AddSessionForm { activeSheet = "" }
                    "session_detail" -> SessionDetailSheet(
                        exercises = exercises,
                        onAddExerciseClick = { activeSheet = "add_exercise" },
                        onDeleteExercise = { id -> exercises = exercises.filter { it.id != id } }
                    )
                    "add_exercise" -> AddExerciseForm(
                        onSave = { newEx ->
                            exercises = exercises + newEx
                            activeSheet = "session_detail"
                        },
                        onCancel = { activeSheet = "session_detail" }
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutTimeframeSelector(selected: String, onSelect: (String) -> Unit) {
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
private fun MuscleMasteryCard() {
    PremiumGlassCard(modifier = Modifier.fillMaxWidth().clickable { }) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("clinical mastery profile", color = textmuted, fontSize = 12.sp)
                Icon(Icons.Default.Star, null, tint = premiumaccent, modifier = Modifier.size(18.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MuscleStatBar("chest", 0, 0.0f, statusgood)
                MuscleStatBar("back", 0, 0.0f, textprimary)
                MuscleStatBar("arms", 0, 0.0f, Color(0xFF4FC3F7))
                MuscleStatBar("legs", 0, 0.0f, premiumaccent)
                MuscleStatBar("core", 0, 0.0f, statuswarning)
            }
        }
    }
}

@Composable
private fun MuscleStatBar(label: String, level: Int, progress: Float, color: Color) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(label, color = textprimary, fontSize = 12.sp, modifier = Modifier.width(48.dp))
        Box(modifier = Modifier.weight(1f).height(6.dp).clip(CircleShape).background(Color.White.copy(0.05f))) {
            Box(modifier = Modifier.fillMaxWidth(progress).fillMaxHeight().background(color))
        }
        Text("lv.$level", color = textsecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp))
    }
}

@Composable
private fun AddSessionForm(onDismiss: () -> Unit) {
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var routineName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        item { Text("buat sesi latihan", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PremiumTextField(value = date, onValueChange = { date = it }, label = "tanggal (dd/mm/yy)", modifier = Modifier.weight(1f))
                PremiumTextField(value = time, onValueChange = { time = it }, label = "jam (hh:mm)", modifier = Modifier.weight(1f))
            }
        }
        item { PremiumTextField(value = routineName, onValueChange = { routineName = it }, label = "nama rutinitas (ex: upper body)") }
        item { PremiumTextField(value = notes, onValueChange = { notes = it }, label = "catatan medis / keluhan", isMultiline = true) }
        item { Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = premiumaccent)) { Text("buat sesi", fontWeight = FontWeight.Bold, color = Color.White) } }
    }
}

@Composable
private fun SessionDetailSheet(exercises: List<ExerciseLog>, onAddExerciseClick: () -> Unit, onDeleteExercise: (String) -> Unit) {
    LazyColumn(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("sesi latihan", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
        }
        if (exercises.isEmpty()) {
            item { Text("belum ada gerakan di sesi ini.", color = textmuted, fontSize = 12.sp) }
        } else {
            items(exercises) { ex -> ExerciseDetailCard(exercise = ex, onDelete = { onDeleteExercise(ex.id) }) }
        }
        item {
            Button(
                onClick = onAddExerciseClick,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("tambah gerakan", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        item { Spacer(Modifier.height(40.dp)) }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExerciseDetailCard(exercise: ExerciseLog, onDelete: () -> Unit) {
    PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(exercise.name, color = textprimary, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Delete, null, tint = statusdanger.copy(0.6f)) }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("sets", color = textmuted, fontSize = 10.sp); Text(exercise.sets, color = textprimary, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
                Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("reps", color = textmuted, fontSize = 10.sp); Text(exercise.reps, color = textprimary, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
                Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("beban", color = textmuted, fontSize = 10.sp); Text("${exercise.weight}kg", color = premiumaccent, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
                Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("durasi", color = textmuted, fontSize = 10.sp); Text("${exercise.duration}m", color = textprimary, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
            }
            Divider(color = Color.White.copy(0.05f))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                exercise.muscles.forEach { muscle ->
                    Text(muscle, color = textsecondary, fontSize = 10.sp, modifier = Modifier.background(Color.White.copy(0.05f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddExerciseForm(onSave: (ExerciseLog) -> Unit, onCancel: () -> Unit) {
    var exName by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var selectedMuscles by remember { mutableStateOf(setOf<String>()) }
    val anatomicalMap = mapOf(
        "chest" to listOf("pectoralis major (clavicular/upper)", "pectoralis major (sternal/lower)", "pectoralis minor"),
        "shoulders" to listOf("anterior deltoid (front)", "lateral deltoid (side)", "posterior deltoid (rear)", "rotator cuff"),
        "back" to listOf("latissimus dorsi", "trapezius (upper/mid/lower)", "rhomboids", "erector spinae"),
        "arms" to listOf("biceps (long head)", "biceps (short head)", "brachialis", "triceps (long head)", "triceps (lateral head)", "triceps (medial head)", "forearm flexors", "forearm extensors"),
        "legs" to listOf("quadriceps", "hamstrings", "gluteus maximus", "gluteus medius/minimus", "calves (gastrocnemius/soleus)"),
        "core" to listOf("rectus abdominis", "obliques", "transversus abdominis"),
        "systemic" to listOf("vo2 max cardio", "zone 2 cardio", "joint mobility (rom)", "dynamic stretch", "static stretch")
    )
    LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("tambah gerakan", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = onCancel) { Text("batal", color = textmuted) }
            }
        }
        item { PremiumTextField(value = exName, onValueChange = { exName = it }, label = "nama gerakan (ex: bench press)") }
        item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { PremiumTextField(value = sets, onValueChange = { sets = it }, label = "sets", modifier = Modifier.weight(1f)); PremiumTextField(value = reps, onValueChange = { reps = it }, label = "reps", modifier = Modifier.weight(1f)) } }
        item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { PremiumTextField(value = weight, onValueChange = { weight = it }, label = "beban (kg)", modifier = Modifier.weight(1f)); PremiumTextField(value = duration, onValueChange = { duration = it }, label = "durasi (menit)", modifier = Modifier.weight(1f)) } }
        item { Text("target anatomi spesifik", color = premiumaccent, fontSize = 14.sp, fontWeight = FontWeight.Bold) }
        anatomicalMap.forEach { (group, muscles) ->
            item {
                Text(group, color = textsecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    muscles.forEach { muscle ->
                        val isSel = selectedMuscles.contains(muscle)
                        Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(if(isSel) premiumaccent else Color.White.copy(0.05f)).clickable { selectedMuscles = if(isSel) selectedMuscles - muscle else selectedMuscles + muscle }.padding(horizontal = 12.dp, vertical = 6.dp)) { Text(muscle, color = if(isSel) Color.White else textsecondary, fontSize = 11.sp) }
                    }
                }
            }
        }
        item { Button(onClick = { if(exName.isNotBlank()) onSave(ExerciseLog(UUID.randomUUID().toString(), exName, selectedMuscles, sets, reps, weight, duration)) }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = statusgood)) { Text("simpan gerakan", fontWeight = FontWeight.Bold, color = Color.White) } }
    }
}