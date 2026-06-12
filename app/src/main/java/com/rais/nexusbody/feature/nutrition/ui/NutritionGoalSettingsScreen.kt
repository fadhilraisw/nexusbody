package com.rais.nexusbody.feature.nutrition.ui

import androidx.compose.foundation.layout.* // UI: Tata letak
import androidx.compose.foundation.lazy.LazyColumn // UI: List vertikal
import androidx.compose.foundation.lazy.items // UI: Iterasi list
import androidx.compose.material.icons.Icons // UI: Ikon
import androidx.compose.material.icons.filled.Delete // UI: Ikon hapus
import androidx.compose.material3.* // UI Framework: Material 3
import androidx.compose.runtime.* // UI State: Variabel reaktif
import androidx.compose.ui.Alignment // UI: Perataan
import androidx.compose.ui.Modifier // UI: Properti visual
import androidx.compose.ui.graphics.Color // UI: Warna
import androidx.compose.ui.text.font.FontWeight // UI: Ketebalan font
import androidx.compose.ui.unit.dp // UI: Ukuran pixel
import androidx.compose.ui.unit.sp // UI: Ukuran teks
import com.rais.nexusbody.core.ui.components.PremiumGlassCard // UI Shared: Kartu kaca
import com.rais.nexusbody.core.ui.components.PremiumTextField // UI Shared: Input premium
import com.rais.nexusbody.core.ui.theme.* // UI Theme: Warna global
import com.rais.nexusbody.feature.nutrition.NutritionViewModel // Logic: Otak gizi
import com.rais.nexusbody.domain.model.NutritionGoal // Model: Target gizi harian
import java.text.SimpleDateFormat // Utils: Format tanggal
import java.util.Date // Utils: Objek waktu
import java.util.Locale // Utils: Lokalisasi

/**
 * NUTRITION GOAL SETTINGS SCREEN (FEATURE LAYER)
 * Peran: Layar untuk mengatur target makro (Kalori, Protein, Karbo, Lemak) harian user.
 * Alur: Input Target -> ViewModel Update -> Repository Save -> UI Reactive Update.
 */
@Composable
fun NutritionGoalSettingsScreen(
    viewModel: NutritionViewModel, // Injeksi logika dari parent screen
    onDismiss: () -> Unit // Callback untuk menutup pop-up
) {
    // Observasi state gizi saat ini dari database
    val uiState by viewModel.state.collectAsState()
    
    // State lokal untuk menampung input form sementara
    var cal by remember { mutableStateOf(uiState.activeGoal.calorieTarget.toString()) }
    var pro by remember { mutableStateOf(uiState.activeGoal.proteinTarget.toString()) }
    var carb by remember { mutableStateOf(uiState.activeGoal.carbTarget.toString()) }
    var fat by remember { mutableStateOf(uiState.activeGoal.fatTarget.toString()) }

    LazyColumn(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // HEADER FORM
        item { Text("🎯 set daily nutritional target", color = textprimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        
        // INPUT FIELD: KALORI
        item { PremiumTextField(value = cal, onValueChange = { cal = it }, label = "calorie target (kcal)") }
        
        // INPUT FIELD: MAKRONUTRISI (Baris Sejajar)
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PremiumTextField(value = pro, onValueChange = { pro = it }, label = "protein (g)", modifier = Modifier.weight(1f))
                PremiumTextField(value = carb, onValueChange = { carb = it }, label = "carbs (g)", modifier = Modifier.weight(1f))
                PremiumTextField(value = fat, onValueChange = { fat = it }, label = "fats (g)", modifier = Modifier.weight(1f))
            }
        }

        // TOMBOL AKSI: SIMPAN
        item {
            Button(
                onClick = { 
                    // Kirim data baru ke ViewModel untuk diproses secara asinkron
                    viewModel.updateDailyGoal(cal.toIntOrNull() ?: 0, pro.toIntOrNull() ?: 0, carb.toIntOrNull() ?: 0, fat.toIntOrNull() ?: 0)
                    onDismiss() 
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = statusgood)
            ) {
                Text("update daily budget", fontWeight = FontWeight.Bold)
            }
        }

        // SEKSI RIWAYAT TARGET (HISTORICAL TRACKING)
        item { Text("previous targets", color = textprimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 12.dp)) }
        
        // Iterasi daftar target lama dari database
        items(uiState.goalHistory.reversed()) { goal ->
            GoalHistoryCard(goal) { viewModel.deleteGoalHistory(goal.id) }
        }
    }
}

/**
 * GOAL HISTORY CARD (UI COMPONENT)
 * Menampilkan ringkasan target lama dalam format kartu premium.
 */
@Composable
private fun GoalHistoryCard(goal: NutritionGoal, onDelete: () -> Unit) {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(sdf.format(Date(goal.dateSet)), color = textmuted, fontSize = 11.sp)
                Text("${goal.calorieTarget} kcal", color = textprimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("P:${goal.proteinTarget}g C:${goal.carbTarget}g F:${goal.fatTarget}g", color = textsecondary, fontSize = 12.sp)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, null, tint = statusdanger.copy(alpha = 0.6f))
            }
        }
    }
}
