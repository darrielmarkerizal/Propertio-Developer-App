package com.propertio.developer.api.services

import com.propertio.developer.api.models.PlaceDetailResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesService {
    @GET("maps/api/place/autocomplete/json")
    suspend fun getPlaceDetails(
        @Query("place_id") placeId: String,
        @Query("key") key: String
    ) : PlaceDetailResponse
}