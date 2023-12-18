package com.propertio.developer.database.facility

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

@Database(entities = [FacilityTable::class], version = 1, exportSchema = true)
abstract class FacilityDatabase : RoomDatabase() {

    abstract fun facilityTableDao(): FacilityTableDao

    companion object {
        @Volatile
        private var INSTANCE: FacilityDatabase? = null

        fun getDatabase(context: Context): FacilityDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = databaseBuilder(
                    context.applicationContext,
                    FacilityDatabase::class.java,
                    "facility_database"
                ).build()
                INSTANCE = instance
                instance
            }

        }
    }
}