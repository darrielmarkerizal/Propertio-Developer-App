package com.propertio.developer.dialog.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.propertio.developer.database.MasterData

class WaterTypeSpinnerViewModel : ViewModel() {
    var waterTypeData = MutableLiveData<MasterData>()
}