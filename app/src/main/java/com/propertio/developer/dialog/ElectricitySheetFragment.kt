package com.propertio.developer.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.propertio.developer.database.MasterDataDeveloperPropertio
import com.propertio.developer.databinding.FragmentBottomRecyclerWithSearchBarSheetBinding
import com.propertio.developer.dialog.adapter.ElectricityAdapter
import com.propertio.developer.dialog.viewmodel.ElectricityTypeSpinnerViewModel

class ElectricitySheetFragment : BottomSheetDialogFragment() {

    private val binding by lazy {
        FragmentBottomRecyclerWithSearchBarSheetBinding.inflate(layoutInflater)
    }

    private lateinit var electricityTypeViewModel: ElectricityTypeSpinnerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewSheetTitle.text = "Pilih Tipe Listrik"
        binding.containerSearchBar.visibility = View.GONE

        electricityTypeViewModel = ViewModelProvider(requireActivity())[ElectricityTypeSpinnerViewModel::class.java]

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        with(binding) {
            recyclerViewSheet.apply {
                adapter = ElectricityAdapter(
                    electricityType = MasterDataDeveloperPropertio.electricity,
                    onClickItemListener = {
                        Log.d("ElectricitySheet", "setupRecyclerView: $it")
                        electricityTypeViewModel.electricityTypeData.postValue(it)

                        dismiss()
                    }
                )
                layoutManager = LinearLayoutManager(requireContext())
            }

            progressIndicatorSheet.visibility = View.GONE
        }
    }
}