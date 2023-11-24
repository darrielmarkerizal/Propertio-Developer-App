package com.propertio.developer.api.auth

import com.propertio.developer.api.models.ChangePasswordRequest
import com.propertio.developer.api.models.ChangePasswordResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi {

    @POST("v1/auth/login")
    fun login(
        @Body userRequest: UserRequest
    ): Call<UserResponse>

    @POST("/v1/reset-password")
    fun changePassword(@Body request: ChangePasswordRequest): Call<ChangePasswordResponse>
}