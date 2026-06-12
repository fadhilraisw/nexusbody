package com.rais.nexusbody.domain.model

/**
 * AI REPORT DOMAIN MODEL
 * Peran: Representasi hasil analisa medis AI.
 */
data class AiReport(
    val id: String,
    val userId: String,
    val timestamp: Long,
    val reportType: String, // Kategori persona (Holistic, Doctor, dll)
    val summary: String, // Teks hasil analisa penuh
    val metricsContext: Map<String, String> // Konteks data yang dikirim saat itu
)
