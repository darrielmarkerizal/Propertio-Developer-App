package com.propertio.developer.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ChangePasswordResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data (
        @SerializedName("old_password")
        @Expose
        var currentPassword: List<String>? = null,

        @SerializedName("password")
        @Expose
        var password: List<String>? = null,

        @SerializedName("password_confirmation")
        @Expose
        var passwordConfirmation: List<String>? = null
    )
}