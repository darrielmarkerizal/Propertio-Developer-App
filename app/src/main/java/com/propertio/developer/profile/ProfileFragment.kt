package com.propertio.developer.profile

import android.R
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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

        val token = requireActivity().getSharedPreferences("account_data", Context.MODE_PRIVATE).getString("token", null)
        val viewModelFactory = ProfileViewModelFactory(token!!)
        profileViewModel = ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)

        profileViewModel.profileData.observe(viewLifecycleOwner, Observer { data ->
            updateUI(data)
        })
    }

    private fun updateUI(data: ProfileResponse.ProfileData?) {
        data?.let {
            val userData = it.userData

            binding.txtIdProfile.text = it.accountId
            binding.txtEmailProfile.text = it.email
            binding.edtNamaLengkapProfil.setText(userData?.fullName)
            binding.edtNomorTeleponProfil.setText(userData?.phone)

            val provinces = listOf(userData?.province)
            val provinceAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, provinces)
            provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerProvinsiProfile.adapter = provinceAdapter
            val provincePosition = provinces.indexOf(userData?.province)
            binding.spinnerProvinsiProfile.setSelection(provincePosition)

            val cities = listOf(userData?.city)
            val cityAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, cities)
            cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerKotaProfile.adapter = cityAdapter
            val cityPosition = cities.indexOf(userData?.city)
            binding.spinnerKotaProfile.setSelection(cityPosition)
            binding.edtAlamatProfil.setText(userData?.address)
        }
    }
}