package com.propertio.developer.dialog.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.propertio.developer.dialog.model.CitiesModel

class CitiesSpinnerViewModel : ViewModel() {
    var citiesData = MutableLiveData<CitiesModel>()
}
