package com.propertio.developer.database.chat

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

@Database(
    entities = [ChatTable::class],
    version = 1,
    exportSchema = true,
)
abstract class ChatDatabase : RoomDatabase() {

    abstract fun chatTableDao(): ChatTableDao

    companion object {
        @Volatile
        private var INSTANCE: ChatDatabase? = null

        fun getDatabase(context: Context): ChatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = databaseBuilder(
                    context.applicationContext,
                    ChatDatabase::class.java,
                    "chat_database"
                ).build()
                INSTANCE = instance
                instance
            }

        }
    }

}
