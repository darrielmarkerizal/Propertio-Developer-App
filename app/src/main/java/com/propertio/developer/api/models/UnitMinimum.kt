package com.propertio.developer.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

abstract class UnitMinimum {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("project_id")
    @Expose
    var projectId: String? = null

    @SerializedName("unit_code")
    @Expose
    var unitCode: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("surface_area")
    @Expose
    var surfaceArea: String? = "0"

    @SerializedName("building_area")
    @Expose
    var buildingArea: String? = "0"

    @SerializedName("order")
    @Expose
    var order: String? = "0"

    @SerializedName("stock")
    @Expose
    var stock: String? = "0"

    @SerializedName("floor")
    @Expose
    var floor: String? = "0"

    @SerializedName("bedroom")
    @Expose
    var bedroom: String? = "0"

    @SerializedName("bathroom")
    @Expose
    var bathroom: String? = "0"

    @SerializedName("garage")
    @Expose
    var garage: String? = "0"

    @SerializedName("price")
    @Expose
    var price: String? = "0"

    @SerializedName("power_supply")
    @Expose
    var powerSupply: String? = "0"

    @SerializedName("water_supply")
    @Expose
    var waterSupply: String? = ""

    @SerializedName("water_type")
    @Expose
    var waterType: String? = null

    @SerializedName("interior")
    @Expose
    var interior: String? = null

    @SerializedName("road_access")
    @Expose
    var roadAccess: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null
}