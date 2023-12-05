package com.propertio.developer.api.developer.projectmanagement

import com.google.gson.annotations.SerializedName
import com.propertio.developer.api.models.DefaultResponse
import com.propertio.developer.model.LitePhotosModel

class PostStoreProjectPhotoResponse : DefaultResponse() {

    @SerializedName("data")
    val data : List<LitePhotosModel>? = null

}
