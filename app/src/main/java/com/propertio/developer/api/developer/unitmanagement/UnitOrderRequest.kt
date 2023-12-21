package com.propertio.developer.api.developer.unitmanagement

import com.google.gson.annotations.SerializedName

data class UnitOrderRequest (
    @SerializedName("unit_id") val unitId: Int,
    @SerializedName("order_count") val orderCount: Int
)
