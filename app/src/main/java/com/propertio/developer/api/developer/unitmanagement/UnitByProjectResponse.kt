package com.propertio.developer.api.developer.unitmanagement

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.propertio.developer.api.models.DefaultResponse
import com.propertio.developer.api.models.UnitMinimum

class UnitByProjectResponse : DefaultResponse() {
    @SerializedName("data")
    @Expose
    var data: List<Unit>? = null

    class Unit : UnitMinimum() {}
}