package com.rais.nexusbody.core.network

import com.rais.nexusbody.data.remote.dto.FoodSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FoodApiService {
    @GET("cgi/search.pl?search_simple=1&action=process&json=1&page_size=5")
    suspend fun searchFood(
        @Query("search_terms") query: String
    ): FoodSearchResponse
}