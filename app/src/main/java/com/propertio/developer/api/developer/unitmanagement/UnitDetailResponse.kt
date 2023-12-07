package com.propertio.developer.api.developer.unitmanagement

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.propertio.developer.api.models.DefaultResponse
import com.propertio.developer.api.models.UnitMinimum
import com.propertio.developer.model.ProjectVideo
import com.propertio.developer.model.ProjectVirtualTour
import com.propertio.developer.model.UnitVirtualTour

class UnitDetailResponse : DefaultResponse() {
    @SerializedName("data")
    @Expose
    var data: Unit? = null

    class Unit : UnitMinimum() {
        @SerializedName("property_type")
        @Expose
        var propertyType: String? = null

        @SerializedName("unitPhotos")
        @Expose
        var unitPhotos: List<UnitPhoto>? = null

        @SerializedName("unitModel")
        @Expose
        var unitModel: String? = null

        @SerializedName("unitVirtualTour")
        @Expose
        var unitVirtualTour: List<UnitVirtualTour>? = null
        // TODO: Define type of unitVirtualTour

        @SerializedName("unitVideo")
        @Expose
        var unitVideo: ProjectVideo? = null

        @SerializedName("unitDocuments")
        @Expose
        var unitDocuments: List<*>? = null
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

    }
}