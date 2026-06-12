package com.rais.nexusbody.core.network

import retrofit2.http.Body // Penanda body request HTTP
import retrofit2.http.Header // Penanda header (untuk API Key)
import retrofit2.http.POST // Metode HTTP POST

/**
 * GROQ API SERVICE (LLM BRIDGE)
 * Peran: Kontrak komunikasi dengan API Groq untuk model Llama/Qwen.
 * Tech Stack: Retrofit 2.
 */
interface GroqApiService {
    
    // Fungsi untuk mendapatkan jawaban chat dari model AI Groq
    @POST("v1/chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") apiKey: String, // Kunci rahasia Groq
        @Body request: GroqRequest // Objek berisi model dan pesan user
    ): GroqResponse
}

// Data Transfer Object (DTO) untuk Request ke Groq
data class GroqRequest(
    val model: String = "llama-3.3-70b-versatile", // Model default
    val messages: List<GroqMessage> // Daftar percakapan
)

// Struktur pesan untuk AI (Role: 'user' atau 'system')
data class GroqMessage(
    val role: String,
    val content: String
)

// Objek penampung respon JSON dari Groq
data class GroqResponse(
    val choices: List<GroqChoice>
)

// Pilihan jawaban dari AI
data class GroqChoice(
    val message: GroqMessage
)
