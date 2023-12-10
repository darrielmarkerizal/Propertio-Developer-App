package com.propertio.developer.unit_management

import com.google.gson.annotations.SerializedName
import com.propertio.developer.api.models.DefaultResponse
import com.propertio.developer.model.LitePhotosModel

class PostStoreUnitPhotoResponse :  DefaultResponse() {

    @SerializedName("data")
    val data : List<LitePhotosModel>? = null

}
