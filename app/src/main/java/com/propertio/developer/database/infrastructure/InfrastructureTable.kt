package com.propertio.developer.database.infrastructure

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "infrastructure_table")
data class InfrastructureTable(
    @PrimaryKey(autoGenerate = false)
    @NonNull
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "icon")
    val icon: String,

    @ColumnInfo(name = "is_selected")
    var isSelected: Boolean = false,
)
