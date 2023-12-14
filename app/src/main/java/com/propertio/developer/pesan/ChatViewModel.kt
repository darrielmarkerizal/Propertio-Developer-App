package com.propertio.developer.pesan

import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
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
import androidx.lifecycle.viewModelScope
import com.propertio.developer.TokenManager
import com.propertio.developer.database.PropertiORepository
import com.propertio.developer.database.chat.ChatTable
import com.propertio.developer.model.Chat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ChatViewModel(
    private val propertiORepository: PropertiORepository
) : ViewModel() {

    val unreadChatCount : MutableLiveData<Int> = MutableLiveData(0)

    suspend fun getAllChat(): List<ChatTable> {
        return withContext(Dispatchers.IO) {
            propertiORepository.getAllChat()
        }
    }

    suspend fun getChatById(id : Int) : ChatTable {
        return withContext(Dispatchers.IO) {
            propertiORepository.getChatById(id)
        }
    }

    suspend fun countUnread(): Int {
        return withContext(Dispatchers.IO) {
            propertiORepository.countUnread()
        }
    }

    suspend fun countAll(): Int {
        return withContext(Dispatchers.IO) {
            propertiORepository.countAll()
        }
    }


    suspend fun getAllChatPaginated(limit: Int, offset: Int = 0): List<ChatTable> {
        return withContext(Dispatchers.IO) {
            propertiORepository.getAllChatPaginated(limit, offset)
        }
    }

    suspend fun updateRead(id: Int) {
        return withContext(Dispatchers.IO) {
            propertiORepository.updateRead(id)
        }
    }

    suspend fun search(search: String, limit: Int, offset: Int = 0): List<ChatTable> {
        return withContext(Dispatchers.IO) {
            propertiORepository.searchChat(search, limit, offset)
        }
    }

    suspend fun delete(id: Int) {
        return withContext(Dispatchers.IO) {
            propertiORepository.deleteChat(id)
        }
    }

    suspend fun deleteAll() {
        return withContext(Dispatchers.IO) {
            propertiORepository.deleteAllChat()
        }
    }


    suspend fun fetchDataFromApi(token : String) {
        val retro = Retro(token).getRetroClientInstance().create(MessageApi::class.java)

        retro.getAllMessage().enqueue(object : Callback<MessageResponse> {
            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    if (data != null) {
                        viewModelScope.launch {
                            for (it in data) {
                                propertiORepository.insertChat(
                                    id = it.id!!,
                                    name = it.name ?: "Unkown Name",
                                    email = it.email ?: "Unkown Email",
                                    phone = it.phone ?: "Unkown Phone",
                                    subject = it.subject ?: "Unkown Subject",
                                    message = it.message ?: "Unkown Message",
                                    read = it.read!!,
                                    createAt = it.createAt!!,
                                )
                            }
                            val newSizeCount = countUnread()
                            unreadChatCount.postValue(newSizeCount)
                        }
                    } else {
                        Log.e("ApiCall ChatViewModel", "onResponse: data is null")
                    }

                } else {
                    Log.e("ApiCall ChatViewModel", "onResponse: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Log.e("ApiCall ChatViewModel", "onFailure: ${t.message}")
            }

        })
    }

}