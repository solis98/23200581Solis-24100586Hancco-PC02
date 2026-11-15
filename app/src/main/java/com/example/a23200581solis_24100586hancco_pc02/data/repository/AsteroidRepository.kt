package com.example.a23200581solis_24100586hancco_pc02.data.repository

import com.example.a23200581solis_24100586hancco_pc02.data.local.apod.neo.AsteroidDao
import com.example.a23200581solis_24100586hancco_pc02.data.local.apod.neo.AsteroidEntity
import com.example.a23200581solis_24100586hancco_pc02.data.remote.neo.NeoApi
import com.example.a23200581solis_24100586hancco_pc02.data.remote.neo.NeoDto
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

class AsteroidRepository(
    private val api: NeoApi,
    private val dao: AsteroidDao,
    private val apiKey: String
) {

    fun observeAll(
        start: String,
        end: String
    ): Flow<List<AsteroidEntity>> {
        return dao.observeAll(start, end)
    }

    fun observeFavorites(): Flow<List<AsteroidEntity>> {
        return dao.observeFavorites()
    }

    fun observeDetail(
        id: String,
        date: String
    ): Flow<AsteroidEntity?> {
        return dao.observeDetail(id, date)
    }

    suspend fun setFavorite(
        id: String,
        favorite: Boolean
    ) {
        dao.setFavorite(id, favorite)
    }

    suspend fun refresh(start: String, end: String) {
        check(apiKey.isNotBlank()) {
            "Falta NASA_API_KEY en local.properties"
        }

        val response = api.getFeed(
            apiKey = apiKey,
            startDate = start,
            endDate = end
        )

        val entities = response.objects
            .orEmpty()
            .flatMap { (date, objects) ->
                objects.orEmpty().mapNotNull { dto ->
                    dto?.toEntityOrNull(date)
                }
            }
            .distinctBy {
                it.asteroidId to it.approachDate
            }

        dao.saveRemote(entities)
    }
}

// Convierte explícitamente el DTO recibido a una Entity de Room.
private fun NeoDto.toEntityOrNull(
    feedDate: String
): AsteroidEntity? {

    val validId = id
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?: return null

    if (runCatching { LocalDate.parse(feedDate) }.isFailure) {
        return null
    }

    val approach = approaches
        .orEmpty()
        .filterNotNull()
        .firstOrNull { it.date == feedDate }

    return AsteroidEntity(
        asteroidId = validId,
        approachDate = feedDate,
        name = name?.takeIf { it.isNotBlank() } ?: "Sin nombre",
        isPotentiallyHazardous = hazardous ?: false,
        diameterMinMeters = diameter?.meters?.min,
        diameterMaxMeters = diameter?.meters?.max,
        velocityKmh = approach?.velocity?.kmh?.toDoubleOrNull(),
        distanceKm = approach?.distance?.kilometers?.toDoubleOrNull()
    )
}