package com.propertio.developer.database.facility

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FacilityTableDao {

    @Query("SELECT * FROM facility_table ORDER BY name ASC")
    suspend fun getAll(): List<FacilityTable>

    @Query("SELECT * FROM facility_table WHERE category = :category ORDER BY name ASC")
    suspend fun getAllByCategory(category: String): List<FacilityTable>

    @Query("SELECT * FROM facility_table WHERE name LIKE :search ORDER BY name ASC")
    suspend fun search(search: String): List<FacilityTable>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insert(facilityTable: FacilityTable)

    @Query("UPDATE facility_table SET is_selected = :isSelected WHERE id = :id")
    suspend fun updateSelectedFacility(id: Int, isSelected: Boolean)

    @Query("DELETE FROM facility_table")
    suspend fun deleteAll()

}