package com.propertio.developer.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

abstract class ProjectMinimum {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("property_type")
    @Expose
    var propertyType: ProjectPropertyType? = null

    @SerializedName("posted_at")
    @Expose
    var postedAt: String? = null

    @SerializedName("count_unit")
    @Expose
    var countUnit: Int? = null

    @SerializedName("price")
    @Expose
    var price: String? = null

    @SerializedName("photo")
    @Expose
    var photo: String? = null

    @SerializedName("project_code")
    @Expose
    var projectCode: String? = null

    @SerializedName("count_views")
    @Expose
    var countViews: Int? = null

    @SerializedName("count_leads")
    @Expose
    var countLeads: Int? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null
}

data class ProjectPropertyType (
    @SerializedName("name")
    @Expose
    var name: String? = null,

    @SerializedName("icon")
    @Expose
    var icon: String? = null
)
