package com.propertio.developer.unit.form

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.propertio.developer.R
import com.propertio.developer.databinding.ActivityUnitSuccessBinding

class CreateUnitSuccessActivity : AppCompatActivity() {
    private val binding by lazy { ActivityUnitSuccessBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val isCreateNew = intent.getBooleanExtra(UnitFormActivity.IS_CREATE_NEW_UNIT, true)
        binding.nextTxtUnitSuccess.text = if (isCreateNew) getString(R.string.unit_anda_berhasil_ditambahkan) else getString(R.string.unit_anda_berhasil_diperbarui)

        binding.buttonKembaliKeDetailProyek.setOnClickListener {
            setResult(RESULT_OK)
            finish()

        }
    }
}