package com.propertio.developer.api.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProfileResponse {

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: ProfileData? = null

    class ProfileData {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("account_id")
        @Expose
        var accountId: String? = null

        @SerializedName("email")
        @Expose
        var email: String? = null

        @SerializedName("status")
        @Expose
        var status: String? = null

        @SerializedName("role")
        @Expose
        var role: String? = null

        @SerializedName("user_data")
        @Expose
        var userData: UserData? = null

        @SerializedName("user_addition")
        @Expose
        var userAddition: UserAddition? = null

        class UserData {
            @SerializedName("full_name")
            @Expose
            var fullName: String? = null

            @SerializedName("phone")
            @Expose
            var phone: String? = null

            @SerializedName("address")
            @Expose
            var address: String? = null

            @SerializedName("city")
            @Expose
            var city: String? = null

            @SerializedName("province")
            @Expose
            var province: String? = null

            @SerializedName("picture_profile")
            @Expose
            var pictureProfile: String? = null
        }

        class UserAddition {
            @SerializedName("description")
            @Expose
            var description: String? = null

            @SerializedName("website")
            @Expose
            var website: String? = null

            @SerializedName("adcredits")
            @Expose
            var adcredits: String? = null
        }
    }

    data class Province(
        @SerializedName("id")
        val id: String,

        @SerializedName("name")
        val name: String,
    )

    data class City(
        @SerializedName("id")
        val id: String,

        @SerializedName("province_id")
        val provinceId: String,

        @SerializedName("name")
        val name: String
    )
}