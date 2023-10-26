package com.propertio.developer.auth

import com.propertio.developer.data.LoginRequest
import com.propertio.developer.data.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("/v1/auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

}