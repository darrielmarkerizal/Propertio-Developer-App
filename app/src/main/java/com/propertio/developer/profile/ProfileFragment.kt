package com.propertio.developer.profile

import android.R
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
import com.propertio.developer.TokenManager
import com.propertio.developer.api.DomainURL
import com.propertio.developer.api.DomainURL.DOMAIN
import com.propertio.developer.api.profile.ProfileResponse
import com.propertio.developer.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = ProfileViewModelFactory(TokenManager(requireContext()).token!!)
        profileViewModel = ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)

        profileViewModel.combinedData.observe(viewLifecycleOwner, Observer { combined ->
            val profileData = combined.first
            val provincesData = combined.second

            if (profileData != null && provincesData != null) {
                val provinceNames = provincesData.map { it.name }
                val provinceAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, provinceNames)
                provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerProvinsiProfile.adapter = provinceAdapter

                // Set the selected item in the spinner to the user's province
                val userProvince = profileData.userData?.province
                if (userProvince != null) {
                    val provincePosition = provinceNames.indexOf(userProvince)
                    binding.spinnerProvinsiProfile.setSelection(provincePosition)
                }

                // Update the UI with the profile data
                updateUI(profileData)
            }
        })

        profileViewModel.combinedCityData.observe(viewLifecycleOwner, Observer { combined ->
            val profileData = combined.first
            val citiesData = combined.second

            if (profileData != null && citiesData != null) {
                val cityNames = citiesData.map { it.name }
                val cityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cityNames)
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerKotaProfile.adapter = cityAdapter

                // Set the selected item in the spinner to the user's city
                val userCity = profileData.userData?.city
                if (userCity != null) {
                    val cityPosition = cityNames.indexOf(userCity)
                    binding.spinnerKotaProfile.setSelection(cityPosition)
                }
            }

            updateUI(profileData)
        })

        binding.btnUbahKataSandiProfil.setOnClickListener {
            val intent = Intent(activity, ChangePassword::class.java)
            startActivity(intent)
        }

        profileViewModel.profileData.observe(viewLifecycleOwner, Observer { profileData ->
            val pictureProfileUrl = DomainURL.DOMAIN + profileData?.userData?.pictureProfile
            Glide.with(requireContext())
                .load(pictureProfileUrl)
                .circleCrop()
                .into(binding.imgProfil)
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


}