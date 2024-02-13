package com.propertio.developer.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ChangePasswordRequest {
    @SerializedName("old_password")
    @Expose
    var currentPassword: String? = null

    @SerializedName("password")
    @Expose
    var password: String? = null

    @SerializedName("password_confirmation")
    @Expose
    var passwordConfirmation: String? = null
}