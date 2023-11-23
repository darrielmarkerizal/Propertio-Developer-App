package com.propertio.developer.profile

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.propertio.developer.R
import com.propertio.developer.api.Retro
import com.propertio.developer.api.auth.ChangePasswordApi
import com.propertio.developer.api.auth.UserApi
import com.propertio.developer.api.auth.UserRequest
import com.propertio.developer.api.auth.UserResponse
import com.propertio.developer.api.models.ChangePasswordRequest
import com.propertio.developer.api.models.ChangePasswordResponse
import com.propertio.developer.databinding.ActivityChangePasswordBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePassword : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var api: UserApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        api = Retro(null).getRetroClientInstance().create(UserApi::class.java)

        binding.btnUbahPassword.setOnClickListener {
            val currentPassword = binding.edtCurrentPassword.text.toString()
            val password = binding.edtNewPassword.text.toString()
            val passwordConfirmation = binding.edtConfirmPassword.text.toString()

            val sharedPreferences = getSharedPreferences("account_data", MODE_PRIVATE)
            val email = sharedPreferences.getString("email", null)

            val currentPasswordRequest = UserRequest().apply {
                this.email = email
                this.password = currentPassword
            }

            // Pengecekan password saat ini
            api.login(currentPasswordRequest).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        // Jika password saat ini benar, lanjutkan dengan mengubah password
                        val request = ChangePasswordRequest().apply {
                            this.password = password
                            this.passwordConfirmation = passwordConfirmation
                        }

                        // Mengubah password
                        api.changePassword(request).enqueue(object : Callback<ChangePasswordResponse> {
                            override fun onResponse(call: Call<ChangePasswordResponse>, response: Response<ChangePasswordResponse>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(this@ChangePassword, "Password changed successfully", Toast.LENGTH_SHORT).show()
                                    // Menutup activity dan kembali ke ProfileFragment
                                    finish()
                                } else {
                                    val errorMessage = response.errorBody()?.string()
                                    Toast.makeText(this@ChangePassword, "Failed to change password: $errorMessage", Toast.LENGTH_SHORT).show()
                                    Log.e("ChangePassword", "Failed to change password: $errorMessage")
                                }
                            }

                            override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                                Toast.makeText(this@ChangePassword, "API call failed: ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    } else {
                        // Jika password saat ini salah, beri tahu pengguna
                        Toast.makeText(this@ChangePassword, "Current password is incorrect", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Toast.makeText(this@ChangePassword, "API call failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}