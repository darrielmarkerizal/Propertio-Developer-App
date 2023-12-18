package com.propertio.developer.dialog.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.propertio.developer.dialog.model.ProvinceModel
import kotlinx.coroutines.flow.first

class ProvinceSpinnerViewModel : ViewModel() {
    var provinceData = MutableLiveData<ProvinceModel>()

    suspend fun getProvinceData() : ProvinceModel {
        return provinceData.asFlow().first()
    }
}