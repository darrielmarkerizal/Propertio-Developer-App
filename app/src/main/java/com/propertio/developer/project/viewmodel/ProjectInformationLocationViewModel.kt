package com.propertio.developer.project.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.propertio.developer.dialog.model.CitiesModel
import com.propertio.developer.dialog.model.DistrictsModel
import com.propertio.developer.dialog.model.ProvinceModel

class ProjectInformationLocationViewModel : ViewModel() {
    var isUploaded = false
    var isAlreadyUploaded : MutableLiveData<Boolean> = MutableLiveData(false)
    var headline : String? = null
    var title : String? = null
    var propertyTypeId : Int? = null
    var propertyTypeName : String? = null
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

    val selectedLocation : MutableLiveData<Pair<Double, Double>> = MutableLiveData()


    // NOTE : Jangan pakai selain untuk edit form
    var isAddressNotEdited : Boolean = false
    var savedProvince : ProvinceModel? = null
    var savedCity : CitiesModel? = null
    var savedDistrict : DistrictsModel? = null


    fun printLog(msg : String = "") {
        Log.d( "ViewModel",
            "loadTextData $msg:" +
                    "\n headline     : $headline " +
                    "\n title        : $title " +
                    "\n description  : $description " +
                    "\n complete_at  : $completedAt " +
                    "\n property_type: $propertyTypeId $propertyTypeName " +
                    "\n certificate  : $certificate " +
                    "\n province     : ${province?.provinceId} ${province?.provinceName} <- saved : $savedProvince" +
                    "\n city         : ${city?.citiesId} ${city?.citiesName} <- saved : $savedCity" +
                    "\n district     : ${district?.districtsId} ${district?.districtsName} <- saved : $savedDistrict" +
                    "\n address      : $address " +
                    "\n postal_code  : $postalCode " +
                    "\n longitude    : $longitude " +
                    "\n latitude     : $latitude "
        )

    }


    fun clear() {
        isAlreadyUploaded.value = false
        headline = null
        title = null
        propertyTypeId = null
        propertyTypeName = null
        description = null
        completedAt = null
        certificate = null
        province = null
        city = null
        district = null
        address = null
        postalCode = null
        longitude = null
        latitude = null
        immersiveSiteplan = null
        immersiveApps = null
        status = null
        listingClass = null
        siteplanImageURL = null

        isAddressNotEdited = false
        savedProvince = null
        savedCity = null
        savedDistrict = null
        isUploaded = false
    }

    fun add(
        isAlreadyUploaded : Boolean= true,
        headline : String?,
        title : String?,
        propertyTypeName : String?,
        description : String?,
        completedAt : String?,
        certificate : String?,
        address : String?,
        postalCode : String?,
        longitude : Double?,
        latitude : Double?,
        immersiveSiteplan : String?,
        immersiveApps : String?,
        status : String?,
        siteplanImageURL : String?,
    ) {
        this.isAlreadyUploaded.postValue(isAlreadyUploaded)
        this.headline = headline
        this.title = title
        this.propertyTypeName = propertyTypeName
        this.description = description
        this.completedAt = completedAt
        this.certificate = certificate
        this.province = province
        this.city = city
        this.district = district
        this.address = address
        this.postalCode = postalCode
        this.longitude = longitude
        this.latitude = latitude
        this.immersiveSiteplan = immersiveSiteplan
        this.immersiveApps = immersiveApps
        this.status = status
        this.siteplanImageURL = siteplanImageURL
    }

    fun addAdresss(
        province : ProvinceModel? = this.province,
        city : CitiesModel? = this.city,
        district : DistrictsModel? = this.district,
    ) {
        this.savedProvince = province
        this.province = province
        this.savedCity = city
        this.city = city
        this.savedDistrict = district
        this.district = district
    }
}