package com.example.a23200581solis_24100586hancco_pc02.data.local.apod.neo

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AsteroidDao {

    @Query(
        """
        SELECT * FROM asteroids
        WHERE approachDate BETWEEN :start AND :end
        ORDER BY approachDate DESC, name ASC
        """
    )
    fun observeAll(
        start: String,
        end: String
    ): Flow<List<AsteroidEntity>>

    @Query(
        """
        SELECT * FROM asteroids
        WHERE asteroidId = :id AND approachDate = :date
        LIMIT 1
        """
    )
    fun observeDetail(
        id: String,
        date: String
    ): Flow<AsteroidEntity?>

    @Query(
        """
        SELECT * FROM asteroids
        WHERE isFavorite = 1
        ORDER BY approachDate DESC, name ASC
        """
    )
    fun observeFavorites(): Flow<List<AsteroidEntity>>

    @Query(
        """
        UPDATE asteroids
        SET isFavorite = :favorite
        WHERE asteroidId = :id
        """
    )
    suspend fun setFavorite(
        id: String,
        favorite: Boolean
    )

    @Query(
        """
        SELECT DISTINCT asteroidId FROM asteroids
        WHERE isFavorite = 1
        """
    )
    suspend fun favoriteIds(): List<String>

    @Upsert
    suspend fun upsertAll(items: List<AsteroidEntity>)

    @Transaction
    suspend fun saveRemote(items: List<AsteroidEntity>) {
        val favorites = favoriteIds().toSet()

        upsertAll(
            items.map {
                it.copy(
                    isFavorite = it.asteroidId in favorites
                )
            }
        )
    }
}