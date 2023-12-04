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
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.type.GeneralTypeResponse
import com.propertio.developer.api.models.GeneralType
import com.propertio.developer.databinding.FragmentBottomRecyclerWithSearchBarSheetBinding
import com.propertio.developer.dialog.adapter.InfrastructureTypeAdapter
import com.propertio.developer.dialog.adapter.PropertyTypeAdapter
import com.propertio.developer.dialog.viewmodel.InfrastructureTypeSpinnerViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InfrastructureTypeSheetFragment : BottomSheetDialogFragment() {

    private val binding by lazy {
        FragmentBottomRecyclerWithSearchBarSheetBinding.inflate(layoutInflater)
    }

    private lateinit var infrastructureTypeViewModel: InfrastructureTypeSpinnerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewSheetTitle.text = "Pilih Tipe Infrastruktur"
        binding.containerSearchBar.hint = "Cari Tipe Infrastruktur"

        infrastructureTypeViewModel = ViewModelProvider(requireActivity())[InfrastructureTypeSpinnerViewModel::class.java]

        fetchInfrastructureTypeApi()
    }

    private fun fetchInfrastructureTypeApi() {
        Log.d("InfrastructureTypeSheet", "fetchInfrastructureTypeApi: ${infrastructureTypeViewModel.infrastructureTypeData.value}")
        val retro = Retro(TokenManager(requireContext()).token)
            .getRetroClientInstance()
            .create(DeveloperApi::class.java)

        retro.getInfrastructureType().enqueue(object : Callback<GeneralTypeResponse> {
            override fun onResponse(
                call: Call<GeneralTypeResponse>,
                response: Response<GeneralTypeResponse>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    if (data != null) {
                        setupRecyclerView(data)
                        Log.d("InfrastructureTypeSheet", "onResponse: $data")
                    } else {
                        Log.d("InfrastructureTypeSheet", "onResponse: data is null")
                    }

                } else {
                    Log.d("InfrastructureTypeSheet", "onResponse UnSuccessful: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<GeneralTypeResponse>, t: Throwable) {
                Log.d("InfrastructureTypeSheet", "onFailure: ${t.message}")
            }
        })
    }


    private fun setupRecyclerView(typeList: List<GeneralType>) {
        Log.d("PropertyTypeSheet", "setupRecyclerView: $typeList")

        with(binding) {
            recyclerViewSheet.apply {
                adapter = InfrastructureTypeAdapter(
                    context = requireContext(),
                    propertyTypes = typeList,
                    onClickItemListener = {
                        Log.d("PropertyTypeSheet", "setupRecyclerView: $it")
                        infrastructureTypeViewModel.infrastructureTypeData
                            .postValue(
                                GeneralType(
                                    id = it.id,
                                    name = it.name
                                )
                            )

                        dismiss()
                    }
                )

                layoutManager = LinearLayoutManager(requireContext())
            }
        }

    }

}
