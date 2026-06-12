package com.rais.nexusbody.feature.nutrition.ui

import androidx.compose.foundation.layout.Box // UI: Tata letak
import androidx.compose.foundation.layout.fillMaxSize // UI: Ukuran penuh
import androidx.compose.material3.Text // UI: Label
import androidx.compose.runtime.Composable // Jetpack Compose
import androidx.compose.ui.Alignment // UI: Posisi
import androidx.compose.ui.Modifier // UI: Modifikator
import com.rais.nexusbody.core.ui.theme.textprimary // Theme: Warna utama

/**
 * FOOD SEARCH SCREEN (NUTRITION UTILITY)
 * Peran: Layar pencarian data makanan ke database OpenFoodFacts.
 * Alur: Komponen ini dipanggil oleh NutritionLogScreen untuk proses input makanan global.
 */
@Composable
fun FoodSearchScreen() {
    // Kontainer penampung visual
    Box(
        modifier = Modifier.fillMaxSize(), // Menutup seluruh area yang tersedia
        contentAlignment = Alignment.Center // Memastikan teks berada di tengah layar
    ) {
        // Teks informasi sementara (Placeholder)
        Text(
            text = "Food Search Logic Integrated", 
            color = textprimary // Sesuai identitas visual NexusBody
        )
    }
}
