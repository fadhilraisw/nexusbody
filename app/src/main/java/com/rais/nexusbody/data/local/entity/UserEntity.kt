package com.rais.nexusbody.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val age: Int,
    val gender: String,
    val activityLevel: String,
    val programGoal: String,
    val heightCm: Float,
    val weightKg: Float,
    val medicalConditions: List<String>
)