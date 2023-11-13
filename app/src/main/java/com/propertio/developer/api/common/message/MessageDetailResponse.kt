package com.propertio.developer.api.common.message

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.propertio.developer.api.models.DefaultResponse
import com.propertio.developer.api.models.MessageMinimum

class MessageDetailResponse : DefaultResponse(){
    @SerializedName("data")
    @Expose
    var data: Data? = null

    class Data : MessageMinimum(){}
}