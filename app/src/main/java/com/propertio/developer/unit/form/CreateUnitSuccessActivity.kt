package com.propertio.developer.unit.form

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.propertio.developer.R
import com.propertio.developer.databinding.ActivityUnitSuccessBinding

class CreateUnitSuccessActivity : AppCompatActivity() {
    private val binding by lazy { ActivityUnitSuccessBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            buttonKembaliKeDetailProyek.setOnClickListener {
                finish()
            }

        binding.buttonKembaliKeDetailProyek.setOnClickListener {
            setResult(RESULT_OK)
            finish()

        }
    }}
}