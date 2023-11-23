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
}