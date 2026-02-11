package com.example.habittracker.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OffRetrofitClient {
    private const val BASE_URL = "https://world.openfoodfacts.org/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val service: OffApiService by lazy {
        retrofit.create(OffApiService::class.java)
    }
}
