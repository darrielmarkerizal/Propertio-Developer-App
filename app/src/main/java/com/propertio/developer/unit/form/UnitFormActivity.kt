package com.propertio.developer.unit.form

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.propertio.developer.R
import com.propertio.developer.databinding.ActivityUnitFormBinding
import com.propertio.developer.project.ProjectDetailActivity.Companion.PROJECT_ID
import com.propertio.developer.unit.form.type.UnitDataGudangFragment
import com.propertio.developer.unit.form.type.UnitDataKantorFragment
import com.propertio.developer.unit.form.type.UnitDataKondominiumFragment
import com.propertio.developer.unit.form.type.UnitDataPabrikFragment
import com.propertio.developer.unit.form.type.UnitDataRuangUsahaFragment
import com.propertio.developer.unit.form.type.UnitDataRukoFragment
import com.propertio.developer.unit.form.type.UnitDataRumahFragment
import com.propertio.developer.unit.form.type.UnitDataTanahFragment
import com.propertio.developer.unit.form.type.UnitDataVillaFragment

class UnitFormActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityUnitFormBinding.inflate(layoutInflater)
    }

    private val formsFragment = listOf(
        CreateUnitUmumFragment(),
        UnitDataGudangFragment(),
        UnitDataKantorFragment(),
        UnitDataKondominiumFragment(),
        UnitDataPabrikFragment(),
        UnitDataRuangUsahaFragment(),
        UnitDataRukoFragment(),
        UnitDataRumahFragment(),
        UnitDataTanahFragment(),
        UnitDataVillaFragment(),
        CreateUnitMediaFragment()
    )

    private var currentFragmentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val propertyType = intent.getStringExtra("Property Type") // Ubah "propertyType" menjadi "Property Type"
        val projectId = intent.getIntExtra(PROJECT_ID, 0)

        Log.d("Unit Form Activity", "Project ID : $projectId")
        Log.d("Unit Form Activity", "Property Type : $propertyType")

        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container_unit_form, formsFragment[currentFragmentIndex])
            .commit()

        binding.floatingButtonNext.setOnClickListener {
            if (currentFragmentIndex < formsFragment.size - 1) {
                currentFragmentIndex++
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_container_unit_form, formsFragment[currentFragmentIndex])
                    .commit()
            }
        }

        binding.floatingButtonBack.setOnClickListener {
            if (currentFragmentIndex > 0) {
                currentFragmentIndex--
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_container_unit_form, formsFragment[currentFragmentIndex])
                    .commit()
            }
        }
    }
}