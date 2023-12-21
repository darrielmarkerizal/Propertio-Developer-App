package com.propertio.developer.api.developer.unitmanagement

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.propertio.developer.api.models.DefaultResponse
import com.propertio.developer.api.models.UnitMinimum
import com.propertio.developer.model.ProjectVideo
import com.propertio.developer.model.ProjectVirtualTour
import com.propertio.developer.model.UnitVirtualTour
import java.io.Serial

class UnitDetailResponse : DefaultResponse() {
    @SerializedName("data")
    @Expose
    var data: Unit? = null

    class Unit : UnitMinimum() {
        @SerializedName("propertyType")
        @Expose
        var propertyType: String? = null

        @SerializedName("unitPhotos")
        @Expose
        var unitPhotos: List<UnitPhoto>? = null

        @SerializedName("unitModel")
        @Expose
        var unitModel: UnitModel? = null

        @SerializedName("unitVirtualTour")
        @Expose
        private var _unitVirtualTour: JsonElement? = null

        val unitVirtualTour: Any?
            get() {
                return when {
                    _unitVirtualTour?.isJsonObject == true -> {
                        val gson = Gson()
                        gson.fromJson(_unitVirtualTour, UnitVirtualTour::class.java)
                    }
                    _unitVirtualTour?.isJsonArray == true -> {
                        val gson = Gson()
                        gson.fromJson(_unitVirtualTour, Array<UnitVirtualTour>::class.java)
                    }
                    else -> null
                }
            }


        @SerializedName("unitVideo")
        @Expose
        var unitVideo: ProjectVideo? = null

        @SerializedName("unitDocuments")
        @Expose
        var unitDocuments: List<UnitDocument>? = null
        // TODO: Define type of unitDocuments


        class UnitPhoto {
            @SerializedName("id")
            @Expose
            var id: Int? = null

            @SerializedName("unit_id")
            @Expose
            var unitId: String? = null

            @SerializedName("caption")
            @Expose
            var caption: String? = null

            @SerializedName("filename")
            @Expose
            var filename: String? = null

            @SerializedName("is_cover")
            @Expose
            var isCover: String? = null

            @SerializedName("type")
            @Expose
            var type: String? = null

            @SerializedName("created_at")
            @Expose
            var createdAt: String? = null

            @SerializedName("updated_at")
            @Expose
            var updatedAt: String? = null
        }

        class UnitDocument {
            @SerializedName("id")
            @Expose
            var id: Int? = null

            @SerializedName("unit_id")
            @Expose
            var unitId: String? = null

            @SerializedName("name")
            @Expose
            var name: String? = null

            @SerializedName("type")
            @Expose
            var type: String? = null

            @SerializedName("filename")
            @Expose
            var filename: String? = null

            @SerializedName("created_at")
            @Expose
            var createdAt: String? = null

            @SerializedName("updated_at")
            @Expose
            var updatedAt: String? = null
        }

        data class UnitModel(
            @SerializedName("id")
            val id: Int,
            @SerializedName("unit_id")
            val unitId: String,
            @SerializedName("link")
            val link: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("updated_at")
            val updatedAt: String
        )

        data class UnitVirtualTour(
            @SerializedName("id")
            val id: Int,
            @SerializedName("unit_id")
            val unitId: String,
            @SerializedName("name")
            val name: String,
            @SerializedName("link")
            val link: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("updated_at")
            val updatedAt: String
        )

    }
}