package com.propertio.developer.dialog.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.propertio.developer.api.models.GeneralType

class InfrastructureTypeSpinnerViewModel : ViewModel() {
    var infrastructureTypeData = MutableLiveData<GeneralType>()
}
