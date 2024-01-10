package com.propertio.developer.api.services


import com.propertio.developer.BuildConfig
import com.propertio.developer.api.DomainURL.BASE_GOOGLE_MAPS
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GoogleGeoCoding {

    private val apiKey : String
        get() = BuildConfig.MAPS_API_KEY

    fun getGeocodingInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_GOOGLE_MAPS)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val interceptor = Interceptor { chain ->
        val original = chain.request()
        val originalHttpUrl = original.url

        val url = originalHttpUrl.newBuilder()
            .addQueryParameter("key", apiKey)
            .build()

        val requestBuilder = original.newBuilder()
            .url(url)

        val request = requestBuilder.build()
        chain.proceed(request)
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()
}