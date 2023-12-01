package com.propertio.developer.api.developer

import com.propertio.developer.api.developer.projectmanagement.ProjectDetail
import com.propertio.developer.api.developer.projectmanagement.ProjectListResponse
import com.propertio.developer.api.developer.type.GeneralTypeResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface DeveloperApi {

    @GET("v1/cms/project-management")
    fun getProjectsList() : Call<ProjectListResponse>

    @GET("v1/cms/project-management/{id}")
    fun getProjectDetail(@Path("id") id : Int) : Call<ProjectDetail>


    @GET("v1/cms/facility-type")
    fun getFacilityType() : Call<GeneralTypeResponse>

    @GET("v1/cms/infrastructure-type")
    fun getInfrastructureType() : Call<GeneralTypeResponse>

    @GET("v1/cms/property-type")
    fun getPropertyType() : Call<GeneralTypeResponse>

    @Multipart
    @POST("v1/cms/project-management/project-location")
    fun uploadProjectLocation(
        @Part("headline") headline: String,
        @Part("title") title: String,
        @Part("property_type_id") propertyTypeId: Int,
        @Part("description") description: String? = null,
        @Part("completed_at") completedAt: String? = null,
        @Part("certificate") certificate: String,
        @Part("province") province: String,
        @Part("city") city: String,
        @Part("district") district: String,
        @Part("address") address: String? = null,
        @Part("postal_code") postalCode: String? = null,
        @Part("longitude") longitude: Double,
        @Part("latitude") latitude: Double,
        @Part("immersive_siteplan") immersiveSiteplan: String? = null,
        @Part("immersive_apps") immersiveApps: String? = null,
        @Part("status") status: String? = null,
        @Part("listing_class") listingClass: String? = null,
        @Part siteplanImage : MultipartBody.Part? = null,
    )

}