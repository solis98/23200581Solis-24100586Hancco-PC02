package com.example.a23200581solis_24100586hancco_pc02.data.local.apod.neo

import androidx.room.Entity

@Entity(
    tableName = "asteroids",
    primaryKeys = ["asteroidId", "approachDate"]
)
data class AsteroidEntity(
    val asteroidId: String,
    val approachDate: String,
    val name: String,
    val isPotentiallyHazardous: Boolean,
    val diameterMinMeters: Double?,
    val diameterMaxMeters: Double?,
    val velocityKmh: Double?,
    val distanceKm: Double?,
    val isFavorite: Boolean = false
)