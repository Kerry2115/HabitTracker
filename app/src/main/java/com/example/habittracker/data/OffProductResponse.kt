package com.example.habittracker.data

import com.google.gson.annotations.SerializedName

data class OffProductResponse(
    val status: Int,
    @SerializedName("status_verbose")
    val statusVerbose: String? = null,
    val product: OffProduct? = null
)

data class OffProduct(
    @SerializedName("product_name")
    val productName: String? = null,
    val brands: String? = null,
    @SerializedName("ingredients_text")
    val ingredientsText: String? = null,
    @SerializedName("ingredients_text_pl")
    val ingredientsTextPl: String? = null,
    val nutriments: OffNutriments? = null
)

data class OffNutriments(
    @SerializedName("energy-kcal_100g")
    val energyKcal100g: Float? = null,
    @SerializedName("fat_100g")
    val fat100g: Float? = null,
    @SerializedName("saturated-fat_100g")
    val saturatedFat100g: Float? = null,
    @SerializedName("carbohydrates_100g")
    val carbohydrates100g: Float? = null,
    @SerializedName("sugars_100g")
    val sugars100g: Float? = null,
    @SerializedName("proteins_100g")
    val proteins100g: Float? = null,
    @SerializedName("salt_100g")
    val salt100g: Float? = null
)
