package com.propertio.developer.dialog.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.propertio.developer.dialog.model.DistrictsModel

class DistrictsSpinnerViewModel : ViewModel() {
    var districtsData = MutableLiveData<DistrictsModel>()

}
