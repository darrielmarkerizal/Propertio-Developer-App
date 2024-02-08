package com.propertio.developer.database.infrastructure

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface InfrastructureTableDao {
    @Query("SELECT * FROM infrastructure_table ORDER BY name ASC")
    suspend fun getAll(): List<InfrastructureTable>

    @Query("SELECT * FROM infrastructure_table WHERE description = :description ORDER BY name ASC")
    suspend fun getAllByDescription(description: String): List<InfrastructureTable>

    @Query("SELECT * FROM infrastructure_table WHERE name LIKE :search ORDER BY name ASC")
    suspend fun search(search: String): List<InfrastructureTable>

    @Query("SELECT * FROM infrastructure_table WHERE id = :id")
    suspend fun getById(id: Int): InfrastructureTable?

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insert(infrastructureTable: InfrastructureTable)

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertAll(infrastructureTables: List<InfrastructureTable>)

    @Query("UPDATE infrastructure_table SET is_selected = :isSelected WHERE id = :id")
    suspend fun updateSelectedInfrastructure(id: Int, isSelected: Boolean)

    @Query("DELETE FROM infrastructure_table")
    suspend fun deleteAll()

}
