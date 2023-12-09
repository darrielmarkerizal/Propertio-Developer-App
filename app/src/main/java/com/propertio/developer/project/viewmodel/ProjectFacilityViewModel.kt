package com.propertio.developer.project.viewmodel

import androidx.lifecycle.ViewModel
import com.propertio.developer.api.models.GeneralType

class ProjectFacilityViewModel : ViewModel() {
    val selectedFacilities = mutableListOf<String>()
    val facilityTypeList = mutableListOf<GeneralType>()

    fun addSelectedFacilities(facilityId: String) {
        selectedFacilities.add(facilityId)
    }
}
