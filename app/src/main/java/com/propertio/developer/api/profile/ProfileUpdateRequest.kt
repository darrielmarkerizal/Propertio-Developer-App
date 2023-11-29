package com.propertio.developer.api.profile

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

data class ProfileUpdateRequest(
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("province") val province: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("role") val role: String? = "developer",
    @SerializedName("picture_profile_file") var pictureProfileFile: MultipartBody.Part? = null
)
