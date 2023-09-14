package com.propertio.developer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.propertio.developer.databinding.GetStartedWelcomePageBinding

class WelcomePage : AppCompatActivity() {
    private val binding by lazy { GetStartedWelcomePageBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}