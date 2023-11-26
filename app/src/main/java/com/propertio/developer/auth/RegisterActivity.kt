package com.propertio.developer.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.databinding.ActivityRegisterBinding
import com.propertio.developer.dialog.CitiesSheetFragment
import com.propertio.developer.dialog.CountryPhoneCodeSheetFragment
import com.propertio.developer.dialog.ProvinceSheetFragment
import com.propertio.developer.dialog.model.CitiesModel
import com.propertio.developer.dialog.viewmodel.CitiesSpinnerViewModel
import com.propertio.developer.dialog.viewmodel.PhoneCodeViewModel
import com.propertio.developer.dialog.viewmodel.ProvinceSpinnerViewModel

class RegisterActivity : AppCompatActivity() {
    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }

    // Phone Code
    private lateinit var phoneCodeViewModel: PhoneCodeViewModel

    // Province & City
    private var isProvinceSelected : Boolean = false
    private lateinit var provinceViewModel: ProvinceSpinnerViewModel
    private lateinit var cityViewModel: CitiesSpinnerViewModel




    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        phoneCodeSpinner()

        provinceSpinner()
        citySpinner()


    }



    private fun citySpinner() {
        cityViewModel = ViewModelProvider(this)[CitiesSpinnerViewModel::class.java]


        binding.spinnerDistrict.setOnClickListener {
            if (isProvinceSelected) {
                CitiesSheetFragment().show(supportFragmentManager, "CitySheetFragment")
            } else {
                Toast.makeText(this, "Pilih Provinsi terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }



        cityViewModel.citiesData.observe(this) {
            binding.spinnerDistrict.text = it.citiesName
        }
    }



    private fun provinceSpinner() {
        provinceViewModel = ViewModelProvider(this)[ProvinceSpinnerViewModel::class.java]
        binding.buttonProvincesSelection.setOnClickListener {
            ProvinceSheetFragment().show(supportFragmentManager, "ProvinceSheetFragment")
            isProvinceSelected = true
            Log.d("RegisterActivity", "provinceSpinner. is selected :$isProvinceSelected")
        }

        provinceViewModel.provinceData.observe(this) {
            binding.buttonProvincesSelection.text = it.provinceName

            cityViewModel.citiesData
                .postValue(
                    CitiesModel(
                        citiesId = "",
                        provinceId = it.provinceId,
                        citiesName = "-- Pilih Kota"
                    )
                )
        }
    }



    private fun phoneCodeSpinner() {
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