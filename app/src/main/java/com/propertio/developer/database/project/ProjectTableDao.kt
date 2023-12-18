package com.propertio.developer.database.project

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectTableDao {

    @get:Query("SELECT * FROM project_table ORDER BY posted_at DESC")
    val allProjects: Flow<List<ProjectTable>>

    @Query("SELECT * FROM project_table WHERE status = :status AND (title LIKE :filter OR address_address LIKE :filter OR address_province LIKE :filter) ORDER BY posted_at DESC LIMIT :limit OFFSET :offset")
    fun allProjectsPaginated(limit: Int, offset: Int, status: String, filter: String): Flow<List<ProjectTable>>

    @Query("SELECT * FROM project_table WHERE id = :id")
    fun checkId(id: Int): Flow<List<ProjectTable>>


    @Query("SELECT * FROM project_table WHERE id = :id LIMIT 1")
    fun getProjectById(id: Int): Flow<ProjectTable>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(projectTable: ProjectTable)

    @Update
    fun update(projectTable: ProjectTable)


    // Specific Update
    @Query("UPDATE project_table SET headline = :headline, description = :description, certificate = :certificate, address_postal_code = :addressPostalCode, address_latitude = :addressLatitude, address_longitude = :addressLongitude WHERE id = :id")
    fun updateProject(
        id: Int,
        headline: String,
        description: String,
        certificate: String,
        addressPostalCode: String,
        addressLatitude: String,
        addressLongitude: String
    )


    // Delete All row in table
    @Query("DELETE FROM project_table")
    fun deleteAll()


}