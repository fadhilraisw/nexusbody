package com.rais.nexusbody.domain.model

/**
 * GAMIFICATION DOMAIN MODEL
 * Peran: Data pencapaian dan kemahiran otot user.
 */
data class GamificationProfile(
    val userId: String,
    val totalXp: Int, // Total XP akumulatif
    val rank: String, // Nama Rank (Bronze, Silver, Platinum, dll)
    val currentStreak: Int, // Berapa hari berturut-turut aktif
    
    // XP spesifik per kelompok otot (Muscle Mastery)
    val chestXp: Int = 0,
    val backUpperXp: Int = 0,
    val backLowerXp: Int = 0,
    val bicepsXp: Int = 0,
    val tricepsXp: Int = 0,
    val shouldersXp: Int = 0,
    val quadricepsXp: Int = 0,
    val hamstringsXp: Int = 0,
    val calvesXp: Int = 0,
    val coreAbsXp: Int = 0,
    val glutesXp: Int = 0,
    
    // XP kategori nutrisi dan kesehatan
    val totalNutritionXp: Int = 0,
    val totalHealthXp: Int = 0
)
