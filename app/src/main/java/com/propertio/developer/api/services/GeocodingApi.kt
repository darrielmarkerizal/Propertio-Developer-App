package com.propertio.developer.api.services

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {
    @GET("geocode/json")
    suspend fun geocodeCoordinate(
        @Query("address") address: String
    ) : Response<GeocodingResponse>

    @GET("geocode/json")
    suspend fun getLocationFromLatLong(
        @Query("latlng") latlng: String
    ) : Response<GeocodingResponse>
}