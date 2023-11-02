package com.propertio.developer.api.common.message

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MessageResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: List<Data>? = null

    class Data {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("developer_id")
        @Expose
        var developerId: Int? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("email")
        @Expose
        var email: String? = null

        @SerializedName("phone")
        @Expose
        var phone: String? = null

        @SerializedName("message")
        @Expose
        var message: String? = null

        @SerializedName("read")
        @Expose
        var read: Int? = 0

        @SerializedName("subject")
        @Expose
        var subject: String? = null

        @SerializedName("created_at")
        @Expose
        var createAt: String? = null

        @SerializedName("updated_at")
        @Expose
        var updateAt: String? = null

    }


}