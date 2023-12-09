package com.propertio.developer.profile

import android.app.Activity.RESULT_OK
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.propertio.developer.PropertioDeveloperApplication
import com.propertio.developer.R
import com.propertio.developer.TokenManager
import com.propertio.developer.api.DomainURL.DOMAIN
import com.propertio.developer.api.Retro
import com.propertio.developer.api.common.address.AddressApi
import com.propertio.developer.api.common.address.Province
import com.propertio.developer.api.profile.ProfileApi
import com.propertio.developer.api.profile.ProfileResponse
import com.propertio.developer.auth.LoginActivity
import com.propertio.developer.database.profile.ProfileDatabase
import com.propertio.developer.database.profile.ProfileTable
import com.propertio.developer.database.profile.ProfileTableDao
import com.propertio.developer.databinding.FragmentProfileBinding
import com.propertio.developer.dialog.CitiesSheetFragment
import com.propertio.developer.dialog.ProvinceSheetFragment
import com.propertio.developer.dialog.model.CitiesModel
import com.propertio.developer.dialog.model.ProvinceModel
import com.propertio.developer.dialog.viewmodel.CitiesSpinnerViewModel
import com.propertio.developer.dialog.viewmodel.ProvinceSpinnerViewModel
import com.propertio.developer.project.ProjectViewModel
import com.propertio.developer.project.ProjectViewModelFactory
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
import java.io.FileOutputStream
import java.net.URL

class ProfileFragment : Fragment() {

    private val binding by lazy {
        FragmentProfileBinding.inflate(layoutInflater)
    }

    private lateinit var profileDao : ProfileTableDao

    private lateinit var projectViewModel: ProjectViewModel


    // Spinner
    private var keepCity = true
    private var isProvinceSelected : Boolean = true
    private var isCitySelected : Boolean = true
    private val provinceViewModel by lazy { ViewModelProvider(requireActivity())[ProvinceSpinnerViewModel::class.java] }
    private val cityViewModel by lazy { ViewModelProvider(requireActivity())[CitiesSpinnerViewModel::class.java] }



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

        val factory = ProjectViewModelFactory(
            (requireActivity().application as PropertioDeveloperApplication).repository
        )
        projectViewModel = ViewModelProvider(requireActivity(), factory)[ProjectViewModel::class.java]


        // Layout
        swipeRefreshHandler()



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

                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            profileDao.deleteAll()
                            projectViewModel.deleteAllLocalProjects()
                        }
                    }

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
        var pictureProfileFile: MultipartBody.Part? = null

        if (imageUri != null) {
            Log.d("ProfileFragment", "updateProfile: imageUri is not null")
            val fileDir = requireContext().applicationContext.filesDir
            val file = File(fileDir, "profile_picture.jpg")
            val fileInputStream = requireContext().contentResolver.openInputStream(imageUri!!)
            val fileOutputStream = FileOutputStream(file)
            fileInputStream!!.copyTo(fileOutputStream)
            fileInputStream.close()
            fileOutputStream.close()

            val fileSizeInBytes = file.length()
            val fileSizeInKB = fileSizeInBytes / 1024
            val fileSizeInMB = fileSizeInKB / 1024

            val maxFileSizeInMB = 5 // MB

            if (fileSizeInMB > maxFileSizeInMB) {
                Toast.makeText(requireContext(), "Ukuran gambar terlalu besar", Toast.LENGTH_SHORT).show()
                return
            }

            pictureProfileFile = MultipartBody.Part.createFormData(
                "picture_profile_file",
                file.name,
                file.asRequestBody("image/*".toMediaTypeOrNull())
            )


        } else {
            Log.d("ProfileFragment", "updateProfile: imageUri is null")
            val file = File(requireContext().applicationContext.filesDir, "profile_picture.jpg")

            pictureProfileFile = MultipartBody.Part.createFormData(
                "picture_profile_file",
                file.name,
                file.asRequestBody("image/*".toMediaTypeOrNull())
            )
        }




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
        val urlWithTimestamp : String = if (pictureProfileUrl.startsWith("http")) {
            "$pictureProfileUrl?timestamp=${System.currentTimeMillis()}"
        } else {
            pictureProfileUrl
        }

        // load image
        binding.imgProfil.load(urlWithTimestamp) {
            crossfade(true)
            transformations(CircleCropTransformation())
            placeholder(R.drawable.ic_profil)
            error(R.drawable.ic_profil)
        }
        Log.d("ProfileFragment", "loadImage: $urlWithTimestamp")
    }

    private suspend fun downloadImage(pictureProfileUrl: String) {
        withContext(Dispatchers.IO) {
            Log.w("ProfileFragment", "downloadImage: $pictureProfileUrl")

            val file = File(requireContext().applicationContext.filesDir, "profile_picture.jpg")
            val url = URL(pictureProfileUrl)
            val connection = url.openConnection()
            val inputStream = connection.getInputStream()
            val fileOutputStream = FileOutputStream(file)

            inputStream.copyTo(fileOutputStream)

            inputStream.close()
            fileOutputStream.close()
        }
    }



    private fun swipeRefreshHandler() {
        val swipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                fetchProfileData()
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }


    private fun updateUI(data: ProfileResponse.ProfileData?) {
        data?.let { profile ->
            keepCity = true

            binding.txtIdProfile.text = profile.accountId
            binding.txtEmailProfile.text = profile.email

            val userData = profile.userData

            if (userData == null) {
                Log.w("ProfileFragment", "updateUI: userData is null")
            }
            else {
                binding.edtNamaLengkapProfil.setText(userData.fullName)
                setTextViewPhoneNumber(userData.phone)

                setTextViewSpinner(userData.province, userData.city)

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

    private fun setTextViewSpinner(province: String?, city: String?) {
        Log.d("ProfileFragment", "setTextViewSpinner: $province, $city")

        if (province != null && city != null) {
            isCitySelected = true
            isProvinceSelected = true
            binding.buttonProvincesSelectionProfile.text = province
            binding.spinnerCityProfile.text = city
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

                                lifecycleScope.launch {
                                    val pictureProfile =  data.userData?.pictureProfile

                                    // check if pictureProfile start with DOMAIN
                                    if (pictureProfile != null) {
                                        if (pictureProfile.startsWith(DOMAIN)) {
                                            downloadImage(pictureProfile)
                                        }
                                        else {
                                            downloadImage(DOMAIN + pictureProfile)
                                        }
                                    }
                                    else {
                                        Log.w("ProfileFragment", "onResponse: pictureProfile is null")
                                    }

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
            val data = withContext(Dispatchers.IO) { profileDao.localProfile() }
            Log.d("ProfileFragment", "useLocalData: $data")

            val localProfileData = withContext(Dispatchers.IO) { ProfileResponse.ProfileData() }
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

        binding.buttonProvincesSelectionProfile.setOnClickListener {
            ProvinceSheetFragment().show(parentFragmentManager, "ProvinceSheetFragment")
            isProvinceSelected = true
            keepCity = false
            Log.d("ProfileFragment", "provinceSpinner. is selected :$isProvinceSelected")
        }

        provinceViewModel.provinceData.observe(viewLifecycleOwner) {
            binding.buttonProvincesSelectionProfile.text = it.provinceName

            isProvinceSelected = true
            isCitySelected = false

            val citiesName = if (keepCity) {
                binding.spinnerCityProfile.text.toString()
                } else {
                    "Pilih Kota"
                }


            cityViewModel.citiesData
                .postValue(
                    CitiesModel(
                        citiesId = "",
                        provinceId = it.provinceId,
                        citiesName = citiesName
                    )
                )
        }
    }




}