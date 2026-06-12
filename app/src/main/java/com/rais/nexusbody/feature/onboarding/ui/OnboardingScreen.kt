package com.rais.nexusbody.feature.onboarding.ui

import androidx.compose.foundation.layout.Box // UI: Kontainer
import androidx.compose.foundation.layout.fillMaxSize // UI: Ukuran penuh
import androidx.compose.material3.Button // UI: Tombol
import androidx.compose.material3.Text // UI: Teks
import androidx.compose.runtime.Composable // Jetpack Compose
import androidx.compose.ui.Alignment // UI: Perataan
import androidx.compose.ui.Modifier // UI: Properti visual
import com.rais.nexusbody.core.ui.theme.* // UI Theme: Impor seluruh konstanta warna

/**
 * ONBOARDING SCREEN (ENTRY LAYER)
 * Peran: Layar pengenalan aplikasi saat pertama kali user masuk.
 * UX: Menjelaskan konsep 'Medical Grade Bodybuilding' kepada pengguna baru.
 */
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    // Kontainer dasar layar
    Box(
        modifier = Modifier.fillMaxSize(), // Gunakan seluruh area layar HP
        contentAlignment = Alignment.Center // Konten diatur ke tengah (Center)
    ) {
        // Tombol aksi untuk menyelesaikan tahap pengenalan
        Button(
            onClick = onFinish, // Jalankan callback pindah layar saat diklik
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = statusgood // Gunakan warna hijau (Progres Positif)
            )
        ) {
            // Label tombol
            Text(
                text = "Get Started", 
                color = textprimary // Teks warna putih bersih
            )
        }
    }
}
