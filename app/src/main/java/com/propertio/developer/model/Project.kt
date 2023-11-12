package com.propertio.developer.model

data class Project(
    val id: Int? = null,
    val title: String? = null,
    val propertyType: String? = null,
    val postedAt: String? = null,
    val countUnit: Int? = null,
    val price: String? = null,
    val projectCode: String? = null,
    val countViews: Int? = null,
    val countLeads: Int? = null,
    val status: String? = null,
    val createdAt: String? = null,
    var address: ProjectAddress? = null,
    var headline: String? = null,
    var description: String? = null,
    var certificate: String? = null,
    var completedAt: String? = null,
    var siteplanImage: String? = null,
    var immersiveSiteplan: String? = null,
    var immersiveApps: String? = null,
    var units: List<Units>? = null,
    var updatedAt: String? = null,
    var projectPhotos: List<ProjectPhoto>? = null,
    var projectVideos: List<ProjectVideo>? = null,
    var projectVirtualTours: List<ProjectVirtualTour>? = null,
    var projectDocuments: List<ProjectDocument>? = null,
    var projectFacilities: List<ProjectFacility>? = null
)

data class ProjectAddress (
    val addressAddress: String? = null,
    val addressDistrict: String? = null,
    val addressCity: String? = null,
    val addressProvince: String? = null,
    var addressPostalCode: String? = null,
    var addressLatitude: String? = null,
    var addressLongitude: String? = null,
)

data class ProjectPhoto(
    var id: Int? = null,
    var projectId: String? = null,
    var caption: String? = null,
    var filename: String? = null,
    var isCover: String? = "0",
    var createdAt: String? = null,
    var updatedAt: String? = null,
)

data class ProjectVideo(
    var id: Int? = null,
    var projectId: String? = null,
    var vendor: String? = null,
    var linkVideoURL: String? = null,
    var thumbnail: String? = null,
    var createdAt: String? = null,
    var updatedAt: String? = null,
)

data class ProjectVirtualTour(
    var id: Int? = null,
    var projectId: String? = null,
    var name: String? = null,
    var linkVirtualTourURL: String? = null,
    var createdAt: String? = null,
    var updatedAt: String? = null,
)

data class ProjectDocument(
    var id: Int? = null,
    var projectId: String? = null,
    var name: String? = null,
    var type: String? = null,
    var filename: String? = null,
    var createdAt: String? = null,
    var updatedAt: String? = null,
)

data class ProjectFacility(
    var id: Int? = null,
    var projectId: String? = null,
    var facilityTypeId: String? = null,
    var createdAt: String? = null,
    var updatedAt: String? = null,
)