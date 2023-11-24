package com.propertio.developer.api.developer.projectmanagement

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.propertio.developer.api.models.DefaultResponse
import com.propertio.developer.api.models.ProjectMinimum

class ProjectListResponse : DefaultResponse() {

    @SerializedName("data")
    @Expose
    var data: List<ProjectDeveloper>? = null

    class ProjectDeveloper : ProjectMinimum() {
        @SerializedName("address")
        @Expose
        var address: ProjectAddress? = null

        class  ProjectAddress {
            @SerializedName("address")
            @Expose
            var address: String? = null

            @SerializedName("district")
            @Expose
            var district: String? = null

            @SerializedName("city")
            @Expose
            var city: String? = null

            @SerializedName("province")
            @Expose
            var province: String? = null
        }
    }



}