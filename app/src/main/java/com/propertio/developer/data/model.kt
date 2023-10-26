package com.propertio.developer.data


data class LoginRequest(
    val email: String,
    val password: String
)



data class LoginResponse(
    val status: String,
    val message: String,
    val data: LoginData
) {
    data class LoginData (
        val user: User,
        val token: String
    ) {
        data class User (
            val account_id: String,
            val full_name: String,
            val role: String,
            val picture_profile_file: String

        )
    }
}



