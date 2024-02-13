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
import com.propertio.developer.dialog.viewmodel.ParkingTypeSpinnerViewModel

class ParkingSheetFragment : BottomSheetDialogAbstract() {



    private lateinit var parkingTypeViewModel: ParkingTypeSpinnerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    private val masterDataAdapter = SimpleMasterDataAdapter(
        onClickItemListener = {
            Log.d("ParkingSheet", "setupRecyclerView: $it")
            parkingTypeViewModel.parkingTypeData.postValue(it)

            dismiss()
        }
    )
    override val onEmptySearchFilter: () -> Unit
        get() = {
            loadData()
        }
    override val onNotEmptySearchFilter: (Editable) -> Unit
        get() = {
            val filteredList = parkingTypes.filter {
                it.toUser.contains(it.toUser, ignoreCase = true)
            }
            masterDataAdapter.submitList(filteredList)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewSheetTitle.text = "Pilih Tipe Parkir"
        binding.containerSearchBar.visibility = View.GONE

        parkingTypeViewModel = ViewModelProvider(requireActivity())[ParkingTypeSpinnerViewModel::class.java]

        setupRecyclerView()
        loadData()
    }


    private val parkingTypes = MasterDataDeveloperPropertio.parking
    private fun loadData() {
        masterDataAdapter.submitList(parkingTypes)
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