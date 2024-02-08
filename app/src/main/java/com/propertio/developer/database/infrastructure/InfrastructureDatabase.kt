package com.propertio.developer.database.infrastructure

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [InfrastructureTable::class], version = 1, exportSchema = true)
abstract class InfrastructureDatabase : RoomDatabase() {

    abstract fun infrastructureTableDao(): InfrastructureTableDao

    companion object {
        @Volatile
        private var INSTANCE: InfrastructureDatabase? = null

        fun getDatabase(context: Context): InfrastructureDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InfrastructureDatabase::class.java,
                    "infrastructure_database"
                ).build()
                INSTANCE = instance
                instance
            }

        }
    }
}