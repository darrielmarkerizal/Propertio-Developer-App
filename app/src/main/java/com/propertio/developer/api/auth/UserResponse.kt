package com.propertio.developer.api.auth

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.propertio.developer.api.models.DefaultResponse

class UserResponse : DefaultResponse(){

    @SerializedName("data")
    @Expose
    var data: LoginResponse? = null

    class LoginResponse {
        @SerializedName("user")
        @Expose
        var user: User? = null

        @SerializedName("token")
        @Expose
        var token: String? = null

        class User {
            @SerializedName("account_id")
            @Expose
            var accountId: String? = null

            @SerializedName("full_name")
            @Expose
            var fullName: String? = null

            @SerializedName("role")
            @Expose
            var role: String? = null

            @SerializedName("picture_profile_file")
            @Expose
            var pictureProfileFile: String? = null
        }
    }
}