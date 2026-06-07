package com.rais.nexusbody

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rais.nexusbody.core.ui.theme.NexusBodyTheme
import com.rais.nexusbody.feature.auth.ui.AuthScreen
import com.rais.nexusbody.feature.dashboard.ui.SpatialDashboardScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NexusBodyTheme {
                // Surface utama menggunakan warna background dari tema
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NexusBodyApp()
                }
            }
        }
    }
}

@Composable
fun NexusBodyApp() {
    // Inisialisasi NavController untuk mengatur perpindahan layar
    val navController = rememberNavController()

    // NavHost adalah kontainer yang menampilkan layar sesuai rute yang aktif
    // startDestination diatur ke "auth" agar halaman login muncul pertama kali
    NavHost(navController = navController, startDestination = "auth") {

        // Rute untuk halaman Autentikasi (Login/Register)
        composable("auth") {
            AuthScreen(
                onAuthSuccess = {
                    // Navigasi ke dashboard ketika login/register berhasil
                    navController.navigate("dashboard") {
                        // popUpTo menghapus layar "auth" dari riwayat navigasi
                        // inclusive = true berarti layar "auth" itu sendiri ikut dihapus
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        // Rute untuk halaman Utama (Dashboard)
        composable("dashboard") {
            SpatialDashboardScreen()
        }
    }
}