package com.propertio.developer.profile

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import coil.load
import coil.transform.CircleCropTransformation
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.propertio.developer.R
import com.propertio.developer.TokenManager
import com.propertio.developer.api.DomainURL.DOMAIN
import com.propertio.developer.api.Retro
import com.propertio.developer.api.common.address.AddressApi
import com.propertio.developer.api.common.address.Province
import com.propertio.developer.api.profile.ProfileApi
import com.propertio.developer.api.profile.ProfileResponse
import com.propertio.developer.api.profile.ProfileUpdateRequest
import com.propertio.developer.auth.LoginActivity
import com.propertio.developer.database.profile.ProfileDatabase
import com.propertio.developer.database.profile.ProfileTable
import com.propertio.developer.database.profile.ProfileTableDao
import com.propertio.developer.databinding.FragmentProfileBinding
import com.propertio.developer.dialog.CitiesSheetFragment
import com.propertio.developer.dialog.ProfileCitiesSheetFragment
import com.propertio.developer.dialog.ProvinceSheetFragment
import com.propertio.developer.dialog.model.CitiesModel
import com.propertio.developer.dialog.model.ProvinceModel
import com.propertio.developer.dialog.viewmodel.CitiesSpinnerViewModel
import com.propertio.developer.dialog.viewmodel.ProvinceSpinnerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ProfileFragment : Fragment() {

    private val binding by lazy {
        FragmentProfileBinding.inflate(layoutInflater)
    }

    private lateinit var profileDao : ProfileTableDao


    private var body: MultipartBody.Part? = null

//    private lateinit var profileViewModel: ProfileViewModel

    // Spinner
    private var isProvinceSelected : Boolean = true
    private var isCitySelected : Boolean = true
    private lateinit var provinceViewModel: ProvinceSpinnerViewModel
    private lateinit var cityViewModel: CitiesSpinnerViewModel


    private var imageUri: Uri? = null
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == RESULT_OK) {
            imageUri = result.data?.data
            loadImage(imageUri.toString())

            Toast.makeText(requireContext(), "Berhasil memilih gambar", Toast.LENGTH_SHORT).show()
        }

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Room
        val databaseRoom = ProfileDatabase.getDatabase(requireActivity())
        profileDao = databaseRoom.profileTableDao()


        // Layout
        swipeRefreshHandler()


        // Initialize View Model
        initializeViewModel()


        // fetch Data From Api
        lifecycleScope.launch {
            fetchProfileData()
        }


        // Spinner
        provinceSpinner()
        citySpinner()


        // Upload Image
        binding.buttonAddProfilePictureProfil.setOnClickListener {
            pickPhoto()
        }


        // Change Password
        binding.btnUbahKataSandiProfil.setOnClickListener {
            val intent = Intent(activity, ChangePassword::class.java)
            startActivity(intent)
        }


        // Update Profile
        binding.btnSimpanProfil.setOnClickListener {
            if (isProvinceSelected && isCitySelected) {
                updateProfile()
            }
        }


        // Log Out
        binding.btnLogoutProfil.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialog)
                .setTitle("Keluar")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya") { _, _ ->
                    TokenManager(requireContext()).deleteToken()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                .setNegativeButton("Tidak", null)
                .show()
        }



    }

    private fun updateProfile() {
        // get Text
        val userID = binding.txtIdProfile.text.toString()
        val email = binding.txtEmailProfile.text.toString()
        val fullName = binding.edtNamaLengkapProfil.text.toString()
        val phone = binding.edtNomorTeleponProfil.text.toString()
        val province = binding.buttonProvincesSelectionProfile.text.toString()
        val city = binding.spinnerCityProfile.text.toString()
        val address = binding.edtAlamatProfil.text.toString()

        // Form Request
        val fullNameBody = fullName.toRequestBody("text/plain".toMediaTypeOrNull())
        val phoneBody = phone.toRequestBody("text/plain".toMediaTypeOrNull())
        val provinceBody = province.toRequestBody("text/plain".toMediaTypeOrNull())
        val cityBody = city.toRequestBody("text/plain".toMediaTypeOrNull())
        val addressBody = address.toRequestBody("text/plain".toMediaTypeOrNull())


        // Image Request Body
        val fileDir = requireContext().applicationContext.filesDir
        val file = File(fileDir, "image.jpg")
        val fileInputStream = requireContext().contentResolver.openInputStream(imageUri!!)
        val fileOutputStream = FileOutputStream(file)
        fileInputStream?.copyTo(fileOutputStream)
        fileInputStream!!.copyTo(fileOutputStream)
        fileInputStream.close()

        val pictureProfileFile = MultipartBody.Part.createFormData(
            "picture_profile",
            file.name,
            file.asRequestBody("image/*".toMediaTypeOrNull())
        )



        Log.d("ProfileFragment", "full name: $fullName, body: $fullNameBody")
        Log.d("ProfileFragment", "phone: $phone, body: $phoneBody")
        Log.d("ProfileFragment", "province: $province, body: $provinceBody")
        Log.d("ProfileFragment", "city: $city, body: $cityBody")
        Log.d("ProfileFragment", "address: $address, body: $addressBody")
        Log.d("ProfileFragment", "file: $file, file name: ${file.name}, file input stream: $fileInputStream, file output stream: $fileOutputStream")


        val retro = Retro(TokenManager(requireContext()).token)
            .getRetroClientInstance()
            .create(ProfileApi::class.java)

        retro.updateProfile(
            fullName = fullNameBody,
            phone = phoneBody,
            address = addressBody,
            province = provinceBody,
            city = cityBody,
            pictureProfileFile = pictureProfileFile
        ).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("ProfileFragment", "onResponse Success: ${response.body()?.message}")

                    // TODO : Snackbar Undo Disini. Ambil data dari Room


                    // Update Room Kalau tidak di Undo
                    Toast.makeText(requireContext(), "Berhasil mengubah profil", Toast.LENGTH_SHORT).show()
                    lifecycleScope.launch {
                        updateRoomDatabase(
                            ProfileTable(
                                idUser = userID,
                                email = email,
                                name = fullName,
                                phone = phone,
                                province = province,
                                city = city,
                                address = address,
                            )
                        )
                    }
                    val data = response.body()?.data?.userData
                    if (data != null) {
                        if (
                            (data.fullName != fullName)
                            || (data.phone != phone)
                            || (data.province != province)
                            || (data.city != city)
                            || (data.address != address)
                            )
                        {
                            updateUI(response.body()?.data)
                        }

                    }
                    // End Update Room Kalau tidak di Undo

                }
                else if (response.code() == 422) {
                    Log.d("ProfileFragment", "onResponse 422: ${response.errorBody()?.string()}")
                }
                else {
                    Log.e("ProfileFragment", "onResponse Else: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Log.e("ProfileFragment", "onFailure: ${t.message}", t)
            }

        })


    }

    private suspend fun updateRoomDatabase(profileTable: ProfileTable) {
        withContext(Dispatchers.IO) {
            profileDao.update(profileTable)
            Log.i("ProfileFragment", "updateRoomDatabase: $profileTable")
        }

    }

    private fun pickPhoto() {
        val mediaStoreIntent = Intent(Intent.ACTION_PICK).apply {
            setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        }
        launcher.launch(mediaStoreIntent)
    }

    private fun loadImage(pictureProfileUrl: String) {
        binding.imgProfil.load(pictureProfileUrl) {
            crossfade(true)
            transformations(CircleCropTransformation())
            placeholder(R.drawable.ic_profil)
            error(R.drawable.ic_profil)
        }
    }


    /**
     * Initialize view model.
     * - profileViewModel: ProfileViewModel
     * - cityViewModel: CitiesSpinnerViewModel
     * - provinceViewModel: ProvinceSpinnerViewModel
     */
    private fun initializeViewModel() {
//        val viewModelFactory = ProfileViewModelFactory(TokenManager(requireContext()).token!!)
//        profileViewModel = ViewModelProvider(this, viewModelFactory)[ProfileViewModel::class.java]
        cityViewModel = ViewModelProvider(requireActivity())[CitiesSpinnerViewModel::class.java]
        provinceViewModel = ViewModelProvider(requireActivity())[ProvinceSpinnerViewModel::class.java]
    }

    private fun swipeRefreshHandler() {
        val swipeRefreshLayout = binding.swipeRefreshLayout
        isCitySelected = true
        isProvinceSelected = true
        swipeRefreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                fetchProfileData()
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }


    private fun updateUI(data: ProfileResponse.ProfileData?) {
        data?.let { profile ->

            binding.txtIdProfile.text = profile.accountId
            binding.txtEmailProfile.text = profile.email

            val userData = profile.userData

            if (userData == null) {
                Log.w("ProfileFragment", "updateUI: userData is null")
            }
            else {
                binding.edtNamaLengkapProfil.setText(userData.fullName)
                setTextViewPhoneNumber(userData.phone)

                setTextViewSpinner(listOf(userData.province, userData.city))

                binding.edtAlamatProfil.setText(userData.address)

                userData.pictureProfile?.let { url ->
                    loadImage(DOMAIN + url)
                }

                Log.w("ProfileFragment", "updateUI: data is updated")
            }
        }

        if (data == null){
            Log.w("ProfileFragment", "updateUI: data is null")
        }

    }

    private fun setTextViewSpinner(listOf: List<String?>) {
        Log.d("ProfileFragment", "setTextViewSpinner: $listOf")

        if (listOf[0] != null && listOf[1] != null) {
            binding.buttonProvincesSelectionProfile.text = listOf[0]
            binding.spinnerCityProfile.text = listOf[1]
        }

    }


    private fun setTextViewPhoneNumber(phone: String?) {
        Log.d("ProfileFragment", "setTextViewPhoneNumber: $phone")
        binding.edtNomorTeleponProfil.setText(phone)
    }


    private suspend fun fetchProfileData() {
        withContext(Dispatchers.IO) {

            val retro = Retro(TokenManager(requireActivity()).token)
            val profileApi = retro.getRetroClientInstance().create(ProfileApi::class.java)

            profileApi.getProfile().enqueue(object : Callback<ProfileResponse> {
                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Log.e("ProfileViewModel", "Failed to fetch profile data: ${t.message}")
                    useLocalData()

                }

                override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                    if (response.isSuccessful) {
                        Log.d("ProfileViewModel", "Profile data fetched successfully: ${response.body()?.data}")
                        val data = response.body()?.data
                        updateUI(data)
                        if (data != null) {
                            if (data.userData != null) {
                                data.userData?.province?.let {
                                    getViewModelData(it)
                                }
                            }
                        }

                    } else {
                        Log.e("ProfileViewModel", "Failed to fetch profile data: ${response.errorBody()}")
                        useLocalData()
                    }


                }



            })

        }




    }

    private fun getViewModelData(province: String) {
        val retro = Retro(TokenManager(requireContext()).token)
            .getRetroClientInstance()
            .create(AddressApi::class.java)

        retro.getProvinces().enqueue(object : Callback<List<Province>> {
            override fun onResponse(
                call: Call<List<Province>>,
                response: Response<List<Province>>
            ) {
                if (response.isSuccessful) {
                    Log.d("ProfileFragment", "onResponse: ${response.body()}")
                    val data = response.body()

                    if (data != null) {
                        val provinceId = data.find { it.name == province }?.id

                        if (provinceId != null) {
                            provinceViewModel.provinceData
                                .postValue(
                                    ProvinceModel(
                                        provinceId,
                                        province
                                    )
                                )
                        }
                        else {
                            Log.w("ProfileFragment", "onResponse: provinceId is null")
                        }


                    } else {
                        Log.w("ProfileFragment", "onResponse: data is null")
                    }

                } else {
                    Log.e("ProfileFragment", "onResponse: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<List<Province>>, t: Throwable) {
                Log.e("ProfileFragment", "onFailure: ${t.message}", t)
            }
        })
    }



    private fun useLocalData() {
        Log.d("ProfileFragment", "useLocalData: ")
        lifecycleScope.launch {
            val data = profileDao.localProfile
            Log.d("ProfileFragment", "useLocalData: $data")

            val localProfileData = ProfileResponse.ProfileData()
            localProfileData.accountId = data.idUser
            localProfileData.email = data.email
            localProfileData.userData = ProfileResponse.ProfileData.UserData().apply {
                this.fullName = data.name
                this.phone = data.phone
                this.province = data.province
                this.city = data.city
                this.address = data.address
            }

            updateUI(localProfileData)
        }
    }


    /*
     * Spinner
     */


    private fun citySpinner() {
        cityViewModel = ViewModelProvider(requireActivity())[CitiesSpinnerViewModel::class.java]


        binding.spinnerCityProfile.setOnClickListener {
            if (isProvinceSelected) {
                CitiesSheetFragment().show(parentFragmentManager, "CitiesSheetFragment")
            } else {
                Toast.makeText(requireContext(), "Pilih Provinsi terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }

        cityViewModel.citiesData.observe(viewLifecycleOwner) {
            binding.spinnerCityProfile.text = it.citiesName
            isCitySelected = true
        }
    }

    private fun provinceSpinner() {
        provinceViewModel = ViewModelProvider(requireActivity())[ProvinceSpinnerViewModel::class.java]
        binding.buttonProvincesSelectionProfile.setOnClickListener {
            ProvinceSheetFragment().show(parentFragmentManager, "ProvinceSheetFragment")
            isProvinceSelected = true
            Log.d("ProfileFragment", "provinceSpinner. is selected :$isProvinceSelected")
        }

        provinceViewModel.provinceData.observe(viewLifecycleOwner) {
            binding.buttonProvincesSelectionProfile.text = it.provinceName

            isProvinceSelected = true
            isCitySelected = false

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




}