package com.propertio.developer.auth


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.propertio.developer.MainActivity
import com.propertio.developer.api.Retro
import com.propertio.developer.api.auth.UserApi
import com.propertio.developer.api.auth.UserRequest
import com.propertio.developer.api.auth.UserResponse
import com.propertio.developer.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {

            buttonLogin.setOnClickListener {
                login()
            }



        }
    }


    private fun login() {
        val request = UserRequest()
        request.email = binding.editTextEmail.text.toString()
        request.password = binding.editTextPassword.text.toString()

        val retro = Retro().getRetroClientInstance().create(UserApi::class.java)
        retro.login(request).enqueue(object: Callback<UserResponse>{
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                val user = response.body()

                Log.d("LoginActivity", "Token: ${user?.data?.token}")

                val sharedPreferences = getSharedPreferences("account_data", MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    putString("token", user?.data?.token)
                }

                val intentToMainActivity = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intentToMainActivity)

            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e("LoginActivity", "Error: ${t.message}")
            }

        })

    }

}