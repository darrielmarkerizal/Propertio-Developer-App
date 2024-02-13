package com.propertio.developer.dialog

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.propertio.developer.database.MasterDataDeveloperPropertio
import com.propertio.developer.dialog.adapter.SimpleMasterDataAdapter
import com.propertio.developer.dialog.viewmodel.ElectricityTypeSpinnerViewModel

class ElectricitySheetFragment : BottomSheetDialogAbstract() {


    private lateinit var electricityTypeViewModel: ElectricityTypeSpinnerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    private val masterDataAdapter = SimpleMasterDataAdapter(
        onClickItemListener = {
            Log.d("ElectricitySheet", "setupRecyclerView: $it")
            electricityTypeViewModel.electricityTypeData.postValue(it)

            dismiss()
        }
    )

    private val electricity = MasterDataDeveloperPropertio.electricity
    override val onEmptySearchFilter: () -> Unit
        get() = { loadData() }
    override val onNotEmptySearchFilter: (Editable) -> Unit
        get() = {
            val filteredList = electricity.filter {
                it.toUser.contains(it.toUser, ignoreCase = true)
            }
            masterDataAdapter.submitList(filteredList)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewSheetTitle.text = "Pilih Tipe Listrik"
        binding.containerSearchBar.visibility = View.GONE

        electricityTypeViewModel = ViewModelProvider(requireActivity())[ElectricityTypeSpinnerViewModel::class.java]

        setupRecyclerView()
        loadData()
    }

    private fun loadData() {
        masterDataAdapter.submitList(electricity)
    }

    private fun setupRecyclerView() {
        with(binding) {
            recyclerViewSheet.apply {
                adapter = masterDataAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            progressIndicatorSheet.visibility = View.GONE
        }
    }
}