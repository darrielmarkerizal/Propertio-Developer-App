package com.propertio.developer.database.project

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "project_table")
data class ProjectTable (
    @PrimaryKey(autoGenerate = false)
    @NonNull
    var id: Int = 0,

    @ColumnInfo(name = "title")
    var title: String? = null,

    @ColumnInfo(name = "property_type")
    var propertyType: String? = null,

    @ColumnInfo(name = "posted_at")
    var postedAt: String? = null,


    // Address #1
    @ColumnInfo(name = "address_address")
    var addressAddress: String? = null,

    @ColumnInfo(name = "address_district")
    var addressDistrict: String? = null,

    @ColumnInfo(name = "address_city")
    var addressCity: String? = null,

    @ColumnInfo(name = "address_province")
    var addressProvince: String? = null,





    @ColumnInfo(name = "count_unit")
    var countUnit: Int? = null,

    @ColumnInfo(name = "price")
    var price: String? = null,

    @ColumnInfo(name = "photo")
    var photo: String? = null,

    @ColumnInfo(name = "project_code")
    var projectCode: String? = null,

    @ColumnInfo(name = "count_views")
    var countViews: Int? = null,

    @ColumnInfo(name = "count_leads")
    var countLeads: Int? = null,

    @ColumnInfo(name = "status")
    var status: String? = null,

    @ColumnInfo(name = "created_at")
    var createdAt: String? = null,


    // Details
    @ColumnInfo(name = "headline")
    var headline: String? = null,

    @ColumnInfo(name = "description")
    var description: String? = null,

    @ColumnInfo(name = "certificate")
    var certificate: String? = null,


    // Address #2
    @ColumnInfo(name = "address_postal_code")
    var addressPostalCode: String? = null,

    @ColumnInfo(name = "address_latitude")
    var addressLatitude: String? = null,

    @ColumnInfo(name = "address_longitude")
    var addressLongitude: String? = null

)