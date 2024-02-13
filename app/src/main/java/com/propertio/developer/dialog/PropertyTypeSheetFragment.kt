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
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.type.GeneralTypeResponse
import com.propertio.developer.api.models.GeneralType
import com.propertio.developer.dialog.adapter.PropertyTypeAdapter
import com.propertio.developer.dialog.viewmodel.PropertyTypeSpinnerViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PropertyTypeSheetFragment : BottomSheetDialogAbstract() {

    private var call: Call<GeneralTypeResponse>? = null


    private lateinit var propertyTypeViewModel: PropertyTypeSpinnerViewModel

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
        get() = {/*NOTE: not implemented*/}
    override val onNotEmptySearchFilter: (Editable) -> Unit
        get() = {/*NOTE: not implemented*/}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewSheetTitle.text = "Pilih Tipe Properti"
        binding.containerSearchBar.visibility = View.GONE

        propertyTypeViewModel = ViewModelProvider(requireActivity())[PropertyTypeSpinnerViewModel::class.java]

        fetchPropertyTypeApi()
    }

    private fun fetchPropertyTypeApi() {
        Log.d("PropertyTypeSheet", "fetchPropertyTypeApi: ${propertyTypeViewModel.propertyTypeData.value}")
        val retro = Retro(TokenManager(requireContext()).token)
            .getRetroClientInstance()
            .create(DeveloperApi::class.java)

        call = retro.getPropertyType()
        call?.enqueue(object : Callback<GeneralTypeResponse> {
            override fun onResponse(
                call: Call<GeneralTypeResponse>,
                response: Response<GeneralTypeResponse>
            ) {
                if (response.isSuccessful) {
                    val typeList = response.body()?.data
                    if (typeList != null) {
                        setupRecyclerView(typeList)
                        Log.d("PropertyTypeSheet", "onResponse: $typeList")
                    } else {
                        Log.e("PropertyTypeSheet", "onResponse: ${response.message()}")
                    }
                } else {
                    Log.e("PropertyTypeSheet", "onResponse: ${response.message()}", Throwable(response.message()))
                }
            }

            override fun onFailure(call: Call<GeneralTypeResponse>, t: Throwable) {
                Log.e("PropertyTypeSheet", "onFailure: ${t.message}", t)
            }

        })
    }

    private fun setupRecyclerView(typeList: List<GeneralType>) {
        Log.d("PropertyTypeSheet", "setupRecyclerView: $typeList")

        with(binding) {
            recyclerViewSheet.apply {
                adapter = PropertyTypeAdapter(
                    propertyTypes = typeList,
                    onClickItemListener = {
                        Log.d("PropertyTypeSheet", "setupRecyclerView: $it")
                        propertyTypeViewModel.propertyTypeData
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