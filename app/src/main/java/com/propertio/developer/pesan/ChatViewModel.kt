package com.propertio.developer.pesan

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.propertio.developer.api.Retro
import com.propertio.developer.api.common.message.MessageApi
import com.propertio.developer.api.common.message.MessageResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.lifecycle.map



class ChatViewModel(token: String?) : ViewModel() {
    private val retro = Retro(token).getRetroClientInstance().create(MessageApi::class.java)
    private val _messageList = MutableLiveData<List<MessageResponse.Data>?>()
    val chatList: LiveData<List<Chat>> = _messageList.map { messageList ->
        messageList?.map { message ->
            Chat(
                id = message.id,
                developerId = message.developerId,
                name = message.name,
                email = message.email,
                phone = message.phone,
                message = message.message,
                read = message.read,
                subject = message.subject,
                time = message.createAt,
                updateAt = message.updateAt
            )
        } ?: emptyList()
    }

    fun getAllMessage() {
        if (_messageList.value.isNullOrEmpty()) {
            retro.getAllMessage().enqueue(object : Callback<MessageResponse> {
                override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
                ) {
                    if(response.isSuccessful){
                        val messageResponse = response.body()
                        _messageList.value = messageResponse?.data
                    }
                }

                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    //TODO: Handle failure
                    Log.e("ChatViewModel", "onFailure: ${t.message}")
                }
            })
        }

    }

    val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing
    fun fetchNewData() {
        _isRefreshing.postValue(true)
        retro.getAllMessage().enqueue(object : Callback<MessageResponse> {
            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                if (response.isSuccessful) {
                    val messageResponse = response.body()
                    val newData = messageResponse?.data
                    if (_messageList.value != newData) {
                        _messageList.postValue(newData)
                    }
                }

                // Signal that the refresh has finished
                _isRefreshing.postValue(false)
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                // Handle failure
                Log.e("ChatViewModel", "onFailure: ${t.message}")
                // Signal that the refresh has finished
                _isRefreshing.postValue(false)
            }
        })
    }

}