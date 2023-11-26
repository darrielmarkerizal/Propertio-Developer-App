package com.propertio.developer.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.profile.ProfileApi
import com.propertio.developer.api.profile.ProfileResponse
import com.propertio.developer.databinding.FragmentBottomRecyclerWithSearchBarSheetBinding
import com.propertio.developer.dialog.adapter.ProvinceAdapter
import com.propertio.developer.dialog.model.ProvinceModel
import com.propertio.developer.dialog.viewmodel.ProvinceSpinnerViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProvinceSheetFragment : BottomSheetDialogFragment() {

    private val binding by lazy {
        FragmentBottomRecyclerWithSearchBarSheetBinding.inflate(layoutInflater)
    }

    private lateinit var provinceViewModel: ProvinceSpinnerViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewSheetTitle.text = "Pilih Provinsi"
        binding.searchBarBottomSheet.hint = "Cari Provinsi"

        provinceViewModel = ViewModelProvider(requireActivity())[ProvinceSpinnerViewModel::class.java]

        fetchProvincesApi()
    }

    private fun fetchProvincesApi() {
        val retro = Retro(TokenManager(requireContext()).token)
            .getRetroClientInstance()
            .create(ProfileApi::class.java)

        retro.getProvinces().enqueue(object : Callback<List<ProfileResponse.Province>> {
            override fun onResponse(
                call: Call<List<ProfileResponse.Province>>,
                response: Response<List<ProfileResponse.Province>>
            ) {
                if (response.isSuccessful) {
                    Log.d("ProvinceSheetFragment", "onResponse: ${response.body()}")
                    val data = response.body()

                    if (data != null) {
                        setupRecycler(data)
                        binding.progressIndicatorSheet.visibility = View.GONE
                    } else {
                        Log.w("ProvinceSheetFragment", "onResponse: data is null")
                    }

                } else {
                    Log.e("ProvinceSheetFragment", "onResponse: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<List<ProfileResponse.Province>>, t: Throwable) {
                Log.e("ProvinceSheetFragment", "onFailure: ${t.message}", t)
            }


        })
    }

    private fun setupRecycler(provinces : List<ProfileResponse.Province>) {
        Log.d("ProvinceSheetFragment", "setupRecycler: $provinces")

        with(binding) {
            recyclerViewSheet.apply {
                adapter = ProvinceAdapter(
                    provinces = provinces,
                    onClickItemListener = {
                        Log.d("ProvinceSheetFragment", "setupRecycler: $it")
                        provinceViewModel.provinceData
                            .postValue(
                                ProvinceModel(
                                    it.id,
                                    it.name
                                )
                            )

                        dismiss()
                    }
                )

                layoutManager = LinearLayoutManager(requireActivity())
            }
        }
    }

}