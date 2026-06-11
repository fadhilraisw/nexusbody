package com.rais.nexusbody.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quests")
data class QuestEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val module: String, // nutrition, workout, health
    val variable: String,
    val operator: String, // >, <, =, >=
    val targetValue: Float,
    val xpReward: Int,
    val isCompleted: Boolean = false
)
