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
        ActivityUnitFormBinding.inflate(layoutInflater)
    }

    private val formsFragment = mutableListOf<Fragment>(
        CreateUnitUmumFragment(),
        CreateUnitMediaFragment()
    )

    private var currentFragmentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val propertyType = intent.getStringExtra("Property Type")
        val projectId = intent.getIntExtra(PROJECT_ID, 0)

        Log.d("Unit Form Activity", "Project ID : $projectId")
        Log.d("Unit Form Activity", "Property Type : $propertyType")

        when (propertyType) {
            "gudang" -> formsFragment.add(1, UnitDataGudangFragment())
            "kantor" -> formsFragment.add(1, UnitDataKantorFragment())
            "kondominium" -> formsFragment.add(1, UnitDataKondominiumFragment())
            "pabrik" -> formsFragment.add(1, UnitDataPabrikFragment())
            "ruangUsaha" -> formsFragment.add(1, UnitDataRuangUsahaFragment())
            "ruko" -> formsFragment.add(1, UnitDataRukoFragment())
            "Rumah" -> formsFragment.add(1, UnitDataRumahFragment())
            "tanah" -> formsFragment.add(1, UnitDataTanahFragment())
            "villa" -> formsFragment.add(1, UnitDataVillaFragment())
            else -> Log.e("UnitFormActivity", "Invalid property type: $propertyType")
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container_unit_form, formsFragment[currentFragmentIndex])
            .commit()

        binding.toolbarContainerUnitForm.textViewTitle.text = "Tambah Unit"

        binding.floatingButtonNext.setOnClickListener {
            if (currentFragmentIndex < formsFragment.size - 1) {
                currentFragmentIndex++
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_container_unit_form, formsFragment[currentFragmentIndex])
                    .commit()
                binding.toolbarContainerUnitForm.textViewTitle.text = "Tambah Unit"
            }
        }

        binding.floatingButtonBack.setOnClickListener {
            if (currentFragmentIndex > 0) {
                currentFragmentIndex--
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_container_unit_form, formsFragment[currentFragmentIndex])
                    .commit()
                binding.toolbarContainerUnitForm.textViewTitle.text = "Edit Unit"
            } else if (currentFragmentIndex == 0) {
                val intentToProjectDetail = Intent(this, ProjectDetailActivity::class.java)
                startActivity(intentToProjectDetail)
            }
        }
    }
}