package com.propertio.developer.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.propertio.developer.PropertioDeveloperApplication
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.type.GeneralTypeResponse
import com.propertio.developer.api.models.GeneralType
import com.propertio.developer.databinding.FragmentBottomRecyclerWithSearchBarSheetBinding
import com.propertio.developer.dialog.adapter.InfrastructureTypeAdapter
import com.propertio.developer.dialog.viewmodel.InfrastructureTypeSpinnerViewModel
import com.propertio.developer.project.list.FacilityAndInfrastructureTypeViewModel
import com.propertio.developer.project.viewmodel.FacilityViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InfrastructureTypeSheetFragment : BottomSheetDialogAbstract() {

    private val binding by lazy {
        FragmentBottomRecyclerWithSearchBarSheetBinding.inflate(layoutInflater)
    }

    private lateinit var infrastructureTypeViewModel: InfrastructureTypeSpinnerViewModel

    private lateinit var facilityAndInfrastructureTypeViewModel : FacilityAndInfrastructureTypeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    private val infrastructureAdapter by lazy {
        InfrastructureTypeAdapter(
            context = requireContext(),
            onClickItemListener = {
                Log.d("PropertyTypeSheet", "setupRecyclerView: $it")
                infrastructureTypeViewModel.infrastructureTypeData
                    .postValue(
                        GeneralType(
                            id = it.id,
                            name = it.name,
                            icon = it.icon
                        )
                    )
                dismiss()
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewSheetTitle.text = "Pilih Tipe Infrastruktur"
        binding.containerSearchBar.hint = "Cari Tipe Infrastruktur"

        infrastructureTypeViewModel = ViewModelProvider(requireActivity())[InfrastructureTypeSpinnerViewModel::class.java]

        val factory = FacilityViewModelFactory((requireActivity().applicationContext as PropertioDeveloperApplication).repository)
        facilityAndInfrastructureTypeViewModel = ViewModelProvider(requireActivity(), factory)[FacilityAndInfrastructureTypeViewModel::class.java]

        fetchInfrastructureTypeApi()
        setupRecyclerView()
        setupSearchBar()
    }

    private fun setupSearchBar() {



        binding.searchBarBottomSheet.doAfterTextChanged { query ->
            Log.d("InfrastructureTypeSheet", "setupSearchBar: $query")
            lifecycleScope.launch {
                if (query != null) {
                    val filteredList = withContext(Dispatchers.IO) {
                        facilityAndInfrastructureTypeViewModel.searchInfrastructure(
                            query.toString()
                        )
                    }

                    infrastructureAdapter.submitList(filteredList)
                }
            }

        }
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
                        lifecycleScope.launch(Dispatchers.IO) {
                            data.forEach {
                                if (it.id != null && it.name != null && it.category != null && it.icon != null) {
                                    facilityAndInfrastructureTypeViewModel.insertInfrastructure(
                                        id = it.id!!,
                                        name = it.name!!,
                                        description = it.category!!,
                                        icon = it.icon!!,
                                    )
                                }
                            }
                        }

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


    private fun setupRecyclerView() {

        with(binding) {
            recyclerViewSheet.apply {
                adapter = infrastructureAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val infrastructureList = facilityAndInfrastructureTypeViewModel.getAllInfrastructureType()
            infrastructureAdapter.submitList(infrastructureList)
        }

    }

}
