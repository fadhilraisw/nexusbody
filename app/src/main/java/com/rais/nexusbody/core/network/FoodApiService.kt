package com.rais.nexusbody.core.network

import com.rais.nexusbody.data.remote.dto.FoodSearchResponse // DTO untuk bungkus hasil API
import retrofit2.http.GET // Metode request HTTP GET
import retrofit2.http.Query // Parameter query di URL

// Interface Retrofit untuk mendefinisikan kontrak komunikasi dengan database makanan luar
interface FoodApiService {

    // Fungsi untuk mencari makanan berdasarkan teks (misal: 'nasi goreng')
    @GET("cgi/search.pl?search_simple=1&action=process&json=1")
    suspend fun searchFood(
        @Query("search_terms") query: String, // Kata kunci pencarian
        @Query("page_size") pageSize: Int = 20, // Batas jumlah hasil
        @Query("fields") fields: String = "product_name,nutriments,brands" // Ambil field spesifik saja agar hemat bandwidth
    ): FoodSearchResponse
}
