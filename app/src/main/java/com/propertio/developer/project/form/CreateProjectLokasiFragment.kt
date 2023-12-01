package com.propertio.developer.project.form

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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

class CreateProjectLokasiFragment : Fragment() {

    private val binding by lazy { FragmentCreateProjectLokasiBinding.inflate(layoutInflater) }

    // Province, City, District
    private var isProvinceSelected = false
    private var isCitySelected = false
    private var isDistrictSelected = false
    private val provinceViewModel by lazy { ViewModelProvider(requireActivity())[ProvinceSpinnerViewModel::class.java] }
    private val cityViewModel by lazy { ViewModelProvider(requireActivity())[CitiesSpinnerViewModel::class.java] }
    private val districtViewModel by lazy { ViewModelProvider(requireActivity())[DistrictsSpinnerViewModel::class.java] }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        
        lifecycleScope.launch {
            checkMapsApi()
        }


        // Spinner
        provinceSpinner()
        citySpinner()
        districtSpinner()




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


    private suspend fun checkMapsApi() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().followRedirects(false).build())
            .build()

        val googlePlacesService = retrofit.create(GooglePlacesService::class.java)
        resolveShortUrlAndRetrieveDetails(googlePlacesService, "https://maps.app.goo.gl/qZt3Jyh3qpvAgY6a8")

    }

    suspend fun resolveShortUrlAndRetrieveDetails(
        googlePlacesService: GooglePlacesService,
        shortUrl: String,
    ) {
        // Resolve Short URL
        val resolvedUrl = resolveShortUrl(shortUrl) // Implement this function to handle the redirect and get the full URL
        Log.d("CreateProjectLokasiFragment", "resolveShortUrlAndRetrieveDetails: $resolvedUrl")

        // Get Lat Long
        val latLong = extractLatLongFromUrl(resolvedUrl) // Implement this function to extract the lat long from the URL
        Log.d("CreateProjectLokasiFragment", "resolveShortUrlAndRetrieveDetails: $latLong")

        val latitude = latLong?.first
        val longitude = latLong?.second
        Log.d("CreateProjectLokasiFragment","https://www.google.com/maps/search/?api=1&query=${latitude},${longitude}")
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