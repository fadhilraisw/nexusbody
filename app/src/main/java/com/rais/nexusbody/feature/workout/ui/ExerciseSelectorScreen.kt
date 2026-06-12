package com.rais.nexusbody.feature.workout.ui

import androidx.compose.foundation.layout.Box // UI: Kontainer
import androidx.compose.foundation.layout.fillMaxSize // UI: Ukuran penuh
import androidx.compose.material3.Text // UI: Teks
import androidx.compose.runtime.Composable // Jetpack Compose
import androidx.compose.ui.Alignment // UI: Perataan
import androidx.compose.ui.Modifier // UI: Properti visual
import com.rais.nexusbody.core.ui.theme.textprimary // UI Theme: Warna teks

/**
 * EXERCISE SELECTOR SCREEN (MODULAR UI)
 * Peran: Layar pemilih gerakan dari database latihan yang tersedia.
 * Status: Komponen ini disematkan di dalam WorkoutLogScreen sebagai Modal Bottom Sheet.
 */
@Composable
fun ExerciseSelectorScreen() {
    // Kontainer utama layar
    Box(
        modifier = Modifier.fillMaxSize(), // Penuhi area yang tersedia
        contentAlignment = Alignment.Center // Letakkan konten tepat di tengah
    ) {
        // Teks placeholder sementara sebelum integrasi library gerakan penuh
        Text(
            text = "Exercise Selector Coming Soon", 
            color = textprimary // Gunakan warna putih sesuai tema premium
        )
    }
}
