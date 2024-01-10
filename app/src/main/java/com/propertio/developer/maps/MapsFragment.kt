package com.propertio.developer.maps

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil.isValidUrl
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.propertio.developer.R
import com.propertio.developer.api.services.GeocodingApi
import com.propertio.developer.api.services.GoogleGeoCoding
import com.propertio.developer.databinding.FragmentMapsBinding
import com.propertio.developer.project.form.ProjectFormActivity
import com.propertio.developer.project.viewmodel.ProjectInformationLocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class MapsFragment : Fragment() {

    private var latitude : Double? = null
    private var longitude : Double? = null
    private lateinit var googleMap: GoogleMap

    private val binding by lazy { FragmentMapsBinding.inflate(layoutInflater) }
    private val formActivity by lazy { activity as ProjectFormActivity }
    private val activityBinding by lazy { formActivity.binding }

    private val projectInformationLocationViewModel : ProjectInformationLocationViewModel by activityViewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val callback = OnMapReadyCallback { googleMap ->
        this.googleMap = googleMap

        latitude = projectInformationLocationViewModel.latitude
        longitude = projectInformationLocationViewModel.longitude

        if (latitude != null && longitude != null) {
            val previousLocation = LatLng(latitude!!, longitude!!)
            googleMap.addMarker(MarkerOptions().position(previousLocation).title("Lokasi Proyek"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(previousLocation))
        } else {
            getCurrentLocation(googleMap)
        }

        googleMap.setOnMapLongClickListener {latLng ->

            CoroutineScope(Dispatchers.Main).launch {
                setTextLatLang(latLng.latitude, latLng.longitude)
                googleMap.addMarker(MarkerOptions().position(latLng).title("Lokasi Proyek"))

                val geoRetrofit = GoogleGeoCoding()
                    .getGeocodingInstance()
                    .create(GeocodingApi::class.java)

                // Load the local.properties file

                val response = withContext(Dispatchers.IO) {
                    geoRetrofit.getLocationFromLatLong(
                        latlng = "${latLng.latitude},${latLng.longitude}"
                    )
                }

                if (response.isSuccessful) {
                    binding.textViewLokasiTerpilih.text = response.body()?.results?.get(0)?.formattedAddress
                    setTextLatLang(latLng.latitude, latLng.longitude)
                } else {
                    // Handle the error
                }


            }




        }
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideNavigation()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        setupUI()
    }

    private fun setupUI() {
        setupSaveButton()
        setupOpenGMapsButton()
        setupSearchBar()
    }

    private fun setupSearchBar() {
        binding.textInputLayoutSearchBar.setEndIconOnClickListener {
            val tempURL = binding.textInputLayoutSearchBar.editText?.text.toString().trim()

            if (tempURL == "" || tempURL.isEmpty()) {
                if (latitude == null || longitude == null) {
                    getCurrentLocation(googleMap)
                    return@setEndIconOnClickListener
                }

                val newLocation = LatLng(latitude!!, longitude!!)
                googleMap.addMarker(MarkerOptions().position(newLocation).title("Lokasi Proyek"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation))


                return@setEndIconOnClickListener
            }
            // check if the tempURL is valid URL
            if (!isValidUrl(tempURL)) {
                Toast.makeText(requireActivity(), "URL tidak valid", Toast.LENGTH_SHORT).show()
                return@setEndIconOnClickListener
            }
            lifecycleScope.launch {
                try {
                    val longUrl = getLongGMAPSURL(tempURL)
                    val newLocation = longUrl.split("=").last().split(",").let {
                        LatLng(it[0].toDouble(), it[1].toDouble())
                    }

                    googleMap.addMarker(MarkerOptions().position(newLocation).title("Lokasi Proyek"))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation))

                } catch (
                    e: Exception
                ) {
                    Toast.makeText(requireActivity(), "URL yang diberikan tidak valid", Toast.LENGTH_SHORT).show()
                }

            }


        }
    }

    private fun setupOpenGMapsButton() {
        binding.buttonBukaGoogleMaps.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:0,0")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(mapIntent)
            }

        }
    }

    private fun setupSaveButton() {
        binding.buttonSimpanMaps.setOnClickListener {
            projectInformationLocationViewModel.selectedLocation.value = Pair(
                binding.textViewLatitude.text.toString().toDouble(),
                binding.textViewLongitude.text.toString().toDouble()
            )
            parentFragmentManager.popBackStack()
        }
    }

    private fun setTextLatLang(latitude: Double, longitude: Double) {
        binding.textViewLatitude.text = latitude.toString()
        binding.textViewLongitude.text = longitude.toString()
    }


    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(googleMap: GoogleMap) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Use the location object to get latitude and longitude
                val latLng = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
                setTextLatLang(latLng.latitude, latLng.longitude)
                // Use the latLng object to move the camera to the current location
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

            }
    }


    /**
     * Old
     */

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
            val geoRetrofit = GoogleGeoCoding()
                .getGeocodingInstance()
                .create(GeocodingApi::class.java)
            val address = url.split("place/").last().split("/").first()

            try {
                val response = geoRetrofit.geocodeCoordinate(address)
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

    private fun hideNavigation() {
        with(activityBinding) {
            floatingButtonNext.visibility = View.GONE
            floatingButtonBack.visibility = View.GONE
        }
    }

    private fun showNavigation() {
        with(activityBinding) {
            floatingButtonNext.visibility = View.VISIBLE
            floatingButtonBack.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showNavigation()
    }




}