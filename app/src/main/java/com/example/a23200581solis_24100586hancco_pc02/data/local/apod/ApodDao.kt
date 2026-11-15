package com.example.a23200581solis_24100586hancco_pc02.data.local.apod

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ApodDao {

    // Publicaciones del periodo solicitado, más recientes primero.
    @Query(
        """
        SELECT * FROM apod_images
        WHERE date BETWEEN :startDate AND :endDate
        ORDER BY date DESC
        """
    )
    fun observeImages(
        startDate: String,
        endDate: String
    ): Flow<List<ApodEntity>>

    // Publicación que se mostrará en el detalle.
    @Query(
        """
        SELECT * FROM apod_images
        WHERE date = :date
        LIMIT 1
        """
    )
    fun observeImageByDate(
        date: String
    ): Flow<ApodEntity?>

    // Favoritas, incluso si ya no pertenecen a los últimos siete días.
    @Query(
        """
        SELECT * FROM apod_images
        WHERE isFavorite = 1
        ORDER BY date DESC
        """
    )
    fun observeFavorites(): Flow<List<ApodEntity>>

    // Cambia únicamente el estado local de favorito.
    @Query(
        """
        UPDATE apod_images
        SET isFavorite = :isFavorite
        WHERE date = :date
        """
    )
    suspend fun setFavorite(
        date: String,
        isFavorite: Boolean
    )

    // Inserta publicaciones nuevas sin reemplazar las existentes.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(
        image: ApodEntity
    ): Long

    // Actualiza los datos de NASA conservando isFavorite.
    @Query(
        """
        UPDATE apod_images
        SET title = :title,
            explanation = :explanation,
            mediaType = :mediaType,
            hdUrl = :hdUrl,
            credit = :credit
        WHERE date = :date
        """
    )
    suspend fun updateRemoteFields(
        date: String,
        title: String,
        explanation: String,
        mediaType: String,
        hdUrl: String?,
        credit: String?
    )

    // Guarda la descarga completa en una transacción.
    @Transaction
    suspend fun saveRemoteImages(
        images: List<ApodEntity>
    ) {
        images.forEach { image ->

            val insertedId = insertIfAbsent(image)

            if (insertedId == -1L) {
                updateRemoteFields(
                    date = image.date,
                    title = image.title,
                    explanation = image.explanation,
                    mediaType = image.mediaType,
                    hdUrl = image.hdUrl,
                    credit = image.credit
                )
            }
        }
    }
}