package com.propertio.developer.api.developer.projectmanagement

import com.google.gson.annotations.SerializedName

data class PostStoreProjectInfrastructureRequest (
    @SerializedName("project_id") var projectId: Int? = null,
    @SerializedName("infrastructure_type_id") var infrastructureTypeId: Int? = null,
    @SerializedName("name") var name: String? = null
)

