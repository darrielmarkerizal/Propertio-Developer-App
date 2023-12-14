package com.propertio.developer.api.developer.unitmanagement

import com.google.gson.annotations.SerializedName

data class UpdateUnitRequest(
    @SerializedName("id") val unitId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("price") val price: String,
    @SerializedName("description") val description: String?,
    @SerializedName("surface_area") val surfaceArea: String?,
    @SerializedName("building_area") val buildingArea: String?,
    @SerializedName("order") val order: String?,
    @SerializedName("stock") val stock: String? = null,
    @SerializedName("floor") val floor: String?,
    @SerializedName("bedroom") val bedroom: String?,
    @SerializedName("bathroom") val bathroom: String?,
    @SerializedName("garage") val garage: String?,
    @SerializedName("power_supply") val powerSupply: String?,
    @SerializedName("water_type") val waterType: String?,
    @SerializedName("interior") val interior: String?,
    @SerializedName("road_access") val roadAccess: String?
)