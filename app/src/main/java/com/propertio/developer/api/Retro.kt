package com.propertio.developer.api

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
 * This class is used to create a Retrofit instance
 * and to provide the base URL for the API calls
 */
class Retro(private val token : String?) {

    //TODO: Change this to your API host propertio
//    private val API_HOST = "http://10.0.2.2:8000/api/"
    private val API_HOST = "http://beta.propertio.id/api/"
    fun getRetroClientInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(API_HOST)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()

            val newRequestBuilder = originalRequest.newBuilder()
            if (token != null) {
                newRequestBuilder.header("Authorization", "Bearer $token")
            }

            val newRequest = newRequestBuilder.build()
            chain.proceed(newRequest)
        }
        .build()

    

}