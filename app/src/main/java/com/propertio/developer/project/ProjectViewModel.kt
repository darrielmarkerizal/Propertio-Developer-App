package com.propertio.developer.project

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertio.developer.Database
import com.propertio.developer.Database._projectDetailList
import com.propertio.developer.Database.listProjectIds
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.projectmanagement.ProjectDetail
import com.propertio.developer.api.developer.projectmanagement.ProjectListResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProjectViewModel(private val token : String) : ViewModel() {
    init {
        viewModelScope.launch {
            fetchAllProjectIds(token)
        }
    }

    val projectList by lazy {
        Database.projectList
    }
    fun fetchProjects(token : String) {
        viewModelScope.launch {
            // Replace this with the actual code to fetch the projects
            fetchAllProjects(token)
        }
    }

    @WorkerThread
    private fun fetchAllProjects(token: String) {
        val retro = Retro(token).getRetroClientInstance().create(DeveloperApi::class.java)

        Log.d("Project", "Fetching all projects")

        listProjectIds.forEach { id ->
            retro.getProjectDetail(id).enqueue(object : Callback<ProjectDetail> {
                override fun onResponse(
                    call: Call<ProjectDetail>,
                    response: Response<ProjectDetail>
                ) {
                    if (response.isSuccessful) {
                        val projectDetailResponse: ProjectDetail? = response.body()
                        val projectDetail = projectDetailResponse?.data

                        if (projectDetail != null){

                            // Add the project detail to _projectDetailList
                            val currentList = _projectDetailList.value ?: emptyList()

                            val updatedList = if (currentList.any { it.id == projectDetail.id }) {
                                currentList.map {
                                    if (it.id == projectDetail.id && it != projectDetail) projectDetail
                                    else it
                                }
                            } else {
                                currentList + projectDetail
                            }

                            _projectDetailList.value = updatedList

                            Log.d("Project $id", "onResponse: $projectDetail")
                        }
                        else {
                            Log.e("Project $id", "onResponse: projectDetail is null")
                        }



                    }
                    else if (response.code() == 401) {
                        Log.e("Project $id", "onResponse 401: ${response.errorBody().toString()}")
                    }
                    else {
                        Log.e("Project $id", "onResponse: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ProjectDetail>, t: Throwable) {
                    Log.e("Project $id", "onFailure: ${t.message}")
                }


            })

        }
    }

    @WorkerThread
    private fun fetchAllProjectIds(token: String) {
        val retro = Retro(token).getRetroClientInstance().create(DeveloperApi::class.java)
        retro.getProjectsList().enqueue(object : Callback<ProjectListResponse> {
            override fun onResponse(
                call: Call<ProjectListResponse>,
                response: Response<ProjectListResponse>
            ) {
                if (response.isSuccessful) {
                    val projectListResponse  = response.body()
                    for (project in projectListResponse?.data!!) {
                        project.id?.let {
                            // add if id is unique
                            if (!listProjectIds.contains(it)) {
                                listProjectIds.add(it)
                            }
                            Log.d("ApiCall", "Get Project Id: $it")
                        }
                    }
                }
                else if (response.code() == 401) {
                    Log.e("ApiCall", "onResponse: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<ProjectListResponse>, t: Throwable) {
                Log.e("ApiCall", "onFailure: ${t.message}")
            }

        })
    }

}