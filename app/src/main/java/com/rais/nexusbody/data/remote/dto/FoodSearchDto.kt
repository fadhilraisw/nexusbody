package com.rais.nexusbody.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FoodSearchResponse(
    @SerializedName("products") val products: List<FoodProductDto>
)

data class FoodProductDto(
    @SerializedName("product_name") val productName: String?,
    @SerializedName("nutriments") val nutriments: NutrimentsDto?
)

data class NutrimentsDto(
    @SerializedName("energy-kcal_100g") val energyKcal: Float?,
    @SerializedName("proteins_100g") val proteins: Float?,
    @SerializedName("carbohydrates_100g") val carbs: Float?,
    @SerializedName("fat_100g") val fat: Float?
)