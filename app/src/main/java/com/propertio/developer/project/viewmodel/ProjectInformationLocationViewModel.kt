package com.propertio.developer.project.viewmodel

import androidx.lifecycle.ViewModel
import com.propertio.developer.dialog.model.CitiesModel
import com.propertio.developer.dialog.model.DistrictsModel
import com.propertio.developer.dialog.model.ProvinceModel

class ProjectInformationLocationViewModel : ViewModel() {
    var isAlreadyUploaded = false
    var headline : String? = null
    var title : String? = null
    var propertyTypeId : Int? = null
    var description : String? = null
    var completedAt : String? = null
    var certificate : String? = null
    var province : ProvinceModel? = null
    var city : CitiesModel? = null
    var district : DistrictsModel? = null
    var address : String? = null
    var postalCode : String? = null
    var longitude : Double? = null
    var latitude : Double? = null
    var immersiveSiteplan : String? = null
    var immersiveApps : String? = null
    var status : String? = null
    var listingClass : String? = null
    var siteplanImageURL : String? = null

}