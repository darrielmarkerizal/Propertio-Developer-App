package com.propertio.developer.api.common.message

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.propertio.developer.api.models.DefaultResponse
import com.propertio.developer.api.models.MessageMinimum

class MessageResponse : DefaultResponse() {
    @SerializedName("data")
    @Expose
    var data: List<Data>? = null

    class Data : MessageMinimum(){}


}