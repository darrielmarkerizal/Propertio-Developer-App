package com.propertio.developer.database.profile

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProfileTableDao {

    @get:Query("SELECT * FROM profile_table LIMIT 1")
    val localProfile: ProfileTable

    @Update
    suspend fun update(profileTable: ProfileTable)

    @Query("DELETE FROM profile_table")
    suspend fun deleteAll()


}
