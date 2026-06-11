package com.rais.nexusbody

import android.os.Bundle // Objek bundle untuk status aktivitas
import androidx.activity.ComponentActivity // Aktivitas dasar Android modern
import androidx.activity.compose.setContent // Penghubung Aktivitas ke UI Compose
import androidx.compose.foundation.layout.fillMaxSize // Pengaturan ukuran UI penuh layar
import androidx.compose.material3.MaterialTheme // Sistem tema Material 3
import androidx.compose.material3.Surface // Bidang permukaan dasar UI
import androidx.compose.runtime.Composable // Penanda fungsi UI deklaratif
import androidx.compose.ui.Modifier // Modifikator properti visual komponen
import androidx.navigation.compose.NavHost // Kontainer sistem navigasi
import androidx.navigation.compose.composable // Definisi rute layar tunggal
import androidx.navigation.compose.rememberNavController // Pengontrol status navigasi
import com.rais.nexusbody.core.ui.theme.NexusBodyTheme // Tema kustom NexusBody
import com.rais.nexusbody.feature.auth.ui.AuthScreen // Layar Login/Register
import com.rais.nexusbody.feature.dashboard.ui.SpatialDashboardScreen // Layar Dashboard Utama
import dagger.hilt.android.AndroidEntryPoint // Anotasi wajib agar Hilt bisa injeksi di Activity

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set UI Utama menggunakan Jetpack Compose
        setContent {
            // Terapkan Tema Global (Warna, Font, Bentuk)
            NexusBodyTheme {
                // Surface utama menggunakan warna background dari skema tema yang sudah kita buat
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Panggil fungsi utama aplikasi
                    NexusBodyApp()
                }
            }
        }
    }
}

@Composable
fun NexusBodyApp() {
    // Inisialisasi NavController untuk mengatur perpindahan antar layar (Routing)
    val navController = rememberNavController()

    // NavHost adalah manajer navigasi pusat
    // startDestination diatur ke "auth" agar user harus login/register dulu saat pertama buka
    NavHost(navController = navController, startDestination = "auth") {

        // --- Rute Layar Autentikasi ---
        composable("auth") {
            AuthScreen(
                onAuthSuccess = {
                    // Berpindah ke dashboard ketika login berhasil
                    navController.navigate("dashboard") {
                        // Hapus layar "auth" dari tumpukan riwayat agar user tidak bisa 'back' ke login
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        // --- Rute Layar Dashboard Utama (Hub Seluruh Fitur) ---
        composable("dashboard") {
            SpatialDashboardScreen()
        }
    }
}
