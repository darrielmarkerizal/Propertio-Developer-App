package com.propertio.developer.api.common.address


import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface AddressApi {

    /**
     * TODO: Transisikan semua ke suspend untuk jangka panjang
     * suspend punya kemampuan untuk menunggu hasil dari API,
     * gunakan suspend hanya untuk program yang tidak bisa dijalankan
     * secara asynchronous
     *
     * NOTE: Jangan pakai network di Main Thread
     */


    @GET("provinces")
    fun getProvinces() : Call<List<Province>>

    @GET("provinces")
    suspend fun getSuspendProvinces() : Response<List<Province>>

    @GET("cities/{province_id}")
    fun getCities(@Path("province_id") provinceId: String) : Call<List<City>>

    @GET("cities/{province_id}")
    suspend fun getSuspendCities(@Path("province_id") provinceId: String) : Response<List<City>>

    @GET("districts/{city_id}")
    fun getDistricts(@Path("city_id") cityId: String) : Call<List<District>>

    @GET("districts/{city_id}")
    suspend fun getSuspendDistricts(@Path("city_id") cityId: String) : Response<List<District>>
}