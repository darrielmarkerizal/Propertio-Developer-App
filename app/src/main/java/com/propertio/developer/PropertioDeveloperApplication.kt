package com.propertio.developer

import android.app.Application
import com.propertio.developer.database.PropertiORepository
import com.propertio.developer.database.chat.ChatDatabase
import com.propertio.developer.database.project.ProjectDatabase

class PropertioDeveloperApplication : Application() {
    private val projectDb by lazy {
        ProjectDatabase.getDatabase(this)
    }
    private val chatDb by lazy {
        ChatDatabase.getDatabase(this)
    }

    val repository by lazy {
        PropertiORepository(
            projectDb.projectTableDao(),
            chatDb.chatTableDao()
        )
    }
}