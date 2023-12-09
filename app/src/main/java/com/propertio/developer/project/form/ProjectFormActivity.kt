package com.propertio.developer.project.form

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.propertio.developer.R
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.common.address.AddressApi
import com.propertio.developer.api.common.address.City
import com.propertio.developer.api.common.address.District
import com.propertio.developer.api.common.address.Province
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.projectmanagement.ProjectDetail
import com.propertio.developer.databinding.ActivityProjectFormBinding
import com.propertio.developer.databinding.ToolbarBinding
import com.propertio.developer.dialog.model.CitiesModel
import com.propertio.developer.dialog.model.DistrictsModel
import com.propertio.developer.dialog.model.ProvinceModel
import com.propertio.developer.model.LitePhotosModel
import com.propertio.developer.project.viewmodel.ProjectFacilityViewModel
import com.propertio.developer.project.viewmodel.ProjectInformationLocationViewModel
import com.propertio.developer.project.viewmodel.ProjectMediaViewModel
import com.propertio.developer.project_management.ButtonNavigationProjectManagementClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProjectFormActivity : AppCompatActivity(), ButtonNavigationProjectManagementClickListener {

    val binding by lazy {
        ActivityProjectFormBinding.inflate(layoutInflater)
    }

    // ViewModels
    internal val projectInformationLocationViewModel : ProjectInformationLocationViewModel by viewModels()
    internal val projectMedia : ProjectMediaViewModel by viewModels()
    internal val projectFacility : ProjectFacilityViewModel by viewModels()
    internal var projectId: Int? = null

    internal val formsFragment = listOf(
        CreateProjectInformasiUmumFragment(),
        CreateProjectLokasiFragment(),
        CreateProjectMediaFragment(),
        CreateProjectFasilitasFragment(),
        CreateProjectInfrastrukturFragment(),
    )

    internal var currentFragmentIndex : Int = 0
        set(value) {
            if (value in formsFragment.indices) {
                Log.d("TAG", "set: $value")
                field = value
            } else {
                Log.d("TAG", "set: $value")
            }
        }

    private val developerApi by lazy {
        Retro(TokenManager(this).token!!)
            .getRetroClientInstance()
            .create(DeveloperApi::class.java)
    }

    private val addressApi by lazy {
        Retro(TokenManager(this).token!!)
            .getRetroClientInstance()
            .create(AddressApi::class.java)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val idFromIntent = intent.getIntExtra("EDIT_PROJECT", 0)
        intent.removeExtra("EDIT_PROJECT")
        if (idFromIntent != 0) {
            lifecycleScope.launch {
                Log.d("ViewModel", "onCreate: $idFromIntent")
                projectId = idFromIntent
                fetchDataProjectDetail(projectId!!)
            }

        }

        val bindingToolbar = binding.toolbarContainerProjectForm

        setToolbarToCreate(bindingToolbar)

        setInitialFragment()

    }


    private fun setInitialFragment() {
        replaceFragment(formsFragment[currentFragmentIndex])
    }

    internal fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_container_project_form, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


    private fun setToolbarToCreate(bindingToolbar: ToolbarBinding) {
        if (projectId != null) {
            bindingToolbar.textViewTitle.text = "Edit Proyek"
        } else {
            bindingToolbar.textViewTitle.text = "Tambah Proyek"
        }
    }

    private fun fetchDataProjectDetail(projectId: Int) {
        developerApi.getProjectDetail(projectId).enqueue(object : Callback<ProjectDetail> {
            override fun onResponse(call: Call<ProjectDetail>, response: Response<ProjectDetail>) {
                if (response.isSuccessful) {
                    val data = response.body()?.data

                    if (data != null) {
                        Log.d("ProjectFormActivity", "onResponse: $data")
                        lifecycleScope.launch {
                            loadDataToViewModel(data)
                        }
                    } else {
                        Log.w("ProjectFormActivity", "onResponse: data is null")
                    }
                } else {
                    Log.w("ProjectFormActivity", "onResponse: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ProjectDetail>, t: Throwable) {
                Log.e("ProjectFormActivity", "onFailure: ${t.message}")
            }


        })
    }

    private suspend fun loadDataToViewModel(data: ProjectDetail.ProjectDeveloper) {
        withContext(Dispatchers.IO) {
            projectInformationLocationViewModel.isAddressNotEdited = true
            projectInformationLocationViewModel.add(
                headline = data.headline,
                title = data.title,
                propertyTypeName = data.propertyType?.name,
                description = data.description,
                completedAt = data.completedAt,
                certificate = data.certificate,
                address = data.address?.address,
                postalCode = data.address?.postalCode,
                longitude = data.address?.longitude?.toDouble(),
                latitude = data.address?.latitude?.toDouble(),
                immersiveSiteplan = data.immersiveSiteplan,
                immersiveApps = data.immersiveApps,
                status = data.status,
                siteplanImageURL = data.siteplanImage,
            )

            setAddressList(data.address)
            projectMedia.add(
                projectPhotos = data.projectPhotos?.map {
                    LitePhotosModel(
                        id = it.id,
                        projectId = it.projectId,
                        filePath = it.filename,
                        isCover = it.isCover?.toInt() ?: 0,
                        caption = it.caption,
                    )
                }?: emptyList(),
                videoLink = data.projectVideos?.linkVideoURL,
                virtualTourName = data.projectVirtualTours?.name,
                virtualTourLink = data.projectVirtualTours?.linkVirtualTourURL,
            )
            if (data.projectDocuments?.isNotEmpty() == true) {
                projectMedia.add(
                    documentFilePath = data.projectDocuments?.first()?.filename
                )
            }

            data.projectFacilities?.forEach {
                if (it.facilityTypeId != null) {
                    projectFacility.addSelectedFacilities(it.facilityTypeId!!)
                }
            }



        }
    }

    private fun setAddressList(address: ProjectDetail.ProjectDeveloper.ProjectAddress?) {
        lifecycleScope.launch {
            val provinceName = address?.province
            val provinceId = withContext(Dispatchers.IO) {getProvinceId(provinceName) }
            if (provinceId == null) {
                Log.d("ProjectFormActivity", "onResponse: provinceId is null")
                return@launch
            }
            projectInformationLocationViewModel.addAdresss(
                province = ProvinceModel(
                    provinceId = provinceId,
                    provinceName = provinceName!!
                ),
            )
            val cityName = address.city
            val cityId = getCityId(cityName, provinceId) ?: return@launch
            projectInformationLocationViewModel.addAdresss(
                city = CitiesModel(
                    cityId,
                    provinceId,
                    cityName!!
                )
            )
            val districtName = address?.district
            val districtId = getDistrictId(districtName, cityId) ?: return@launch
            projectInformationLocationViewModel.addAdresss(
                district = DistrictsModel(
                    districtId,
                    cityId,
                    districtName!!
                )
            )
        }

    }

    private suspend fun getDistrictId(districtName: String?, cityId: String?): String? {
        return withContext(Dispatchers.IO) {
            try {
                val response = addressApi.getSuspendDistricts(cityId!!)
                if (response.isSuccessful) {
                    val data = response.body()

                    if (data == null) {
                        Log.e("ProjectFormActivity", "getDistrictId: data is null")
                        return@withContext null
                    }
                    Log.d("ProjectFormActivity", "getDistrictId: Try to get : ${data.find { it.name == districtName }?.id}")
                    // Return Result
                    data.find { it.name == districtName }?.id
                } else {
                    Log.e("ProjectFormActivity", "getDistrictId: ${response.message()}")

                    // Return Result
                    null
                }
            } catch (e: Exception) {
                Log.e("ProjectFormActivity", "getDistrictId: ${e.message}")

                // Return Result
                null
            }
        }
    }

    private suspend fun getCityId(cityName: String?, provinceId: String?): String? {
        return withContext(Dispatchers.IO) {
            try {
                val response = addressApi.getSuspendCities(provinceId!!)
                if (response.isSuccessful) {
                    val data = response.body()

                    if (data == null) {
                        Log.e("ProjectFormActivity", "getCityId: data is null")
                        return@withContext null
                    }
                    Log.d("ProjectFormActivity", "getCityId: Try to get : ${data.find { it.name == cityName }?.id}")
                    // Return Result
                    data.find { it.name == cityName }?.id
                } else {
                    Log.w("ProjectFormActivity", "getCityId: ${response.message()}")

                    // Return Result
                    null
                }
            } catch (e: Exception) {
                Log.e("ProjectFormActivity", "getCityId: ${e.message}")

                // Return Result
                null
            }
        }
    }

    private suspend fun getProvinceId(provinceName: String?): String? {
        return withContext(Dispatchers.IO) {
            try {
                val response = addressApi.getSuspendProvinces()
                if (response.isSuccessful) {
                    val data = response.body()

                    if (data == null) {
                        Log.e("ProjectFormActivity", "getProvinceId: data is null")
                        return@withContext null
                    }
                    Log.d("ProjectFormActivity", "getProvinceId: Try to get : ${data.find { it.name == provinceName }?.id}")
                    // Return Result
                    data.find { it.name == provinceName }?.id
                } else {
                    Log.e("ProjectFormActivity", "getProvinceId: ${response.message()}")

                    // Return Result
                    null
                }
            } catch (e: Exception) {
                Log.e("ProjectFormActivity", "getProvinceId: ${e.message}")

                // Return Result
                null
            }
        }
    }



    override fun onNextButtonProjectManagementClick() {
        if (currentFragmentIndex == formsFragment.size - 1) {
            val intent = Intent(this, CreateProjectSuccessActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        currentFragmentIndex++
        replaceFragment(formsFragment[currentFragmentIndex])
    }


    override fun onBackButtonProjectManagementClick() {
        if (currentFragmentIndex <= 0) {
            Log.d("ProjectForm", "Exit From Project Form")
            finish()
            return
        }

        currentFragmentIndex--
        replaceFragment(formsFragment[currentFragmentIndex])
    }




}