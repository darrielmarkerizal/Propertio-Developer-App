package com.propertio.developer.profile

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.google.gson.Gson
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.auth.UserApi
import com.propertio.developer.api.models.ChangePasswordRequest
import com.propertio.developer.api.models.ChangePasswordResponse
import com.propertio.developer.databinding.ActivityChangePasswordBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePassword : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var api: UserApi

    private fun EditText.trimValidate(): String? {
        val trimmed = this.text.toString().trim()
        return if (trimmed.length >= 5) trimmed else null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editTextObserver()

        api = Retro(TokenManager(this).token)
            .getRetroClientInstance()
            .create(UserApi::class.java)

        setupChangePasswordButton()
        setupCancelBUtton()

    }

    private fun setupCancelBUtton() {
        binding.btnBatalUbahPassword.setOnClickListener {
            finish()
        }
    }

    private fun setupChangePasswordButton() {
        binding.btnUbahPassword.setOnClickListener {
            clearAllFocus()

            // Pre Validation
            val currentPassword = binding.edtCurrentPassword.trimValidate() ?: run {
                val errorMsg = "Kata sandi harus lebih dari 5 karakter"
                binding.edtCurrentPassword.error = errorMsg
                return@setOnClickListener
            }
            val password = binding.edtNewPassword.trimValidate() ?: run {
                val errorMsg = "Kata sandi harus lebih dari 5 karakter"
                binding.edtCurrentPassword.error = errorMsg
                return@setOnClickListener
            }
            val passwordConfirmation = binding.edtConfirmPassword.trimValidate().also {
                if (it != password) {
                    val errorMsg = "Kata sandi tidak cocok"
                    binding.edtConfirmPassword.error = errorMsg
                    return@setOnClickListener
                }
            } ?: run {
                val errorMsg = "Kata sandi harus lebih dari 5 karakter"
                binding.edtCurrentPassword.error = errorMsg
                return@setOnClickListener
            }


            val changePasswordRequest = ChangePasswordRequest().apply {
                this.currentPassword = currentPassword
                this.password = password
                this.passwordConfirmation = passwordConfirmation
            }

            api.changePassword(changePasswordRequest).enqueue(object : Callback<ChangePasswordResponse> {
                override fun onResponse(call: Call<ChangePasswordResponse>, response: Response<ChangePasswordResponse>) {
                    val data = response.body()?.data
                    Log.i("ChangePassword", "Response: $data")

                    if (response.isSuccessful) {
                        data ?: return changePasswordSuccess()

                        // Error handling that should not happen
                        val errorMessage = response.errorBody()?.string()
                        validationServerSide(data)
                        Log.wtf("ChangePassword", "Failed to change password: $errorMessage")
                    } else {
                        // Error handling
                        val gson = Gson()
                        val errorBodyString = response.errorBody()?.string()
                        val errorResponse = gson.fromJson(errorBodyString, ChangePasswordResponse::class.java)
                        val errorData = errorResponse.data
                        Log.e("ChangePassword", "Failed to change password: $errorData")
                        validationServerSide(errorData!!)
                    }

                }

                override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                    Log.e("ChangePassword", "API call failed: ${t.message}")
                    Toast.makeText(this@ChangePassword, "API call failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })

        }
    }

    private fun validationServerSide(data: ChangePasswordResponse.Data) {
        if (data.currentPassword != null) {
            Log.e("ChangePassword", "Current Password Error: ${data.currentPassword}")
            val errorMsg = data.currentPassword!!.joinToString("\n")
            binding.edtCurrentPassword.error = errorMsg
        }
        if (data.password != null) {
            Log.e("ChangePassword", "Password Error: ${data.password}")
            val errorMsg = data.password!!.joinToString("\n")
            binding.edtNewPassword.error = errorMsg
        }
        if (data.passwordConfirmation != null) {
            Log.e("ChangePassword", "Password Confirmation Error: ${data.passwordConfirmation}")
            val errorMsg = data.passwordConfirmation!!.joinToString("\n")
            binding.edtConfirmPassword.error = errorMsg
        }
    }

    private fun editTextObserver() {
        with(binding){
            edtCurrentPassword.doAfterTextChanged {
                if ( edtCurrentPassword.error != null && it!!.length >= 5) edtCurrentPassword.error = null
            }
            edtNewPassword.doAfterTextChanged {
                if (edtNewPassword.error != null &&it!!.length >= 5) edtNewPassword.error = null
            }
            edtConfirmPassword.doAfterTextChanged {
                if (edtConfirmPassword.error != null && it!!.length >= 5) edtConfirmPassword.error = null
            }
        }
    }

    private fun changePasswordSuccess() {
        Log.i("ChangePassword", "Password changed successfully")
        Toast.makeText(this@ChangePassword, "Password changed successfully", Toast.LENGTH_SHORT).show()
        // Menutup activity dan kembali ke ProfileFragment
        finish()
    }

    private fun clearAllFocus() {
        with(binding) {
            edtCurrentPassword.clearFocus()
            edtNewPassword.clearFocus()
            edtConfirmPassword.clearFocus()
        }
    }
}