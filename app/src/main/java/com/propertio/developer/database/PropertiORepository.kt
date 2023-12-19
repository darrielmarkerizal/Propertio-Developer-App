package com.propertio.developer.database

import android.util.Log
import com.propertio.developer.database.chat.ChatTable
import com.propertio.developer.database.chat.ChatTableDao
import com.propertio.developer.database.facility.FacilityTable
import com.propertio.developer.database.facility.FacilityTableDao
import com.propertio.developer.database.project.ProjectTable
import com.propertio.developer.database.project.ProjectTableDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PropertiORepository(
    private val projectTableDao: ProjectTableDao,
    private val chatTableDao: ChatTableDao,
    private val facilityTableDao: FacilityTableDao
) {

    val listProject :Flow<List<ProjectTable>> = projectTableDao.allProjects

    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    fun allProjectsPaginated(limit: Int, offset: Int, status : String, filter : String = ""): Flow<List<ProjectTable>> {
        Log.d("Repository", "allProjectsPaginated: from $limit to $offset where status is $status and filter is $filter")
        return projectTableDao.allProjectsPaginated(limit, offset, status, filter = "%$filter%")
    }

    suspend fun getProjectById(id: Int): ProjectTable {
        Log.d("Repository", "getProjectById: $id")
        return projectTableDao.getProjectById(id).filterNotNull().first()
    }

    suspend fun isNotIdTaken(id: Int): Boolean {
        Log.d("Repository", "isIdTaken: $id")
        val projectList = projectTableDao.checkId(id).first()
        return projectList.isEmpty()
    }

    // Methods

    fun insertProject(projectTable: ProjectTable) {
        executorService.execute {
            projectTableDao.insert(projectTable)
            Log.w("Repository", "insertProject: $projectTable")
        }
    }

    fun updateProject(projectTable: ProjectTable) {
        executorService.execute {
            projectTableDao.update(projectTable)
            Log.w("Repository", "updateProject: $projectTable")
        }
    }

    fun updateProject(
        id: Int,
        headline: String,
        description: String,
        certificate: String,
        addressPostalCode: String,
        addressLatitude: String,
        addressLongitude: String
    ) {
        executorService.execute {
            projectTableDao.updateProject(
                id,
                headline,
                description,
                certificate,
                addressPostalCode,
                addressLatitude,
                addressLongitude
            )
            Log.w("Repository", "updateProject: $id")
        }
    }

    fun deleteAll() {
        executorService.execute {
            projectTableDao.deleteAll()
            Log.w("Repository", "deleteAll: ")
        }
    }



    // Chat
    suspend fun getAllChat(): List<ChatTable> {
        return withContext(Dispatchers.IO) {
            chatTableDao.getAll()
        }
    }

    suspend fun getChatById(id: Int): ChatTable {
        return withContext(Dispatchers.IO) {
            chatTableDao.getById(id)
        }
    }

    suspend fun countUnread(): Int {
        return withContext(Dispatchers.IO) {
            chatTableDao.countUnread()
        }
    }

    suspend fun countAll(): Int {
        return withContext(Dispatchers.IO) {
            chatTableDao.countAll()
        }
    }

    suspend fun getAllChatPaginated(limit: Int, offset: Int): List<ChatTable> {
        return withContext(Dispatchers.IO) {
            chatTableDao.getAllPaginated(limit, offset)
        }
    }

    suspend fun updateRead(id: Int) {
        return withContext(Dispatchers.IO) {
            chatTableDao.updateRead(id)
        }
    }

    suspend fun insertChat(
        id: Int,
        name: String,
        email: String,
        phone: String,
        subject: String,
        message: String,
        read: String,
        createAt: String,
    ) {
        return withContext(Dispatchers.IO) {
            chatTableDao.insert(
                ChatTable(
                    id = id,
                    name = name,
                    email = email,
                    phone = phone,
                    subject = subject,
                    message = message,
                    read = read,
                    createAt = createAt,
                )
            )
        }
    }

    suspend fun searchChat(search: String, limit: Int, offset: Int): List<ChatTable> {
        return withContext(Dispatchers.IO) {
            chatTableDao.search(search = "%$search%", limit, offset)
        }
    }

    suspend fun deleteChat(id: Int) {
        return withContext(Dispatchers.IO) {
            chatTableDao.delete(id)
        }
    }

    suspend fun deleteAllChat() {
        return withContext(Dispatchers.IO) {
            chatTableDao.deleteAll()
        }
    }


    // Facility

    suspend fun getAllFacility(): List<FacilityTable> {
        return withContext(Dispatchers.IO) {
            facilityTableDao.getAll()
        }
    }

    suspend fun getAllFacilityByCategory(category: String): List<FacilityTable> {
        return withContext(Dispatchers.IO) {
            facilityTableDao.getAllByCategory(category)
        }
    }

    suspend fun searchFacility(search: String): List<FacilityTable> {
        return withContext(Dispatchers.IO) {
            facilityTableDao.search(search = "%$search%")
        }
    }

    suspend fun insertFacility(
        id: Int,
        name: String,
        category: String,
        icon: String,
    ) {
        withContext(Dispatchers.IO) {
            facilityTableDao.insert(
                FacilityTable(
                    id = id,
                    name = name,
                    category = category,
                    icon = icon,
                )
            )
        }
    }
    suspend fun updateSelectedFacility(id: Int, isSelected: Boolean = false) {
        return withContext(Dispatchers.IO) {
            facilityTableDao.updateSelectedFacility(id, isSelected)
        }
    }

    suspend fun deleteAllFacility() {
        return withContext(Dispatchers.IO) {
            facilityTableDao.deleteAll()
        }
    }






}