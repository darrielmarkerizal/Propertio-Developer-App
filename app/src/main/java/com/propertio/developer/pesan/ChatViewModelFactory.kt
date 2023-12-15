package com.propertio.developer.pesan

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.database.PropertiORepository
import com.propertio.developer.project.ProjectViewModel

class ChatViewModelFactory(
    private val propertiORepository: PropertiORepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(propertiORepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}