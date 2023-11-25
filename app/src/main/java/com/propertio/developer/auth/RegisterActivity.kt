package com.propertio.developer.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.databinding.ActivityRegisterBinding
import com.propertio.developer.dialog.CountryPhoneCodeSheetFragment
import com.propertio.developer.dialog.viewmodel.PhoneCodeViewModel

class RegisterActivity : AppCompatActivity() {
    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }

    // Phone Code
    private lateinit var phoneCodeViewModel: PhoneCodeViewModel



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Phone Code
        phoneCodeViewModel = ViewModelProvider(this)[PhoneCodeViewModel::class.java]
        binding.spinnerNomorTeleponPrefix.setOnClickListener {
            CountryPhoneCodeSheetFragment().show(supportFragmentManager, "CountryPhoneCodeSheetFragment")
        }
        phoneCodeViewModel.phoneCodeData.observe(this) {
            val countryId = it.emoji + " " + it.countryId
            binding.spinnerNomorTeleponPrefix.text = countryId
            binding.textViewNomorTeleponPrefix.text = it.code
        }

    }
}