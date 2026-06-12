package com.rais.nexusbody.core.util

import kotlinx.coroutines.CoroutineDispatcher // Pengatur thread
import kotlinx.coroutines.Dispatchers // Thread IO/Main/Default
import kotlinx.coroutines.withContext // Berpindah thread aman
import android.database.SQLException // Pengecualian database SQL

/**
 * SAFE DATABASE CALL WRAPPER
 * Peran: Menangani error database secara terpusat agar aplikasi tidak crash saat query gagal.
 * Departemen: Back-End (Infrastructure Support).
 * Alur: ViewModel -> SafeDbCall -> Room DAO.
 */
suspend fun <T> safeDbCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO, // Gunakan thread IO (Input-Output) secara default
    dbCall: suspend () -> T // Blok kode database yang akan dieksekusi
): Result<T> = withContext(dispatcher) {
    try {
        // Coba jalankan perintah database
        Result.Success(dbCall.invoke())
    } catch (throwable: Throwable) {
        // Tangkap jika terjadi kegagalan SQL mentah atau error lainnya
        when (throwable) {
            is SQLException -> Result.Error(throwable, "operasi database gagal.")
            else -> Result.Error(throwable, "kesalahan tak terduga saat akses database.")
        }
    }
}
