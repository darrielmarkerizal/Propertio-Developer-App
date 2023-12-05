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
import com.propertio.developer.dialog.adapter.WaterAdapter
import com.propertio.developer.dialog.viewmodel.WaterTypeSpinnerViewModel

class WaterSheetFragment : BottomSheetDialogFragment() {

    private val binding by lazy {
        FragmentBottomRecyclerWithSearchBarSheetBinding.inflate(layoutInflater)
    }

    private lateinit var waterTypeViewModel: WaterTypeSpinnerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewSheetTitle.text = "Pilih Tipe Air"
        binding.containerSearchBar.visibility = View.GONE

        waterTypeViewModel = ViewModelProvider(requireActivity())[WaterTypeSpinnerViewModel::class.java]

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        with(binding) {
            recyclerViewSheet.apply {
                adapter = WaterAdapter(
                    waterTypes = MasterDataDeveloperPropertio.water,
                    onClickItemListener = {
                        Log.d("WaterSheet", "setupRecyclerView: $it")
                        waterTypeViewModel.waterTypeData.postValue(it)

                        dismiss()
                    }
                )
                layoutManager = LinearLayoutManager(requireContext())
            }

            progressIndicatorSheet.visibility = View.GONE
        }
    }
}