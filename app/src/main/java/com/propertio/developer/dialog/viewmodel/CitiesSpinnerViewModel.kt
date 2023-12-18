package com.propertio.developer.dialog.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.propertio.developer.dialog.model.CitiesModel
import kotlinx.coroutines.flow.first

class CitiesSpinnerViewModel : ViewModel() {
    var citiesData = MutableLiveData<CitiesModel>()

    suspend fun getCitiesData(): CitiesModel {
        return citiesData.asFlow().first()
    }
}
