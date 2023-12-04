package com.propertio.developer.project

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.projectmanagement.ProjectListResponse
import com.propertio.developer.database.ProjectRepository
import com.propertio.developer.database.project.ProjectTable
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProjectViewModel(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    val projectList : MutableLiveData<List<ProjectTable>> = projectRepository.listProject.asLiveData() as MutableLiveData<List<ProjectTable>>

    fun allProjectsPaginated(limit: Int, offset: Int = 0, theStatus: Boolean, filter: String = ""): MutableLiveData<List<ProjectTable>> {
        Log.d("ProjectViewModel", "allProjectsPaginated: from $limit to $offset")
        val status = if (theStatus) "active" else "not_active"

        return projectRepository.allProjectsPaginated(limit, offset, status, filter).asLiveData() as MutableLiveData<List<ProjectTable>>
    }

    suspend fun getProjectById(id: Int): ProjectTable {
        Log.d("ProjectViewModel", "getProjectById: $id")
        return projectRepository.getProjectById(id)
    }


    fun insertLocalProject(projectTable: ProjectTable) {
        viewModelScope.launch {
            projectRepository.insertProject(projectTable)
            Log.w("ProjectViewModel", "insertLocalProject: $projectTable")
        }
    }

    fun updateLocalProject(projectTable: ProjectTable) {
        viewModelScope.launch {
            projectRepository.updateProject(projectTable)
            Log.w("ProjectViewModel", "updateLocalProject: $projectTable")
        }
    }

    fun updateLocalProject(
        id: Int,
        headline: String,
        description: String,
        certificate: String,
        addressPostalCode: String,
        addressLatitude: String,
        addressLongitude: String
    ) {
        viewModelScope.launch {
            projectRepository.updateProject(
                id,
                headline,
                description,
                certificate,
                addressPostalCode,
                addressLatitude,
                addressLongitude
            )
            Log.w("ProjectViewModel", "updateLocalProject: $id")
        }
    }

    fun deleteAllLocalProjects() {
        viewModelScope.launch {
            projectRepository.deleteAll()
            Log.w("ProjectViewModel", "deleteAllLocalProjects: ")
        }
    }

    @WorkerThread
    fun fetchLiteProject(token: String) {
        viewModelScope.launch {
            Log.d("ProjectViewModel ApiCall", "fetchAllProjects")
            val retro = Retro(token).getRetroClientInstance().create(DeveloperApi::class.java)

            retro.getProjectsList().enqueue(object : Callback<ProjectListResponse> {
                override fun onResponse(
                    call: Call<ProjectListResponse>,
                    response: Response<ProjectListResponse>
                ) {
                    if (response.isSuccessful) {
                        val projectListResponse  = response.body()

                        if (projectListResponse != null) {
                            if (projectListResponse.data != null) {
                                syncToLocalDatabase(projectListResponse.data!!)
                            } else {
                                Log.e("ApiCall ProjectViewModel", "onResponse: projectListResponse.data is null")
                            }
                        } else {
                            Log.e("ApiCall ProjectViewModel", "onResponse: projectListResponse is null")
                        }

                    } else if (response.code() == 401) {
                        Log.e("ApiCall ProjectViewModel", "onResponse: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<ProjectListResponse>, t: Throwable) {
                    Log.e("ApiCall ProjectViewModel", "onFailure: ${t.message}")
                }

            })
        }

    }



    @WorkerThread
    private fun syncToLocalDatabase(simpleProjects: List<ProjectListResponse.ProjectDeveloper>) {
        viewModelScope.launch {
            for (it in simpleProjects) {
                if (it.id == null || it.id == 0) {
                    Log.wtf("ProjectViewModel", "syncToLocalDatabase: id is null")
                    continue
                }

                compareAndUpdate(it)
            }
        }



    }

    @WorkerThread
    private fun compareAndUpdate(it : ProjectListResponse.ProjectDeveloper) {
        viewModelScope.launch {
            val newProject = ProjectTable(
                id = it.id!!,
                title = it.title,
                propertyTypeName = it.propertyType?.name,
                propertyTypeIcon = it.propertyType?.icon,
                postedAt = it.postedAt,
                addressAddress = it.address?.address,
                addressDistrict = it.address?.district,
                addressCity = it.address?.city,
                addressProvince = it.address?.province,
                countUnit = it.countUnit,
                price = it.price,
                photo = it.photo,
                projectCode = it.projectCode,
                countViews = it.countViews,
                countLeads = it.countLeads,
                status = it.status,
                createdAt = it.createdAt,
            )

            if(projectRepository.isNotIdTaken(it.id!!)) {
                Log.i("ProjectViewModel", "syncToLocalDatabase Create New Project: $newProject")
                insertLocalProject(newProject)

            } else {
                val projectTable = getProjectById(it.id!!)

                if (projectTable != newProject){
                    Log.i("ProjectViewModel", "syncToLocalDatabase Update Project: from :\n $projectTable :\nto:\n $newProject")
                    updateLocalProject(newProject)
                }




            }
        }


    }

}

