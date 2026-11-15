package com.example.a23200581solis_24100586hancco_pc02.data.remote
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {

    private const val APOD_BASE_URL =
        "https://science.nasa.gov/"

    private const val NEO_BASE_URL =
        "https://api.nasa.gov/"

    val apodRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(APOD_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val neoRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(NEO_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}