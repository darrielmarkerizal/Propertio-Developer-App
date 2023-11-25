package com.propertio.developer.dialog.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.propertio.developer.dialog.model.PhoneCode

class PhoneCodeViewModel : ViewModel(){
    var phoneCodeData = MutableLiveData<PhoneCode>()
}