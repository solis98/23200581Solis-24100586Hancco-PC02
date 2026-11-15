package com.example.a23200581solis_24100586hancco_pc02.data.local.apod

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apod_images")
data class ApodEntity(
    @PrimaryKey
    val date: String,

    val title: String,
    val explanation: String,
    val mediaType: String,
    val hdUrl: String?,
    val credit: String?,
    val isFavorite: Boolean = false
)