package com.propertio.developer.profile

import android.R
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.propertio.developer.databinding.FragmentProfileBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchProfileData()

        ArrayAdapter.createFromResource(
            requireContext(),
            com.propertio.developer.R.array.list_of_provinces, // change this line
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.spinnerProvinsiProfile.adapter = adapter
        }

        binding.spinnerProvinsiProfile.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedProvince = parent.getItemAtPosition(position).toString()
                val districtsArrayId = when (selectedProvince) {
                    "Jawa Tengah" -> com.propertio.developer.R.array.list_of_districts_jawatengah
                    "Jawa Barat" -> com.propertio.developer.R.array.list_of_districts_jawabarat
                    "DKI Jakarta" -> com.propertio.developer.R.array.list_of_districts_dki
                    "Jawa Timur" -> com.propertio.developer.R.array.list_of_districts_jawatimur
                    "DI Yogyakarta" -> com.propertio.developer.R.array.list_of_districts_diy
                    else -> 0
                }

                if (districtsArrayId != 0) {
                    ArrayAdapter.createFromResource(
                        requireContext(),
                        districtsArrayId,
                        android.R.layout.simple_spinner_item
                    ).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerKotaProfile.adapter = adapter
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }
    }

    private fun fetchProfileData() {
        val token = requireActivity().getSharedPreferences("account_data", Context.MODE_PRIVATE).getString("token", null)
        val request = Request.Builder()
            .url("http://beta.propertio.id/api/v1/profile")
            .addHeader("Authorization", "Bearer $token")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONObject(response.body?.string())
                val data = json.getJSONObject("data")
                val userData = data.getJSONObject("user_data")

                activity?.runOnUiThread {
                    binding.txtIdProfile.text = data.getString("account_id")
                    binding.txtEmailProfile.text = data.getString("email")
                    binding.edtNamaLengkapProfil.setText(userData.getString("full_name"))
                    binding.edtNomorTeleponProfil.setText(userData.getString("phone"))

                    val provinces = listOf(userData.getString("province"))
                    val provinceAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, provinces)
                    provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerProvinsiProfile.adapter = provinceAdapter
                    val provincePosition = provinces.indexOf(userData.getString("province"))
                    binding.spinnerProvinsiProfile.setSelection(provincePosition)

                    val cities = listOf(userData.getString("city"))
                    val cityAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, cities)
                    cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerKotaProfile.adapter = cityAdapter
                    val cityPosition = cities.indexOf(userData.getString("city"))
                    binding.spinnerKotaProfile.setSelection(cityPosition)
                    binding.edtAlamatProfil.setText(userData.getString("address"))
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}