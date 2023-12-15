package com.propertio.developer.unit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.propertio.developer.model.LitePhotosModel
import com.propertio.developer.model.UnitDocument
import kotlinx.coroutines.flow.first

class UnitMediaViewModel : ViewModel() {
    var unitPhoto : MutableLiveData<List<LitePhotosModel>> = MutableLiveData(emptyList())
    var unitDenah : MutableLiveData<List<LitePhotosModel>> = MutableLiveData(emptyList())
    var videoLink : String? = null
    var virtualTourName : String? = null
    var virtualTourLink : String? = null
    var document : UnitDocument? = null
    var isDocumentNotEdited : Boolean = false

    suspend fun isUnitPhotosEmpty() : Boolean {
        val photos = unitPhoto.asFlow().first()
        return photos.isEmpty()
    }

    suspend fun getCoverPhoto() : Pair<LitePhotosModel?, Int?> {
        val photos = unitPhoto.asFlow().first()
        val index = photos.indexOfFirst { it.isCover == 1 }
        return Pair(photos.getOrNull(index), index)
    }

    fun add(
        unitPhoto : List<LitePhotosModel> = emptyList(),
        unitDenah : List<LitePhotosModel> = emptyList(),
        videoLink : String? = null,
        virtualTourName : String? = null,
        virtualTourLink : String? = null,
        document : UnitDocument? = null
    ){
        this.unitPhoto.postValue(unitPhoto)
        this.unitDenah.postValue(unitDenah)
        this.videoLink = videoLink
        this.virtualTourName = virtualTourName
        this.virtualTourLink = virtualTourLink
        this.document = document
    }
}