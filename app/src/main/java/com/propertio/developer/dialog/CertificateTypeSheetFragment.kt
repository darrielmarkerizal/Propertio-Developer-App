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
import com.propertio.developer.dialog.viewmodel.CertificateTypeSpinnerViewModel

class CertificateTypeSheetFragment : BottomSheetDialogAbstract() {


    private lateinit var certificateTypeViewModel: CertificateTypeSpinnerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    private val certificateTypes = MasterDataDeveloperPropertio.certificate
    private val simpleMasterDataAdapter = SimpleMasterDataAdapter(
        onClickItemListener = {
            Log.d("CertificateTypeSheet", "setupRecyclerView: $it")
            certificateTypeViewModel.certificateTypeData.postValue(it)


            dismiss()
        }
    )
    override val onEmptySearchFilter: () -> Unit
        get() = { loadData() }
    override val onNotEmptySearchFilter: (Editable) -> Unit
        get() = {
            val filteredList = certificateTypes.filter {
                it.toUser.contains(it.toUser, ignoreCase = true)
            }
            simpleMasterDataAdapter.submitList(filteredList)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewSheetTitle.text = "Pilih Tipe Properti"
        binding.containerSearchBar.visibility = View.GONE

        certificateTypeViewModel = ViewModelProvider(requireActivity())[CertificateTypeSpinnerViewModel::class.java]

        setupRecyclerView()
        loadData()
    }

    private fun loadData() {
        simpleMasterDataAdapter.submitList(certificateTypes)
    }

    private fun setupRecyclerView() {
        with(binding) {
            recyclerViewSheet.apply {
                adapter = simpleMasterDataAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            progressIndicatorSheet.visibility = View.GONE
        }
    }


}
