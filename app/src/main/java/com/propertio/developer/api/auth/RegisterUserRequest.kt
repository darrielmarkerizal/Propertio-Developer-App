package com.propertio.developer.api.auth

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

data class RegisterUserRequest (
    @SerializedName("email") var email: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("password_confirmation") var passwordConfirmation: String? = null,
    @SerializedName("first_name") var firstName: String? = null,
    @SerializedName("last_name") var lastName: String? = null,
    @SerializedName("phone") var phoneNumber: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("province") var province: String? = null,
    @SerializedName("role") var role: String = "developer",
    @SerializedName("status") var status: String = "active",
    @SerializedName("address") var address: String? = null,
    @SerializedName("picture_profile_file") var pictureProfileFile: MultipartBody.Part? = null,

    )
