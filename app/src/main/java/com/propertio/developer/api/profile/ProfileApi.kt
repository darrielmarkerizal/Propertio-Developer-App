package com.propertio.developer.api.profile

import com.propertio.developer.api.auth.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ProfileApi {
    @GET("v1/profile")
    fun getProfile() : Call<ProfileResponse>

    @GET("provinces")
    fun getProvinces() : Call<List<ProfileResponse.Province>>

    @GET("cities/{province_id}")
    fun getCities(@Path("province_id") provinceId: String) : Call<List<ProfileResponse.City>>

    @Multipart
    @POST("v1/profile?_method=PUT")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Part("full_name") fullName: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("address") address: RequestBody,
        @Part("city") city: RequestBody,
        @Part("province") province: RequestBody,
        @Part pictureProfileFile: MultipartBody.Part?
    ) : Call<UserResponse>
}