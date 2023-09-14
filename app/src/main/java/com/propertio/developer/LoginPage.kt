package com.propertio.developer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.propertio.developer.databinding.LoginPageBinding

class LoginPage : AppCompatActivity() {
    private val binding by lazy { LoginPageBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val registerTextLink = binding.registerHere

        registerTextLink.setOnClickListener {
            
        }
    }
}