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
        get() = {
            propertyAdapter.submitList(generalLists)
        }
    override val onNotEmptySearchFilter: (Editable) -> Unit
        get() = { search ->
            val filteredList = generalLists.filter {
                it.name?.contains(search.toString(), ignoreCase = true) ?: false
            }
            propertyAdapter.submitList(filteredList)
        }

    private val generalLists = mutableListOf<GeneralType>()

    private val propertyAdapter = PropertyTypeAdapter(
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewSheetTitle.text = "Pilih Tipe Properti"

        propertyTypeViewModel = ViewModelProvider(requireActivity())[PropertyTypeSpinnerViewModel::class.java]

        fetchPropertyTypeApi()
        setupRecyclerView()
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
                        generalLists.clear()
                        generalLists.addAll(typeList)
                        propertyAdapter.submitList(generalLists)
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

    private fun setupRecyclerView() {
        Log.d("PropertyTypeSheet", "setupRecyclerView")

        with(binding) {
            recyclerViewSheet.apply {
                adapter = propertyAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }

    }

}