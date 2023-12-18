package com.propertio.developer.database.facility

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "facility_table")
data class FacilityTable(
    @PrimaryKey(autoGenerate = false)
    @NonNull
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "icon")
    val icon: String,

    @ColumnInfo(name = "is_selected")
    var isSelected: Boolean = false,
)
