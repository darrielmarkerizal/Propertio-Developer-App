package com.propertio.developer.api.profile

import com.propertio.developer.api.auth.UserResponse
import com.propertio.developer.api.common.address.AddressApi
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ProfileApi : AddressApi{
    @GET("v1/profile")
    fun getProfile() : Call<ProfileResponse>

    @FormUrlEncoded
    @POST("v1/profile?_method=PUT")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Field("full_name") fullName: String?,
        @Field("phone") phone: String?,
        @Field("address") address: String?,
        @Field("city") city: String?,
        @Field("province") province: String?,
//        @Part pictureProfileFile: MultipartBody.Part?
    ) : Call<UserResponse>
}