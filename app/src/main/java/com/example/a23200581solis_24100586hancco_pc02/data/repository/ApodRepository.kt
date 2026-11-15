package com.example.a23200581solis_24100586hancco_pc02.data.repository

import com.example.a23200581solis_24100586hancco_pc02.data.local.apod.ApodDao
import com.example.a23200581solis_24100586hancco_pc02.data.local.apod.ApodEntity
import com.example.a23200581solis_24100586hancco_pc02.data.mapper.toEntityOrNull
import com.example.a23200581solis_24100586hancco_pc02.data.remote.apod.ApodApi
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

class ApodRepository(
    private val api: ApodApi,
    private val dao: ApodDao,
    private val apiKey: String
) {

    // Las pantallas observarán los datos de Room.
    fun observeImages(
        startDate: String,
        endDate: String
    ): Flow<List<ApodEntity>> {
        return dao.observeImages(
            startDate = startDate,
            endDate = endDate
        )
    }

    fun observeImageByDate(
        date: String
    ): Flow<ApodEntity?> {
        return dao.observeImageByDate(date)
    }

    fun observeFavorites(): Flow<List<ApodEntity>> {
        return dao.observeFavorites()
    }

    suspend fun setFavorite(
        date: String,
        isFavorite: Boolean
    ) {
        dao.setFavorite(
            date = date,
            isFavorite = isFavorite
        )
    }

    // Consulta NASA y guarda el resultado en Room.
    suspend fun refreshImages(
        startDate: String,
        endDate: String
    ) {
        check(apiKey.isNotBlank()) {
            "Configura NASA_API_KEY en local.properties"
        }

        val response = api.getImages(
            apiKey = apiKey,
            startDate = startDate,
            endDate = endDate
        )

        val entities = response.mapNotNull { dto ->
            dto.toEntityOrNull()
        }

        dao.saveRemoteImages(entities)
    }

    companion object {

        // Hoy y los seis días anteriores.
        fun lastSevenDays(
            today: LocalDate = LocalDate.now()
        ): Pair<String, String> {
            return today.minusDays(6).toString() to today.toString()
        }
    }
}