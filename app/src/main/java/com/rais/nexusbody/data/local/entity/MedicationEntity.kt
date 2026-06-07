package com.rais.nexusbody.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val dosage: String,
    val frequency: String,
    val scheduledTimes: List<String>, // format ["08:00", "20:00"]
    val startDate: Long,
    val endDate: Long?,
    val isActive: Boolean = true,
    val notes: String = ""
)
