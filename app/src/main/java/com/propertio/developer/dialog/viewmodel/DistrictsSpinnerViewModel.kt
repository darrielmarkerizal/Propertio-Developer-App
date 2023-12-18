package com.propertio.developer.dialog.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.propertio.developer.dialog.model.DistrictsModel
import kotlinx.coroutines.flow.first

class DistrictsSpinnerViewModel : ViewModel() {
    var districtsData = MutableLiveData<DistrictsModel>()

    suspend fun getDistrictsData(): DistrictsModel {
        return districtsData.asFlow().first()
    }
}
