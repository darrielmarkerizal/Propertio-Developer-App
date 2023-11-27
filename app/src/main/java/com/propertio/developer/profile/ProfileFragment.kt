package com.propertio.developer.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.propertio.developer.R
import com.propertio.developer.TokenManager
import com.propertio.developer.api.DomainURL.DOMAIN
import com.propertio.developer.api.profile.ProfileResponse
import com.propertio.developer.api.profile.ProfileUpdateRequest
import com.propertio.developer.auth.LoginActivity
import com.propertio.developer.databinding.FragmentProfileBinding
import com.propertio.developer.dialog.CitiesSheetFragment
import com.propertio.developer.dialog.ProfileCitiesSheetFragment
import com.propertio.developer.dialog.ProvinceSheetFragment
import com.propertio.developer.dialog.model.CitiesModel
import com.propertio.developer.dialog.viewmodel.CitiesSpinnerViewModel
import com.propertio.developer.dialog.viewmodel.ProvinceSpinnerViewModel

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileViewModel: ProfileViewModel

    private var isProvinceSelected : Boolean = false
    private lateinit var provinceViewModel: ProvinceSpinnerViewModel
    private lateinit var cityViewModel: CitiesSpinnerViewModel

    companion object {
        private const val PICK_IMAGE_REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val swipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            profileViewModel.fetchProfileData()
            swipeRefreshLayout.isRefreshing = false
        }

        val viewModelFactory = ProfileViewModelFactory(TokenManager(requireContext()).token!!)
        profileViewModel = ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)
        cityViewModel = ViewModelProvider(requireActivity())[CitiesSpinnerViewModel::class.java]
        provinceViewModel = ViewModelProvider(requireActivity())[ProvinceSpinnerViewModel::class.java]

        profileViewModel.provincesData.observe(viewLifecycleOwner, Observer { provincesData ->
            if (provincesData.isNotEmpty()) {
                val firstProvinceId = provincesData[0].id
                profileViewModel.fetchCityData(firstProvinceId)
            }
        })

        profileViewModel.profileData.observe(viewLifecycleOwner, Observer { profileData ->
            updateUI(profileData)
            Log.d("ProfileFragment", "Profile data updated: $profileData")

            val userProvinceName = profileData?.userData?.province
            val selectedProvince = profileViewModel.provincesData.value?.find { it.name == userProvinceName }
            val defaultProvinceId = selectedProvince?.id

            if (defaultProvinceId != null) {
                profileViewModel.fetchCityData(defaultProvinceId)
                ProfileCitiesSheetFragment(defaultProvinceId).show(parentFragmentManager, "CitySheetFragment")
            }

            val userProfileProvince = profileData?.userData?.province
            val userCity = profileData?.userData?.city
            if (userProfileProvince != null) {
                binding.buttonProvincesSelectionProfile.text = userProfileProvince
            }
            if (userCity != null) {
                binding.spinnerDistrictProfile.text = userCity
            }
        })

        citySpinner()
        provinceSpinner()

        binding.btnUbahKataSandiProfil.setOnClickListener {
            val intent = Intent(activity, ChangePassword::class.java)
            startActivity(intent)
        }

        profileViewModel.profileData.observe(viewLifecycleOwner, Observer { profileData ->
            val pictureProfileUrl = DOMAIN + profileData?.userData?.pictureProfile
            Glide.with(requireContext())
                .load(pictureProfileUrl)
                .circleCrop()
                .into(binding.imgProfil)

            binding.btnSimpanProfil.setOnClickListener {
                Log.d("ProfileFragment", "Save button clicked")
                val fullName = binding.edtNamaLengkapProfil.text.toString()
                val phone = binding.edtNomorTeleponProfil.text.toString()
                val address = binding.edtAlamatProfil.text.toString()
                val city = binding.spinnerDistrictProfile.text.toString()
                val province = binding.buttonProvincesSelectionProfile.text.toString()
                val role = profileViewModel.profileData.value?.role
                val pictureProfile = profileViewModel.profileData.value?.userData?.pictureProfile

                val request = ProfileUpdateRequest(fullName, phone, address, city, province, role, pictureProfile)
                Log.d("ProfileFragment", "Profile update request: $request")
                profileViewModel.updateProfile(request)
            }

            binding.buttonAddProfilePictureProfil.setOnClickListener() {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
            }
        })

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

        profileViewModel.updateProfileResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response.isSuccessful) {
                if (profileViewModel.isUndoSuccessful.value == true) {
                    Snackbar.make(binding.root, "Update profil dibatalkan", Snackbar.LENGTH_SHORT).show()
                    profileViewModel.resetUndoSuccessStatus()
                } else {
                    val snackbar = Snackbar.make(binding.root, "Profil Berhasil Diperbarui", Snackbar.LENGTH_LONG)
                    snackbar.setAction("Batal") {
                        profileViewModel.undoProfileUpdate()
                    }
                    snackbar.setActionTextColor(Color.RED)
                    snackbar.show()
                }
            } else {
                Snackbar.make(binding.root, "Profil Gagal Diperbarui", Snackbar.LENGTH_SHORT).show()
            }
        })


    }

    private fun updateUI(data: ProfileResponse.ProfileData?) {
        data?.let {
            val userData = it.userData

            binding.txtIdProfile.text = it.accountId
            binding.txtEmailProfile.text = it.email
            val sharedPreferences = requireActivity().getSharedPreferences("account_data", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putString("email", it.email)
                commit()
            }

            binding.edtNamaLengkapProfil.setText(userData?.fullName)
            binding.edtNomorTeleponProfil.setText(userData?.phone)
            binding.edtAlamatProfil.setText(userData?.address)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            Log.d("ProfileFragment", "Image selected with URI: $imageUri")
            Glide.with(this)
                .load(imageUri)
                .circleCrop()
                .into(binding.imgProfil)
        }
    }

    private fun citySpinner() {
        cityViewModel = ViewModelProvider(requireActivity())[CitiesSpinnerViewModel::class.java]

        binding.spinnerDistrictProfile.setOnClickListener {
            val userProvinceName = binding.buttonProvincesSelectionProfile.text.toString()
            val selectedProvince = profileViewModel.provincesData.value?.find { it.name == userProvinceName }
            val provinceId = selectedProvince?.id
            if (provinceId != null) {
                profileViewModel.fetchCityData(provinceId)
                ProfileCitiesSheetFragment(provinceId).show(parentFragmentManager, "CitySheetFragment")
            } else {
                Toast.makeText(requireContext(), "Provinsi tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }

        provinceViewModel.provinceData.observe(viewLifecycleOwner) { provinceData ->
            val provinceId = provinceData.provinceId
            profileViewModel.fetchCityData(provinceId)
        }

        cityViewModel.citiesData.observe(viewLifecycleOwner) {
            binding.spinnerDistrictProfile.text = it.citiesName
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