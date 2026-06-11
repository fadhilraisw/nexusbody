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
import com.rais.nexusbody.data.local.entity.NutritionLogEntity
import com.rais.nexusbody.feature.nutrition.NutritionViewModel
import com.rais.nexusbody.domain.model.NutritionGoal
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionLogScreen(viewModel: NutritionViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var activeSheet by remember { mutableStateOf("") }
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

            item { TimeframeSelector(selectedTimeframe) { 
                selectedTimeframe = it 
                viewModel.updateTimeframe(it)
            } }

            item {
                MacroBudgetCard(
                    timeframe = selectedTimeframe,
                    activeGoal = state.activeGoal,
                    currentCalories = state.currentCalories,
                    currentProtein = state.currentProtein.toInt(),
                    currentCarbs = state.currentCarbs.toInt(),
                    currentFat = state.currentFat.toInt(),
                    onClick = { showGoalEditSheet = true }
                )
            }

            item { Text("recent intake", color = textsecondary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold) }

            if (state.todaysLogs.isEmpty()) {
                item {
                    Text("belum ada log makanan tercatat. tekan '+' untuk mulai.", color = textmuted, fontSize = 12.sp)
                }
            } else {
                items(state.todaysLogs.reversed()) { log ->
                    IntakeCard(
                        log = log,
                        onDelete = { viewModel.deleteLog(log.id) },
                        onClick = { activeSheet = "view_log_${log.id}" }
                    )
                }
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

        if (activeSheet.isNotEmpty()) {
            ModalBottomSheet(
                onDismissRequest = { activeSheet = "" },
                containerColor = Color(0xFF0D0F12)
            ) {
                if (activeSheet.startsWith("view_log_")) {
                    val logId = activeSheet.removePrefix("view_log_")
                    val log = state.todaysLogs.find { it.id == logId }
                    if (log != null) {
                        ViewNutritionDetailSheet(log) { activeSheet = "" }
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroBudgetCard(
    timeframe: String, 
    activeGoal: NutritionGoal, 
    currentCalories: Int,
    currentProtein: Int,
    currentCarbs: Int,
    currentFat: Int,
    onClick: () -> Unit
) {
    PremiumGlassCard(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("current budget ($timeframe)", color = textmuted, fontSize = 12.sp)
                    Text("$currentCalories / ${activeGoal.calorieTarget} kcal", color = statuswarning, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                }
                Icon(Icons.Default.Edit, null, tint = premiumaccent, modifier = Modifier.size(18.dp))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MacroBudgetBar("protein", currentProtein, activeGoal.proteinTarget, statusgood)
                MacroBudgetBar("carbs", currentCarbs, activeGoal.carbTarget, textprimary)
                MacroBudgetBar("fats", currentFat, activeGoal.fatTarget, statusdanger)
            }
        }
    }
}

@Composable
private fun IntakeCard(log: NutritionLogEntity, onDelete: () -> Unit, onClick: () -> Unit) {
    PremiumGlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { onClick() }) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(log.foodName, color = textprimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("${log.portionGrams.toInt()}g · ${log.calories} kcal", color = textsecondary, fontSize = 12.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NutrientTinyBadge("P", log.proteinGrams.toInt().toString(), statusgood)
                    NutrientTinyBadge("C", log.carbsGrams.toInt().toString(), textprimary)
                    NutrientTinyBadge("F", log.fatGrams.toInt().toString(), statusdanger)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, null, tint = statusdanger.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun ViewNutritionDetailSheet(log: NutritionLogEntity, onDismiss: () -> Unit) {
    val sdf = SimpleDateFormat("dd MMMM yyyy · HH:mm", Locale.getDefault())
    LazyColumn(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        item {
            Column {
                Text(log.foodName, color = textprimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(sdf.format(Date(log.timestamp)), color = premiumaccent, fontSize = 13.sp)
            }
        }
        item { Divider(color = Color.White.copy(0.05f)) }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Porsi", color = textmuted, fontSize = 12.sp)
                    Text("${log.portionGrams.toInt()} gram", color = textprimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Total Energi", color = textmuted, fontSize = 12.sp)
                    Text("${log.calories} kcal", color = statuswarning, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        item {
            PremiumGlassCard {
                Row(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    DetailNutrientItem("Protein", "${log.proteinGrams}g", statusgood)
                    DetailNutrientItem("Carbs", "${log.carbsGrams}g", textprimary)
                    DetailNutrientItem("Fats", "${log.fatGrams}g", statusdanger)
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

@Composable
private fun DetailNutrientItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(label, color = textmuted, fontSize = 11.sp)
    }
}

@Composable
private fun NutrientTinyBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Text(label, color = textmuted, fontSize = 8.sp)
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
    val state by viewModel.state.collectAsState()
    var isManualMode by remember { mutableStateOf(false) }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    LazyColumn(contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        item { 
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("log konsumsi", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                if (isManualMode) {
                    TextButton(onClick = { isManualMode = false }) { Text("cari database", color = premiumaccent) }
                } else {
                    TextButton(onClick = { isManualMode = true }) { Text("input manual/ai", color = premiumaccent) }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(Color.White.copy(0.05f)).clickable { showDatePicker = true }.padding(16.dp)) {
                    Text(state.mealDate, color = textprimary, fontSize = 14.sp)
                }
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(Color.White.copy(0.05f)).clickable { showTimePicker = true }.padding(16.dp)) {
                    Text(state.mealTime, color = textprimary, fontSize = 14.sp)
                }
            }
        }

        if (!isManualMode) {
            item {
                PremiumTextField(
                    value = state.searchQuery, 
                    onValueChange = { viewModel.updateSearchQuery(it) }, 
                    label = "cari makanan (Indo/Global)..."
                )

                if (state.isSearching) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), color = premiumaccent)
                }

                if (state.searchResults.isNotEmpty() && state.selectedFood == null) {
                    PremiumGlassCard(modifier = Modifier.padding(top = 8.dp)) {
                        Column {
                            state.searchResults.take(5).forEach { food ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.selectFood(food) }
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(food.productName ?: "Tanpa Nama", color = textprimary, fontWeight = FontWeight.Bold)
                                        Text("Global Database", color = textmuted, fontSize = 11.sp)
                                    }
                                    Icon(Icons.Default.Add, null, tint = premiumaccent)
                                }
                                Divider(color = Color.White.copy(0.05f))
                            }
                        }
                    }
                } else if (state.searchQuery.length > 2 && !state.isSearching && state.searchResults.isEmpty() && state.selectedFood == null) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp).clickable { isManualMode = true }, contentAlignment = Alignment.Center) {
                        Text("data tidak ditemukan. klik untuk input manual/ai", color = premiumaccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (isManualMode) {
            item {
                PremiumGlassCard {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("mode manual & ai (segera hadir)", color = statusgood, fontWeight = FontWeight.Bold)
                        PremiumTextField(value = state.searchQuery, onValueChange = { viewModel.updateSearchQuery(it) }, label = "nama makanan")
                        Text("fitur integrasi AI untuk melengkapi data otomatis akan tersedia di update berikutnya.", color = textmuted, fontSize = 11.sp)
                    }
                }
            }
        }

        state.selectedFood?.let { food ->
            item {
                PremiumGlassCard {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Detail Nutrisi: ${food.productName}", color = premiumaccent, fontWeight = FontWeight.Bold)
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            PremiumTextField(
                                value = state.servingSizeGrams, 
                                onValueChange = { viewModel.updateServingSize(it) }, 
                                label = "Porsi (gram)", 
                                modifier = Modifier.weight(1f)
                            )
                        }

                        val weight = state.servingSizeGrams.toFloatOrNull() ?: 100f
                        val multiplier = weight / 100f
                        val cals = ((food.nutriments?.energyKcal ?: 0f) * multiplier).toInt()
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            NutrientSmallInfo("Kcal", cals.toString())
                            NutrientSmallInfo("Pro", String.format(Locale.getDefault(), "%.1fg", (food.nutriments?.proteins ?: 0f) * multiplier))
                            NutrientSmallInfo("Carb", String.format(Locale.getDefault(), "%.1fg", (food.nutriments?.carbs ?: 0f) * multiplier))
                            NutrientSmallInfo("Fat", String.format(Locale.getDefault(), "%.1fg", (food.nutriments?.fat ?: 0f) * multiplier))
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = { 
                    viewModel.saveLog()
                    onDismiss()
                }, 
                enabled = state.selectedFood != null,
                modifier = Modifier.fillMaxWidth().height(50.dp), 
                colors = ButtonDefaults.buttonColors(containerColor = statusgood)
            ) {
                Text("simpan log", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { 
                TextButton(onClick = { 
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val dateStr = datePickerState.selectedDateMillis?.let { sdf.format(Date(it)) } ?: state.mealDate
                    viewModel.updateDateTime(state.mealTime, dateStr)
                    showDatePicker = false 
                }) { Text("OK", color = premiumaccent) } 
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("BATAL") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = { 
                TextButton(onClick = { 
                    val timeStr = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                    viewModel.updateDateTime(timeStr, state.mealDate)
                    showTimePicker = false 
                }) { Text("OK", color = premiumaccent) } 
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("BATAL") } },
            text = { TimePicker(state = timePickerState) }
        )
    }
}

@Composable
private fun NutrientSmallInfo(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = textprimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(label, color = textmuted, fontSize = 10.sp)
    }
}
