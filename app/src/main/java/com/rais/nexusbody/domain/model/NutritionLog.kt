package com.rais.nexusbody.domain.model

import java.util.Date

enum class NutritionItemType { WHOLE_FOOD, SUPPLEMENT, BEVERAGE, SNACK, MEAL_PLAN_ITEM }

data class MacroNutrients(
    val proteinGrams: Float,
    val carbohydratesGrams: Float,
    val fatsGrams: Float,
    val fiberGrams: Float,
    val sugarGrams: Float,
    val sodiumMg: Float
)

data class NutritionLog(
    val id: String,
    val userId: String,
    val itemType: com.rais.nexusbody.domain.model.NutritionItemType,
    val itemName: String,
    val servingSizeGrams: Float,
    val calories: Float,
    val macros: com.rais.nexusbody.domain.model.MacroNutrients,
    val consumptionTime: Date,
    val mealLabel: String?,              // "Breakfast", "Lunch", "Dinner", "Snack"
    val xpEarned: Int,
    val createdAt: Date
)

data class NutritionGoal(
    val id: String,
    val dateSet: Long,
    val calorieTarget: Int,
    val proteinTarget: Int,
    val carbTarget: Int,
    val fatTarget: Int,
    val isActive: Boolean = false
)
