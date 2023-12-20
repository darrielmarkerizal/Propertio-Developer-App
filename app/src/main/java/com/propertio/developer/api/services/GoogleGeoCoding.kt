package com.propertio.developer.api.services

import com.propertio.developer.api.DomainURL.BASE_GOOGLE_MAPS
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GoogleGeoCoding {
    fun getGeocodingInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_GOOGLE_MAPS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}