package com.propertio.developer.dialog

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.common.address.AddressApi
import com.propertio.developer.api.common.address.City
import com.propertio.developer.dialog.adapter.CitiesAdapter
import com.propertio.developer.dialog.model.CitiesModel
import com.propertio.developer.dialog.viewmodel.CitiesSpinnerViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CitiesSheetFragment : BottomSheetDialogAbstract() {

    private lateinit var citiesViewModel: CitiesSpinnerViewModel
    private var call: Call<List<City>>? = null

    private val dataLists = mutableListOf<City>()
    private val citiesAdapter = CitiesAdapter(
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

    override fun onDestroyView() {
        super.onDestroyView()
        call?.cancel()
    }

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
        setupRecyclerView()
    }

    override val onEmptySearchFilter: () -> Unit
        get() = { citiesAdapter.submitList(dataLists) }
    override val onNotEmptySearchFilter: (Editable) -> Unit
        get() = { text ->
            val filteredList = dataLists.filter {
                it.name.contains(text.toString(), ignoreCase = true)
            }
            citiesAdapter.submitList(filteredList)
        }



    private fun fetchCitiesApi() {
        val retro = Retro(TokenManager(requireContext()).token)
            .getRetroClientInstance()
            .create(AddressApi::class.java)

        citiesViewModel.citiesData.value?.let {
            call = retro.getCities(it.provinceId)
            call?.enqueue(object : Callback<List<City>> {
                override fun onResponse(
                    call: Call<List<City>>,
                    response: Response<List<City>>
                ) {
                    if (response.isSuccessful) {
                        Log.d("CitiesSheetFragment", "onResponse: ${response.body()}")
                        val data = response.body()

                        if (data != null) {

                            dataLists.clear()
                            dataLists.addAll(data)
                            citiesAdapter.submitList(data)
                            binding.progressIndicatorSheet.visibility = View.GONE
                        } else {
                            Log.w("CitiesSheetFragment", "onResponse: data is null")
                        }
                    } else {
                        Log.e("CitiesSheetFragment", "onResponse: ${response.message()}")

                    }
                }

                override fun onFailure(call: Call<List<City>>, t: Throwable) {
                    Log.e("CitiesSheetFragment", "onFailure: ${t.message}", t)
                }


            })
        }
    }

    private fun setupRecyclerView() {
        Log.d("CitiesSheetFragment", "setupRecyclerView")

        with(binding) {
            recyclerViewSheet.apply {
                adapter = citiesAdapter

                layoutManager = LinearLayoutManager(requireActivity())
            }
        }
    }


}