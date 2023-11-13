package com.propertio.developer.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlin.reflect.KClass

abstract class DefaultResponse {
    @SerializedName("email")
    @Expose
    var status: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null
}