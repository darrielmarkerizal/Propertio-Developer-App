package com.propertio.developer.api.common.dashboard

import retrofit2.Call
import retrofit2.http.GET

interface DashboardApi {

    @GET("v1/dashboard")
    fun getDashboard() : Call<DashboardResponse>
}