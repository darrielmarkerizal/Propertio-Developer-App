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
import com.propertio.developer.api.common.address.District
import com.propertio.developer.dialog.adapter.DistrictAdapter
import com.propertio.developer.dialog.model.DistrictsModel
import com.propertio.developer.dialog.viewmodel.DistrictsSpinnerViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DistrictsSheetFragment : BottomSheetDialogAbstract() {

    private var call: Call<List<District>>? = null

    private lateinit var districtsViewModel: DistrictsSpinnerViewModel

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

    private val districts = mutableListOf<District>()
    private val districtAdapter = DistrictAdapter(
        onClickItemListener = {
            Log.d("DistrictsSheetFragment", "setupRecyclerView: $it")
            districtsViewModel.districtsData
                .postValue(
                    DistrictsModel(
                        districtsId = it.id,
                        citiesId = it.cityId,
                        districtsName = it.name
                    )
                )

            dismiss()
        }
    )
    override val onEmptySearchFilter: () -> Unit
        get() = {
            districtAdapter.submitList(districts)
        }
    override val onNotEmptySearchFilter: (Editable) -> Unit
        get() = {
            val filteredList = districts.filter {
                it.name.contains(it.name, ignoreCase = true)
            }
            districtAdapter.submitList(filteredList)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewSheetTitle.text = "Pilih Kecamatan"
        binding.searchBarBottomSheet.hint = "Cari Kecamatan"

        districtsViewModel = ViewModelProvider(requireActivity())[DistrictsSpinnerViewModel::class.java]

        setupRecyclerView()
        fetchDistrictsApi()

    }

    private fun fetchDistrictsApi() {
        val retro = Retro(TokenManager(requireContext()).token)
            .getRetroClientInstance()
            .create(AddressApi::class.java)

        districtsViewModel.districtsData.value?.let {
            call = retro.getDistricts(it.citiesId)
            call?.enqueue(object :
                Callback<List<District>> {
                override fun onResponse(
                    call: Call<List<District>>,
                    response: Response<List<District>>
                ) {
                    if (response.isSuccessful) {
                        Log.d("DistrictsSheetFragment", "onResponse: ${response.body()}")
                        val districtsList = response.body()

                        if (districtsList != null) {
                            districts.clear()
                            districts.addAll(districtsList)
                            districtAdapter.submitList(districts)
                        }
                    }
                }

                override fun onFailure(call: Call<List<District>>, t: Throwable) {
                    Log.e("DistrictsSheetFragment", "onFailure: ${t.message}")
                }
            })
        }
    }

    private fun setupRecyclerView() {
        Log.d("CitiesSheetFragment", "setupRecyclerView")

        with(binding) {
            recyclerViewSheet.apply {
                adapter = districtAdapter
                layoutManager = LinearLayoutManager(requireActivity())
            }
        }
    }

}
