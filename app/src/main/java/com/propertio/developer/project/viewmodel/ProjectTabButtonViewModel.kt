package com.propertio.developer.project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import kotlinx.coroutines.flow.first

class ProjectTabButtonViewModel : ViewModel() {
    val tabActiveStatus : MutableLiveData<Boolean> = MutableLiveData(true)


    suspend fun isActive() : Boolean {
        return tabActiveStatus.asFlow().first()
    }

}
