package com.propertio.developer.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.database.PropertiORepository
import com.propertio.developer.project.list.FacilityAndInfrastructureTypeViewModel

class FacilityViewModelFactory(
    private val propertiORepository: PropertiORepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FacilityAndInfrastructureTypeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FacilityAndInfrastructureTypeViewModel(propertiORepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
