package com.propertio.developer.profile

import android.R
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.TokenManager
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

        binding.btnUbahKataSandiProfil.setOnClickListener {
            val intent = Intent(activity, ChangePassword::class.java)
            startActivity(intent)
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