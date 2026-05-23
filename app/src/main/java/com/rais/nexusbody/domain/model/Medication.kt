package com.rais.nexusbody.domain.model

data class Medication(
    val id: String,
    val name: String,
    val dosage: String, // ex: 20mg
    val frequency: String, // ex: 2x sehari
    val scheduledTime: String, // ex: 08:00
    val isActive: Boolean = true,
    val startDate: Long = System.currentTimeMillis()
)