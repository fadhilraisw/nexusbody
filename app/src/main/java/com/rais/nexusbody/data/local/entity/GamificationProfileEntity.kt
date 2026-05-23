package com.rais.nexusbody.data.local.entity

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import java.util.Date

@DatabaseTable(tableName = "gamification_profiles")
data class GamificationProfileEntity(
    @DatabaseField(id = true, columnName = "id")
    val id: String = "",

    @DatabaseField(columnName = "user_id", unique = true, canBeNull = false)
    val userId: String = "",

    @DatabaseField(columnName = "total_nutrition_xp")
    val totalNutritionXp: Int = 0,

    @DatabaseField(columnName = "daily_diet_streak")
    val dailyDietStreak: Int = 0,

    @DatabaseField(columnName = "longest_diet_streak")
    val longestDietStreak: Int = 0,

    // Muscle Levels — Chest
    @DatabaseField(columnName = "chest_level") val chestLevel: Int = 1,
    @DatabaseField(columnName = "chest_xp") val chestXp: Int = 0,

    // Muscle Levels — Back
    @DatabaseField(columnName = "back_upper_level") val backUpperLevel: Int = 1,
    @DatabaseField(columnName = "back_upper_xp") val backUpperXp: Int = 0,
    @DatabaseField(columnName = "back_lower_level") val backLowerLevel: Int = 1,
    @DatabaseField(columnName = "back_lower_xp") val backLowerXp: Int = 0,

    // Muscle Levels — Shoulders
    @DatabaseField(columnName = "shoulders_level") val shouldersLevel: Int = 1,
    @DatabaseField(columnName = "shoulders_xp") val shouldersXp: Int = 0,

    // Muscle Levels — Arms
    @DatabaseField(columnName = "biceps_level") val bicepsLevel: Int = 1,
    @DatabaseField(columnName = "biceps_xp") val bicepsXp: Int = 0,
    @DatabaseField(columnName = "triceps_level") val tricepsLevel: Int = 1,
    @DatabaseField(columnName = "triceps_xp") val tricepsXp: Int = 0,

    // Muscle Levels — Legs
    @DatabaseField(columnName = "quadriceps_level") val quadricepsLevel: Int = 1,
    @DatabaseField(columnName = "quadriceps_xp") val quadricepsXp: Int = 0,
    @DatabaseField(columnName = "hamstrings_level") val hamstringsLevel: Int = 1,
    @DatabaseField(columnName = "hamstrings_xp") val hamstringsXp: Int = 0,
    @DatabaseField(columnName = "glutes_level") val glutesLevel: Int = 1,
    @DatabaseField(columnName = "glutes_xp") val glutesXp: Int = 0,
    @DatabaseField(columnName = "calves_level") val calvesLevel: Int = 1,
    @DatabaseField(columnName = "calves_xp") val calvesXp: Int = 0,

    // Muscle Levels — Core
    @DatabaseField(columnName = "core_abs_level") val coreAbsLevel: Int = 1,
    @DatabaseField(columnName = "core_abs_xp") val coreAbsXp: Int = 0,

    // Composite Levels
    @DatabaseField(columnName = "agility_level") val agilityLevel: Int = 1,
    @DatabaseField(columnName = "agility_xp") val agilityXp: Int = 0,
    @DatabaseField(columnName = "strength_level") val strengthLevel: Int = 1,
    @DatabaseField(columnName = "strength_xp") val strengthXp: Int = 0,
    @DatabaseField(columnName = "endurance_level") val enduranceLevel: Int = 1,
    @DatabaseField(columnName = "endurance_xp") val enduranceXp: Int = 0,

    @DatabaseField(columnName = "overall_rank")
    val overallRank: String = "Bronze I",

    @DatabaseField(columnName = "total_workouts_completed")
    val totalWorkoutsCompleted: Int = 0,

    @DatabaseField(columnName = "total_nutrition_logs")
    val totalNutritionLogs: Int = 0,

    @DatabaseField(columnName = "created_at", dataType = DataType.DATE_LONG)
    val createdAt: Date = Date(),

    @DatabaseField(columnName = "updated_at", dataType = DataType.DATE_LONG)
    val updatedAt: Date = Date()
)