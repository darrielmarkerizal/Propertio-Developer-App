package com.propertio.developer

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.database.PropertiORepository
import com.propertio.developer.database.chat.ChatDatabase
import com.propertio.developer.database.facility.FacilityDatabase
import com.propertio.developer.database.infrastructure.InfrastructureDatabase
import com.propertio.developer.database.project.ProjectDatabase
import com.propertio.developer.project.ProjectViewModel
import com.propertio.developer.project.ProjectViewModelFactory
import com.propertio.developer.project.list.FacilityAndInfrastructureTypeViewModel
import com.propertio.developer.project.viewmodel.FacilityViewModelFactory

class PropertioDeveloperApplication : Application() {
    private val projectDb by lazy {
        ProjectDatabase.getDatabase(this)
    }
    private val chatDb by lazy {
        ChatDatabase.getDatabase(this)
    }
    private val facilityDb by lazy {
        FacilityDatabase.getDatabase(this)
    }
    private val infrastructureDb by lazy {
        InfrastructureDatabase.getDatabase(this)
    }

    val repository by lazy {
        PropertiORepository(
            projectDb.projectTableDao(),
            chatDb.chatTableDao(),
            facilityDb.facilityTableDao(),
            infrastructureDb.infrastructureTableDao()
        )
    }

}