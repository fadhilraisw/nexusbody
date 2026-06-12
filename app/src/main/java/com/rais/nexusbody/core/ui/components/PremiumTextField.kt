package com.rais.nexusbody.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth // UI: Lebar penuh
import androidx.compose.foundation.shape.RoundedCornerShape // UI: Sudut lengkung
import androidx.compose.material3.OutlinedTextField // UI: Komponen input bergaris tepi
import androidx.compose.material3.Text // UI: Label teks
import androidx.compose.material3.TextFieldDefaults // UI: Pengaturan visual input
import androidx.compose.runtime.Composable // Jetpack Compose
import androidx.compose.ui.Modifier // Modifikator
import androidx.compose.ui.graphics.Color // Warna
import androidx.compose.ui.unit.dp // Satuan pixel
import androidx.compose.ui.unit.sp // Satuan font
import com.rais.nexusbody.core.ui.theme.* // Impor tema global

/**
 * PREMIUM TEXT FIELD (UI COMPONENT)
 * Peran: Komponen input teks kustom dengan skema warna premium.
 * UX: Input yang bersih dengan placeholder yang mengecil saat aktif dan skema warna gelap.
 */
@Composable
fun PremiumTextField(
    value: String, // Nilai teks saat ini
    onValueChange: (String) -> Unit, // Callback saat user mengetik
    label: String, // Teks panduan
    modifier: Modifier = Modifier,
    isMultiline: Boolean = false // Mendukung banyak baris (misal untuk Catatan Medis)
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        // Placeholder teks panduan di dalam input
        placeholder = { Text(label, color = textmuted, fontSize = 14.sp) },
        modifier = modifier.fillMaxWidth(), // Paksa lebar penuh secara default
        shape = RoundedCornerShape(16.dp), // Sudut bulat 16dp
        minLines = if (isMultiline) 3 else 1, // Jika multiline, berikan tinggi minimal 3 baris
        // Konfigurasi Warna Interaktif
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = 0.05f), // Latar saat diklik
            unfocusedContainerColor = Color.White.copy(alpha = 0.02f), // Latar saat diam
            focusedIndicatorColor = premiumaccent, // Garis tepi oranye saat aktif
            unfocusedIndicatorColor = Color.White.copy(alpha = 0.1f), // Garis tepi redup saat diam
            focusedTextColor = textprimary, // Warna ketikan putih
            unfocusedTextColor = textprimary,
            cursorColor = premiumaccent // Warna kursor oranye
        )
    )
}
