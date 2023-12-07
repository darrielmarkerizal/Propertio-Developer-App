package com.propertio.developer.unit.form

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.R
import com.propertio.developer.auth.RegisterActivity
import com.propertio.developer.databinding.ActivityUnitFormBinding
import com.propertio.developer.project.ProjectDetailActivity.Companion.PROJECT_ID
import com.propertio.developer.unit_management.ButtonNavigationUnitManagementClickListener
import com.propertio.developer.unit.form.type.UnitDataApartemenFragment
import com.propertio.developer.unit.form.type.UnitDataGudangFragment
import com.propertio.developer.unit.form.type.UnitDataKantorFragment
import com.propertio.developer.unit.form.type.UnitDataKondominiumFragment
import com.propertio.developer.unit.form.type.UnitDataPabrikFragment
import com.propertio.developer.unit.form.type.UnitDataRuangUsahaFragment
import com.propertio.developer.unit.form.type.UnitDataRukoFragment
import com.propertio.developer.unit.form.type.UnitDataRumahFragment
import com.propertio.developer.unit.form.type.UnitDataTanahFragment
import com.propertio.developer.unit.form.type.UnitDataVillaFragment
import org.jetbrains.annotations.Contract

class UnitFormActivity : AppCompatActivity(), ButtonNavigationUnitManagementClickListener {

    val unitFormViewModel : UnitFormViewModel by lazy {
        ViewModelProvider(this).get(UnitFormViewModel::class.java)
    }

    val binding by lazy {
        Log.d("UnitFormActivity", "Inflating layout")
        ActivityUnitFormBinding.inflate(layoutInflater)
    }


    private val launcherToSuccess = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            setResult(RESULT_OK)
            finish()
        }
    }

    private val formsFragment = mutableListOf<Fragment>(
        CreateUnitUmumFragment(),
        CreateUnitMediaFragment()
    )

    private var currentFragmentIndex = 0
    internal var unitId : Int? = null
    internal var projectId : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("UnitFormActivity", "onCreate called")
        setContentView(binding.root)

        binding.toolbarContainerUnitForm.textViewTitle.text = "Unit"

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
        if (currentFragmentIndex == formsFragment.size - 1) {
            val intentToSuccess = Intent(this, CreateUnitSuccessActivity::class.java)
            launcherToSuccess.launch(intentToSuccess)
            return
        }

        currentFragmentIndex++
        Log.d("UnitFormActivity", "Next button clicked, currentFragmentIndex: $currentFragmentIndex")
        replaceFragment(formsFragment[currentFragmentIndex])



    }

    override fun onBackButtonUnitManagementClick() {
        if (currentFragmentIndex <= 0) {
            Log.d("UnitFormActivity", "Navigating to ProjectDetailActivity")
            finish()
            return
        }
        currentFragmentIndex--
        Log.d("UnitFormActivity", "Back button clicked, currentFragmentIndex: $currentFragmentIndex")
        replaceFragment(formsFragment[currentFragmentIndex])
    }



    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container_unit_form, fragment)
            .commit()
    }
}