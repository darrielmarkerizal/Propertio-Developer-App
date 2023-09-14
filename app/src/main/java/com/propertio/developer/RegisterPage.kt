package com.propertio.developer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.propertio.developer.databinding.RegisterPageBinding

class RegisterPage : AppCompatActivity() {
    private val binding by lazy { RegisterPageBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}