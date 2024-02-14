package com.propertio.developer.api.auth

import com.google.gson.annotations.SerializedName

data class RegisterDeveloperRequest (
    @SerializedName("user_id") var userId: Int? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("website") var website: String? = null,
)
