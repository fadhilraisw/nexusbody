package com.rais.nexusbody.core.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme // Pendeteksi mode gelap sistem
import androidx.compose.material3.MaterialTheme // Framework tema Material 3
import androidx.compose.material3.darkColorScheme // Skema warna gelap
import androidx.compose.material3.dynamicDarkColorScheme // Warna dinamis Android 12+
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable // Penanda fungsi UI reaktif
import androidx.compose.runtime.SideEffect // Efek samping untuk status bar
import androidx.compose.ui.graphics.toArgb // Konversi warna ke format ARGB
import androidx.compose.ui.platform.LocalContext // Context UI saat ini
import androidx.compose.ui.platform.LocalView // Tampilan saat ini
import androidx.core.view.WindowCompat // Utilitas pengaturan jendela/layar

// Definisi skema warna gelap untuk NexusBody (Medical Dark Mode)
private val DarkColorScheme = darkColorScheme(
    primary = NeonViolet, // Warna utama (tombol, link)
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = DeepSpace // Background hitam pekat premium
)

// Definisi skema warna terang (Default)
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

/**
 * NEXUS BODY THEME SYSTEM (ROOT UI)
 * Peran: Konfigurasi visual tertinggi aplikasi. Semua UI akan mewarisi pengaturan dari sini.
 */
@Composable
fun NexusBodyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Cek preferensi HP
    dynamicColor: Boolean = true, // Mendukung wallpaper-based coloring (Android 12+)
    content: @Composable () -> Unit // Konten layar yang akan dibungkus tema
) {
    // Tentukan skema warna berdasarkan versi Android dan preferensi user
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Mengatur status bar (bar atas HP) agar serasi dengan aplikasi
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb() // Set warna status bar
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // Terapkan pengaturan ke MaterialTheme global
    MaterialTheme(
        colorScheme = colorScheme,
        typography = NexusBodyTypography, // Hubungkan dengan definisi font di Type.kt
        content = content
    )
}
