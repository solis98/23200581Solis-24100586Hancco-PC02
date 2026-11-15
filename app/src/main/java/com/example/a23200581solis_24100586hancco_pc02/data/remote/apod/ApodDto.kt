package com.example.a23200581solis_24100586hancco_pc02.data.remote.apod

import com.google.gson.annotations.SerializedName

data class ApodDto(
    @SerializedName("date")
    val date: String? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("explanation")
    val explanation: String? = null,

    @SerializedName("media_type")
    val mediaType: String? = null,

    @SerializedName("hdurl")
    val hdUrl: String? = null,

    @SerializedName("credit")
    val credit: String? = null
)