package com.propertio.developer.auth


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.propertio.developer.MainActivity
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.auth.UserApi
import com.propertio.developer.api.auth.UserRequest
import com.propertio.developer.api.auth.UserResponse
import com.propertio.developer.databinding.ActivityLoginBinding
import com.propertio.developer.permissions.NetworkAccess
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        tokenManager = TokenManager(this@LoginActivity)

        if (tokenManager.token != null) {
            if (NetworkAccess.isNetworkAvailable(this@LoginActivity).not()) run {
                NetworkAccess.buildNoConnectionToast(this@LoginActivity).show()
            } else {
                goToMainActivity()
            }

        }

        with(binding) {

            editTextPassword.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    editTextPassword.error = null
                    editTextPasswordLayout.isEndIconVisible = true
                }
            })

            buttonLogin.setOnClickListener {
                if (editTextEmail.text.toString().isEmpty()) {
                    editTextEmail.error = "Mohon isi email"
                    return@setOnClickListener
                }
                if (editTextPassword.text.toString().isEmpty()) {
                    editTextPasswordLayout.isEndIconVisible = false
                    editTextPassword.error = "Mohon isi password"
                    return@setOnClickListener
                }

                login()
            }

            linkToCreateAccount.setOnClickListener {
                val intentToRegisterActivity = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intentToRegisterActivity)
            }

        }
    }

    private fun goToMainActivity() {
        val intentToMainActivity = Intent(this@LoginActivity, MainActivity::class.java)
        intentToMainActivity.putExtra("toastMessage", "Berhasil Login")
        startActivity(intentToMainActivity)
        finish()
    }


    private fun login(email: String? = null, password: String? = null) {
        if (NetworkAccess.isNetworkAvailable(this@LoginActivity).not()) run {
            NetworkAccess.buildNoConnectionToast(this@LoginActivity).show()
            return
        }

        val request = UserRequest()
        request.email = email ?: binding.editTextEmail.text.toString()
        request.password = password ?: binding.editTextPassword.text.toString()

        val retro = Retro(null).getRetroClientInstance().create(UserApi::class.java)
        retro.login(request).enqueue(object: Callback<UserResponse>{
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                val user = response.body()

                Log.d("LoginActivity", "Response: ${user?.message}")

                if (response.isSuccessful) {
                    if (user?.data?.token == null) {
                        Log.e("LoginActivity", "Error: ${user?.message}")
                        return
                    }
                    if (user.data!!.user!!.role == "developer") {


                        user.data!!.token?.let {
                            tokenManager.saveToken(it)
                        }

                        goToMainActivity()
                    }
                    else {
                        Toast.makeText(this@LoginActivity, "Email yang Anda gunakan bukan akun developer", Toast.LENGTH_SHORT).show()
                    }
                }
                else if (response.code() == 401) {
                    Toast.makeText(this@LoginActivity, "Email atau password salah", Toast.LENGTH_SHORT).show()
                }



            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e("LoginActivity", "Error: ${t.message}")
            }

        })

    }

}