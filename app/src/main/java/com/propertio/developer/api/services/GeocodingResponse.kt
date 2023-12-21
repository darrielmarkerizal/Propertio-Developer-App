package com.propertio.developer.api.services

import com.google.gson.annotations.SerializedName

class GeocodingResponse {

    @SerializedName("results")
    var results: List<GeocodingResult>? = null

}

data class GeocodingResult (
    @SerializedName("formatted_address")
    var formattedAddress: String? = null,
    @SerializedName("geometry")
    var geometry: GeocodingGeometry? = null
)

data class GeocodingGeometry (
    @SerializedName("location")
    var location: GeocodingLocation? = null
)

data class GeocodingLocation (
    @SerializedName("lat")
    var lat: Double? = null,
    @SerializedName("lng")
    var lng: Double? = null
)
