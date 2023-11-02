package com.propertio.developer.pesan

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
) {

    val formattedTime: String?
        get() {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(time)
            return if (date != null) outputFormat.format(date) else time
        }
}
