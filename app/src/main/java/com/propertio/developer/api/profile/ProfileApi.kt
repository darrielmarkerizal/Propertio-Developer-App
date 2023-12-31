package com.propertio.developer.api.profile

import com.propertio.developer.api.common.address.AddressApi
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ProfileApi : AddressApi{
    @GET("v1/profile")
    fun getProfile() : Call<ProfileResponse>

    @Multipart
    @POST("v1/profile?_method=PUT")
    fun updateProfile(
//        @Header("Authorization") token: String,
        @Part("full_name") fullName: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("address") address: RequestBody,
        @Part("city") city: RequestBody,
        @Part("province") province: RequestBody,
        @Part pictureProfileFile: MultipartBody.Part?
    ) : Call<ProfileResponse>
}