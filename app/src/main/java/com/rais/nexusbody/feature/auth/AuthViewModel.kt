package com.rais.nexusbody.feature.auth

import androidx.lifecycle.ViewModel // Menggunakan ViewModel dari Jetpack Lifecycle
import androidx.lifecycle.viewModelScope // Scope coroutine yang terikat dengan siklus hidup ViewModel
import com.rais.nexusbody.feature.auth.AuthUiState // Model state untuk UI login
import dagger.hilt.android.lifecycle.HiltViewModel // Anotasi agar ViewModel bisa di-inject oleh Hilt
import io.github.jan.supabase.SupabaseClient // SDK Supabase untuk komunikasi cloud
import io.github.jan.supabase.gotrue.auth // Akses ke fitur autentikasi Supabase
import io.github.jan.supabase.gotrue.providers.builtin.Email // Provider login email standar
import kotlinx.coroutines.Job // Interface untuk mengontrol coroutine (cooldown)
import kotlinx.coroutines.delay // Fungsi pause coroutine
import kotlinx.coroutines.flow.MutableStateFlow // Aliran data state yang bisa diubah (private)
import kotlinx.coroutines.flow.StateFlow // Aliran data state untuk dibaca UI (public)
import kotlinx.coroutines.flow.asStateFlow // Mengubah mutable menjadi read-only state
import kotlinx.coroutines.flow.update // Fungsi helper untuk update data state secara atomik
import kotlinx.coroutines.launch // Menjalankan blok kode di thread latar belakang
import javax.inject.Inject // Injeksi constructor oleh Hilt

// Data class untuk membungkus seluruh status UI di layar Autentikasi
data class AuthUiState(
    val isLoading: Boolean = false, // Status apakah sedang memproses data (spinner)
    val error: String? = null, // Pesan error jika pendaftaran/login gagal
    val isSuccess: Boolean = false, // Penanda jika proses berhasil untuk navigasi ke Dashboard
    val rateLimitCooldown: Int = 0 // Hitung mundur jika terkena limit request dari Supabase
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val supabase: SupabaseClient // Injeksi klien Supabase yang sudah di-setup di DI module
) : ViewModel() {

    // Aliran data state internal (Mutable)
    private val _uiState = MutableStateFlow(AuthUiState())
    // Aliran data state yang diekspos ke UI (Read-Only)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Variabel untuk menyimpan referensi job hitung mundur cooldown
    private var cooldownJob: Job? = null

    // Fungsi untuk proses Login
    fun login(email: String, pass: String) {
        // Jika sedang masa tunggu (rate limit), hentikan proses
        if (_uiState.value.rateLimitCooldown > 0) return
        
        // Membersihkan spasi liar di input user
        val trimmedEmail = email.trim()
        val trimmedPass = pass.trim()
        
        // Validasi input kosong
        if (trimmedEmail.isBlank() || trimmedPass.isBlank()) {
            _uiState.update { it.copy(error = "Email dan password tidak boleh kosong.") }
            return
        }

        // Jalankan proses asinkron di scope ViewModel
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) } // Set status loading
            try {
                // Memanggil fungsi login email ke server Supabase
                supabase.auth.signInWith(Email) {
                    this.email = trimmedEmail
                    this.password = trimmedPass
                }
                // Jika berhasil, set flag success
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                // Tangani error jika gagal
                handleAuthError(e)
            }
        }
    }

    // Fungsi untuk proses Registrasi Akun Baru
    fun register(email: String, pass: String, confirmPass: String) {
        if (_uiState.value.rateLimitCooldown > 0) return

        val trimmedEmail = email.trim()
        val trimmedPass = pass.trim()
        
        // Validasi kelengkapan form
        if (trimmedEmail.isBlank() || trimmedPass.isBlank()) {
            _uiState.update { it.copy(error = "Semua form wajib diisi.") }
            return
        }
        // Validasi keamanan password minimal
        if (trimmedPass.length < 6) {
            _uiState.update { it.copy(error = "Password harus minimal 6 karakter.") }
            return
        }
        // Validasi kecocokan password
        if (trimmedPass != confirmPass.trim()) {
            _uiState.update { it.copy(error = "Konfirmasi password tidak cocok dengan password awal.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Memanggil pendaftaran user baru ke Supabase Auth
                supabase.auth.signUpWith(Email) {
                    this.email = trimmedEmail
                    this.password = trimmedPass
                }
                // Berhasil daftar langsung dianggap sukses (jika confirm_email mati)
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                handleAuthError(e)
            }
        }
    }

    // Fungsi helper untuk manajemen pesan error
    private fun handleAuthError(e: Exception) {
        val errorMsg = e.localizedMessage ?: "Terjadi kesalahan"
        _uiState.update { it.copy(isLoading = false, error = errorMsg) }
        
        // Jika error adalah karena rate limit, mulai hitung mundur keamanan
        if (errorMsg.contains("rate limit", ignoreCase = true)) {
            startCooldown()
        }
    }

    // Logika hitung mundur keamanan (60 detik)
    private fun startCooldown() {
        cooldownJob?.cancel() // Hentikan job sebelumnya jika ada
        cooldownJob = viewModelScope.launch {
            for (i in 60 downTo 0) {
                _uiState.update { it.copy(rateLimitCooldown = i) } // Update sisa waktu di UI
                delay(1000) // Tunggu 1 detik tiap iterasi
            }
        }
    }

    // Fungsi untuk membersihkan banner error di UI
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
