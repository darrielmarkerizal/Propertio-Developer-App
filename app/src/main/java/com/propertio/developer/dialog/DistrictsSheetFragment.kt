package com.propertio.developer.dialog

import android.os.Bundle
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
import com.propertio.developer.databinding.FragmentBottomRecyclerWithSearchBarSheetBinding
import com.propertio.developer.dialog.adapter.DistrictAdapter
import com.propertio.developer.dialog.model.DistrictsModel
import com.propertio.developer.dialog.viewmodel.DistrictsSpinnerViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DistrictsSheetFragment : BottomSheetDialogAbstract() {

    private var call: Call<List<District>>? = null
    private val binding by lazy {
        FragmentBottomRecyclerWithSearchBarSheetBinding.inflate(layoutInflater)
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewSheetTitle.text = "Pilih Kecamatan"
        binding.searchBarBottomSheet.hint = "Cari Kecamatan"

        districtsViewModel = ViewModelProvider(requireActivity())[DistrictsSpinnerViewModel::class.java]

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
                        val districts = response.body()

                        if (districts != null) {
                            setupRecyclerView(districts)



                        }
                    }
                }

                override fun onFailure(call: Call<List<District>>, t: Throwable) {
                    Log.e("DistrictsSheetFragment", "onFailure: ${t.message}")
                }
            })
        }
    }

    private fun setupRecyclerView(district: List<District>) {
        Log.d("CitiesSheetFragment", "setupRecyclerView: $district")

        with(binding) {
            recyclerViewSheet.apply {
                adapter = DistrictAdapter(
                    districts = district,
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

                layoutManager = LinearLayoutManager(requireActivity())
            }
        }
    }

}
