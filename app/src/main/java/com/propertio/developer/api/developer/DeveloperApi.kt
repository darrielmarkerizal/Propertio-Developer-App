package com.propertio.developer.api.developer

import com.propertio.developer.api.developer.projectmanagement.PostStoreProjectInfrastructureRequest
import com.propertio.developer.api.developer.projectmanagement.PostStoreProjectLocationResponse
import com.propertio.developer.api.developer.projectmanagement.PostStoreProjectPhotoResponse
import com.propertio.developer.api.developer.projectmanagement.ProjectDetail
import com.propertio.developer.api.developer.projectmanagement.ProjectListResponse
import com.propertio.developer.api.developer.projectmanagement.UpdateProjectResponse
import com.propertio.developer.api.developer.type.GeneralTypeResponse
import com.propertio.developer.model.Caption
import com.propertio.developer.model.StatusProject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @DELETE("v1/cms/project-management/project-photos/{id}")
    fun deleteProjectPhoto(@Path("id") id : Int) : Call<UpdateProjectResponse>

    @Multipart
    @POST("v1/cms/project-management/project-photos")
    fun uploadMultipleFiles(
        @Part("project_id") projectId: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): Call<PostStoreProjectPhotoResponse>

    @Multipart
    @POST("v1/cms/project-management/project-other-media")
    fun uploadAnotherProjectMedia(
        @Part("project_id") projectId: RequestBody,
        @Part("video_link") videoLink: RequestBody? = null,
        @Part("virtual_tour_name") virtualTourName: RequestBody? = null,
        @Part("virtual_tour_link") virtualTourLink: RequestBody? = null,
        @Part document_file: MultipartBody.Part? = null
    ): Call<UpdateProjectResponse>

    @POST("v1/cms/project-management/project-photos/{id}/cover?_method=PUT")
    fun updateCoverProjectPhoto(@Path("id") id : Int) : Call<UpdateProjectResponse>


    @PUT("v1/cms/project-management/project-photos/{id}/caption")
    fun updateCaptionProjectPhoto(
            @Path("id") id: Int,
            @Body caption : Caption
    ) : Call<UpdateProjectResponse>


    @POST("v1/cms/project-management/{id}/unit")
    fun createUnit(
        @Path("id") id : Int,
        @Body unit : RequestBody
    ) : Call<DefaultResponse>


    @FormUrlEncoded
    @POST("v1/cms/project-management/project-facilities")
    fun sendFacilities(
        @Field("project_id") projectId : String,
        @FieldMap facilities: Map<String, String>
    ): Call<UpdateProjectResponse>

    @POST("v1/cms/project-management/project-infrastructure")
    fun sendInfrastructure(
        @Body infrastructure: PostStoreProjectInfrastructureRequest
    ): Call<UpdateProjectResponse>

    @DELETE("v1/cms/project-management/project-infrastructure/{id}")
    fun deleteProjectInfrastructure(@Path("id") id : Int) : Call<UpdateProjectResponse>

    @POST("v1/cms/project-management/project-publish/{id}")
    fun publishProject(@Path("id") id : Int) : Call<UpdateProjectResponse>

    @POST("v1/cms/project-management/project-repost/{id}")
    fun repostProject(@Path("id") id : Int) : Call<UpdateProjectResponse>

    @POST("v1/cms/project-management/project-update-status/{id}")
    fun updateProjectStatus(
        @Path("id") id : Int,
        @Body status : StatusProject
    ) : Call<UpdateProjectResponse>

}

