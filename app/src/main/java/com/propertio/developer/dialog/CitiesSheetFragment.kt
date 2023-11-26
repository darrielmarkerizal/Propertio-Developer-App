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
import com.propertio.developer.dialog.adapter.CitiesAdapter
import com.propertio.developer.dialog.model.CitiesModel
import com.propertio.developer.dialog.viewmodel.CitiesSpinnerViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CitiesSheetFragment : BottomSheetDialogFragment() {

    private val binding by lazy {
        FragmentBottomRecyclerWithSearchBarSheetBinding.inflate(layoutInflater)
    }

    private lateinit var citiesViewModel: CitiesSpinnerViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewSheetTitle.text = "Pilih Kota"
        binding.searchBarBottomSheet.hint = "Cari Kota"

        citiesViewModel = ViewModelProvider(requireActivity())[CitiesSpinnerViewModel::class.java]

        fetchCitiesApi()
    }

    private fun fetchCitiesApi() {
        val retro = Retro(TokenManager(requireContext()).token)
            .getRetroClientInstance()
            .create(ProfileApi::class.java)

        citiesViewModel.citiesData.value?.let {
            retro.getCities(it.provinceId).enqueue(object : Callback<List<ProfileResponse.City>> {
                override fun onResponse(
                    call: Call<List<ProfileResponse.City>>,
                    response: Response<List<ProfileResponse.City>>
                ) {
                    if (response.isSuccessful) {
                        Log.d("CitiesSheetFragment", "onResponse: ${response.body()}")
                        val data = response.body()

                        if (data != null) {
                            setupRecyclerView(data)
                            binding.progressIndicatorSheet.visibility = View.GONE
                        } else {
                            Log.w("CitiesSheetFragment", "onResponse: data is null")
                        }
                    } else {
                        Log.e("CitiesSheetFragment", "onResponse: ${response.message()}")

                    }
                }

                override fun onFailure(call: Call<List<ProfileResponse.City>>, t: Throwable) {
                    Log.e("CitiesSheetFragment", "onFailure: ${t.message}", t)
                }


            })
        }
    }

    private fun setupRecyclerView(cities: List<ProfileResponse.City>) {
        Log.d("CitiesSheetFragment", "setupRecyclerView: $cities")

        with(binding) {
            recyclerViewSheet.apply {
                adapter = CitiesAdapter(
                    cities = cities,
                    onClickItemListener = {
                        Log.d("CitiesSheetFragment", "setupRecyclerView: $it")
                        citiesViewModel.citiesData
                            .postValue(
                                CitiesModel(
                                    citiesId = it.id,
                                    provinceId = it.provinceId,
                                    citiesName = it.name
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