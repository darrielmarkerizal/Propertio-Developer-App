package com.propertio.developer.dialog.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.propertio.developer.dialog.model.ProvinceModel

class ProvinceSpinnerViewModel : ViewModel() {
    var provinceData = MutableLiveData<ProvinceModel>()
}