package com.propertio.developer.model

import android.provider.ContactsContract.CommonDataKinds.Email
import java.text.SimpleDateFormat
import java.util.Locale

data class Chat(
    val id: Int? = null,
    val developerId: Int? = null,
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val message: String? = null,
    var read: Int? = 0,
    val subject: String? = null,
    val time: String? = null,
    val updateAt: String? = null
//    val pictureURL: String
)