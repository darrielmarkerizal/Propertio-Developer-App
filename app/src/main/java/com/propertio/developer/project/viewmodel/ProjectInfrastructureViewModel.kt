package com.propertio.developer.project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.propertio.developer.api.developer.projectmanagement.ProjectDetail

class ProjectInfrastructureViewModel : ViewModel() {
    val projectInfrastructureList = MutableLiveData<List<ProjectDetail.ProjectDeveloper.ProjectInfrastructure>>()

}
