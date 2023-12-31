package com.propertio.developer.unit.form

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.propertio.developer.R
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.unitmanagement.UnitDetailResponse
import com.propertio.developer.databinding.ActivityUnitFormBinding
import com.propertio.developer.databinding.ToolbarBinding
import com.propertio.developer.model.LitePhotosModel
import com.propertio.developer.model.UnitDocument
import com.propertio.developer.project.ProjectDetailActivity.Companion.PROJECT_ID
import com.propertio.developer.unit.UnitMediaViewModel
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UnitFormActivity : AppCompatActivity(), ButtonNavigationUnitManagementClickListener {

    private val developerApi by lazy {
        Retro(TokenManager(this).token)
            .getRetroClientInstance()
            .create(DeveloperApi::class.java)
    }

    internal val unitFormViewModel : UnitFormViewModel by viewModels()
    internal val unitMedia : UnitMediaViewModel by viewModels()

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

        val propertyType = intent.getStringExtra("Property Type")
        val projectId = intent.getIntExtra(PROJECT_ID, 0)

        if (projectId != 0) {
            unitFormViewModel.projectId = projectId
            Log.d("UnitFormActivity", "onCreate: projectId: $projectId")
        } else {
            Log.e("UnitFormActivity", "onCreate: projectId is null")
        }

        val unitIdFromIntent = intent.getIntExtra(PROJECT_DETAIL_UID, 0)
        val projectIdFromIntent = intent.getIntExtra(PROJECT_DETAIL_PID, 0)

        if (unitIdFromIntent != 0) {
            lifecycleScope.launch {
                Log.d("UnitFormActivity", "onCreate Fetch Edit: $unitIdFromIntent")
                unitId = unitIdFromIntent
                fetchUnitDetail(projectIdFromIntent, unitId!!)
                startUnitForm()
            }
        } else {
            Log.e("UnitFormActivity", "onCreate: unitId is null")

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
            startUnitForm()

        }

        setToolbarToCreate(binding.toolbarContainerUnitForm)

    }

    private fun startUnitForm() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container_unit_form, formsFragment[currentFragmentIndex])
            .commit()
    }

    private fun setToolbarToCreate(bindingToolbar: ToolbarBinding) {
        Log.d("UnittFormActivity", "setToolbarToCreate: $unitId")
        if (unitId != 0) {
            bindingToolbar.textViewTitle.text = "Edit Unit"
        } else {
            bindingToolbar.textViewTitle.text = "Tambah Unit"
        }
    }

    override fun onNextButtonUnitManagementClick() {
        if (currentFragmentIndex == formsFragment.size - 1) {
            binding.progressIndicatorUnitForm.setProgressCompat(100, true)
            val intentToSuccess = Intent(this, CreateUnitSuccessActivity::class.java)
            launcherToSuccess.launch(intentToSuccess)
            return
        }

        currentFragmentIndex++
        Log.d("UnitFormActivity", "Next button clicked, currentFragmentIndex: $currentFragmentIndex")
        val progressValue: Double = currentFragmentIndex.toDouble() / formsFragment.size.toDouble() * 100.0
        binding.progressIndicatorUnitForm.setProgressCompat(progressValue.toInt(), true)
        replaceFragment(formsFragment[currentFragmentIndex])



    }

    override fun onBackButtonUnitManagementClick() {
        if (currentFragmentIndex <= 0) {
            binding.progressIndicatorUnitForm.setProgressCompat(0, true)
            Log.d("UnitFormActivity", "Navigating to ProjectDetailActivity")
            setResult(RESULT_OK)
            finish()
            return
        }
        currentFragmentIndex--
        Log.d("UnitFormActivity", "Back button clicked, currentFragmentIndex: $currentFragmentIndex")
        val progressValue: Double = currentFragmentIndex.toDouble() / formsFragment.size.toDouble() * 100.0
        binding.progressIndicatorUnitForm.setProgressCompat(progressValue.toInt(), true)
        replaceFragment(formsFragment[currentFragmentIndex])
    }



    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container_unit_form, fragment)
            .commit()
    }

    companion object {
        const val PROJECT_DETAIL_UID = "project_detail_uid"
        const val PROJECT_DETAIL_PID = "project_detail_pid"
    }

    private suspend fun fetchUnitDetail(projectId: Int, unitId: Int) {
        lifecycleScope.launch {
            val response = withContext(Dispatchers.IO) {developerApi.getUnitDetail(projectId, unitId).execute()}

            try {
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    if (data != null) {
                        Log.d("UnitFormActivity", "onResponse: $data")
                        Log.d("UnitFormActivity", "onResponse: ${data.propertyType}")
                        lifecycleScope.launch {
                            loadDataToViewModel(data)

                            when (data.propertyType) {
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
                                else -> Log.e(
                                    "UnitFormActivity",
                                    "Invalid property type: ${data.propertyType}"
                                )
                            }
                        }
                    } else {
                        Log.e("UnitFormActivity", "onResponse: data is null")
                    }
                } else {
                    Log.e("UnitFormActivity", "onResponse: ${response.errorBody()}")
                }

            } catch (e: Exception) {
                Log.e("UnitFormActivity", "onFailure: ${e.message}")
            }

        }
    }

    private fun loadDataToViewModel(data : UnitDetailResponse.Unit) {
        unitFormViewModel.add(
            namaUnit = data.title.toString(),
            propertyType = data.propertyType.toString(),
            deskripsiUnit = data.description.toString(),
            stokUnit = data.stock.toString(),
            hargaUnit = data.price.toString(),
            luasTanah = data.surfaceArea.toString(),
            luasBangunan = data.buildingArea.toString(),
            jumlahKamar = data.bedroom.toString(),
            jumlahKamarMandi = data.bathroom.toString(),
            jumlahLantai = data.floor.toString(),
            interiorType = data.interior.toString(),
            roadAccessType = data.roadAccess.toString(),
            parkingType = data.garage.toString(),
            electricityType = data.powerSupply.toString(),
            waterType = data.waterSupply.toString(),
            projectId = data.projectId!!.toInt(),
            unitId = data.id!!.toInt()
        )

        val unitVirtualTour = data.unitVirtualTour
        var virtualTourName : String? = null
        var virtualTourLink : String? = null

        if (unitVirtualTour is UnitDetailResponse.Unit.UnitVirtualTour) {
            // unitVirtualTour is an object

            virtualTourName = unitVirtualTour.name
            virtualTourLink = unitVirtualTour.link

        } else if (unitVirtualTour is Array<*> && unitVirtualTour.isArrayOf<UnitDetailResponse.Unit.UnitVirtualTour>()) {
            // unitVirtualTour is an array
            val unitVirtualTourArray = unitVirtualTour as Array<UnitDetailResponse.Unit.UnitVirtualTour>
            if (unitVirtualTour.isNotEmpty()) {
                virtualTourName = unitVirtualTourArray[0].name
                virtualTourLink = unitVirtualTourArray[0].link
            }

        }

        unitMedia.add(
            unitPhoto = data.unitPhotos?.map {
                LitePhotosModel(
                    id = it.id,
                    projectId = it.unitId,
                    filePath = it.filename,
                    isCover = it.isCover!!.toInt(),
                    type = it.type,
                    caption = it.caption
                )
            } ?: emptyList(),
            videoLink = data.unitVideo?.link        ,
            virtualTourName = virtualTourName,
            virtualTourLink = virtualTourLink,
            linkModel = data.unitModel?.link
        )


        Log.d("UnitFormActivity", "loadDataToViewModel: ${data.unitVideo} ${data.unitVideo?.link}")

        if (data.unitDocuments?.isNotEmpty() == true) {
            unitMedia.isDocumentNotEdited = true
            unitMedia.addDocument(
                document = UnitDocument(
                    id = data.unitDocuments?.get(0)?.id,
                    unitId = data.unitDocuments?.get(0)?.unitId,
                    name = data.unitDocuments?.get(0)?.name,
                    type = data.unitDocuments?.get(0)?.type,
                    filename = data.unitDocuments?.get(0)?.filename,
                    createdAt = data.unitDocuments?.get(0)?.createdAt,
                    updatedAt = data.unitDocuments?.get(0)?.updatedAt,
                )
            )
        }
    }
}