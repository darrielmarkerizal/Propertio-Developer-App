package com.propertio.developer.model

import com.propertio.developer.api.developer.unitmanagement.UnitDetailResponse

data class Units(
    var id: Int? = null,
    var projectId: String? = null,
    var unitCode: String? = null,
    var title: String? = null,
    var description: String? = null,
    var surfaceArea: String? = "0",
    var buildingArea: String? = "0",
    var order: String? = "0",
    var stock: String? = "0",
    var floor: String? = "0",
    var bedroom: String? = "0",
    var bathroom: String? = "0",
    var garage: String? = "0",
    var price: String? = "0",
    var powerSupply: String? = "0",
    var waterSupply: String? = "0",
    var waterType: String? = null,
    var interior: String? = null,
    var roadAccess: String? = null,
    var createdAt: String? = null,
    var updatedAt: String? = null,

    var unitPhotos: List<UnitPhoto>? = null,
    var unitModel: String? = null,
    var unitVirtualTour: List<*>? = null,
    var unitVideo: String? = null,
    var unitDocuments: List<*>? = null,
)

data class UnitPhoto(
    var id: Int? = null,
    var unitId: String? = null,
    var caption: String? = null,
    var filename: String? = null,
    var isCover: String? = null,
    var type: String? = null,
    var createdAt: String? = null,
    var updatedAt: String? = null,
)

data class UnitVirtualTour(
    var id: Int? = null,
    var unitId: String? = null,
    var name: String? = null,
    var linkVirtualTourURL: String? = null,
    var createdAt: String? = null,
    var updatedAt: String? = null,
)