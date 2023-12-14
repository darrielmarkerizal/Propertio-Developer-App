package com.propertio.developer.database.chat

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_table")
data class ChatTable (
    @PrimaryKey(autoGenerate = false)
    @NonNull
    var id: Int = 0,

    @ColumnInfo(name = "name")
    var name: String? = null,

    @ColumnInfo(name = "email")
    var email: String? = null,

    @ColumnInfo(name = "phone")
    var phone: String? = null,

    @ColumnInfo(name = "subject")
    var subject: String? = null,

    @ColumnInfo(name = "message")
    var message: String? = null,

    @ColumnInfo(name = "read")
    var read: String? = null,

    @ColumnInfo(name = "create_at")
    var createAt: String? = null,
)