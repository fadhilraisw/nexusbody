package com.rais.nexusbody.core.util

import kotlinx.coroutines.CoroutineDispatcher // Coroutines: Pengatur antrian thread
import kotlinx.coroutines.Dispatchers // Coroutines: Daftar thread tersedia (IO, Main, dll)
import kotlinx.coroutines.withContext // Coroutines: Pengalih thread asinkron
import retrofit2.HttpException // Network: Error status server (404, 500, dll)
import java.io.IOException // Network: Error koneksi internet/fisik
import android.database.SQLException // Database: Error query lokal

/**
 * SAFE API CALL WRAPPER
 * Peran: Penangkap error universal untuk permintaan jaringan (Retrofit) dan cloud.
 * Departemen: Back-End (Infrastructure).
 * Kegunaan: Menjamin UI tetap stabil meskipun server mati atau internet putus.
 */
suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO, // Menjalankan di thread latar belakang khusus data
    apiCall: suspend () -> T // Blok kode eksekusi API (misal: Groq atau Food API)
): Result<T> = withContext(dispatcher) {
    try {
        // Eksekusi pemanggilan fungsi aslinya dan bungkus dengan status Success
        Result.Success(apiCall.invoke())
    } catch (throwable: Throwable) {
        // Pemetaan error berdasarkan jenis kegagalan jaringan
        when (throwable) {
            // Jika kabel putus, sinyal hilang, atau DNS error
            is IOException -> Result.Error(throwable, "network error. check your connection.")
            // Jika server Groq/Supabase memberikan error (misal: 401 Unauthorized)
            is HttpException -> Result.Error(throwable, "server error: ${throwable.code()}")
            // Kegagalan integritas data saat sinkronisasi cloud
            is SQLException -> Result.Error(throwable, "database operation failed.")
            // Error tak dikenal lainnya
            else -> Result.Error(throwable, "an unexpected error occurred.")
        }
    }
}
