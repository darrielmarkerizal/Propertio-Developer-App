package com.propertio.developer.api.developer.unitmanagement

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CreateUnitResponse(
    @SerializedName("status")
    @Expose
    var status: String? = null,

    @SerializedName("message")
    @Expose
    var message: String? = null,

    @SerializedName("data")
    @Expose
    var data: UnitData? = null
) {
    data class UnitData(
        @SerializedName("title")
        @Expose
        var title: String? = null,

        @SerializedName("price")
        @Expose
        var price: String? = null,

        @SerializedName("description")
        @Expose
        var description: String? = null,

        @SerializedName("surface_area")
        @Expose
        var surface_area: String? = null,

        @SerializedName("building_area")
        @Expose
        var building_area: String? = null,

        @SerializedName("order")
        @Expose
        var order: String? = null,

        @SerializedName("stock")
        @Expose
        var stock: String? = null,

        @SerializedName("floor")
        @Expose
        var floor: String? = null,

        @SerializedName("bedroom")
        @Expose
        var bedroom: String? = null,

        @SerializedName("bathroom")
        @Expose
        var bathroom: String? = null,

        @SerializedName("garage")
        @Expose
        var garage: String? = null,

        @SerializedName("power_supply")
        @Expose
        var power_supply: String? = null,

        @SerializedName("water_type")
        @Expose
        var water_type: String? = null,

        @SerializedName("interior")
        @Expose
        var interior: String? = null,

        @SerializedName("road_access")
        @Expose
        var road_access: String? = null,

        @SerializedName("project_id")
        @Expose
        var project_id: String? = null,

        @SerializedName("unit_code")
        @Expose
        var unit_code: String? = null,

        @SerializedName("updated_at")
        @Expose
        var updated_at: String? = null,

        @SerializedName("created_at")
        @Expose
        var created_at: String? = null,

        @SerializedName("id")
        @Expose
        var id: Int? = null
    )
}