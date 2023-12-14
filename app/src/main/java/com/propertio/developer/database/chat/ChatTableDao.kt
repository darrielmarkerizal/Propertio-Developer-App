package com.propertio.developer.database.chat

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ChatTableDao {

    @Query("SELECT * FROM chat_table")
    suspend fun getAll(): List<ChatTable>

    @Query("SELECT * FROM chat_table WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): ChatTable

    @Query("SELECT COUNT(*) FROM chat_table WHERE read = '0'")
    suspend fun countUnread(): Int

    @Query("SELECT COUNT(*) FROM chat_table")
    suspend fun countAll(): Int

    @Query("SELECT * FROM chat_table LIMIT :limit OFFSET :offset")
    suspend fun getAllPaginated(limit: Int, offset: Int): List<ChatTable>


    @Query("UPDATE chat_table SET read = '1' WHERE id = :id")
    suspend fun updateRead(id: Int)

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insert(chatTable: ChatTable)

    @Query("SELECT * FROM chat_table WHERE name LIKE :search OR subject LIKE :search OR email LIKE :search OR phone LIKE :search OR message LIKE :search LIMIT :limit OFFSET :offset")
    suspend fun search(search: String, limit: Int, offset: Int): List<ChatTable>


    @Query("DELETE FROM chat_table WHERE id = :id")
    suspend fun delete(id: Int)


    @Query("DELETE FROM chat_table")
    suspend fun deleteAll()

}
