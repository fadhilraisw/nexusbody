package com.rais.nexusbody.core.ui.components

import androidx.compose.foundation.background // UI: Mewarnai background
import androidx.compose.foundation.border // UI: Membuat garis tepi (Border)
import androidx.compose.foundation.layout.Box // UI: Kontainer dasar
import androidx.compose.foundation.shape.RoundedCornerShape // UI: Bentuk sudut lengkung
import androidx.compose.runtime.Composable // Jetpack Compose
import androidx.compose.ui.Modifier // Modifikator
import androidx.compose.ui.draw.clip // UI: Memotong konten agar sesuai bentuk
import androidx.compose.ui.graphics.Brush // UI: Membuat gradasi warna
import androidx.compose.ui.graphics.Color // Warna hex
import androidx.compose.ui.unit.dp // Satuan pixel

/**
 * PREMIUM GLASS CARD (UI COMPONENT)
 * Peran: Komponen visual kartu efek "Kaca Transparan" (Glassmorphism).
 * UX: Memberikan kedalaman visual dan estetika modern berstandar premium/spatial computing.
 */
@Composable
fun PremiumGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit // Konten yang akan diletakkan di dalam kartu
) {
    Box(
        modifier = modifier
            // 1. Potong kartu agar memiliki sudut bulat 24dp
            .clip(RoundedCornerShape(24.dp))
            // 2. Beri warna latar transparan putih (alpha 5%) untuk efek kaca
            .background(Color.White.copy(alpha = 0.05f))
            // 3. Beri garis tepi gradasi halus (Stroke) agar kartu terlihat terpisah dari background
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.1f), // Atas lebih terang
                        Color.White.copy(alpha = 0.02f) // Bawah lebih gelap
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        // Tampilkan konten di dalam kotak kartu
        content()
    }
}
