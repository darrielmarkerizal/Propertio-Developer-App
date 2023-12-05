package com.propertio.developer.model

import com.google.gson.annotations.SerializedName

data class StatusProject (
    @SerializedName("status") var status: String? = "not_active",
)
