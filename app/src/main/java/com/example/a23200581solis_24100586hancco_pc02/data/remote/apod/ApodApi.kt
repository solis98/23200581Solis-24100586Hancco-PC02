package com.example.a23200581solis_24100586hancco_pc02.data.remote.apod

import retrofit2.http.GET
import retrofit2.http.Query

interface ApodApi {

    @GET("wp-json/wp/v2/apod-basic")
    suspend fun getImages(
        @Query("api_key") apiKey: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): List<ApodDto>
}