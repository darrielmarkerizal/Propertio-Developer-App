package com.propertio.developer

import android.app.Application
import com.propertio.developer.database.ProjectRepository
import com.propertio.developer.database.project.ProjectDatabase

class PropertioDeveloperApplication : Application() {
    private val database by lazy {
        ProjectDatabase.getDatabase(this)
    }

    val repository by lazy {
        ProjectRepository(database.projectTableDao())
    }
}