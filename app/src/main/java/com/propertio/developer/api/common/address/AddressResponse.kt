package com.propertio.developer.api.common.address

import com.google.gson.annotations.SerializedName


data class Province(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,
)

data class City(
    @SerializedName("id")
    val id: String,

    @SerializedName("province_id")
    val provinceId: String,

    @SerializedName("name")
    val name: String
)

data class District(
    @SerializedName("id")
    val id: String,

    @SerializedName("regency_id")
    val cityId: String,

    @SerializedName("name")
    val name: String
)