package com.propertio.developer.unit_management

import com.google.gson.annotations.SerializedName
import com.propertio.developer.api.models.DefaultResponse

class UpdateUnitResponse : DefaultResponse() {
    @SerializedName("data")
    val data : Any? = null
}