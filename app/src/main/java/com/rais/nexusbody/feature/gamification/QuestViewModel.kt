package com.rais.nexusbody.feature.gamification

import androidx.lifecycle.ViewModel // Framework ViewModel
import androidx.lifecycle.viewModelScope // Scope coroutine
import com.rais.nexusbody.data.local.entity.QuestEntity // Tabel Quest
import com.rais.nexusbody.domain.repository.QuestRepository // Kontrak data quest
import com.rais.nexusbody.domain.repository.GamificationRepository // Kontrak data profil
import dagger.hilt.android.lifecycle.HiltViewModel // Hilt
import kotlinx.coroutines.flow.* // Aliran data asinkron
import kotlinx.coroutines.launch // Menjalankan coroutine
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import java.util.UUID // ID unik
import javax.inject.Inject // Injeksi constructor

@HiltViewModel
class QuestViewModel @Inject constructor(
    private val questRepo: QuestRepository, // Repo khusus pencarian quest
    private val gamificationRepo: GamificationRepository, // Repo untuk ambil data profil XP
    private val supabase: SupabaseClient
) : ViewModel() {

    // Identitas user login
    private val userId: String
        get() = supabase.auth.currentUserOrNull()?.id ?: "anonymous_user"

    // Aliran data quest aktif (belum selesai) yang ditarik dari database
    val activeQuests: StateFlow<List<QuestEntity>> = questRepo.getActiveQuests(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Aliran data profil gamifikasi (Total XP, Rank)
    val profile = gamificationRepo.getProfile(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Fungsi untuk membuat tantangan baru (Quest Forging)
    fun createQuest(title: String, module: String, variable: String, operator: String, target: Float, xp: Int) {
        viewModelScope.launch {
            val quest = QuestEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                title = title,
                module = module, // Kategori: nutrition/workout/health
                variable = variable, // Variabel picu: misal 'protein'
                operator = operator, // Logika: >=, <=, dsb
                targetValue = target, // Target angka
                xpReward = xp // Hadiah XP jika berhasil
            )
            // Simpan quest ke Room & Supabase
            questRepo.insertQuest(quest)
        }
    }
}
