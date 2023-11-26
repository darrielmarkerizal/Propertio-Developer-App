package com.propertio.developer.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.propertio.developer.TokenManager
import com.propertio.developer.api.DomainURL
import com.propertio.developer.api.DomainURL.DOMAIN
import com.propertio.developer.api.profile.ProfileResponse
import com.propertio.developer.databinding.FragmentProfileBinding
import android.app.AlertDialog
import com.propertio.developer.api.profile.ProfileUpdateRequest
import android.app.Activity
import android.graphics.Color
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.propertio.developer.auth.LoginActivity
import kotlinx.coroutines.launch
import com.propertio.developer.R


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileViewModel: ProfileViewModel

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

        profileViewModel.profileData.observe(viewLifecycleOwner, Observer { profileData ->
            // Update the UI with the profile data
            updateUI(profileData)
            Log.d("ProfileFragment", "Profile data updated: $profileData")
        })

        profileViewModel.provincesData.observe(viewLifecycleOwner, Observer { provincesData ->
            val provinceNames = provincesData.map { it.name }
            val provinceAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, provinceNames)
            provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerProvinsiProfile.adapter = provinceAdapter

            // Set the selected item in the spinner to the user's province
            val userProvince = profileViewModel.profileData.value?.userData?.province
            if (userProvince != null) {
                val provincePosition = provinceAdapter.getPosition(userProvince)
                binding.spinnerProvinsiProfile.setSelection(provincePosition)
                Log.d("ProfileFragment", "Province spinner set to: $userProvince")
            }
        })

        profileViewModel.cityData.observe(viewLifecycleOwner, Observer { citiesData ->
            val cityNames = citiesData.map { it.name }
            val cityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cityNames)
            cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerKotaProfile.adapter = cityAdapter

            // Set the selected item in the spinner to the user's city
            val userCity = profileViewModel.profileData.value?.userData?.city
            if (userCity != null) {
                val cityPosition = cityAdapter.getPosition(userCity)
                binding.spinnerKotaProfile.setSelection(cityPosition)
                Log.d("ProfileFragment", "City spinner set to: $userCity")
            }
        })

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
                val city = binding.spinnerKotaProfile.selectedItem.toString()
                val province = binding.spinnerProvinsiProfile.selectedItem.toString()
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

        binding.spinnerProvinsiProfile.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedProvince = profileViewModel.provincesData.value?.get(position)
                if (selectedProvince != null) {
                    profileViewModel.fetchCityData(selectedProvince.id)
                    Log.d("ProfileFragment", "Selected province: id = ${selectedProvince.id}, name = ${selectedProvince.name}")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

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

}