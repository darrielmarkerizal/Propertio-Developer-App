package com.propertio.developer.unit.form

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.databinding.FragmentCreateUnitUmumBinding

class CreateUnitUmumFragment : Fragment() {
    private val unitFormViewModel : UnitFormViewModel by activityViewModels()

    private val binding by lazy {
        FragmentCreateUnitUmumBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as? UnitFormActivity
        val activityBinding = activity?.binding

        unitFormViewModel.isAlreadyUploaded.observe(viewLifecycleOwner) {
            if (it) {
                Log.d("CreateProjectInformasiUmumFragment", "onViewCreated Updated: $it")
                loadTextData()
                binding.textTitleUnit.text = "Edit Unit"
                unitFormViewModel.isUploaded = it
            }
        }

        activityBinding?.floatingButtonBack?.setOnClickListener {
            activity?.onBackButtonUnitManagementClick()
        }

        activityBinding?.floatingButtonNext?.setOnClickListener {
            with(binding) {
                if (!validateEditText(editNamaUnit, "Nama unit tidak boleh kosong")) return@setOnClickListener
                if (!validateNumberEditText(editStokUnit, "Stok unit harus berupa angka dan tidak boleh ada koma")) return@setOnClickListener
                if (!validateEditText(editHargaUnit, "Harga unit tidak boleh kosong")) return@setOnClickListener
                if (!validateNumberEditText(editHargaUnit, "Harga unit harus berupa angka dan tidak boleh ada koma")) return@setOnClickListener

                unitFormViewModel.apply {
                    namaUnit = editNamaUnit.text.toString()
                    deskripsiUnit = editDeskripsiUnit.text.toString()
                    stokUnit = editStokUnit.text.toString()
                    hargaUnit = editHargaUnit.text.toString()
                }
            }

            activity?.onNextButtonUnitManagementClick()
        }
    }

    private fun validateEditText(editText: EditText, errorMessage: String): Boolean {
        return editText.text.toString().let { text ->
            if (text.isEmpty()) {
                editText.error = errorMessage
                false
            } else {
                true
            }
        }
    }

    private fun validateNumberEditText(editText: EditText, errorMessage: String): Boolean {
        return editText.text.toString().let { text ->
            if (!text.all { it.isDigit() }) {
                editText.error = errorMessage
                false
            } else {
                true
            }
        }
    }

    private fun loadTextData() {
        unitFormViewModel.printLog()
        with(binding) {
            editNamaUnit.setText(unitFormViewModel.namaUnit)
            editDeskripsiUnit.setText(unitFormViewModel.deskripsiUnit)
            editStokUnit.setText(unitFormViewModel.stokUnit)
            editHargaUnit.setText(unitFormViewModel.hargaUnit)
        }
    }
}