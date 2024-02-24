package com.propertio.developer.api.developer.unitmanagement

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.propertio.developer.api.developer.projectmanagement.ProjectDetail
import com.propertio.developer.api.models.DefaultResponse

class UnitListResponse : DefaultResponse() {

    @SerializedName("data")
    @Expose
    var data: List<ProjectDetail.ProjectDeveloper.ProjectUnit>? = null

}
