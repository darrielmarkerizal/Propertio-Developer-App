package com.propertio.developer.api.auth

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.propertio.developer.api.models.DefaultResponse

class RegisterUserResponse : DefaultResponse() {
    @SerializedName("data")
    @Expose
    var data : RegisterResponse ?= null

    data class RegisterResponse (
        @SerializedName("user")
        @Expose
        var user: RegisterUser? = null,

        @SerializedName("user_data")
        @Expose
        var user_data: RegisterUserData? = null,

    )

    data class RegisterUser (
        @SerializedName("email")
        @Expose
        var email: String? = null,

        @SerializedName("role")
        @Expose
        var role: String? = null,
    )

    data class RegisterUserData (
        @SerializedName("full_name")
        @Expose
        var fullName: String? = null,

        @SerializedName("user_id")
        @Expose
        var userId: Int? = null,
    )


}
