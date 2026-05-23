package com.rais.nexusbody.feature.nutrition.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.rais.nexusbody.feature.nutrition.NutritionViewModel
import com.rais.nexusbody.domain.model.NutritionGoal
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionLogScreen(viewModel: NutritionViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var showAddMealSheet by remember { mutableStateOf(false) }
    var showGoalEditSheet by remember { mutableStateOf(false) }
    var selectedTimeframe by remember { mutableStateOf("daily") }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(20.dp, 20.dp, 20.dp, 120.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text("▦ nutrition analytics", color = textprimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            item { TimeframeSelector(selectedTimeframe) { selectedTimeframe = it } }

            item {
                MacroBudgetCard(
                    timeframe = selectedTimeframe,
                    activeGoal = state.activeGoal,
                    onClick = { showGoalEditSheet = true }
                )
            }

            item { Text("recent intake", color = textsecondary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold) }

            // data dikosongkan
            item {
                Text("belum ada log makanan tercatat. tekan '+' untuk mulai.", color = textmuted, fontSize = 12.sp)
            }
        }

        FloatingActionButton(
            onClick = { showAddMealSheet = true },
            containerColor = statusgood,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        ) {
            Icon(Icons.Default.Add, "add", tint = Color.White)
        }

        if (showGoalEditSheet) {
            ModalBottomSheet(
                onDismissRequest = { showGoalEditSheet = false },
                containerColor = Color(0xFF0D0F12),
                modifier = Modifier.fillMaxHeight(0.85f)
            ) {
                GoalEditContent(viewModel) { showGoalEditSheet = false }
            }
        }

        if (showAddMealSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddMealSheet = false },
                containerColor = Color(0xFF15171C),
                modifier = Modifier.fillMaxHeight(0.9f)
            ) {
                SmartNutritionForm(viewModel) { showAddMealSheet = false }
            }
        }
    }
}

@Composable
private fun MacroBudgetCard(timeframe: String, activeGoal: NutritionGoal, onClick: () -> Unit) {
    PremiumGlassCard(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("current budget ($timeframe)", color = textmuted, fontSize = 12.sp)
                    Text("0 / ${activeGoal.calorieTarget} kcal", color = statuswarning, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                }
                Icon(Icons.Default.Edit, null, tint = premiumaccent, modifier = Modifier.size(18.dp))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MacroBudgetBar("protein", 0, activeGoal.proteinTarget, statusgood)
                MacroBudgetBar("carbs", 0, activeGoal.carbTarget, textprimary)
                MacroBudgetBar("fats", 0, activeGoal.fatTarget, statusdanger)
            }
        }
    }
}

@Composable
private fun GoalEditContent(viewModel: NutritionViewModel, onDismiss: () -> Unit) {
    val state by viewModel.state.collectAsState()
    var newCal by remember { mutableStateOf(state.activeGoal.calorieTarget.toString()) }
    var newPro by remember { mutableStateOf(state.activeGoal.proteinTarget.toString()) }
    var newCarb by remember { mutableStateOf(state.activeGoal.carbTarget.toString()) }
    var newFat by remember { mutableStateOf(state.activeGoal.fatTarget.toString()) }

    LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        item {
            Text("⚙️ update daily budget", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("target ini akan digunakan sebagai budget harian aktif anda", color = textsecondary, fontSize = 12.sp)
        }

        item {
            PremiumGlassCard {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    PremiumTextField(value = newCal, onValueChange = { newCal = it }, label = "Kalori (kcal)")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PremiumTextField(value = newPro, onValueChange = { newPro = it }, label = "Pro (g)", modifier = Modifier.weight(1f))
                        PremiumTextField(value = newCarb, onValueChange = { newCarb = it }, label = "Carb (g)", modifier = Modifier.weight(1f))
                        PremiumTextField(value = newFat, onValueChange = { newFat = it }, label = "Fat (g)", modifier = Modifier.weight(1f))
                    }
                    Button(
                        onClick = {
                            viewModel.updateDailyGoal(newCal.toIntOrNull() ?: 0, newPro.toIntOrNull() ?: 0, newCarb.toIntOrNull() ?: 0, newFat.toIntOrNull() ?: 0)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = statusgood)
                    ) {
                        Text("Aktifkan Target Baru", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item { Text("goal history", color = textprimary, fontWeight = FontWeight.Bold) }

        if (state.goalHistory.isEmpty()) {
            item { Text("belum ada riwayat target", color = textmuted, fontSize = 12.sp) }
        } else {
            items(state.goalHistory.reversed()) { goal ->
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                PremiumGlassCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(sdf.format(Date(goal.dateSet)), color = textmuted, fontSize = 11.sp)
                            Text("${goal.calorieTarget} kcal", color = textprimary, fontWeight = FontWeight.Bold)
                            Text("P: ${goal.proteinTarget}g C: ${goal.carbTarget}g F: ${goal.fatTarget}g", color = textsecondary, fontSize = 12.sp)
                        }
                        IconButton(onClick = { viewModel.deleteGoalHistory(goal.id) }) {
                            Icon(Icons.Default.Delete, null, tint = statusdanger.copy(0.6f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroBudgetBar(label: String, current: Int, max: Int, color: Color) {
    val progress = if (max > 0) (current.toFloat() / max.toFloat()).coerceIn(0f, 1f) else 0f
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("$current/${max}g", color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Box(modifier = Modifier.width(80.dp).height(6.dp).clip(CircleShape).background(Color.White.copy(0.05f))) {
            Box(modifier = Modifier.fillMaxWidth(progress).fillMaxHeight().background(color))
        }
        Text(label, color = textsecondary, fontSize = 10.sp)
    }
}

@Composable
private fun TimeframeSelector(selected: String, onSelect: (String) -> Unit) {
    val frames = listOf("daily", "weekly", "monthly", "yearly", "custom")
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(frames) { frame ->
            val isSelected = selected == frame
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) statusgood else Color.White.copy(0.05f))
                    .clickable { onSelect(frame) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(frame, color = if (isSelected) Color.White else textsecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SmartNutritionForm(viewModel: NutritionViewModel, onDismiss: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var showAiFallback by remember { mutableStateOf(false) }
    var isManualMode by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("pilih tanggal") }
    var selectedTime by remember { mutableStateOf("pilih jam") }

    LazyColumn(contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        item { Text("log konsumsi", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(Color.White.copy(0.05f)).clickable { showDatePicker = true }.padding(16.dp)) {
                    Text(selectedDate, color = textprimary, fontSize = 14.sp)
                }
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(Color.White.copy(0.05f)).clickable { showTimePicker = true }.padding(16.dp)) {
                    Text(selectedTime, color = textprimary, fontSize = 14.sp)
                }
            }
        }

        if (!isManualMode) {
            item {
                PremiumTextField(value = query, onValueChange = { query = it; showAiFallback = it.isNotEmpty() }, label = "cari di database global...")

                if (showAiFallback) {
                    Spacer(Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White.copy(0.02f))) {
                        Column {
                            Text("mencari '$query'...", color = textmuted, modifier = Modifier.padding(16.dp), fontSize = 12.sp)
                            Divider(color = Color.White.copy(0.05f))
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { isManualMode = true }.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Star, null, tint = premiumaccent, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("tidak ada di database? analisa dengan ai", color = premiumaccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        if (isManualMode) {
            item {
                PremiumGlassCard {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("mode manual & ai", color = textprimary, fontWeight = FontWeight.Bold)
                            TextButton(onClick = { isManualMode = false }) { Text("batal", color = textmuted) }
                        }
                        PremiumTextField(value = query, onValueChange = { query = it }, label = "nama makanan")
                        PremiumTextField(value = "", onValueChange = {}, label = "porsi (gram / mangkuk)")

                        Text("isi yang anda tahu, ai melengkapi sisanya:", color = premiumaccent, fontSize = 11.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            PremiumTextField(value = "", onValueChange = {}, label = "kcal", modifier = Modifier.weight(1f))
                            PremiumTextField(value = "", onValueChange = {}, label = "pro", modifier = Modifier.weight(1f))
                            PremiumTextField(value = "", onValueChange = {}, label = "carb", modifier = Modifier.weight(1f))
                            PremiumTextField(value = "", onValueChange = {}, label = "fat", modifier = Modifier.weight(1f))
                        }

                        Button(onClick = { }, colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f)), modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Star, null, tint = premiumaccent, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("lengkapi data dengan ai", color = textprimary)
                        }
                    }
                }
            }
        }

        item {
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = statusgood)) {
                Text("simpan log", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { TextButton(onClick = { selectedDate = "terpilih"; showDatePicker = false }) { Text("ok") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = { TextButton(onClick = { selectedTime = "${timePickerState.hour}:${timePickerState.minute}"; showTimePicker = false }) { Text("ok") } },
            text = { TimePicker(state = timePickerState) }
        )
    }
}