package com.propertio.developer.project.list

import androidx.lifecycle.ViewModel
import com.propertio.developer.database.PropertiORepository
import com.propertio.developer.database.facility.FacilityTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FacilityTypeViewModel(
    private val propertiORepository: PropertiORepository
) : ViewModel() {

    suspend fun getAllFacilityType() : List<FacilityTable> {
        return withContext(Dispatchers.IO) {
            propertiORepository.getAllFacility()
        }
    }

    suspend fun getAllFacilityByCategory(category : String) : List<FacilityTable> {
        return withContext(Dispatchers.IO) {
            propertiORepository.getAllFacilityByCategory(category)
        }
    }

    suspend fun searchFacility(search : String) : List<FacilityTable> {
        return withContext(Dispatchers.IO) {
            propertiORepository.searchFacility(search)
        }
    }

    /**
     * Insert facility to database
     * @param id Int
     * @param name String
     * @param category String
     * @param icon String
     */
    suspend fun insertFacility(
        id: Int,
        name: String,
        category: String,
        icon: String,
    ) {
        withContext(Dispatchers.IO) {
            propertiORepository.insertFacility(
                id = id,
                name = name,
                category = category,
                icon = icon,
            )
        }
    }

    /**
     * Update facility to database
     * @param id Int
     * @param isSelected Boolean
     */
    suspend fun updateSelectedFacility(id: Int, isSelected: Boolean = false) {
        return withContext(Dispatchers.IO) {
            propertiORepository.updateSelectedFacility(id, isSelected)
        }
    }

    suspend fun deleteAllFacility() {
        return withContext(Dispatchers.IO) {
            propertiORepository.deleteAllFacility()
        }
    }

}