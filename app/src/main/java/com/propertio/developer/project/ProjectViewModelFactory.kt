package com.propertio.developer.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.database.PropertiORepository

class ProjectViewModelFactory(
    private val propertiORepository: PropertiORepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProjectViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProjectViewModel(propertiORepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}