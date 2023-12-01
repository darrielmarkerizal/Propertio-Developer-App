package com.propertio.developer.api.developer.type

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.propertio.developer.api.models.DefaultResponse
import com.propertio.developer.api.models.GeneralType

class GeneralTypeResponse : DefaultResponse() {

    @SerializedName("data")
    @Expose
    var data: List<GeneralType>? = null

}

