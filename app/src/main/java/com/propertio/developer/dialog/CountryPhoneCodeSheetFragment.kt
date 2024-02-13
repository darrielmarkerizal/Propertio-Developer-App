package com.propertio.developer.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.propertio.developer.databinding.FragmentCountryPhoneCodeSheetBinding
import com.propertio.developer.dialog.adapter.PhoneCodeAdapter
import com.propertio.developer.dialog.viewmodel.PhoneCodeViewModel


class CountryPhoneCodeSheetFragment : BottomSheetDialogFragment() {

    private val binding by lazy { FragmentCountryPhoneCodeSheetBinding.inflate(layoutInflater) }


    private lateinit var phoneCodeViewModel: PhoneCodeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        phoneCodeViewModel = ViewModelProvider(requireActivity())[PhoneCodeViewModel::class.java]

        setupRecycler()
    }

    private fun setupRecycler() {
        with(binding) {
            recyclerViewSheetPhoneCode.apply {
                adapter = PhoneCodeAdapter(
                    onClickCardListener = {
                        Log.d("CountryPhoneCodeSheetFragment", "onViewCreated: $it")
                        phoneCodeViewModel.phoneCodeData.postValue(it)
                        dismiss()
                    }
                )

                layoutManager = LinearLayoutManager(context)
            }
        }
    }


}