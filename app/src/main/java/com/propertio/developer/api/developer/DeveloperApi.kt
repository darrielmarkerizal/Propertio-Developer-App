package com.propertio.developer.api.developer

import com.propertio.developer.api.developer.projectmanagement.PostStoreProjectInfrastructureRequest
import com.propertio.developer.api.developer.projectmanagement.PostStoreProjectLocationResponse
import com.propertio.developer.api.developer.projectmanagement.PostStoreProjectPhotoResponse
import com.propertio.developer.api.developer.projectmanagement.ProjectDetail
import com.propertio.developer.api.developer.projectmanagement.ProjectListResponse
import com.propertio.developer.api.developer.projectmanagement.UpdateProjectResponse
import com.propertio.developer.api.developer.type.GeneralTypeResponse
import com.propertio.developer.api.developer.unitmanagement.PostStoreUnitPhotoResponse
import com.propertio.developer.api.developer.unitmanagement.PostUnitResponse
import com.propertio.developer.api.developer.unitmanagement.UnitDetailResponse
import com.propertio.developer.api.developer.unitmanagement.UnitRequest
import com.propertio.developer.model.Caption
import com.propertio.developer.model.StatusProject
import com.propertio.developer.unit_management.UpdateUnitResponse
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

    @Multipart
    @POST("v1/cms/project-management/project-location/{id}?_method=PUT")
    fun updateProjectLocation(
        @Path("id") id : Int,
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
    fun deleteProjectPhoto(
        @Path("id") id : Int
    ) : Call<UpdateProjectResponse>

    @DELETE("v1/cms/project-management/{project_id}/unit-photo/{id}")
    fun deleteUnitPhoto(
        @Path("project_id") project : String,
        @Path("id") id : Int
    ) : Call<UpdateUnitResponse>

    @Multipart
    @POST("v1/cms/project-management/project-photos")
    fun uploadMultipleFiles(
        @Part("project_id") projectId: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): Call<PostStoreProjectPhotoResponse>


    @DELETE("v1/cms/project-management/project-other-media/{document_id}/document")
    fun deleteProjectDocument(
        @Path("document_id") documentId : Int
    ) : Call<UpdateProjectResponse>

    @Multipart
    @POST("v1/cms/project-management/{project_id}/unit-photo")
    fun uploadUnitPhoto(
        @Path("project_id") projectId : String,
        @Part("unit_id") unitId: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): Call<PostStoreUnitPhotoResponse>

    @Multipart
    @POST("v1/cms/project-management/{project_id}/unit-plan-image")
    fun uploadUnitPlanImage(
        @Path("project_id") projectId : String,
        @Part("unit_id") unitId: RequestBody,
        @Part ("type") type: String? = null,
        @Part files: List<MultipartBody.Part>
    ): Call<PostStoreUnitPhotoResponse>



    @Multipart
    @POST("v1/cms/project-management/project-other-media")
    fun uploadAnotherProjectMedia(
        @Part("project_id") projectId: RequestBody,
        @Part("video_link") videoLink: RequestBody? = null,
        @Part("virtual_tour_name") virtualTourName: RequestBody? = null,
        @Part("virtual_tour_link") virtualTourLink: RequestBody? = null,
        @Part document_file: MultipartBody.Part? = null
    ): Call<UpdateProjectResponse>

    @Multipart
    @POST("v1/cms/project-management/{project_id}/unit-other-media")
    fun uploadAnotherUnitMedia(
        @Path("project_id") projectId : String,
        @Part("unit_id") unitId: RequestBody,
        @Part("video_link") videoLink: RequestBody? = null,
        @Part("virtual_tour_name") virtualTourName: RequestBody? = null,
        @Part("virtual_tour_link") virtualTourLink: RequestBody? = null,
        @Part("model_link") modelLink: RequestBody? = null,
        @Part document_file: MultipartBody.Part? = null
    ): Call<UpdateUnitResponse>

    @POST("v1/cms/project-management/project-photos/{id}/cover?_method=PUT")
    fun updateCoverProjectPhoto(
        @Path("id") id : Int
    ) : Call<UpdateProjectResponse>

    @POST("v1/cms/project-management/{project_id}/unit-photo/{id}/cover?_method=PUT")
    fun updateCoverUnitPhoto(
        @Path("project_id") projectId : String,
        @Path("id") id : Int
    ) : Call<UpdateUnitResponse>


    @PUT("v1/cms/project-management/project-photos/{id}/caption")
    fun updateCaptionProjectPhoto(
            @Path("id") id: Int,
            @Body caption : Caption
    ) : Call<UpdateProjectResponse>

    @PUT("v1/cms/project-management/{project_id}/unit-photo/{id}/caption?_method=PUT")
    fun updateCaptionUnitPhoto(
        @Path("project_id") projectId: String,
        @Path("id") id : Int,
        @Body caption : Caption
    ) : Call<UpdateUnitResponse>


    @POST("v1/cms/project-management/{id}/unit")
    fun postStoreUnit(
        @Path("id") id : Int,
        @Body unitRequest: UnitRequest
    ) : Call<PostUnitResponse>
  
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


    @GET("v1/cms/project-management/{projectId}/unit/{unitId}")
    fun getUnitDetail(
        @Path("projectId") projectId : Int,
        @Path("unitId") unitId : Int
    ) : Call<UnitDetailResponse>
}