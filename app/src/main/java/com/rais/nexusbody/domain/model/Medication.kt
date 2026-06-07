package com.rais.nexusbody.domain.model

data class Medication(
    val id: String,
    val name: String,
    val dosage: String, // ex: 20mg
    val frequency: String, // ex: 2x sehari
    val scheduledTimes: List<String> = emptyList(), // list of times like ["08:00", "20:00"]
    val isActive: Boolean = true,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null, // Rentang tanggal selesai
    val notes: String = ""
)
