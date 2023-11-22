package com.propertio.developer.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.api.Retro
import com.propertio.developer.api.profile.ProfileApi
import com.propertio.developer.project.ProjectViewModel

class ProfileViewModelFactory(private val token: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(token) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}