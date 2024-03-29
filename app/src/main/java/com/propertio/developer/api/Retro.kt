package com.propertio.developer.api

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
 * This class is used to create a Retrofit instance
 * and to provide the base URL for the API calls
 */
class Retro(private val token : String?) {

    private val API_HOST = DomainURL.HOST_API
    fun getRetroClientInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(API_HOST)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // TODO: Remove this interceptor
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val originalRequest = chain.request()

            val newRequestBuilder = originalRequest.newBuilder()
            if (token != null) {
                newRequestBuilder.header("Authorization", "Bearer $token")
            }

            val newRequest = newRequestBuilder.build()
            chain.proceed(newRequest)
        }
        .addInterceptor { chain ->
            val originalRequest = chain.request()

            val newRequestBuilder = originalRequest.newBuilder()
            newRequestBuilder.header("Device", "mobile")

            val newRequest = newRequestBuilder.build()
            chain.proceed(newRequest)
        }
        .build()



}