package com.propertio.developer


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.propertio.developer.api.developer.projectmanagement.ProjectDetail
import com.propertio.developer.model.Project
import com.propertio.developer.model.ProjectAddress
import com.propertio.developer.model.ProjectDocument
import com.propertio.developer.model.ProjectFacility
import com.propertio.developer.model.ProjectPhoto
import com.propertio.developer.model.ProjectVideo
import com.propertio.developer.model.ProjectVirtualTour
import com.propertio.developer.model.Units
import com.propertio.developer.project.ProjectViewModel

object Database {

    val listProjectIds by lazy { ArrayList<Int>() }

    lateinit var projectViewModel : ProjectViewModel

    val _projectDetailList = MutableLiveData<List<ProjectDetail.ProjectDeveloper>>()
    val projectList: LiveData<List<Project>> = _projectDetailList.map { projectDetailList ->
        projectDetailList?.map { projectDetail ->
            Project(
                id = projectDetail.id,
                title = projectDetail.title,
                propertyType = projectDetail.propertyType,
                postedAt = projectDetail.postedAt,
                countUnit= projectDetail.countUnit,
                price = projectDetail.price,
                projectCode = projectDetail.projectCode,
                countViews = projectDetail.countViews,
                countLeads = projectDetail.countLeads,
                status = projectDetail.status,
                createdAt = projectDetail.createdAt,
                address = projectDetail.address?.let {
                    ProjectAddress(
                        it.address,
                        it.district,
                        it.city,
                        it.province,
                        it.postalCode,
                        it.latitude,
                        it.longitude
                    )
                },
                headline = projectDetail.headline,
                description = projectDetail.description,
                certificate = projectDetail.certificate,
                completedAt = projectDetail.completedAt,
                siteplanImage = projectDetail.siteplanImage,
                immersiveSiteplan = projectDetail.immersiveSiteplan,
                immersiveApps = projectDetail.immersiveApps,
                units = projectDetail.units?.map {
                    Units(
                        it.id,
                        it.title,
                        it.photoURL,
                        it.price,
                        it.bedroom,
                        it.bathroom,
                        it.surfaceArea,
                        it.buildingArea,
                        it.stock
                    )
                },
                updatedAt = projectDetail.updatedAt,
                projectPhotos = projectDetail.projectPhotos?.map {
                    ProjectPhoto(
                        it.id,
                        it.projectId,
                        it.caption,
                        it.filename,
                        it.isCover,
                        it.createdAt,
                        it.updatedAt
                    )
                },
                projectVideos = projectDetail.projectVideos?.map {
                    ProjectVideo(
                        it.id,
                        it.projectId,
                        it.vendor,
                        it.linkVideoURL,
                        it.thumbnail,
                        it.createdAt,
                        it.updatedAt
                    )

                },
                projectVirtualTours = projectDetail.projectVirtualTours?.map{
                    ProjectVirtualTour(
                        it.id,
                        it.projectId,
                        it.name,
                        it.linkVirtualTourURL,
                        it.createdAt,
                        it.updatedAt
                    )

                },
                projectDocuments = projectDetail.projectDocuments?.map {
                    ProjectDocument(
                        it.id,
                        it.projectId,
                        it.name,
                        it.type,
                        it.filename,
                        it.createdAt,
                        it.updatedAt
                    )


                },
                projectFacilities = projectDetail.projectFacilities?.map{
                    ProjectFacility(
                        it.id,
                        it.projectId,
                        it.facilityTypeId,
                        it.createdAt,
                        it.updatedAt
                    )
                }
            )
        } ?: emptyList()


    }

}