package com.propertio.developer.api.auth

import com.propertio.developer.api.models.ChangePasswordRequest
import com.propertio.developer.api.models.ChangePasswordResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UserApi {

    @POST("v1/auth/login")
    fun login(
        @Body userRequest: UserRequest
    ): Call<UserResponse>

    @POST("v1/reset-password")
    fun changePassword(@Body request: ChangePasswordRequest): Call<ChangePasswordResponse>

    @Multipart
    @POST("v1/auth/register/user")
    fun registerUserForm(
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("password_confirmation") passwordConfirmation: RequestBody,
        @Part("first_name") firstName: RequestBody,
        @Part("last_name") lastName: RequestBody,
        @Part("phone") phoneNumber:RequestBody,
        @Part("city") city: RequestBody,
        @Part("province") province: RequestBody,
        @Part("role") role: RequestBody,
        @Part("status") status: RequestBody,
        @Part("address") address: RequestBody,
        @Part pictureProfileFile: MultipartBody.Part
    ) : Call<RegisterUserResponse>

    @POST("v1/auth/register/developer")
    fun registerDeveloper(
        @Body request: RegisterDeveloperRequest
    ): Call<RegisterDeveloperResponse>
}