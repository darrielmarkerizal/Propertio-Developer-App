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

        @SerializedName("status")
        @Expose
        var status: String? = null,

        @SerializedName("account_id")
        @Expose
        var accountId: String? = null,

        @SerializedName("created_at")
        @Expose
        var createdAt: String? = null,

        @SerializedName("updated_at")
        @Expose
        var updatedAt: String? = null,

        @SerializedName("id")
        @Expose
        var id: Int? = null,
    )

    data class RegisterUserData (
        @SerializedName("phone")
        @Expose
        var phone: String? = null,

        @SerializedName("city")
        @Expose
        var city: String? = null,

        @SerializedName("province")
        @Expose
        var province: String? = null,

        @SerializedName("picture_profile_file")
        @Expose
        var pictureProfileFile: String? = null,

        @SerializedName("full_name")
        @Expose
        var fullName: String? = null,

        @SerializedName("user_id")
        @Expose
        var userId: Int? = null,

        @SerializedName("updated_at")
        @Expose
        var updatedAt: String? = null,

        @SerializedName("created_at")
        @Expose
        var createdAt: String? = null,

        @SerializedName("id")
        @Expose
        var id: Int? = null,
    )


}
