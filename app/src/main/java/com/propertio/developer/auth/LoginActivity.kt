package com.propertio.developer.auth

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import com.propertio.developer.PropertioApiInformation
import com.propertio.developer.PropertioApiInformation.Companion.HOST
import com.propertio.developer.R
import com.propertio.developer.data.LoginRequest
import com.propertio.developer.data.LoginResponse
import com.propertio.developer.databinding.ActivityLoginBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://$HOST/v1/auth/login/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val authService = retrofit.create(AuthService::class.java)

        with(binding) {

            buttonLogin.setOnClickListener {
                Log.d("LoginActivity", "onCreate: ${editTextEmail.text}")

                authService.login(
                    LoginRequest(
                    email = editTextEmail.text.toString(),
                    password = editTextPassword.text.toString()
                )
                ).enqueue(object : retrofit2.Callback<LoginResponse> {
                    override fun onResponse(
                        call: retrofit2.Call<LoginResponse>,
                        response: retrofit2.Response<LoginResponse>
                    ) {

                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            if (loginResponse != null) {
                                val sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE)

                                with(sharedPreferences.edit()) {
                                    putString("token", loginResponse.data.token)
                                    apply()
                                }
                                Log.d("LoginActivity", "onResponse [Success]: ${response.code()}")
                            }
                            Log.d("LoginActivity", "onResponse [Empty]: ${response.code()}")
                        } else {
                            Log.d("LoginActivity", "onResponse [Not Success]: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<LoginResponse>, t: Throwable) {
                        Log.e("LoginActivity", "onFailure: ${t.message}")
                    }
                })
            }
        }
    }
}