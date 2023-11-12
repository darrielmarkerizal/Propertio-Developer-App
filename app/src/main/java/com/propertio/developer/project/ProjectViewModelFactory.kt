package com.propertio.developer.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ProjectViewModelFactory(private val token: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProjectViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProjectViewModel(token) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}