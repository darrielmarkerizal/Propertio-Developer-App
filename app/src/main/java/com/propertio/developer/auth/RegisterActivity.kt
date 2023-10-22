package com.propertio.developer.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.propertio.developer.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            //TODO: Add register functionality
        }
    }
}