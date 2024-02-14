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
import com.propertio.developer.dialog.viewmodel.RoadAccessTypeSpinnerViewModel

class RoadAccessSheetFragment : BottomSheetDialogAbstract() {


    private lateinit var roadAccessTypeViewModel: RoadAccessTypeSpinnerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    private val masterDataAdapter = SimpleMasterDataAdapter(
        onClickItemListener = {
            Log.d("RoadAccessSheet", "setupRecyclerView: $it")
            roadAccessTypeViewModel.roadAccessTypeData.postValue(it)

            dismiss()
        }
    )
    override val onEmptySearchFilter: () -> Unit
        get() = { loadData() }
    override val onNotEmptySearchFilter: (Editable) -> Unit
        get() = { search ->
            val filteredList = roadAccessTypes.filter {
                it.toUser.contains(search.toString(), ignoreCase = true)
            }
            masterDataAdapter.submitList(filteredList)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewSheetTitle.text = "Pilih Tipe Akses Jalan"

        roadAccessTypeViewModel = ViewModelProvider(requireActivity())[RoadAccessTypeSpinnerViewModel::class.java]

        setupRecyclerView()
        loadData()
    }


    private val roadAccessTypes = MasterDataDeveloperPropertio.roadAccess
    private fun loadData() {
        masterDataAdapter.submitList(roadAccessTypes)
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