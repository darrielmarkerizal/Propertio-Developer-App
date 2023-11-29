package com.propertio.developer.api.common.address


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface AddressApi {
    @GET("provinces")
    fun getProvinces() : Call<List<Province>>

    @GET("cities/{province_id}")
    fun getCities(@Path("province_id") provinceId: String) : Call<List<City>>

    @GET("districts/{city_id}")
    fun getDistricts(@Path("city_id") cityId: String) : Call<List<District>>
}