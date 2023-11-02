package com.propertio.developer.pesan

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.propertio.developer.api.Retro
import com.propertio.developer.api.common.message.MessageApi
import com.propertio.developer.api.common.message.MessageResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatViewModel(token: String?) : ViewModel() {
    private val retro = Retro(token).getRetroClientInstance().create(MessageApi::class.java)
    val chatList = MutableLiveData<List<Chat>?>()

    fun getAllMessage() {
        retro.getAllMessage().enqueue(object : Callback<MessageResponse> {
            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                val messageResponse = response.body()
                val listChat = mutableListOf<Chat>()
                if (messageResponse != null) {
                    for (message in messageResponse.data!!.reversed()) {
                        Log.d("ChatViewModel", "onResponse: $message")
                        listChat.add(
                            Chat(
                                id = message.id,
                                developerId = message.developerId,
                                name = message.name,
                                email = message.email,
                                phone = message.phone,
                                read = message.read,
                                subject = message.subject,
                                time = message.createAt,
                                updateAt = message.updateAt
                            )
                        )
                    }
                }
                chatList.value = listChat
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                //TODO: Handle failure
                Log.e("ChatViewModel", "onFailure: ${t.message}")
            }
        })
    }

}