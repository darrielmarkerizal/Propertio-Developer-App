package com.propertio.developer.database.project

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

@Database(
    entities = [ProjectTable::class],
    version = 1,
    exportSchema = true,
//    autoMigrations = [
//        AutoMigration(from = 1, to = 2)
//    ]
)
abstract class ProjectDatabase : RoomDatabase(){

    abstract fun projectTableDao(): ProjectTableDao

    companion object {
        @Volatile
        private var INSTANCE: ProjectDatabase? = null

        fun getDatabase(context: Context): ProjectDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = databaseBuilder(
                    context.applicationContext,
                    ProjectDatabase::class.java,
                    "project_database"
                ).build()
                INSTANCE = instance
                instance
            }

        }
    }




}