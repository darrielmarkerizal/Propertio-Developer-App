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
import com.propertio.developer.api.common.address.Province
import com.propertio.developer.dialog.adapter.ProvinceAdapter
import com.propertio.developer.dialog.model.ProvinceModel
import com.propertio.developer.dialog.viewmodel.ProvinceSpinnerViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProvinceSheetFragment: BottomSheetDialogAbstract() {

    private lateinit var provinceViewModel: ProvinceSpinnerViewModel
    private var call: Call<List<Province>>? = null

    private val dataLists = mutableListOf<Province>()
    private val provinceAdapter = ProvinceAdapter(
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

    override val onEmptySearchFilter: () -> Unit
        get() = { provinceAdapter.submitList(dataLists) }
    override val onNotEmptySearchFilter: (Editable) -> Unit
        get() = { text ->
                val filteredList = dataLists.filter {
                    it.name.contains(text.toString(), ignoreCase = true)
                }
                provinceAdapter.submitList(filteredList)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewSheetTitle.text = "Pilih Provinsi"
        binding.searchBarBottomSheet.hint = "Cari Provinsi"

        provinceViewModel = ViewModelProvider(requireActivity())[ProvinceSpinnerViewModel::class.java]
        setupRecycler()

        try {
            fetchProvincesApi()
        } catch (e: Exception) {
            Log.e("ProvinceSheetFragment", "onViewCreated: ${e.message}", e)
            dismissNow()
        }

    }


    private fun fetchProvincesApi() {
        val retro = Retro(TokenManager(requireContext()).token)
            .getRetroClientInstance()
            .create(AddressApi::class.java)

        call = retro.getProvinces()
        call?.enqueue(object : Callback<List<Province>> {
            override fun onResponse(
                call: Call<List<Province>>,
                response: Response<List<Province>>
            ) {
                if (response.isSuccessful) {
                    Log.d("ProvinceSheetFragment", "onResponse: ${response.body()}")
                    val data = response.body()

                    if (data != null) {
                        binding.progressIndicatorSheet.visibility = View.GONE
                        dataLists.clear()
                        dataLists.addAll(data)
                        provinceAdapter.submitList(data)
                    } else {
                        Log.w("ProvinceSheetFragment", "onResponse: data is null")
                    }

                } else {
                    Log.e("ProvinceSheetFragment", "onResponse: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<List<Province>>, t: Throwable) {
                Log.e("ProvinceSheetFragment", "onFailure: ${t.message}", t)
            }


        })
    }

    private fun setupRecycler() {

        with(binding) {
            recyclerViewSheet.apply {
                adapter = provinceAdapter
                layoutManager = LinearLayoutManager(requireActivity())
            }
        }
    }

}