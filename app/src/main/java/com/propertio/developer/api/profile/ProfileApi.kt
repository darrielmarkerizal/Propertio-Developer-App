package com.propertio.developer.api.profile

import com.propertio.developer.api.auth.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ProfileApi {
    @GET("v1/profile")
    fun getProfile() : Call<ProfileResponse>

    @GET("provinces")
    fun getProvinces() : Call<List<ProfileResponse.Province>>

    @GET("cities/{province_id}")
    fun getCities(@Path("province_id") provinceId: String) : Call<List<ProfileResponse.City>>

    @FormUrlEncoded
    @POST("v1/profile?_method=PUT")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Field("full_name") fullName: String?,
        @Field("phone") phone: String?,
        @Field("address") address: String?,
        @Field("city") city: String?,
        @Field("province") province: String?
    ) : Call<UserResponse>
}