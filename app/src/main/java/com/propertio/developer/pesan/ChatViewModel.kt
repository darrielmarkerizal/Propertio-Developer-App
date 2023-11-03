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

@WorkerThread
class ChatViewModel(token: String?) : ViewModel() {
    private val retro = Retro(token).getRetroClientInstance().create(MessageApi::class.java)
    private val _messageList = MutableLiveData<List<MessageResponse.Data>>()
    val chatList: LiveData<List<Chat>> = _messageList.map { messageList ->
        messageList.map { message ->
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
        }
    }

    @WorkerThread
    fun getAllMessage() {
        retro.getAllMessage().enqueue(object : Callback<MessageResponse> {
            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                val messageResponse = response.body()
                _messageList.value = messageResponse?.data
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                //TODO: Handle failure
                Log.e("ChatViewModel", "onFailure: ${t.message}")
            }
        })
    }

}