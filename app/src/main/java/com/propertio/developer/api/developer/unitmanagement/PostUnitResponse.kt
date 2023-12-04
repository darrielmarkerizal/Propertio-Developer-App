package com.propertio.developer.api.developer.unitmanagement

import com.google.gson.annotations.SerializedName
import com.propertio.developer.api.models.DefaultResponse

class PostUnitResponse : DefaultResponse() {
    @SerializedName("data")
    var data: PostStoreUnit? = null

    data class PostStoreUnit (
        @SerializedName("id")
        var id: Int? = null
    )


}
