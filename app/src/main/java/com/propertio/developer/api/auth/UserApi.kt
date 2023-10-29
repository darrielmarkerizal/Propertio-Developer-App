package com.propertio.developer.api.auth

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {

    @POST("v1/auth/login")
    fun login(
        @Body userRequest: UserRequest
    ): Call<UserResponse>



}