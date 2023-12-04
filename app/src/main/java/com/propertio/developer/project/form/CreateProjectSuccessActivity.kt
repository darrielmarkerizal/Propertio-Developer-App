package com.propertio.developer.project.form

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.propertio.developer.R
import com.propertio.developer.databinding.ActivityProjectSuccessBinding

class CreateProjectSuccessActivity : AppCompatActivity() {

    private val binding by lazy { ActivityProjectSuccessBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            buttonKembaliKeHalamanProyek.setOnClickListener {
                finish()
            }
        }

    }
}