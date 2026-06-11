package com.rais.nexusbody.feature.auth.ui

// --- FRONTEND TECH STACK IMPORTS ---
import androidx.compose.foundation.background // UI: Mewarnai latar belakang
import androidx.compose.foundation.clickable // UX: Menangani klik pada teks/tombol
import androidx.compose.foundation.layout.* // UI: Pengaturan tata letak (Box, Column, Row)
import androidx.compose.foundation.shape.RoundedCornerShape // UI: Membuat sudut melengkung premium
import androidx.compose.material3.* // UI Framework: Material Design 3 Components
import androidx.compose.runtime.* // UI State: Pengelolaan variabel UI reaktif
import androidx.compose.ui.Alignment // UI: Posisi tengah/tepi
import androidx.compose.ui.Modifier // UI: Modifikator properti visual
import androidx.compose.ui.graphics.Color // UI: Definisi warna hex
import androidx.compose.ui.text.font.FontWeight // UI: Ketebalan huruf
import androidx.compose.ui.text.input.PasswordVisualTransformation // UX: Menyembunyikan password
import androidx.compose.ui.text.input.VisualTransformation // UX: Transformasi teks input
import androidx.compose.ui.unit.dp // UI: Satuan jarak pixel
import androidx.compose.ui.unit.sp // UI: Satuan ukuran font
import androidx.hilt.navigation.compose.hiltViewModel // DI: Mengambil ViewModel via Hilt
import com.rais.nexusbody.core.ui.components.PremiumGlassCard // UI: Komponen kaca kustom
import com.rais.nexusbody.core.ui.components.PremiumTextField // UI: Input teks kustom
import com.rais.nexusbody.core.ui.theme.* // UI: Tema warna global
import com.rais.nexusbody.feature.auth.AuthViewModel // Logic: Jembatan ke backend

/**
 * AUTH SCREEN (FRONTEND LAYER)
 * Konsep UX: Glassmorphism / Spatial Computing.
 * Alur: User Input -> ViewModel Validation -> Supabase Auth -> Navigation to Dashboard.
 */
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(), // Injeksi otomatis logic via Hilt
    onAuthSuccess: () -> Unit = {} // Callback ke MainActivity untuk pindah layar
) {
    // --- STATE OBSERVATION (FRONTEND FLOW) ---
    // Mengamati status loading, error, dan success dari database secara real-time.
    val uiState by viewModel.uiState.collectAsState()

    // State internal UI (UX State)
    var isLoginMode by remember { mutableStateOf(true) } // Toggle antara Masuk atau Daftar
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // --- NAVIGATION TRIGGER ---
    // Memantau jika status isSuccess berubah jadi TRUE di database, picu pindah halaman.
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onAuthSuccess()
        }
    }

    // --- UI STRUCTURE (LAYOUT) ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF08090A)) // Background hitam pekat premium
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // BRANDING HEADER
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("nexusbody", color = textprimary, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Text("v2.0 medical grade", color = premiumaccent, fontSize = 12.sp, letterSpacing = 4.sp, fontWeight = FontWeight.SemiBold)
            }

            // ERROR BANNER (UX Feedback): Muncul hanya jika ada error dari backend/network
            if (uiState.error != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(statusdanger.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(text = uiState.error!!, color = statusdanger, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // MAIN AUTH CARD (Glassmorphism Effect)
            PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (isLoginMode) "Sign In" else "Create Account",
                        color = textprimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // EMAIL INPUT
                    PremiumTextField(
                        value = email,
                        onValueChange = { email = it; viewModel.clearError() },
                        label = "Email Address"
                    )

                    // PASSWORD INPUT
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; viewModel.clearError() },
                        label = { Text("Password", color = textmuted) },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // CONFIRM PASSWORD (Muncul hanya saat mode Register)
                    if (!isLoginMode) {
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it; viewModel.clearError() },
                            label = { Text("Confirm Password", color = textmuted) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // --- MAIN ACTION BUTTON (UX INTERACTION) ---
                    Button(
                        onClick = {
                            if (isLoginMode) {
                                viewModel.login(email, password) // Panggil logic login
                            } else {
                                viewModel.register(email, password, confirmPassword) // Panggil logic daftar
                            }
                        },
                        // UX Guard: Tombol mati jika sedang loading atau terkena rate limit
                        enabled = !uiState.isLoading && uiState.rateLimitCooldown <= 0,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = statusgood),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState.isLoading) {
                            // Feedback visual saat menunggu server
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = if (uiState.rateLimitCooldown > 0) "Tunggu ${uiState.rateLimitCooldown}s" 
                                       else if (isLoginMode) "Log In" else "Register",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // TOGGLE LOGIN/REGISTER UX
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isLoginMode) "Belum punya akun? " else "Sudah punya akun? ", color = textsecondary)
                Text(
                    text = if (isLoginMode) "Daftar di sini" else "Masuk sekarang",
                    color = premiumaccent,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { isLoginMode = !isLoginMode; viewModel.clearError() }
                )
            }
        }
    }
}
