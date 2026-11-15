package com.example.a23200581solis_24100586hancco_pc02.data.remote.neo

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface NeoApi {

    @GET("neo/rest/v1/feed")
    suspend fun getFeed(
        @Query("api_key") apiKey: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): NeoFeedDto
}

data class NeoFeedDto(
    @SerializedName("near_earth_objects")
    val objects: Map<String, List<NeoDto?>?>? = null
)

data class NeoDto(
    val id: String? = null,
    val name: String? = null,

    @SerializedName("is_potentially_hazardous_asteroid")
    val hazardous: Boolean? = null,

    @SerializedName("estimated_diameter")
    val diameter: NeoDiameterDto? = null,

    @SerializedName("close_approach_data")
    val approaches: List<NeoApproachDto?>? = null
)

data class NeoDiameterDto(
    val meters: NeoMetersDto? = null
)

data class NeoMetersDto(
    @SerializedName("estimated_diameter_min")
    val min: Double? = null,

    @SerializedName("estimated_diameter_max")
    val max: Double? = null
)

data class NeoApproachDto(
    @SerializedName("close_approach_date")
    val date: String? = null,

    @SerializedName("relative_velocity")
    val velocity: NeoVelocityDto? = null,

    @SerializedName("miss_distance")
    val distance: NeoDistanceDto? = null
)

data class NeoVelocityDto(
    @SerializedName("kilometers_per_hour")
    val kmh: String? = null
)

data class NeoDistanceDto(
    val kilometers: String? = null
)