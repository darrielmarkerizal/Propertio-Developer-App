package com.propertio.developer

import android.content.Context
import android.icu.util.Calendar

class TokenManager(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("account_data", Context.MODE_PRIVATE)
    val token: String?
        get() = sharedPreferences.getString("token", null)

    fun saveToken(token: String) {
        val expirationDate = Calendar.getInstance().apply {
            add(Calendar.MONTH, 1)
        }.timeInMillis

        with(sharedPreferences.edit()) {
            putString("token", token)
            putLong("token_expiration_date", expirationDate)
            apply()
        }
    }

    fun deleteToken() {
        with(sharedPreferences.edit()) {
            remove("token")
            apply()
        }
    }

}