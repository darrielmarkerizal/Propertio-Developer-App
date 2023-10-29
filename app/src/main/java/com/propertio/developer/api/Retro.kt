package com.propertio.developer.api

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
 * This class is used to create a Retrofit instance
 * and to provide the base URL for the API calls
 */
class Retro {

    //TODO: Change this to your API host propertio
    private val API_HOST = "http://10.0.2.2:8000/api/"
    fun getRetroClientInstance() : Retrofit {
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl(API_HOST)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}