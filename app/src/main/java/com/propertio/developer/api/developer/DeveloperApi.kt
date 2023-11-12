package com.propertio.developer.api.developer

import com.propertio.developer.api.developer.projectmanagement.ProjectDetail
import com.propertio.developer.api.developer.projectmanagement.ProjectListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface DeveloperApi {

    @GET("v1/cms/project-management")
    fun getProjectsList() : Call<ProjectListResponse>

    @GET("v1/cms/project-management/{id}")
    fun getProjectDetail(@Path("id") id : Int) : Call<ProjectDetail>


}