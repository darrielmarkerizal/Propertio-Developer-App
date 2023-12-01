package com.propertio.developer.api.models

data class PlaceDetailResponse(
    val result: PlaceDetailsResult
)

data class PlaceDetailsResult(
    val geometry: PlaceGeometry
)

data class PlaceGeometry(
    val location: PlaceLocation
)

data class PlaceLocation(
    val lat: Double,
    val lng: Double
)
