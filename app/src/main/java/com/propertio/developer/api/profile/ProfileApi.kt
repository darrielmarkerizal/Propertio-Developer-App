package com.propertio.developer.api.profile

import com.propertio.developer.api.auth.UserResponse
import retrofit2.Call
import retrofit2.http.GET

interface ProfileApi {
    @GET("v1/profile")
    fun getProfile() : Call<ProfileResponse>

    @GET("provinces")
    fun getProvinces() : Call<List<ProfileResponse.Province>>

}