package com.rais.nexusbody.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "gamification_profiles")
data class GamificationProfileEntity(
    @PrimaryKey val id: String = "",
    val userId: String = "",
    val totalNutritionXp: Int = 0,
    val dailyDietStreak: Int = 0,
    val longestDietStreak: Int = 0,
    
    // Muscle XPs
    val chestXp: Int = 0,
    val backUpperXp: Int = 0,
    val backLowerXp: Int = 0,
    val shouldersXp: Int = 0,
    val bicepsXp: Int = 0,
    val tricepsXp: Int = 0,
    val quadricepsXp: Int = 0,
    val hamstringsXp: Int = 0,
    val glutesXp: Int = 0,
    val calvesXp: Int = 0,
    val coreAbsXp: Int = 0,
    
    val totalWorkoutsCompleted: Int = 0,
    val totalNutritionLogs: Int = 0,
    val overallRank: String = "Bronze I",
    val createdAt: Long = System.currentTimeMillis()
)
