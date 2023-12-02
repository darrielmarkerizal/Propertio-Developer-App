package com.propertio.developer.api.developer

import com.propertio.developer.api.developer.projectmanagement.PostStoreProjectLocationResponse
import com.propertio.developer.api.developer.projectmanagement.ProjectDetail
import com.propertio.developer.api.developer.projectmanagement.ProjectListResponse
import com.propertio.developer.api.developer.type.GeneralTypeResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
        @Part("headline") headline: RequestBody,
        @Part("title") title: RequestBody,
        @Part("property_type_id") propertyTypeId: RequestBody,
        @Part("description") description: RequestBody? = null,
        @Part("completed_at") completedAt: RequestBody? = null,
        @Part("certificate") certificate: RequestBody,
        @Part("province") province: RequestBody,
        @Part("city") city: RequestBody,
        @Part("district") district: RequestBody,
        @Part("address") address: RequestBody? = null,
        @Part("postal_code") postalCode: RequestBody? = null,
        @Part("longitude") longitude: RequestBody? = null,
        @Part("latitude") latitude: RequestBody? = null,
        @Part("immersive_siteplan") immersiveSiteplan: RequestBody? = null,
        @Part("immersive_apps") immersiveApps: RequestBody? = null,
        @Part("status") status: RequestBody? = null,
        @Part("listing_class") listingClass: RequestBody? = null,
        @Part siteplanImage : MultipartBody.Part? = null,
    ) : Call<PostStoreProjectLocationResponse>

}