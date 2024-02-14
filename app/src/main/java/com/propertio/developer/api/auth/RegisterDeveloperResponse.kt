package com.propertio.developer.api.auth

import com.google.gson.annotations.SerializedName
import com.propertio.developer.api.models.DefaultResponse

class RegisterDeveloperResponse : DefaultResponse() {
    @SerializedName("data")
    var data: RegisterDeveloperData? = null

    inner class RegisterDeveloperData {
        @SerializedName("user_id")
        var userId: String? = null

        @SerializedName("description")
        var description: String? = null

        @SerializedName("website")
        var website: String? = null

        @SerializedName("adcredits")
        var adcredits: Int? = null

        @SerializedName("updated_at")
        var updatedAt: String? = null

        @SerializedName("created_at")
        var createdAt: String? = null

        @SerializedName("id")
        var id: Int? = null
    }
}
