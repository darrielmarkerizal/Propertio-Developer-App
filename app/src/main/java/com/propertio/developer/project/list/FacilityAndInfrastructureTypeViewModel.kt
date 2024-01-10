package com.propertio.developer.project.list

import androidx.lifecycle.ViewModel
import com.propertio.developer.database.PropertiORepository
import com.propertio.developer.database.facility.FacilityTable
import com.propertio.developer.database.infrastructure.InfrastructureTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FacilityAndInfrastructureTypeViewModel(
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

    suspend fun getAllInfrastructureType() : List<InfrastructureTable> {
        return withContext(Dispatchers.IO) {
            propertiORepository.getAllInfrastructure()
        }
    }

    suspend fun getAllInfrastructureByDescription(description : String) : List<InfrastructureTable> {
        return withContext(Dispatchers.IO) {
            propertiORepository.getAllInfrastructureByDescription(description)
        }
    }

    suspend fun searchInfrastructure(search : String) : List<InfrastructureTable> {
        return withContext(Dispatchers.IO) {
            propertiORepository.searchInfrastructure(search)
        }
    }

    suspend fun getInfrastructureById(id: Int): InfrastructureTable? {
        return withContext(Dispatchers.IO) {
            propertiORepository.getInfrastructureById(id)
        }
    }

    suspend fun insertInfrastructure(infrastructureTables: List<InfrastructureTable>) {
        withContext(Dispatchers.IO) {
            propertiORepository.insertInfrastructure(infrastructureTables)
        }

    }

    /**
     * Insert infrastructure to database
     * @param id Int
     * @param name String
     * @param description String
     * @param icon String
     */
    suspend fun insertInfrastructure(
        id: Int,
        name: String,
        description: String,
        icon: String,
    ) {
        withContext(Dispatchers.IO) {
            propertiORepository.insertInfrastructure(
                id = id,
                name = name,
                description = description,
                icon = icon,
            )
        }

    }

    /**
     * Update infrastructure to database
     * @param id Int
     * @param isSelected Boolean
     */
    suspend fun updateSelectedInfrastructure(id: Int, isSelected: Boolean = false) {
        return withContext(Dispatchers.IO) {
            propertiORepository.updateSelectedInfrastructure(id, isSelected)
        }
    }

    suspend fun deleteAllInfrastructure() {
        return withContext(Dispatchers.IO) {
            propertiORepository.deleteAllInfrastructure()
        }
    }



}