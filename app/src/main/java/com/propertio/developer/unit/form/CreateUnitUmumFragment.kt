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
                unitFormViewModel.isUploaded = it
            }
        }

        activityBinding?.floatingButtonBack?.setOnClickListener {
            activity.onBackButtonUnitManagementClick()
        }

        activityBinding?.floatingButtonNext?.setOnClickListener {
            if (!validateEditText(binding.editNamaUnit, "Nama unit tidak boleh kosong")) return@setOnClickListener
            if (!validateNumberEditText(binding.editStokUnit, "Stok unit harus berupa angka dan tidak boleh ada koma")) return@setOnClickListener
            if (!validateEditText(binding.editHargaUnit, "Harga unit tidak boleh kosong")) return@setOnClickListener
            if (!validateNumberEditText(binding.editHargaUnit, "Harga unit harus berupa angka dan tidak boleh ada koma")) return@setOnClickListener

            unitFormViewModel.namaUnit = binding.editNamaUnit.text.toString()
            unitFormViewModel.deskripsiUnit = binding.editDeskripsiUnit.text.toString()
            unitFormViewModel.stokUnit = binding.editStokUnit.text.toString()
            unitFormViewModel.hargaUnit = binding.editHargaUnit.text.toString()

            activity.onNextButtonUnitManagementClick()
        }
    }

    private fun validateEditText(editText: EditText, errorMessage: String): Boolean {
        val text = editText.text.toString()
        if (text.isEmpty()) {
            editText.error = errorMessage
            return false
        }
        return true
    }

    private fun validateNumberEditText(editText: EditText, errorMessage: String): Boolean {
        val text = editText.text.toString()
        if (!text.all { it.isDigit() }) {
            editText.error = errorMessage
            return false
        }
        return true
    }

    private fun loadTextData() {
        UnitFormViewModel().printLog()
        binding.editNamaUnit.setText(unitFormViewModel.namaUnit)
        binding.editDeskripsiUnit.setText(unitFormViewModel.deskripsiUnit)
        binding.editStokUnit.setText(unitFormViewModel.stokUnit)
        binding.editHargaUnit.setText(unitFormViewModel.hargaUnit)
    }
}