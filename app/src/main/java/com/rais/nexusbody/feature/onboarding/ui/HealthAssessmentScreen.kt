package com.rais.nexusbody.feature.onboarding.ui

import androidx.compose.foundation.layout.Box // UI: Kontainer dasar
import androidx.compose.foundation.layout.fillMaxSize // UI: Penuhi layar
import androidx.compose.material3.Text // UI: Komponen teks
import androidx.compose.runtime.Composable // Penanda fungsi UI deklaratif
import androidx.compose.ui.Alignment // UI: Posisi sentral
import androidx.compose.ui.Modifier // UI: Modifikator visual
import com.rais.nexusbody.core.ui.theme.textprimary // Theme: Warna teks identitas

/**
 * ONBOARDING HEALTH ASSESSMENT SCREEN (ONBOARDING UTILITY)
 * Peran: Layar pengumpulan data medis awal bagi pengguna baru.
 * Status: Terintegrasi dalam alur navigasi Onboarding (Pre-Dashboard).
 */
@Composable
fun OnboardingHealthAssessmentScreen() {
    // Kontainer penampung visual utama
    Box(
        modifier = Modifier.fillMaxSize(), // Menutupi seluruh layar yang tersedia
        contentAlignment = Alignment.Center // Memastikan konten berada di titik tengah
    ) {
        // Teks informasi status modul (Placeholder)
        Text(
            text = "Pre-Assessment Module", 
            color = textprimary // Menggunakan skema warna standar NexusBody
        )
    }
}
