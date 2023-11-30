package com.propertio.developer.database.profile

import android.net.Uri
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "profile_table")
data class ProfileTable (

    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id: Int = 0,


    @ColumnInfo(name = "id_user")
    var idUser: String? = null,

    @ColumnInfo(name = "email")
    var email: String? = null,

    @ColumnInfo(name = "name")
    var name: String? = null,

    @ColumnInfo(name = "phone")
    var phone: String? = null,

    @ColumnInfo(name = "province")
    var province: String? = null,

    @ColumnInfo(name = "city")
    var city: String? = null,

    @ColumnInfo(name = "address")
    var address: String? = null,
)
