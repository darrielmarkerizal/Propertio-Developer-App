package com.propertio.developer.api.common.message


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface MessageApi {

    @GET("v1/message")
    fun getAllMessage() : Call<MessageResponse>


    @GET("v1/message/{id}")
    fun getDetailMessage(@Path("id") id : Int) : Call<MessageDetailResponse>

}