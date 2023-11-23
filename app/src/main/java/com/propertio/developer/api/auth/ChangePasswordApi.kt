package com.propertio.developer.api.auth

import com.propertio.developer.api.models.ChangePasswordRequest
import com.propertio.developer.api.models.ChangePasswordResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ChangePasswordApi {
    @POST("/v1/reset-password")
    fun changePassword(@Body request: ChangePasswordRequest): Call<ChangePasswordResponse>
}