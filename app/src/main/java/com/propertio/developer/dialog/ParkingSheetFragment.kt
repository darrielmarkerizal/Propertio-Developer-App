package com.propertio.developer.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.propertio.developer.database.MasterDataDeveloperPropertio
import com.propertio.developer.databinding.FragmentBottomRecyclerWithSearchBarSheetBinding
import com.propertio.developer.dialog.adapter.ParkingAdapter
import com.propertio.developer.dialog.viewmodel.ParkingTypeSpinnerViewModel

class ParkingSheetFragment : BottomSheetDialogAbstract() {

    private val binding by lazy {
        FragmentBottomRecyclerWithSearchBarSheetBinding.inflate(layoutInflater)
    }

    private lateinit var parkingTypeViewModel: ParkingTypeSpinnerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewSheetTitle.text = "Pilih Tipe Parkir"
        binding.containerSearchBar.visibility = View.GONE

        parkingTypeViewModel = ViewModelProvider(requireActivity())[ParkingTypeSpinnerViewModel::class.java]

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        with(binding) {
            recyclerViewSheet.apply {
                adapter = ParkingAdapter(
                    parkingTypes = MasterDataDeveloperPropertio.parking,
                    onClickItemListener = {
                        Log.d("ParkingSheet", "setupRecyclerView: $it")
                        parkingTypeViewModel.parkingTypeData.postValue(it)

                        dismiss()
                    }
                )
                layoutManager = LinearLayoutManager(requireContext())
            }

            progressIndicatorSheet.visibility = View.GONE
        }
    }
}