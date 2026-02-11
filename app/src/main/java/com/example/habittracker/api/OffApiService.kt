package com.example.habittracker.api

import com.example.habittracker.data.OffProductResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OffApiService {
    @GET("api/v0/product/{barcode}.json")
    suspend fun getProduct(
        @Path("barcode") barcode: String,
        @Query("fields") fields: String = "product_name,ingredients_text,ingredients_text_pl,nutriments,brands",
        @Query("lc") locale: String = "pl"
    ): OffProductResponse
}
