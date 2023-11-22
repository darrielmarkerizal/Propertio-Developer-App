package com.propertio.developer.api.profile

import retrofit2.Call
import retrofit2.http.GET

interface ProfileApi {
    @GET("v1/profile")
    fun getProfile() : Call<ProfileResponse>
}