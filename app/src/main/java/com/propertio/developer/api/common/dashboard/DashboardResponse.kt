package com.propertio.developer.api.common.dashboard

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.propertio.developer.api.models.DefaultResponse

class DashboardResponse : DefaultResponse()  {

    @SerializedName("data")
    @Expose
    var data: Data? = null

    class Data {
        @SerializedName("unit_count")
        @Expose
        var unitCount: Int? = null

        @SerializedName("project_count")
        @Expose
        var projectCount: Int? = null

        @SerializedName("view_count")
        @Expose
        var viewCount: Int? = null

        @SerializedName("lead_count")
        @Expose
        var leadCount: Int? = null

        @SerializedName("message_count")
        @Expose
        var messageCount: Int? = null

        @SerializedName("leads")
        @Expose
        var leads: Leads? = null

        @SerializedName("views")
        @Expose
        var views: Views? = null

        class Leads {
            @SerializedName("monthly")
            @Expose
            var monthly: List<Monthly>? = null

            @SerializedName("weekly")
            @Expose
            var weekly: List<Weekly>? = null

            class Monthly {
                @SerializedName("year")
                @Expose
                var year: String? = null

                @SerializedName("month")
                @Expose
                var month: String? = null

                @SerializedName("total")
                @Expose
                var total: String? = null
            }

            class Weekly {
                @SerializedName("date")
                @Expose
                var date: String? = null

                @SerializedName("total")
                @Expose
                var total: String? = null
            }
        }

        class Views {
            @SerializedName("monthly")
            @Expose
            var monthly: List<Monthly>? = null

            @SerializedName("weekly")
            @Expose
            var weekly: List<Weekly>? = null

            class Monthly {
                @SerializedName("year")
                @Expose
                var year: String? = null

                @SerializedName("month")
                @Expose
                var month: String? = null

                @SerializedName("total")
                @Expose
                var total: String? = null
            }

            class Weekly {
                @SerializedName("date")
                @Expose
                var date: String? = null

                @SerializedName("total")
                @Expose
                var total: String? = null
            }
        }

    }

}