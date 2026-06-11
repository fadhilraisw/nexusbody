package com.rais.nexusbody.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_reports")
data class AiReportEntity(
    @PrimaryKey val id: String = "",
    val userId: String = "",
    val reportType: String = "holistic",
    val summary: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
