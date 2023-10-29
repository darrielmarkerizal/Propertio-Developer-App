package com.propertio.developer.api.auth

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserResponse {
    @SerializedName("email")
    @Expose
    var status: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

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