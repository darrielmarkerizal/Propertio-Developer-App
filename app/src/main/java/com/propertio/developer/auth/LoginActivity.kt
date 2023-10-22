package com.propertio.developer.auth

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import com.propertio.developer.R
import com.propertio.developer.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
}