package com.propertio.developer.project.form

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.load
import com.propertio.developer.R
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.projectmanagement.PostStoreProjectLocationResponse
import com.propertio.developer.api.services.GeocodingApi
import com.propertio.developer.api.services.GoogleGeoCoding
import com.propertio.developer.databinding.FragmentCreateProjectLokasiBinding
import com.propertio.developer.dialog.CitiesSheetFragment
import com.propertio.developer.dialog.DistrictsSheetFragment
import com.propertio.developer.dialog.ProvinceSheetFragment
import com.propertio.developer.dialog.model.CitiesModel
import com.propertio.developer.dialog.model.DistrictsModel
import com.propertio.developer.dialog.model.ProvinceModel
import com.propertio.developer.dialog.viewmodel.CitiesSpinnerViewModel
import com.propertio.developer.dialog.viewmodel.DistrictsSpinnerViewModel
import com.propertio.developer.dialog.viewmodel.ProvinceSpinnerViewModel
import com.propertio.developer.project.viewmodel.ProjectInformationLocationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.net.MalformedURLException
import java.net.URL

class CreateProjectLokasiFragment : Fragment() {

    private val binding by lazy { FragmentCreateProjectLokasiBinding.inflate(layoutInflater) }
    private val formActivity by lazy { activity as ProjectFormActivity }
    private val activityBinding by lazy { formActivity.binding }

    // ViewModels
    private val projectInformationLocationViewModel : ProjectInformationLocationViewModel by activityViewModels()

    // Province, City, District
    private var isProvinceSelected = false
    private var isCitySelected = false
    private var isDistrictSelected = false
    private val provinceViewModel by lazy { ViewModelProvider(requireActivity())[ProvinceSpinnerViewModel::class.java] }
    private val cityViewModel by lazy { ViewModelProvider(requireActivity())[CitiesSpinnerViewModel::class.java] }
    private val districtViewModel by lazy { ViewModelProvider(requireActivity())[DistrictsSpinnerViewModel::class.java] }


    // Location
    private var latitude : Double = 0.0
    private var longitude : Double = 0.0


    // Siteplan/Masterplan
    private var imageUri: Uri? = null
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            loadImage(imageUri.toString())

            // get image name
            val filename = getPathFromUri(imageUri).split("/").last()
            binding.cardFileThumbnail.textViewFilename.text = filename

            Toast.makeText(requireActivity(), "Berhasil menambahkan gambar", Toast.LENGTH_SHORT).show()
        }

    }

    // developerApi
    private val developerApi by lazy {
        Retro(TokenManager(requireActivity()).token)
            .getRetroClientInstance()
            .create(DeveloperApi::class.java)
    }

    private fun getPathFromUri(uri: Uri?): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireActivity().contentResolver.query(uri!!, projection, null, null, null)
        cursor?.moveToNext()
        val columnIndex = cursor?.getColumnIndex(MediaStore.Images.Media.DATA) ?: 0
        val path = cursor?.getString(if (columnIndex >= 0) columnIndex else 0)
        cursor?.close()
        return path!!
    }

    private fun pickPhoto() {
        val mediaStoreIntent = Intent(Intent.ACTION_PICK).apply {
            setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        }
        launcher.launch(mediaStoreIntent)
    }



    private fun loadImage(pictureProfileUrl: String) {
        binding.cardFileThumbnail.cardFileThumbnail.visibility = View.VISIBLE
        val urlWithTimestamp : String = if (pictureProfileUrl.startsWith("http")) {
            "$pictureProfileUrl?timestamp=${System.currentTimeMillis()}"
        } else {
            pictureProfileUrl
        }

        // load image
        binding.cardFileThumbnail.imageViewThumbnail.load(urlWithTimestamp) {
            crossfade(true)
            placeholder(R.drawable.baseline_attach_file_24)
            error(R.drawable.baseline_attach_file_24)
        }
        Log.d("CreateProjectLokasiFragment", "loadImage: $urlWithTimestamp")
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Spinner
        provinceSpinner()
        citySpinner()
        districtSpinner()

        // Load Data
        if (projectInformationLocationViewModel.isUploaded) {
            loadTextData()
            checkSpinnerData(true)
        } else {
            checkSpinnerData()
        }

        binding.buttonHubungiKamiLokasiProyek.setOnClickListener {
            openContactUs()
        }

        binding.buttonContohImmersiveLokasiProyek.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://immersive.propertio.id/siteplan-model/sp-000001/index.html"))
            startActivity(intent)
        }




        // Siteplan/Masterplan
        if (imageUri == null) {
            binding.cardFileThumbnail.cardFileThumbnail.visibility = View.GONE
        }

        binding.buttonSiteplanPhotoProject.setOnClickListener {
            pickPhoto()
        }

        // Gmaps
        binding.buttonSearchMapsProject.setOnClickListener {
            val tempURL = binding.editTextLinkMapsProject.text.toString().trim()

            if (tempURL == "" || tempURL.isEmpty()) {
                val intentToMaps = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${projectInformationLocationViewModel.latitude},${projectInformationLocationViewModel.longitude}"))
                startActivity(intentToMaps)

                return@setOnClickListener
            }
            // check if the tempURL is valid URL
            if (!isValidUrl(tempURL)) {
                Toast.makeText(requireActivity(), "URL tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getLongGMAPSURL(tempURL)))
                    startActivity(intent)
                } catch (
                    e: Exception
                ) {
                    Toast.makeText(requireActivity(), "URL yang diberikan tidak valid", Toast.LENGTH_SHORT).show()
                }

            }


        }


        activityBinding.floatingButtonBack.setOnClickListener {
            // TODO: do something here


            formActivity.onBackButtonProjectManagementClick()
        }

        activityBinding.floatingButtonNext.setOnClickListener {
            projectInformationLocationViewModel.printLog()

            if (!isProvinceSelected && !isCitySelected && !isDistrictSelected && !projectInformationLocationViewModel.isAddressNotEdited) {
                binding.spinnerProvinceProject.error = "Wajib diisi"
                binding.spinnerCityProject.error = "Wajib diisi"
                binding.spinnerDistrictProject.error = "Wajib diisi"
                Toast.makeText(requireActivity(), "Mohon pilih lokasi terlebih dahulu *", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            projectInformationLocationViewModel.printLog("when click next button")

            // Form Request
            Log.d("CreateProjectLokasiFragment", "Update or Edit: ${projectInformationLocationViewModel.isUploaded}")
            if (projectInformationLocationViewModel.isUploaded) {
                updateProjectLocation()
            } else {
                createProjectLocation()
            }





        }


    }


    private fun openContactUs() {
        // intent to whatsapp
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://wa.me/6285702750455")
        }
        startActivity(intent)
    }



    private fun loadTextData() {
        binding.editTextAddressProject.setText(projectInformationLocationViewModel.address)
        binding.editTextPosProject.setText(projectInformationLocationViewModel.postalCode)
        val siteplanImageURL = "https://www.google.com/maps/search/?api=1&query=${projectInformationLocationViewModel.latitude},${projectInformationLocationViewModel.longitude}"
        binding.editTextLinkMapsProject.setText(siteplanImageURL)
        binding.editTextLinkImmersiveProject.setText(projectInformationLocationViewModel.immersiveSiteplan)
        binding.editTextLinkImmersiveAppsProject.setText(projectInformationLocationViewModel.immersiveApps)


        projectInformationLocationViewModel.printLog()
    }

    private fun updateProjectLocation() {

        // get Text
        val headline = projectInformationLocationViewModel.headline!!
        val title = projectInformationLocationViewModel.title!!
        val propertyTypeId = projectInformationLocationViewModel.propertyTypeId!!
        val description = projectInformationLocationViewModel.description
        val completedAt = projectInformationLocationViewModel.completedAt
        val certificate = projectInformationLocationViewModel.certificate!!

        val province : String
        val city : String
        val district : String
        if (projectInformationLocationViewModel.isAddressNotEdited) {
            province = projectInformationLocationViewModel.savedProvince?.provinceName!!
            city = projectInformationLocationViewModel.savedCity?.citiesName!!
            district = projectInformationLocationViewModel.savedDistrict?.districtsName!!
        } else {
            province = provinceViewModel.provinceData.value?.provinceName!!
            city = cityViewModel.citiesData.value?.citiesName!!
            district = districtViewModel.districtsData.value?.districtsName!!
        }

        val address = binding.editTextAddressProject.text.toString()
        val postalCode = binding.editTextPosProject.text.toString()
        val longitude = longitude.toString()
        val latitude = latitude.toString()
        val immersiveSiteplan = binding.editTextLinkImmersiveProject.text.toString()
        val immersiveApps = binding.editTextLinkImmersiveAppsProject.text.toString()

        projectInformationLocationViewModel.printLog("when click next button $propertyTypeId.toString()")


        // Image Request Body
        val headlineBody = headline.toRequestBody("text/plain".toMediaTypeOrNull())
        val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val propertyTypeIdBody = propertyTypeId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val descriptionBody = description?.toRequestBody("text/plain".toMediaTypeOrNull())
        val completedAtBody = completedAt?.toRequestBody("text/plain".toMediaTypeOrNull())
        val certificateBody = certificate.toRequestBody("text/plain".toMediaTypeOrNull())
        val provinceBody = province.toRequestBody("text/plain".toMediaTypeOrNull())
        val cityBody = city.toRequestBody("text/plain".toMediaTypeOrNull())
        val districtBody = district.toRequestBody("text/plain".toMediaTypeOrNull())
        val addressBody = address.toRequestBody("text/plain".toMediaTypeOrNull())
        val postalCodeBody = postalCode.toRequestBody("text/plain".toMediaTypeOrNull())
        val longitudeBody = longitude.toRequestBody("text/plain".toMediaTypeOrNull())
        val latitudeBody = latitude.toRequestBody("text/plain".toMediaTypeOrNull())
        val immersiveSiteplanBody = immersiveSiteplan.toRequestBody("text/plain".toMediaTypeOrNull())
        val immersiveAppsBody = immersiveApps.toRequestBody("text/plain".toMediaTypeOrNull())

        var siteplanImage : MultipartBody.Part? = null

        if (imageUri != null) {
            Log.d("CreateProjectLokasiFragment", "onViewCreated: $imageUri")
            val fileDir = requireContext().applicationContext.filesDir
            val file = File(fileDir, "siteplan_image_create_project.jpg")
            val fileInputStream = requireContext().contentResolver.openInputStream(imageUri!!)
            val fileOutputStream = FileOutputStream(file)
            fileInputStream!!.copyTo(fileOutputStream)
            fileInputStream.close()
            fileOutputStream.close()

            val fileSizeInBytes = file.length()
            val fileSizeInKB = fileSizeInBytes / 1024
            val fileSizeInMB = fileSizeInKB / 1024

            val maxFileSizeInMB = 4 // MB

            if (fileSizeInMB > maxFileSizeInMB) {
                Toast.makeText(requireContext(), "Ukuran gambar terlalu besar (diatas 4MB)", Toast.LENGTH_SHORT).show()
                return
            }

            siteplanImage = MultipartBody.Part.createFormData(
                "siteplan_image",
                file.name,
                file.asRequestBody("image/*".toMediaTypeOrNull())
            )

        }

        developerApi.updateProjectLocation(
            id = formActivity.projectId!!,
            headline = headlineBody,
            title = titleBody,
            propertyTypeId = propertyTypeIdBody,
            certificate = certificateBody,
            province = provinceBody,
            city = cityBody,
            district = districtBody,

            description = descriptionBody,
            completedAt = completedAtBody,
            address = addressBody,
            postalCode = postalCodeBody,
            longitude = longitudeBody,
            latitude = latitudeBody,
            immersiveSiteplan = immersiveSiteplanBody,
            immersiveApps = immersiveAppsBody,
            siteplanImage = siteplanImage
        ).enqueue(object : Callback<PostStoreProjectLocationResponse> {
            override fun onResponse(
                call: Call<PostStoreProjectLocationResponse>,
                response: Response<PostStoreProjectLocationResponse>
            ) {
                if (response.isSuccessful) {
                    val responseData = response.body()?.data
                    if (responseData != null) {
                        formActivity.projectId = responseData.id
                        projectInformationLocationViewModel.isAlreadyUploaded.postValue(true)
                        formActivity.onNextButtonProjectManagementClick()
                    } else {
                        Toast.makeText(requireActivity(), "Terjadi Kesalahan", Toast.LENGTH_SHORT).show()
                        Log.w("CreateProjectLokasiFragment", "onResponse: responseData is null")
                    }
                } else {
                    // Mendapatkan Error Message
                    var errorMessage = response.errorBody()?.string()
                    errorMessage = errorMessage?.split("\"data\":")?.last()
                    errorMessage = errorMessage?.trim('{', '}')

                    if (errorMessage != null) {
                        for (error in errorMessage.split(',')){
                            Toast.makeText(requireActivity(), "Gagal membuat project: $error", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Log.w("CreateProjectLokasiFragment", "onResponse Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<PostStoreProjectLocationResponse>, t: Throwable) {
                Log.e("CreateProjectLokasiFragment", "onFailure: ${t.message}")
                Toast.makeText(requireActivity(), "Terjadi Kesalahan", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun createProjectLocation() {

        // get Text
        val headline = projectInformationLocationViewModel.headline!!
        val title = projectInformationLocationViewModel.title!!
        val propertyTypeId = projectInformationLocationViewModel.propertyTypeId!!
        val description = projectInformationLocationViewModel.description
        val completedAt = projectInformationLocationViewModel.completedAt
        val certificate = projectInformationLocationViewModel.certificate!!

        val province = provinceViewModel.provinceData.value?.provinceName!!
        val city = cityViewModel.citiesData.value?.citiesName!!
        val district = districtViewModel.districtsData.value?.districtsName!!
        val address = binding.editTextAddressProject.text.toString()
        val postalCode = binding.editTextPosProject.text.toString()
        val immersiveSiteplan = binding.editTextLinkImmersiveProject.text.toString()
        val immersiveApps = binding.editTextLinkImmersiveAppsProject.text.toString()

        projectInformationLocationViewModel.printLog("when click next button $propertyTypeId.toString()")


        // Image Request Body
        val headlineBody = headline.toRequestBody("text/plain".toMediaTypeOrNull())
        val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val propertyTypeIdBody = propertyTypeId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val descriptionBody = description?.toRequestBody("text/plain".toMediaTypeOrNull())
        val completedAtBody = completedAt?.toRequestBody("text/plain".toMediaTypeOrNull())
        val certificateBody = certificate.toRequestBody("text/plain".toMediaTypeOrNull())
        val provinceBody = province.toRequestBody("text/plain".toMediaTypeOrNull())
        val cityBody = city.toRequestBody("text/plain".toMediaTypeOrNull())
        val districtBody = district.toRequestBody("text/plain".toMediaTypeOrNull())
        val addressBody = address.toRequestBody("text/plain".toMediaTypeOrNull())
        val postalCodeBody = postalCode.toRequestBody("text/plain".toMediaTypeOrNull())
        val longitudeBody = longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val latitudeBody = latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val immersiveSiteplanBody = immersiveSiteplan.toRequestBody("text/plain".toMediaTypeOrNull())
        val immersiveAppsBody = immersiveApps.toRequestBody("text/plain".toMediaTypeOrNull())

        var siteplanImage : MultipartBody.Part? = null

        if (imageUri != null) {
            Log.d("CreateProjectLokasiFragment", "onViewCreated: $imageUri")
            val fileDir = requireContext().applicationContext.filesDir
            val file = File(fileDir, "siteplan_image_create_project.jpg")
            val fileInputStream = requireContext().contentResolver.openInputStream(imageUri!!)
            val fileOutputStream = FileOutputStream(file)
            fileInputStream!!.copyTo(fileOutputStream)
            fileInputStream.close()
            fileOutputStream.close()

            val fileSizeInBytes = file.length()
            val fileSizeInKB = fileSizeInBytes / 1024
            val fileSizeInMB = fileSizeInKB / 1024

            val maxFileSizeInMB = 4 // MB

            if (fileSizeInMB > maxFileSizeInMB) {
                Toast.makeText(requireContext(), "Ukuran gambar terlalu besar (diatas 4MB)", Toast.LENGTH_SHORT).show()
                return
            }

            siteplanImage = MultipartBody.Part.createFormData(
                "siteplan_image",
                file.name,
                file.asRequestBody("image/*".toMediaTypeOrNull())
            )

        }


        developerApi.uploadProjectLocation(
            headline = headlineBody,
            title = titleBody,
            propertyTypeId = propertyTypeIdBody,
            certificate = certificateBody,
            province = provinceBody,
            city = cityBody,
            district = districtBody,

            description = descriptionBody,
            completedAt = completedAtBody,
            address = addressBody,
            postalCode = postalCodeBody,
            longitude = longitudeBody,
            latitude = latitudeBody,
            immersiveSiteplan = immersiveSiteplanBody,
            immersiveApps = immersiveAppsBody,
            siteplanImage = siteplanImage
        ).enqueue(object : Callback<PostStoreProjectLocationResponse> {
            override fun onResponse(
                call: Call<PostStoreProjectLocationResponse>,
                response: Response<PostStoreProjectLocationResponse>
            ) {
                if (response.isSuccessful) {
                    val responseData = response.body()?.data
                    if (responseData != null) {
                        formActivity.projectId = responseData.id
                        projectInformationLocationViewModel.isAlreadyUploaded.postValue(true)
                        formActivity.onNextButtonProjectManagementClick()
                    } else {
                        Toast.makeText(requireActivity(), "Terjadi Kesalahan", Toast.LENGTH_SHORT).show()
                        Log.w("CreateProjectLokasiFragment", "onResponse: responseData is null")
                    }
                } else {
                    // Mendapatkan Error Message
                    Log.w("CreateProjectLokasiFragment", "onResponse Create Error: ${response.errorBody()?.string() ?: "Unknown Error"}")
                    Log.w("CreateProjectLokasiFragment", "onResponse Create Error Not Successful: " +
                            "\n\tResponse Code ${response.code()}" +
                            "\n\t${response.body()?.status}" +
                            "\n\t${response.body()?.message}" +
                            "\n\t${response.body()?.data}"
                    )

                    var errorMessage = response.errorBody()?.string()
                    errorMessage = errorMessage?.split("\"data\":")?.last()
                    errorMessage = errorMessage?.trim('{', '}')


                    if (errorMessage != null) {
                        for (error in errorMessage.split(',')){
                            Toast.makeText(requireActivity(), "Gagal membuat project: $error", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Log.w("CreateProjectLokasiFragment", "onResponse Create Error: ${response.body()?.message}")
                }
            }

            override fun onFailure(call: Call<PostStoreProjectLocationResponse>, t: Throwable) {
                Log.e("CreateProjectLokasiFragment", "onFailure: ${t.message}")
                Toast.makeText(requireActivity(), "Terjadi Kesalahan", Toast.LENGTH_SHORT).show()
            }

        })
    }



    private fun isValidUrl(url: String): Boolean {
        return try {
            URL(url)
            true
        } catch (e: MalformedURLException) {
            false
        }
    }

    private fun checkSpinnerData(isAlreadyUploaded: Boolean = false) {
        lifecycleScope.launch {
            val province : ProvinceModel?
            val city : CitiesModel?
            val district : DistrictsModel?
            if (isAlreadyUploaded && projectInformationLocationViewModel.isAddressNotEdited) {
                binding.spinnerProvinceProject.text = projectInformationLocationViewModel.savedProvince?.provinceName
                binding.spinnerCityProject.text = projectInformationLocationViewModel.savedCity?.citiesName
                binding.spinnerDistrictProject.text = projectInformationLocationViewModel.savedDistrict?.districtsName
                return@launch
            } else {
                province = projectInformationLocationViewModel.province?.copy()
                city = projectInformationLocationViewModel.city?.copy()
                district = projectInformationLocationViewModel.district?.copy()
            }

            Log.d("CreateProjectLokasiFragment", "checkSpinnerData: ${province?.provinceId} ${province?.provinceName}")
            Log.d("CreateProjectLokasiFragment", "checkSpinnerData: ${city?.provinceId} ${city?.citiesName}")
            Log.d("CreateProjectLokasiFragment", "checkSpinnerData: ${district?.districtsId} ${district?.districtsName}")


            if (province != null) {
                for (i in 0 .. 3) {
                    provinceViewModel.provinceData.value = province
                    isProvinceSelected = true
                    binding.spinnerProvinceProject.text = province.provinceName

                    if (provinceViewModel.getProvinceData().provinceId == province.provinceId) {
                        break
                    }
                }
                for (i in 0 .. 3) {
                    if (city == null) {
                        break
                    }

                    cityViewModel.citiesData.value = city
                    isCitySelected = true
                    binding.spinnerCityProject.text = city.citiesName

                    if (cityViewModel.getCitiesData().citiesId == city.citiesId) {
                        break
                    }
                }
                for (i in 0 .. 3) {
                    if (district == null) {
                        break
                    }

                    districtViewModel.districtsData.value = district
                    isDistrictSelected = true
                    binding.spinnerDistrictProject.text = district.districtsName

                    if (districtViewModel.getDistrictsData().districtsId == district.districtsId) {
                        break
                    }
                }
            }
        }



    }


    private fun districtSpinner() {
//        districtViewModel = ViewModelProvider(this)[DistrictsSpinnerViewModel::class.java]

        binding.spinnerDistrictProject.setOnClickListener {
            if (isCitySelected && isProvinceSelected) {
                DistrictsSheetFragment().show(parentFragmentManager, "DistrictsSheetFragment")
                Log.d("CreateProjectLokasiFragment", "districtSpinner. is selected :$isDistrictSelected")
            } else {
                Toast.makeText(requireActivity(), "Pilih Kota terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }

        districtViewModel.districtsData.observe(viewLifecycleOwner) {
            binding.spinnerDistrictProject.text = it.districtsName
            binding.spinnerDistrictProject.error = null

            projectInformationLocationViewModel.isAddressNotEdited = false

            isDistrictSelected = true

            projectInformationLocationViewModel.district = it
        }
    }



    private fun citySpinner() {

        binding.spinnerCityProject.setOnClickListener {
            if (isProvinceSelected) {
                CitiesSheetFragment().show(parentFragmentManager, "CitiesSheetFragment")
                Log.d("CreateProjectLokasiFragment", "citySpinner. is selected :$isCitySelected")
            } else {
                Toast.makeText(requireActivity(), "Pilih Provinsi terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }


        cityViewModel.citiesData.observe(viewLifecycleOwner) {
            binding.spinnerCityProject.text = it.citiesName
            binding.spinnerCityProject.error = null

            projectInformationLocationViewModel.isAddressNotEdited = false

            isCitySelected = true
            isDistrictSelected = false

            districtViewModel.districtsData
                .postValue(
                    DistrictsModel(
                        districtsId = "",
                        citiesId = it.citiesId,
                        districtsName = "Pilih Kecamatan"
                    )
                )

            projectInformationLocationViewModel.city = it
        }
    }



    private fun provinceSpinner() {
//        provinceViewModel = ViewModelProvider(this)[ProvinceSpinnerViewModel::class.java]

        binding.spinnerProvinceProject.setOnClickListener {
            ProvinceSheetFragment().show(parentFragmentManager, "ProvinceSheetFragment")
            Log.d("CreateProjectLokasiFragment", "provinceSpinner. is selected :$isProvinceSelected")
        }

        provinceViewModel.provinceData.observe(viewLifecycleOwner) {
            Log.d("CreateProjectLokasiFragment", "provinceSpinner: ${it.provinceName}")
            binding.spinnerProvinceProject.text = it.provinceName
            binding.spinnerProvinceProject.error = null

            projectInformationLocationViewModel.isAddressNotEdited = false

            isProvinceSelected = true
            isCitySelected = false
            isDistrictSelected = false


            cityViewModel.citiesData
                .postValue(
                    CitiesModel(
                        citiesId = "",
                        provinceId = it.provinceId,
                        citiesName = "Pilih Kota"
                    )
                )

            projectInformationLocationViewModel.province = it
        }


    }



    private suspend fun getLongGMAPSURL(shortUrl: String) : String {
        // first. check if the url is already long
        if (shortUrl.contains("google.com/maps")) {
            Log.d("CreateProjectLokasiFragment", "resolveShortUrlAndRetrieveDetails: $shortUrl")
            return shortUrl
        }

        // Resolve Short URL
        val resolvedUrl = resolveShortUrl(shortUrl) // Implement this function to handle the redirect and get the full URL
        Log.d("CreateProjectLokasiFragment", "resolveShortUrlAndRetrieveDetails: $resolvedUrl")

        // Get Lat Long
        val latLong = extractLatLongFromUrl(resolvedUrl) // Implement this function to extract the lat long from the URL
        if (latLong == null) {
            Log.e("CreateProjectLokasiFragment", "resolveShortUrlAndRetrieveDetails: latLong is null")
        }
        Log.d("CreateProjectLokasiFragment", "lat long: $latLong")

        latitude = latLong?.first ?: 0.0
        longitude = latLong?.second ?: 0.0
        Log.d("CreateProjectLokasiFragment","https://www.google.com/maps/search/?api=1&query=${latitude},${longitude}")
        return "https://www.google.com/maps/search/?api=1&query=${latitude},${longitude}"
    }



    private suspend fun extractLatLongFromUrl(url: String): Pair<Double, Double>? {
        val latLongPattern = Regex("!3d([-0-9.]+).*!4d([-0-9.]+)")
        val matchResult = latLongPattern.find(url)
        Log.d("CreateProjectLokasiFragment", "extractLatLongFromUrl: ${matchResult?.destructured}")
        if (matchResult == null) {
            Log.d("CreateProjectLokasiFragment", "extractLatLongFromUrl: null")
            //TODO: Geocoding!! AIzaSyAN6a7kSklwRHnNojU72nDnCfhYGhrATh0
            val geoRetrofit = GoogleGeoCoding()
                .getGeocodingInstance()
                .create(GeocodingApi::class.java)
            val address = url.split("place/").last().split("/").first()

            try {
                val response = geoRetrofit.geocodeCoordinate(address, "AIzaSyAN6a7kSklwRHnNojU72nDnCfhYGhrATh0")
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        val location = data.results?.first()?.geometry?.location
                        val lat = location?.lat
                        val lng = location?.lng
                        if (lat != null && lng != null) {
                            return Pair(lat, lng)
                        } else {
                            Log.w("CreateProjectLokasiFragment", "extractLatLongFromUrl: location is null")
                        }

                    } else {
                        Log.d("CreateProjectLokasiFragment", "extractLatLongFromUrl: data is null ${response.errorBody()?.string()}")

                    }
                } else {
                    Log.e("CreateProjectLokasiFragment", "extractLatLongFromUrl: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("CreateProjectLokasiFragment", "extractLatLongFromUrl: ${e.message}")
            }



        }
        return matchResult?.let {
            val (latitude, longitude) = it.destructured
            Pair(latitude.toDouble(), longitude.toDouble())
        }
    }



    private suspend fun resolveShortUrl(shortUrl: String): String {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder().url(shortUrl).build()
            client.newCall(request).execute().use { response ->
                return@withContext response.request.url.toString() // This should be the full URL
            }
        }
    }





}