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
import androidx.hilt.navigation.compose.hiltViewModel
import com.rais.nexusbody.core.ui.components.PremiumGlassCard
import com.rais.nexusbody.core.ui.components.PremiumTextField
import com.rais.nexusbody.core.ui.theme.*
import com.rais.nexusbody.feature.workout.WorkoutViewModel
import com.rais.nexusbody.feature.workout.ExerciseInput
import java.text.SimpleDateFormat
import java.util.*
import java.util.UUID

data class ExerciseLog(val id: String, val name: String, val muscles: Set<String>, val sets: String, val reps: String, val weight: String, val duration: String)

/**
 * WORKOUT LOG SCREEN (UI LAYER)
 * Peran: Manajemen riwayat latihan dan sistem bonus XP Muscle Mastery.
 * Logic: User Input -> WorkoutViewModel -> Parallel Persistence (Room & Supabase).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutLogScreen(viewModel: WorkoutViewModel = hiltViewModel()) {
    var activeSheet by remember { mutableStateOf("") }
    var selectedTimeframe by remember { mutableStateOf("weekly") }
    
    var pendingRoutineName by remember { mutableStateOf("") }
    var pendingDuration by remember { mutableStateOf(0) }
    var pendingNotes by remember { mutableStateOf("") }
    var pendingTimestamp by remember { mutableStateOf(System.currentTimeMillis()) }

    val sessions by viewModel.sessions.collectAsState()
    var exercises by remember { mutableStateOf(emptyList<ExerciseLog>()) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(20.dp, 20.dp, 20.dp, 120.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item { Text("⚡ training engine", color = textprimary, fontSize = 24.sp, fontWeight = FontWeight.Bold) }
            item { WorkoutTimeframeSelector(selectedTimeframe) { 
                selectedTimeframe = it 
                viewModel.updateTimeframe(it)
            } }
            item { MuscleMasteryCard() }

            item { Text("jadwal & riwayat", color = textsecondary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold) }

            if (sessions.isEmpty()) {
                item { Text("belum ada jadwal atau log latihan.", color = textmuted, fontSize = 12.sp) }
            } else {
                items(sessions) { sessionWithEx ->
                    WorkoutSessionCard(
                        sessionWithEx = sessionWithEx,
                        onDelete = { viewModel.deleteSession(sessionWithEx.session.id) },
                        onClick = { 
                            activeSheet = "view_session_${sessionWithEx.session.id}"
                        }
                    )
                }
            }
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
                    "add_session" -> AddSessionForm(
                        onSave = { name, dur, note, time ->
                            pendingRoutineName = name
                            pendingDuration = dur
                            pendingNotes = note
                            pendingTimestamp = time
                            exercises = emptyList()
                            activeSheet = "session_detail"
                        },
                        onDismiss = { activeSheet = "" }
                    )
                    "session_detail" -> SessionDetailSheet(
                        exercises = exercises,
                        onAddExerciseClick = { activeSheet = "add_exercise" },
                        onDeleteExercise = { id -> exercises = exercises.filter { it.id != id } },
                        onFinishSession = {
                            val inputs = exercises.map { 
                                ExerciseInput(it.name, it.muscles.toList(), it.sets.toIntOrNull() ?: 0, it.reps.toIntOrNull() ?: 0, it.weight.toFloatOrNull() ?: 0f)
                            }
                            viewModel.saveWorkoutSession(pendingRoutineName, pendingDuration, pendingNotes, inputs, pendingTimestamp)
                            activeSheet = ""
                        }
                    )
                    "add_exercise" -> AddExerciseForm(
                        onSave = { newEx ->
                            exercises = exercises + newEx
                            activeSheet = "session_detail"
                        },
                        onCancel = { activeSheet = "session_detail" }
                    )
                    else -> {
                        if (activeSheet.startsWith("view_session_")) {
                            val sessionId = activeSheet.removePrefix("view_session_")
                            val sessionData = sessions.find { it.session.id == sessionId }
                            if (sessionData != null) {
                                ViewSessionDetailSheet(sessionData) { activeSheet = "" }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkoutSessionCard(
    sessionWithEx: com.rais.nexusbody.data.local.dao.SessionWithExercises, 
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    PremiumGlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { onClick() }) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(sessionWithEx.session.routineName, color = textprimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${sessionWithEx.session.totalDurationMinutes}m · ${sdf.format(Date(sessionWithEx.session.timestamp))}", color = textsecondary, fontSize = 12.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${sessionWithEx.exercises.size} gerakan", color = premiumaccent, fontSize = 11.sp)
                    if (sessionWithEx.session.xpEarned > 0) {
                        Spacer(Modifier.width(8.dp))
                        Text("+${sessionWithEx.session.xpEarned} XP", color = statusgood, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, null, tint = statusdanger.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
private fun ViewSessionDetailSheet(sessionWithEx: com.rais.nexusbody.data.local.dao.SessionWithExercises, onDismiss: () -> Unit) {
    val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy · HH:mm", Locale.getDefault())
    LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Column {
                Text(sessionWithEx.session.routineName, color = textprimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(sdf.format(Date(sessionWithEx.session.timestamp)), color = premiumaccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                if (sessionWithEx.session.clinicalNotes.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text("Notes: ${sessionWithEx.session.clinicalNotes}", color = textsecondary, fontSize = 13.sp)
                }
            }
        }
        
        item { Divider(color = Color.White.copy(0.05f)) }
        
        item { Text("Gerakan (${sessionWithEx.exercises.size})", color = textprimary, fontWeight = FontWeight.Bold) }
        
        items(sessionWithEx.exercises) { ex ->
            PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(ex.exerciseName, color = textprimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${ex.sets} Sets", color = textsecondary, fontSize = 13.sp)
                        Text("${ex.reps} Reps", color = textsecondary, fontSize = 13.sp)
                        Text("${ex.weightKg}kg", color = premiumaccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddSessionForm(onSave: (String, Int, String, Long) -> Unit, onDismiss: () -> Unit) {
    var routineName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var selectedTime by remember { mutableStateOf("08:00") }

    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        item { Text("buat sesi latihan", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(Color.White.copy(0.05f)).clickable { showDatePicker = true }.padding(16.dp)) {
                    Text(sdf.format(Date(selectedDateMillis)), color = textprimary, fontSize = 14.sp)
                }
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(Color.White.copy(0.05f)).clickable { showTimePicker = true }.padding(16.dp)) {
                    Text(selectedTime, color = textprimary, fontSize = 14.sp)
                }
            }
        }
        item { PremiumTextField(value = routineName, onValueChange = { routineName = it }, label = "nama rutinitas (ex: upper body)") }
        item { PremiumTextField(value = duration, onValueChange = { duration = it }, label = "durasi (menit)") }
        item { PremiumTextField(value = notes, onValueChange = { notes = it }, label = "catatan medis / keluhan", isMultiline = true) }
        item { 
            Button(
                onClick = { 
                    if(routineName.isNotBlank()) {
                        val finalTime = combineDateAndTime(selectedDateMillis, selectedTime)
                        onSave(routineName, duration.toIntOrNull() ?: 0, notes, finalTime)
                    }
                }, 
                modifier = Modifier.fillMaxWidth().height(50.dp), 
                colors = ButtonDefaults.buttonColors(containerColor = premiumaccent)
            ) { Text("lanjut tambah gerakan", fontWeight = FontWeight.Bold, color = Color.White) } 
        }
    }

    if (showDatePicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
        DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = {
            TextButton(onClick = { selectedDateMillis = state.selectedDateMillis ?: selectedDateMillis; showDatePicker = false }) { Text("OK", color = premiumaccent) }
        }) { DatePicker(state = state) }
    }
    if (showTimePicker) {
        val state = rememberTimePickerState(initialHour = 8, initialMinute = 0)
        AlertDialog(onDismissRequest = { showTimePicker = false }, confirmButton = {
            TextButton(onClick = { selectedTime = String.format("%02d:%02d", state.hour, state.minute); showTimePicker = false }) { Text("OK", color = premiumaccent) }
        }, text = { TimePicker(state = state) })
    }
}

private fun combineDateAndTime(dateMillis: Long, timeStr: String): Long {
    val cal = Calendar.getInstance()
    cal.timeInMillis = dateMillis
    val parts = timeStr.split(":")
    cal.set(Calendar.HOUR_OF_DAY, parts[0].toInt())
    cal.set(Calendar.MINUTE, parts[1].toInt())
    return cal.timeInMillis
}

@Composable
private fun SessionDetailSheet(exercises: List<ExerciseLog>, onAddExerciseClick: () -> Unit, onDeleteExercise: (String) -> Unit, onFinishSession: () -> Unit) {
    LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("sesi latihan", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        if (exercises.isEmpty()) { item { Text("belum ada gerakan di sesi ini.", color = textmuted, fontSize = 12.sp) } }
        else { items(exercises) { ex -> ExerciseDetailCard(exercise = ex, onDelete = { onDeleteExercise(ex.id) }) } }
        item {
            Button(onClick = onAddExerciseClick, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f))) {
                Icon(Icons.Default.Add, null, tint = Color.White); Spacer(Modifier.width(8.dp)); Text("tambah gerakan", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        if (exercises.isNotEmpty()) {
            item {
                Button(onClick = onFinishSession, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = statusgood)) {
                    Text("selesai & simpan sesi", color = Color.White, fontWeight = FontWeight.Bold)
                }
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
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Delete, null, tint = statusdanger.copy(alpha = 0.6f)) }
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
        "chest" to listOf("CHEST"),
        "shoulders" to listOf("SHOULDERS"),
        "back" to listOf("BACK_UPPER", "BACK_LOWER"),
        "arms" to listOf("BICEPS", "TRICEPS", "FOREARMS"),
        "legs" to listOf("QUADRICEPS", "HAMSTRINGS", "GLUTES", "CALVES"),
        "core" to listOf("CORE_ABS", "CORE_OBLIQUES", "CORE_LOWER_BACK"),
        "systemic" to listOf("CARDIOVASCULAR", "AGILITY")
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

@Composable
private fun WorkoutTimeframeSelector(selected: String, onSelect: (String) -> Unit) {
    val frames = listOf("daily", "weekly", "monthly", "yearly", "custom")
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(frames) { frame ->
            val isSelected = selected == frame
            Box(modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(if (isSelected) premiumaccent else Color.White.copy(0.05f)).clickable { onSelect(frame) }.padding(horizontal = 16.dp, vertical = 8.dp)) {
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
