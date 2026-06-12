package com.rais.nexusbody.core.util

/**
 * GENERIC RESULT WRAPPER
 * Peran: Pola desain untuk menangani status keberhasilan atau kegagalan aksi (API/DB).
 * Departemen: Core (Architecture Infrastructure).
 */
sealed class Result<out T> {
    // Digunakan saat operasi berhasil membawa data <T>
    data class Success<out T>(val data: T) : Result<T>()
    
    // Digunakan saat terjadi error dengan pesan penjelasan dan pengecualian mentah
    data class Error(val exception: Throwable, val message: String? = null) : Result<Nothing>()
    
    // Digunakan saat UI sedang dalam masa tunggu (Loading)
    object Loading : Result<Nothing>()
}
