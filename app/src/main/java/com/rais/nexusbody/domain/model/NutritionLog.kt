package com.rais.nexusbody.domain.model

/**
 * NUTRITION LOG DOMAIN MODEL
 * Peran: Representasi data asupan makanan di layer logika bisnis.
 */
data class NutritionLog(
    val id: String, // ID Unik
    val userId: String, // ID Pemilik
    val foodName: String, // Nama Makanan
    val calories: Int, // Total Kalori
    val proteinGrams: Float, // Gram Protein
    val carbsGrams: Float, // Gram Karbo
    val fatGrams: Float, // Gram Lemak
    val portionGrams: Float, // Berat Porsi
    val timestamp: Long, // Waktu Makan
    val isAiEstimated: Boolean = false // Apakah data hasil input manual atau tebakan AI
)

/**
 * NUTRITION GOAL DOMAIN MODEL
 * Peran: Target gizi harian yang ditetapkan user.
 */
data class NutritionGoal(
    val id: String,
    val dateSet: Long, // Waktu target ditetapkan
    val calorieTarget: Int,
    val proteinTarget: Int,
    val carbTarget: Int,
    val fatTarget: Int,
    val isActive: Boolean = true // Apakah ini target yang sedang berjalan
)
