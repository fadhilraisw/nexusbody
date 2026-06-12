package com.rais.nexusbody.domain.repository

import com.rais.nexusbody.domain.model.User // Model User murni
import kotlinx.coroutines.flow.Flow // Aliran data asinkron

/**
 * USER REPOSITORY INTERFACE
 * Peran: Kontrak untuk manajemen profil pengguna (Pribadi & Keamanan).
 * Departemen: Back-End (Logic Contract).
 */
interface UserRepository {
    // Mengambil profil user secara reaktif berdasarkan ID unik
    fun getUser(userId: String): Flow<User?>
    
    // Menyimpan atau memperbarui data profil user (Tinggi, Nama, dll)
    suspend fun saveUser(user: User)
}
