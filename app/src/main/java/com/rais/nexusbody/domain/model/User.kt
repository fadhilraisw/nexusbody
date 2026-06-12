package com.rais.nexusbody.domain.model

/**
 * USER DOMAIN MODEL
 * Peran: Definisi struktur data profil pengguna inti di layer bisnis.
 * Departemen: Domain (Murni Kotlin).
 */
data class User(
    val id: String, // ID Unik dari Supabase Auth
    val email: String, // Alamat email terdaftar
    val fullName: String?, // Nama lengkap profil
    val heightCm: Float?, // Data dasar antropometri
    val birthDate: Long?, // Tanggal lahir untuk kalkulasi usia klinis
    val createdAt: Long // Waktu pembuatan akun (Timestamp)
)
