package com.rais.nexusbody.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// State untuk mengatur status UI
data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val rateLimitCooldown: Int = 0 // Cooldown dalam detik
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private var cooldownJob: Job? = null

    fun login(email: String, pass: String) {
        if (_uiState.value.rateLimitCooldown > 0) return
        
        val trimmedEmail = email.trim()
        val trimmedPass = pass.trim()
        
        if (trimmedEmail.isBlank() || trimmedPass.isBlank()) {
            _uiState.update { it.copy(error = "Email dan password tidak boleh kosong.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                supabase.auth.signInWith(Email) {
                    this.email = trimmedEmail
                    this.password = trimmedPass
                }
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                handleAuthError(e)
            }
        }
    }

    fun register(email: String, pass: String, confirmPass: String) {
        if (_uiState.value.rateLimitCooldown > 0) return

        val trimmedEmail = email.trim()
        val trimmedPass = pass.trim()
        
        if (trimmedEmail.isBlank() || trimmedPass.isBlank()) {
            _uiState.update { it.copy(error = "Semua form wajib diisi.") }
            return
        }
        if (trimmedPass.length < 6) {
            _uiState.update { it.copy(error = "Password harus minimal 6 karakter.") }
            return
        }
        if (trimmedPass != confirmPass.trim()) {
            _uiState.update { it.copy(error = "Konfirmasi password tidak cocok dengan password awal.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                supabase.auth.signUpWith(Email) {
                    this.email = trimmedEmail
                    this.password = trimmedPass
                }
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                handleAuthError(e)
            }
        }
    }

    private fun handleAuthError(e: Exception) {
        val errorMsg = e.localizedMessage ?: "Terjadi kesalahan"
        _uiState.update { it.copy(isLoading = false, error = errorMsg) }
        
        if (errorMsg.contains("rate limit", ignoreCase = true)) {
            startCooldown()
        }
    }

    private fun startCooldown() {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            for (i in 60 downTo 0) {
                _uiState.update { it.copy(rateLimitCooldown = i) }
                delay(1000)
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}