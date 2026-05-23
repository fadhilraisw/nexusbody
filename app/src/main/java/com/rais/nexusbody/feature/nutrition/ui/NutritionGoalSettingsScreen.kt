package com.rais.nexusbody.feature.nutrition.ui

import com.rais.nexusbody.domain.model.NutritionGoal
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rais.nexusbody.core.ui.components.PremiumGlassCard
import com.rais.nexusbody.core.ui.components.PremiumTextField
import com.rais.nexusbody.core.ui.theme.*
import com.rais.nexusbody.feature.nutrition.NutritionViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NutritionGoalSettingsScreen(viewModel: NutritionViewModel, onBack: () -> Unit) {
    val state by viewModel.state.collectAsState()

    var newCal by remember { mutableStateOf("") }
    var newPro by remember { mutableStateOf("") }
    var newCarb by remember { mutableStateOf("") }
    var newFat by remember { mutableStateOf("") }

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text("⚙️ budget configuration", color = textprimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("setel target harian terbaru anda", color = textsecondary, fontSize = 12.sp)
        }

        // form input target baru
        item {
            PremiumGlassCard {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("konfigurasi target aktif", color = premiumaccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    PremiumTextField(value = newCal, onValueChange = { newCal = it }, label = "target kalori (kcal)")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PremiumTextField(value = newPro, onValueChange = { newPro = it }, label = "pro (g)", modifier = Modifier.weight(1f))
                        PremiumTextField(value = newCarb, onValueChange = { newCarb = it }, label = "carb (g)", modifier = Modifier.weight(1f))
                        PremiumTextField(value = newFat, onValueChange = { newFat = it }, label = "fat (g)", modifier = Modifier.weight(1f))
                    }
                    Button(
                        onClick = { viewModel.updateDailyGoal(newCal.toInt(), newPro.toInt(), newCarb.toInt(), newFat.toInt()) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = statusgood)
                    ) {
                        Text("aktifkan target baru", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Divider(color = Color.White.copy(0.05f))
            Spacer(Modifier.height(8.dp))
            Text("riwayat target", color = textprimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        // list riwayat target (per card)
        items(state.goalHistory) { goal ->
            GoalHistoryCard(goal = goal, onDelete = { viewModel.deleteGoalHistory(goal.id) })
        }
    }
}

@Composable
fun GoalHistoryCard(goal: NutritionGoal, onDelete: () -> Unit) {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(sdf.format(Date(goal.dateSet)), color = textmuted, fontSize = 11.sp)
                Text("${goal.calorieTarget} kcal", color = textprimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("p: ${goal.proteinTarget}g | c: ${goal.carbTarget}g | f: ${goal.fatTarget}g", color = textsecondary, fontSize = 12.sp)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = statusdanger.copy(alpha = 0.6f))
            }
        }
    }
}