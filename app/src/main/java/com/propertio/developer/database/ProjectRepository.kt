package com.propertio.developer.database

import android.util.Log
import androidx.lifecycle.asLiveData
import com.propertio.developer.database.project.ProjectTable
import com.propertio.developer.database.project.ProjectTableDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ProjectRepository(
    private val projectTableDao: ProjectTableDao,
) {

    val listProject :Flow<List<ProjectTable>> = projectTableDao.allProjects

    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    fun allProjectsPaginated(limit: Int, offset: Int): Flow<List<ProjectTable>> {
        Log.d("Repository", "allProjectsPaginated: from $limit to $offset")
        return projectTableDao.allProjectsPaginated(limit, offset)
    }

    suspend fun getProjectById(id: Int): ProjectTable {
        Log.d("Repository", "getProjectById: $id")
        return projectTableDao.getProjectById(id).filterNotNull().first()
    }

    fun isNotIdTaken(id: Int): Boolean {
        Log.d("Repository", "isIdTaken: $id")
        return projectTableDao.checkId(id).asLiveData().value?.isEmpty() ?: true
    }

    // Methods

    fun insertProject(projectTable: ProjectTable) {
        executorService.execute {
            projectTableDao.insert(projectTable)
            Log.w("Repository", "insertProject: $projectTable")
        }
    }

    fun updateProject(projectTable: ProjectTable) {
        executorService.execute {
            projectTableDao.update(projectTable)
            Log.w("Repository", "updateProject: $projectTable")
        }
    }

    fun updateProject(
        id: Int,
        headline: String,
        description: String,
        certificate: String,
        addressPostalCode: String,
        addressLatitude: String,
        addressLongitude: String
    ) {
        executorService.execute {
            projectTableDao.updateProject(
                id,
                headline,
                description,
                certificate,
                addressPostalCode,
                addressLatitude,
                addressLongitude
            )
            Log.w("Repository", "updateProject: $id")
        }
    }

    fun deleteAll() {
        executorService.execute {
            projectTableDao.deleteAll()
            Log.w("Repository", "deleteAll: ")
        }
    }





}