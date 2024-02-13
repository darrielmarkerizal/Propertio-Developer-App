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
import com.propertio.developer.dialog.adapter.CertificateTypeAdapter
import com.propertio.developer.dialog.viewmodel.CertificateTypeSpinnerViewModel

class CertificateTypeSheetFragment : BottomSheetDialogAbstract() {

    private val binding by lazy {
        FragmentBottomRecyclerWithSearchBarSheetBinding.inflate(layoutInflater)
    }

    private lateinit var certificateTypeViewModel: CertificateTypeSpinnerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewSheetTitle.text = "Pilih Tipe Properti"
        binding.containerSearchBar.visibility = View.GONE

        certificateTypeViewModel = ViewModelProvider(requireActivity())[CertificateTypeSpinnerViewModel::class.java]

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        with(binding) {
            recyclerViewSheet.apply {
                adapter = CertificateTypeAdapter(
                    certificateTypes = MasterDataDeveloperPropertio.certificate,
                    onClickItemListener = {
                        Log.d("CertificateTypeSheet", "setupRecyclerView: $it")
                        certificateTypeViewModel.certificateTypeData.postValue(it)


                        dismiss()
                    }
                )
                layoutManager = LinearLayoutManager(requireContext())
            }

            progressIndicatorSheet.visibility = View.GONE
        }
    }


}
