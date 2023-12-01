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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.propertio.developer.R
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.services.GooglePlacesService
import com.propertio.developer.databinding.FragmentCreateProjectLokasiBinding
import com.propertio.developer.dialog.CitiesSheetFragment
import com.propertio.developer.dialog.DistrictsSheetFragment
import com.propertio.developer.dialog.ProvinceSheetFragment
import com.propertio.developer.dialog.model.CitiesModel
import com.propertio.developer.dialog.model.DistrictsModel
import com.propertio.developer.dialog.viewmodel.CitiesSpinnerViewModel
import com.propertio.developer.dialog.viewmodel.DistrictsSpinnerViewModel
import com.propertio.developer.dialog.viewmodel.ProvinceSpinnerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.net.MalformedURLException
import java.net.URL

class CreateProjectLokasiFragment : Fragment() {

    private val binding by lazy { FragmentCreateProjectLokasiBinding.inflate(layoutInflater) }

    // Province, City, District
    private var isProvinceSelected = false
    private var isCitySelected = false
    private var isDistrictSelected = false
    private val provinceViewModel by lazy { ViewModelProvider(requireActivity())[ProvinceSpinnerViewModel::class.java] }
    private val cityViewModel by lazy { ViewModelProvider(requireActivity())[CitiesSpinnerViewModel::class.java] }
    private val districtViewModel by lazy { ViewModelProvider(requireActivity())[DistrictsSpinnerViewModel::class.java] }


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

            Toast.makeText(requireContext(), "Berhasil menambahkan gambar", Toast.LENGTH_SHORT).show()
        }

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

        val activity = activity as? ProjectFormActivity
        val activityBinding = activity?.binding



        // Spinner
        provinceSpinner()
        citySpinner()
        districtSpinner()


        // Siteplan/Masterplan
        if (imageUri == null) {
            binding.cardFileThumbnail.cardFileThumbnail.visibility = View.GONE
        }

        binding.buttonSiteplanPhotoProject.setOnClickListener {
            pickPhoto()
        }

        // Gmaps
        binding.buttonSearchMapsProject.setOnClickListener {
            val tempURL = binding.editTextLinkMapsProject.text.toString()

            if (tempURL == "" || tempURL.isEmpty()) {
                Toast.makeText(requireActivity(), "Mohon isi link google Maps terlebih dahulu", Toast.LENGTH_SHORT).show()
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


        activityBinding?.floatingButtonBack?.setOnClickListener {
            // TODO: do something here


            activity.onBackButtonProjectManagementClick()
        }

        activityBinding?.floatingButtonNext?.setOnClickListener {
            if (!isProvinceSelected && !isCitySelected && !isDistrictSelected) {
                Toast.makeText(requireActivity(), "Mohon pilih lokasi terlebih dahulu *", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val retro = Retro(TokenManager(requireActivity()).token)
                .getRetroClientInstance()
                .create(DeveloperApi::class.java)

            // TODO: fetch API



            activity.onNextButtonProjectManagementClick()

        }


    }


    private fun isValidUrl(url: String): Boolean {
        return try {
            URL(url)
            true
        } catch (e: MalformedURLException) {
            false
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
            isDistrictSelected = true
        }
    }



    private fun citySpinner() {
//        cityViewModel = ViewModelProvider(this)[CitiesSpinnerViewModel::class.java]

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
        Log.d("CreateProjectLokasiFragment", "resolveShortUrlAndRetrieveDetails: $latLong")

        val latitude = latLong?.first
        val longitude = latLong?.second
        Log.d("CreateProjectLokasiFragment","https://www.google.com/maps/search/?api=1&query=${latitude},${longitude}")
        return "https://www.google.com/maps/search/?api=1&query=${latitude},${longitude}"
    }



    private fun extractLatLongFromUrl(url: String): Pair<Double, Double>? {
        val latLongPattern = Regex("3d([-0-9.]+)!4d([-0-9.]+)")
        val matchResult = latLongPattern.find(url)

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