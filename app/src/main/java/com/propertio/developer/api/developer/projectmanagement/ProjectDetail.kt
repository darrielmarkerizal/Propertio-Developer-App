package com.propertio.developer.api.developer.projectmanagement

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.propertio.developer.api.models.DefaultResponse
import com.propertio.developer.api.models.ProjectMinimum
import java.io.Serial

class ProjectDetail : DefaultResponse() {

    @SerializedName("data")
    @Expose
    var data: ProjectDeveloper? = null

    class ProjectDeveloper : ProjectMinimum(){
        @SerializedName("headline")
        @Expose
        var headline: String? = null

        @SerializedName("address")
        @Expose
        var address: ProjectAddress? = null

        @SerializedName("description")
        @Expose
        var description: String? = null

        @SerializedName("certificate")
        @Expose
        var certificate: String? = null

        @SerializedName("completed_at")
        @Expose
        var completedAt: String? = null

        @SerializedName("siteplan_image")
        @Expose
        var siteplanImage: String? = null

        @SerializedName("immersive_siteplan")
        @Expose
        var immersiveSiteplan: String? = null

        @SerializedName("immersive_apps")
        @Expose
        var immersiveApps: String? = null

        @SerializedName("units")
        @Expose
        var units: List<ProjectUnit>? = null

        @SerializedName("updated_at")
        @Expose
        var updatedAt: String? = null

        @SerializedName("project_photos")
        @Expose
        var projectPhotos: List<ProjectPhoto>? = null

        @SerializedName("project_video")
        @Expose
        var projectVideos: ProjectVideo? = null

        @SerializedName("project_virtual_tours")
        @Expose
        var projectVirtualTours: ProjectVirtualTour? = null

        @SerializedName("project_documents")
        @Expose
        var projectDocuments: List<ProjectDocument>? = null

        @SerializedName("project_facilities")
        @Expose
        var projectFacilities: List<ProjectFacility>? = null

        @SerializedName("project_infrastructures")
        @Expose
        var projectInfrastructures: List<ProjectInfrastructure>? = null

        class ProjectInfrastructure {
            @SerializedName("id")
            @Expose
            var id: Int? = null

            @SerializedName("project_id")
            @Expose
            var projectId: String? = null

            @SerializedName("infrastructure_type_id")
            @Expose
            var infrastructureTypeId: String? = null

            @SerializedName("name")
            @Expose
            var name: String? = null

            @SerializedName("distance")
            @Expose
            var distance: String? = null
        }

        class ProjectFacility {
            @SerializedName("id")
            @Expose
            var id: Int? = null

            @SerializedName("project_id")
            @Expose
            var projectId: String? = null

            @SerializedName("facility_type_id")
            @Expose
            var facilityTypeId: String? = null

            @SerializedName("created_at")
            @Expose
            var createdAt: String? = null

            @SerializedName("updated_at")
            @Expose
            var updatedAt: String? = null
        }


        class ProjectDocument {
            @SerializedName("id")
            @Expose
            var id: Int? = null

            @SerializedName("project_id")
            @Expose
            var projectId: String? = null

            @SerializedName("name")
            @Expose
            var name: String? = null

            @SerializedName("type")
            @Expose
            var type: String? = null

            @SerializedName("filename")
            @Expose
            var filename: String? = null

            @SerializedName("created_at")
            @Expose
            var createdAt: String? = null

            @SerializedName("updated_at")
            @Expose
            var updatedAt: String? = null
        }


        class ProjectVirtualTour {
            @SerializedName("id")
            @Expose
            var id: Int? = null

            @SerializedName("project_id")
            @Expose
            var projectId: String? = null

            @SerializedName("name")
            @Expose
            var name: String? = null

            @SerializedName("link")
            @Expose
            var linkVirtualTourURL: String? = null

            @SerializedName("created_at")
            @Expose
            var createdAt: String? = null

            @SerializedName("updated_at")
            @Expose
            var updatedAt: String? = null
        }


        class ProjectVideo {
            @SerializedName("id")
            @Expose
            var id: Int? = null

            @SerializedName("project_id")
            @Expose
            var projectId: String? = null

            @SerializedName("vendor")
            @Expose
            var vendor: String? = null

            @SerializedName("link")
            @Expose
            var linkVideoURL: String? = null

            @SerializedName("thumbnail")
            @Expose
            var thumbnail: String? = null

            @SerializedName("created_at")
            @Expose
            var createdAt: String? = null

            @SerializedName("updated_at")
            @Expose
            var updatedAt: String? = null

        }


        class ProjectPhoto {
            @SerializedName("id")
            @Expose
            var id: Int? = null

            @SerializedName("project_id")
            @Expose
            var projectId: String? = null

            @SerializedName("caption")
            @Expose
            var caption: String? = null

            @SerializedName("filename")
            @Expose
            var filename: String? = null

            @SerializedName("is_cover")
            @Expose
            var isCover: String? = "0"

            @SerializedName("created_at")
            @Expose
            var createdAt: String? = null

            @SerializedName("updated_at")
            @Expose
            var updatedAt: String? = null
        }


        class ProjectUnit {
            @SerializedName("id")
            @Expose
            var id: Int? = null

            @SerializedName("title")
            @Expose
            var title: String? = null

            @SerializedName("photo")
            @Expose
            var photoURL: String? = null

            @SerializedName("price")
            @Expose
            var price: String? = null

            @SerializedName("bedroom")
            @Expose
            var bedroom: String? = null

            @SerializedName("bathroom")
            @Expose
            var bathroom: String? = null

            @SerializedName("surface_area")
            @Expose
            var surfaceArea: String? = null

            @SerializedName("building_area")
            @Expose
            var buildingArea: String? = null

            @SerializedName("stock")
            @Expose
            var stock: String? = null

        }

        class  ProjectAddress {
            @SerializedName("address")
            @Expose
            var address: String? = null

            @SerializedName("district")
            @Expose
            var district: String? = null

            @SerializedName("city")
            @Expose
            var city: String? = null

            @SerializedName("province")
            @Expose
            var province: String? = null

            @SerializedName("postal_code")
            @Expose
            var postalCode: String? = null

            @SerializedName("latitude")
            @Expose
            var latitude: String? = null

            @SerializedName("longitude")
            @Expose
            var longitude: String? = null
        }

    }



}