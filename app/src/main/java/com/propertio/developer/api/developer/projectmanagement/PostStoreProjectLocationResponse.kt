package com.propertio.developer.api.developer.projectmanagement

import com.google.gson.annotations.SerializedName
import com.propertio.developer.api.models.DefaultResponse

class PostStoreProjectLocationResponse : DefaultResponse() {

    @SerializedName("data")
    var data: ProjectLocationResponse? = null

    data class ProjectLocationResponse(
        @SerializedName("id")
        var id: Int? = null,

        @SerializedName("developer_id")
        var developerId: Int? = null,

        @SerializedName("headline")
        var headline: String? = null,

        @SerializedName("title")
        var title: String? = null,

        @SerializedName("property_type_id")
        var propertyTypeId: String? = null,

        @SerializedName("description")
        var description: String? = null,

        @SerializedName("address")
        var address: String? = null,

        @SerializedName("province")
        var province: String? = null,

        @SerializedName("city")
        var city: String? = null,

        @SerializedName("district")
        var district: String? = null,

        @SerializedName("postal_code")
        var postalCode: String? = null,

        @SerializedName("longitude")
        var longitude: Double? = null,

        @SerializedName("latitude")
        var latitude: Double? = null,

        @SerializedName("certificate")
        var certificate: String? = null,

        @SerializedName("completed_at")
        var completedAt: String? = null,

//        @SerializedName("siteplan_image")
//        var siteplanImage: String? = null,

        @SerializedName("immersive_siteplan")
        var immersiveSiteplan: String? = null,

        @SerializedName("immersive_apps")
        var immersiveApps: String? = null,


        @SerializedName("created_at")
        var createdAt: String? = null,

        @SerializedName("updated_at")
        var updatedAt: String? = null
    )
}