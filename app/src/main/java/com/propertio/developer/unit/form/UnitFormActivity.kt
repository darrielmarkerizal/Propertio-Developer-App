package com.propertio.developer.unit.form

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.R
import com.propertio.developer.databinding.ActivityUnitFormBinding
import com.propertio.developer.project.ProjectDetailActivity.Companion.PROJECT_ID
import com.propertio.developer.unit.form.type.*
import com.propertio.developer.unit_management.ButtonNavigationUnitManagementClickListener
import androidx.lifecycle.Observer

class UnitFormActivity : AppCompatActivity(), ButtonNavigationUnitManagementClickListener {

    val unitFormViewModel : UnitFormViewModel by lazy {
        ViewModelProvider(this).get(UnitFormViewModel::class.java)
    }

    val binding by lazy {
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

        binding.toolbarContainerUnitForm.textViewTitle.text = "Tambah Unit"

        val propertyType = intent.getStringExtra("Property Type") // Bisa ganti jadi val propertyType = "Apartemen"
        val projectId = intent.getIntExtra(PROJECT_ID, 0)
        unitFormViewModel.updateProjectId(projectId)
        Log.d("UnitFormActivity", "Project ID updated successfully in ViewModel: $projectId")

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
    }

    override fun onNextButtonUnitManagementClick() {
        navigateToNextFragment()
    }

    override fun onBackButtonUnitManagementClick() {
        navigateToPreviousFragment()
    }

    private fun navigateToNextFragment() {
        if (currentFragmentIndex < formsFragment.size - 1) {
            currentFragmentIndex++
            Log.d("UnitFormActivity", "Next button clicked, currentFragmentIndex: $currentFragmentIndex")
            replaceFragment(formsFragment[currentFragmentIndex])
            binding.toolbarContainerUnitForm.textViewTitle.text = "Tambah Unit"
        }
    }

    private fun navigateToPreviousFragment() {
        if (currentFragmentIndex > 0) {
            currentFragmentIndex--
            Log.d("UnitFormActivity", "Back button clicked, currentFragmentIndex: $currentFragmentIndex")
            replaceFragment(formsFragment[currentFragmentIndex])
            binding.toolbarContainerUnitForm.textViewTitle.text = "Edit Unit"
        } else if (currentFragmentIndex == 0) {
            Log.d("UnitFormActivity", "Navigating to ProjectDetailActivity")
            finish()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container_unit_form, fragment)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        unitFormViewModel.clearData()
    }
}