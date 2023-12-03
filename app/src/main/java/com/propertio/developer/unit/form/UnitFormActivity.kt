package com.propertio.developer.unit.form

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.propertio.developer.R
import com.propertio.developer.databinding.ActivityUnitFormBinding
import com.propertio.developer.project.ProjectDetailActivity
import com.propertio.developer.project.ProjectDetailActivity.Companion.PROJECT_ID
import com.propertio.developer.unit.form.type.*

class UnitFormActivity : AppCompatActivity() {

    private val binding by lazy {
        Log.d("UnitFormActivity", "Inflating layout")
        ActivityUnitFormBinding.inflate(layoutInflater)
    }

    private val formsFragment = mutableListOf<Fragment>(
        CreateUnitUmumFragment(),
        CreateUnitMediaFragment()
    )

    private var currentFragmentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("UnitFormActivity", "onCreate called")
        setContentView(binding.root)

        val propertyType = intent.getStringExtra("Property Type")
        val projectId = intent.getIntExtra(PROJECT_ID, 0)

        Log.d("UnitFormActivity", "Project ID : $projectId")
        Log.d("UnitFormActivity", "Property Type : $propertyType")

        when (propertyType) {
            "Gudang" -> formsFragment.add(1, UnitDataGudangFragment())
            "Kantor" -> formsFragment.add(1, UnitDataKantorFragment())
            "Kondominium" -> formsFragment.add(1, UnitDataKondominiumFragment())
            "Pabrik" -> formsFragment.add(1, UnitDataPabrikFragment())
            "Ruang usaha" -> formsFragment.add(1, UnitDataRuangUsahaFragment())
            "Ruko" -> formsFragment.add(1, UnitDataRukoFragment())
            "Rumah" -> formsFragment.add(1, UnitDataRumahFragment())
            "Tanah" -> formsFragment.add(1, UnitDataTanahFragment())
            "Villa" -> formsFragment.add(1, UnitDataVillaFragment())
            "Apartemen" -> formsFragment.add(1, UnitDataApartemenFragment())
            else -> Log.e("UnitFormActivity", "Invalid property type: $propertyType")
        }

        Log.d("UnitFormActivity", "Starting fragment transaction")
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container_unit_form, formsFragment[currentFragmentIndex])
            .commit()

        binding.toolbarContainerUnitForm.textViewTitle.text = "Tambah Unit"

        binding.floatingButtonNext.setOnClickListener {
            if (currentFragmentIndex < formsFragment.size - 1) {
                currentFragmentIndex++
                Log.d("UnitFormActivity", "Next button clicked, currentFragmentIndex: $currentFragmentIndex")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_container_unit_form, formsFragment[currentFragmentIndex])
                    .commit()
                binding.toolbarContainerUnitForm.textViewTitle.text = "Tambah Unit"
            }
        }

        binding.floatingButtonBack.setOnClickListener {
            if (currentFragmentIndex > 0) {
                currentFragmentIndex--
                Log.d("UnitFormActivity", "Back button clicked, currentFragmentIndex: $currentFragmentIndex")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_container_unit_form, formsFragment[currentFragmentIndex])
                    .commit()
                binding.toolbarContainerUnitForm.textViewTitle.text = "Edit Unit"
            } else if (currentFragmentIndex == 0) {
                Log.d("UnitFormActivity", "Navigating to ProjectDetailActivity")
                val intentToProjectDetail = Intent(this, ProjectDetailActivity::class.java)
                startActivity(intentToProjectDetail)
            }
        }
    }
}