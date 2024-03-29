package com.propertio.developer.dialog

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.propertio.developer.database.MasterData
import com.propertio.developer.database.MasterDataDeveloperPropertio
import com.propertio.developer.dialog.adapter.SimpleMasterDataAdapter
import com.propertio.developer.dialog.viewmodel.WaterTypeSpinnerViewModel

class WaterSheetFragment : BottomSheetDialogAbstract() {


    private lateinit var waterTypeViewModel: WaterTypeSpinnerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    private val adapter = SimpleMasterDataAdapter(
        onClickItemListener = {
            Log.d("WaterSheet", "setupRecyclerView: $it")
            waterTypeViewModel.waterTypeData.postValue(it)

            dismiss()
        }
    )
    override val onEmptySearchFilter: () -> Unit
        get() = {
            loadData()
        }
    override val onNotEmptySearchFilter: (Editable) -> Unit
        get() = { text ->
            val filteredList = waters.filter {
                it.toUser.contains(text.toString(), ignoreCase = true)
            }
            adapter.submitList(filteredList)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewSheetTitle.text = "Pilih Tipe Air"

        waterTypeViewModel = ViewModelProvider(requireActivity())[WaterTypeSpinnerViewModel::class.java]

        setupRecyclerView()
        loadData()
    }


    private val waters : List<MasterData> = MasterDataDeveloperPropertio.water
    private fun loadData() {
        adapter.submitList(waters)
    }

    private fun setupRecyclerView() {
        Log.d("WaterSheet", "setupRecyclerView: \n\t initial : $waters")
        with(binding) {
            recyclerViewSheet.apply {
                adapter = this@WaterSheetFragment.adapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            progressIndicatorSheet.visibility = View.GONE
        }
    }
}