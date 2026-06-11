package com.rais.nexusbody.core.network

import com.google.ai.client.generativeai.GenerativeModel // SDK Google Gemini
import com.rais.nexusbody.BuildConfig // Untuk akses API Key dari local.properties
import kotlinx.coroutines.flow.MutableStateFlow // State manajemen reaktif
import kotlinx.coroutines.flow.asStateFlow // Konversi mutable ke read-only flow
import javax.inject.Inject // Dependency Injection
import javax.inject.Singleton // Menjamin hanya ada satu instansi AI Manager

@Singleton
class AiManager @Inject constructor(
    private val groqApi: GroqApiService // Injeksi API Groq (Retrofit)
) {
    // Model default yang digunakan saat aplikasi pertama kali dibuka
    private val _selectedModel = MutableStateFlow("gemini-1.5-flash") 
    val selectedModel = _selectedModel.asStateFlow()

    // Fungsi untuk mengganti "Otak" AI secara real-time dari UI
    fun setModel(modelId: String) {
        _selectedModel.value = modelId
    }

    // Fungsi UTAMA: Menghasilkan konten teks berdasarkan Prompt menggunakan model terpilih
    suspend fun generateContent(prompt: String): String {
        val modelId = _selectedModel.value
        
        // Cek apakah ID model termasuk keluarga Google (Gemini/Gemma/dlsb)
        val isGoogleModel = modelId.contains("gemini") || 
                           modelId.contains("gemma") || 
                           modelId.contains("nano") || 
                           modelId.contains("lyria") || 
                           modelId.contains("deep-research")

        return if (isGoogleModel) {
            // EKSEKUSI GEMINI
            val model = GenerativeModel(
                modelName = modelId,
                apiKey = BuildConfig.GEMINI_API_KEY
            )
            try {
                val response = model.generateContent(prompt)
                response.text ?: "No response from Google AI"
            } catch (e: Exception) {
                "Google AI Error ($modelId): ${e.localizedMessage}"
            }
        } else {
            // EKSEKUSI GROQ (Llama / Qwen)
            try {
                // Gunakan Retrofit untuk memanggil API Groq
                val response = groqApi.getChatCompletion(
                    apiKey = "Bearer ${BuildConfig.GROQ_API_KEY}",
                    request = GroqRequest(
                        model = modelId, // Kirimkan modelId pilihan user (misal: llama-4)
                        messages = listOf(GroqMessage(role = "user", content = prompt))
                    )
                )
                // Ambil hasil teks dari struktur JSON Groq
                response.choices.firstOrNull()?.message?.content ?: "No response from Groq"
            } catch (e: Exception) {
                "Groq Error ($modelId): ${e.localizedMessage}"
            }
        }
    }
}
