package com.rais.nexusbody.feature.gamification

import androidx.lifecycle.ViewModel // Framework ViewModel
import androidx.lifecycle.viewModelScope // Scope coroutine
import com.rais.nexusbody.data.local.entity.GamificationProfileEntity // Tabel Profil XP
import com.rais.nexusbody.domain.repository.GamificationRepository // Kontrak data gamifikasi
import dagger.hilt.android.lifecycle.HiltViewModel // Hilt
import kotlinx.coroutines.flow.* // Flow reaktif
import kotlinx.coroutines.launch // Coroutine
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import javax.inject.Inject // Injeksi constructor

@HiltViewModel
class GamificationViewModel @Inject constructor(
    private val repository: GamificationRepository, // Injeksi repo
    private val supabase: SupabaseClient
) : ViewModel() {

    // Ambil user ID dari session Supabase
    private val userId: String
        get() = supabase.auth.currentUserOrNull()?.id ?: "anonymous_user"

    // Aliran profil gamifikasi yang berisi data XP per otot dan Rank global
    val profile: StateFlow<GamificationProfileEntity?> = repository.getProfile(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Fungsi untuk memperbarui statistik profil (misal saat dapat reward)
    fun updateProfile(updatedProfile: GamificationProfileEntity) {
        viewModelScope.launch {
            repository.updateProfile(updatedProfile) // Simpan ke Room & Supabase
        }
    }
}
