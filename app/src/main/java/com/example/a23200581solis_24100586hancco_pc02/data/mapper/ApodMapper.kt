package com.example.a23200581solis_24100586hancco_pc02.data.mapper

import com.example.a23200581solis_24100586hancco_pc02.data.local.apod.ApodEntity
import com.example.a23200581solis_24100586hancco_pc02.data.remote.apod.ApodDto
import java.time.LocalDate
import java.util.Locale

fun ApodDto.toEntityOrNull(): ApodEntity? {

    val validDate = date
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?: return null

    // Una fecha ausente o inválida no permite identificar la publicación.
    if (runCatching { LocalDate.parse(validDate) }.isFailure) {
        return null
    }

    val cleanTitle = title
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?: "Sin título"

    val cleanExplanation = explanation
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?: "Explicación no disponible"

    val cleanMediaType = mediaType
        ?.trim()
        ?.lowercase(Locale.ROOT)
        ?.takeIf { it.isNotEmpty() }
        ?: "unknown"

    val cleanHdUrl = hdUrl
        ?.trim()
        ?.takeIf { it.isNotEmpty() }

    val cleanCredit = credit
        ?.trim()
        ?.takeIf { it.isNotEmpty() }

    return ApodEntity(
        date = validDate,
        title = cleanTitle,
        explanation = cleanExplanation,
        mediaType = cleanMediaType,
        hdUrl = if (cleanMediaType == "image") cleanHdUrl else null,
        credit = cleanCredit,
        isFavorite = false
    )
}