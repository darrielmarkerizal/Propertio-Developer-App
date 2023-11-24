package com.propertio.developer.api.profile

import com.propertio.developer.api.auth.UserResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileApi {
    @GET("v1/profile")
    fun getProfile() : Call<ProfileResponse>

    @GET("provinces")
    fun getProvinces() : Call<List<ProfileResponse.Province>>

    @GET("cities/{province_id}")
    fun getCities(@Path("province_id") provinceId: String) : Call<List<ProfileResponse.City>>

}