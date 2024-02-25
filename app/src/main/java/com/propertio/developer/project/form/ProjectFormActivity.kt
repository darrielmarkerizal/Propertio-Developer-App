package com.propertio.developer.project.form

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.propertio.developer.R
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.common.address.AddressApi
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.projectmanagement.ProjectDetail
import com.propertio.developer.databinding.ActivityProjectFormBinding
import com.propertio.developer.databinding.ToolbarBinding
import com.propertio.developer.dialog.model.CitiesModel
import com.propertio.developer.dialog.model.DistrictsModel
import com.propertio.developer.dialog.model.ProvinceModel
import com.propertio.developer.model.LitePhotosModel
import com.propertio.developer.model.ProjectDocument
import com.propertio.developer.permissions.NetworkAccess
import com.propertio.developer.project.ProjectDetailActivity.Companion.PROJECT_ID
import com.propertio.developer.project.list.FacilityAndInfrastructureTypeViewModel
import com.propertio.developer.project.viewmodel.ProjectFacilityViewModel
import com.propertio.developer.project.viewmodel.ProjectInformationLocationViewModel
import com.propertio.developer.project.viewmodel.ProjectMediaViewModel
import com.propertio.developer.project_management.ButtonNavigationProjectManagementClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProjectFormActivity : AppCompatActivity(), ButtonNavigationProjectManagementClickListener {

    val binding by lazy {
        ActivityProjectFormBinding.inflate(layoutInflater)
    }
    private var isCreateNew : Boolean = true

    // ViewModels
    internal val projectInformationLocationViewModel : ProjectInformationLocationViewModel by viewModels()
    internal val projectMedia : ProjectMediaViewModel by viewModels()
    internal val projectFacility : ProjectFacilityViewModel by viewModels()
    internal var projectId: Int? = null

    internal lateinit var facilityAndInfrastructureTypeViewModel : FacilityAndInfrastructureTypeViewModel

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
        projectInformationLocationViewModel.clear()
        val idFromIntent = intent.getIntExtra("EDIT_PROJECT", 0)
        intent.removeExtra("EDIT_PROJECT")


        val bindingToolbar = binding.toolbarContainerProjectForm



        if (idFromIntent != 0) {
            if (NetworkAccess.isNetworkAvailable(this).not()) run {
                NetworkAccess.buildNoConnectionToast(this).show()
                finish()
            }
            lifecycleScope.launch {
                Log.d("ViewModel", "onCreate: $idFromIntent")
                projectId = idFromIntent
                fetchDataProjectDetail(projectId!!)
            }

        } else {
            setInitialFragment()
        }

        setToolbarToCreate(bindingToolbar)


        if (NetworkAccess.isNetworkAvailable(this).not()) run {
            NetworkAccess.buildNoConnectionToast(this).show()
        }

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
        Log.d("ProjectFormActivity", "setToolbarToCreate: $projectId")
        if (projectId != null) {
            bindingToolbar.textViewTitle.text = "Edit Proyek"
            isCreateNew = false
        } else {
            bindingToolbar.textViewTitle.text = "Tambah Proyek"
            isCreateNew = true
        }
    }

    private fun fetchDataProjectDetail(projectId: Int) {
        showLoading()
        developerApi.getProjectDetail(projectId).enqueue(object : Callback<ProjectDetail> {
            override fun onResponse(call: Call<ProjectDetail>, response: Response<ProjectDetail>) {
                if (response.isSuccessful) {
                    val data = response.body()?.data

                    if (data != null) {
                        Log.d("ProjectFormActivity", "onResponse: $data")
                        lifecycleScope.launch {
                            loadDataToViewModel(data)
                            delay(1400)
                            hideLoading()
                            setInitialFragment()
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

    private fun showLoading() {
        binding.projectFormFetchLoadingIndicator.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.projectFormFetchLoadingIndicator.visibility = View.GONE
    }

    private fun loadDataToViewModel(data: ProjectDetail.ProjectDeveloper) {
        CoroutineScope(Dispatchers.IO).launch {
            this.launch {
                projectInformationLocationViewModel.isAddressNotEdited = true
            }
            this.launch {
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
            }
            this.launch {
                val lat = data.address?.latitude?.toDouble()
                val long = data.address?.longitude?.toDouble()
                if (lat != null && long != null) {
                    withContext(Dispatchers.Main) {
                        Log.d("ProjectFormActivity", "loadDataToViewModel: $lat, $long")
                        projectInformationLocationViewModel.selectedLocation.value = Pair(lat, long)
                    }
                }
            }
            this.launch {
                setAddressList(data.address)
            }
            this.launch {
                projectMedia.add(
                    projectPhotos = data.projectPhotos?.map {
                        LitePhotosModel(
                            id = it.id,
                            projectId = it.projectId,
                            filePath = it.filename,
                            isCover = it.isCover?.toInt() ?: 0,
                            caption = it.caption,
                        )
                    }?: emptyList<LitePhotosModel>().toMutableList(),
                    videoLink = data.projectVideos?.linkVideoURL,
                    virtualTourName = data.projectVirtualTours?.name,
                    virtualTourLink = data.projectVirtualTours?.linkVirtualTourURL,
                )
            }
            this.launch {
                if (data.projectDocuments?.isNotEmpty() == true) {
                    projectMedia.isDocumentNotEdited = true
                    projectMedia.addDocument(
                        document = ProjectDocument(
                            id = data.projectDocuments?.get(0)?.id,
                            projectId = data.projectDocuments?.get(0)?.projectId,
                            name = data.projectDocuments?.get(0)?.name,
                            filename = data.projectDocuments?.get(0)?.filename,
                            createdAt = data.projectDocuments?.get(0)?.createdAt,
                            updatedAt = data.projectDocuments?.get(0)?.updatedAt,
                        )
                    )
                }
            }
            this.launch {
                data.projectFacilities?.forEach {
                    if (it.facilityTypeId != null) {
                        projectFacility.addSelectedFacilities(it.facilityTypeId!!)
                    }
                }
            }
        }
    }

    private suspend fun setAddressList(address: ProjectDetail.ProjectDeveloper.ProjectAddress?) {
        projectInformationLocationViewModel.printLog("before add address")
        CoroutineScope(Dispatchers.IO).launch {
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
            val cityId = withContext(Dispatchers.IO) {getCityId(cityName, provinceId) }
            if (cityId == null) {
                Log.d("ProjectFormActivity", "onResponse: cityId is null")
                return@launch
            }
            projectInformationLocationViewModel.addAdresss(
                city = CitiesModel(
                    cityId,
                    provinceId,
                    cityName!!
                )
            )
            val districtName = address.district
            val districtId = withContext(Dispatchers.IO) {getDistrictId(districtName, cityId) }
            if (districtId == null) {
                Log.d("ProjectFormActivity", "onResponse: districtId is null")
                return@launch
            }
            projectInformationLocationViewModel.addAdresss(
                district = DistrictsModel(
                    districtId,
                    cityId,
                    districtName!!
                )
            )
            projectInformationLocationViewModel.printLog("after add address")
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
        if (NetworkAccess.isNetworkAvailable(this).not()) run {
            NetworkAccess.buildNoConnectionToast(this).show()
            return
        }

        if (currentFragmentIndex == formsFragment.size - 1) {
            binding.progressIndicatorProjectForm.setProgressCompat(100, true)
            val intentToSuccess = Intent(this, CreateProjectSuccessActivity::class.java)
            intentToSuccess.putExtra(IS_CREATE_NEW, isCreateNew)
            intentToSuccess.putExtra(PROJECT_ID, projectId)
            intentToSuccess.putExtra("Property Type", projectInformationLocationViewModel.propertyTypeName)
            startActivity(intentToSuccess)
            finish()
            return
        }
        currentFragmentIndex++
        val progressValue: Double = currentFragmentIndex.toDouble() / formsFragment.size.toDouble() * 100.0
        binding.progressIndicatorProjectForm.setProgressCompat(progressValue.toInt(), true)
        replaceFragment(formsFragment[currentFragmentIndex])
    }


    override fun onBackButtonProjectManagementClick() {
        if (currentFragmentIndex <= 0) {
            Log.d("ProjectForm", "Exit From Project Form")
            binding.progressIndicatorProjectForm.setProgressCompat(0, true)
            finish()
            return
        }
        currentFragmentIndex--
        val progressValue: Double = currentFragmentIndex.toDouble() / formsFragment.size.toDouble() * 100.0
        binding.progressIndicatorProjectForm.setProgressCompat(progressValue.toInt(), true)
        replaceFragment(formsFragment[currentFragmentIndex])
    }


    companion object {
        const val IS_CREATE_NEW = "ProjectFormActivityIsCreateNew"
    }

}