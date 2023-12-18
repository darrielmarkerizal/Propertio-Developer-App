package com.propertio.developer.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GeneralType(
    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @SerializedName("name")
    @Expose
    var name: String? = null,

    @SerializedName("icon")
    @Expose
    var icon: String? = null,

    @SerializedName("category")
    @Expose
    var category: String? = null
)