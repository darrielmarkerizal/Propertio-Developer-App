package com.propertio.developer.unit.form

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.propertio.developer.databinding.ActivityUnitSuccessBinding

class CreateUnitSuccessActivity : AppCompatActivity() {
    private val binding by lazy { ActivityUnitSuccessBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonKembaliKeDetailProyek.setOnClickListener {
            setResult(RESULT_OK)
            finish()

        }
    }
}