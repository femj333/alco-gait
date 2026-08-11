package wpics.alcogait.data

import retrofit2.http.GET
import retrofit2.http.Query

data class GeocodingResponse(
    val results: List<GeocodingResults>,
    val status: String
)

data class GeocodingResults(
    val formatted_address: String,
    val place_id: String,
    val geometry: Geometry
)

data class Geometry(
    val location: LatLngDto
)

data class LatLngDto(
    val lat: Float,
    val lng: Float
)

interface GeocodingService {
    @GET("maps/api/geocode/json")
    suspend fun geocode(
        @Query("address") address: String,
        @Query("key") apiKey: String
    ) : GeocodingResponse

    @GET("maps/api/geocode/json")
    suspend fun reverseGeocode(
        @Query("latlng") latlng: String,
        @Query("key") apiKey: String
    ): GeocodingResponse
}
