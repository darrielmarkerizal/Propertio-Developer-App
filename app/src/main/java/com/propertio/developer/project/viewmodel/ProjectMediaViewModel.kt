package com.propertio.developer.project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.propertio.developer.model.LitePhotosModel
import com.propertio.developer.model.ProjectDocument
import com.propertio.developer.project.list.UnggahFotoAdapter
import kotlinx.coroutines.flow.first

class ProjectMediaViewModel : ViewModel() {
    var projectPhotos  : MutableLiveData<List<LitePhotosModel>> = MutableLiveData(emptyList())
    var videoLink : String? = null
    var virtualTourName : String? = null
    var virtualTourLink : String? = null
    var document : ProjectDocument? = null
    var isDocumentNotEdited : Boolean = false



    suspend fun isProjectPhotosEmpty() : Boolean {
        val photos = projectPhotos.asFlow().first()
        return photos.isEmpty()
    }


    suspend fun getCoverPhoto() : Pair<LitePhotosModel?, Int?> {
        val photos = projectPhotos.asFlow().first()
        val index = photos.indexOfFirst { it.isCover == 1 }
        return Pair(photos.getOrNull(index), index)
    }

    fun add(
        projectPhotos  : List<LitePhotosModel> = emptyList(),
        videoLink : String? = null,
        virtualTourName : String? = null,
        virtualTourLink : String? = null,
        document : ProjectDocument? = null
    ){
        this.projectPhotos.postValue(projectPhotos)
        this.videoLink = videoLink
        this.virtualTourName = virtualTourName
        this.virtualTourLink = virtualTourLink
        this.document = document
    }


}

