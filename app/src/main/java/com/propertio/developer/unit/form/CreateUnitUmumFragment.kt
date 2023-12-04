package com.propertio.developer.unit.form

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.databinding.FragmentCreateUnitUmumBinding

class CreateUnitUmumFragment : Fragment() {
    val unitFormViewModel : UnitFormViewModel by lazy {
        ViewModelProvider(this).get(UnitFormViewModel::class.java)
    }

    private val binding by lazy {
        FragmentCreateUnitUmumBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as? UnitFormActivity
        val activityBinding = activity?.binding

        unitFormViewModel.namaUnit.observe(viewLifecycleOwner, { value ->
            binding.editNamaUnit.setText(value)
        })

        unitFormViewModel.deskripsiUnit.observe(viewLifecycleOwner, { value ->
            binding.editDeskripsiUnit.setText(value)
        })

        unitFormViewModel.stokUnit.observe(viewLifecycleOwner, { value ->
            binding.editStokUnit.setText(value)
        })

        unitFormViewModel.hargaUnit.observe(viewLifecycleOwner, { value ->
            binding.editHargaUnit.setText(value)
        })

        activityBinding?.floatingButtonBack?.setOnClickListener {
            activity.onBackButtonUnitManagementClick()
        }

        activityBinding?.floatingButtonNext?.setOnClickListener {
            if (!validateEditText(binding.editNamaUnit, "Nama unit tidak boleh kosong")) return@setOnClickListener
            if (!validateNumberEditText(binding.editStokUnit, "Stok unit harus berupa angka dan tidak boleh ada koma")) return@setOnClickListener
            if (!validateEditText(binding.editHargaUnit, "Harga unit tidak boleh kosong")) return@setOnClickListener
            if (!validateNumberEditText(binding.editHargaUnit, "Harga unit harus berupa angka dan tidak boleh ada koma")) return@setOnClickListener

            Log.d("CreateUnitUmumFragment", "Nama unit: ${binding.editNamaUnit.text}")
            Log.d("CreateUnitUmumFragment", "Deskripsi unit: ${binding.editDeskripsiUnit.text}")
            Log.d("CreateUnitUmumFragment", "Stok unit: ${binding.editStokUnit.text}")
            Log.d("CreateUnitUmumFragment", "Harga unit: ${binding.editHargaUnit.text}")

            activity?.unitFormViewModel?.updateNamaUnit(binding.editNamaUnit.text.toString())
            activity?.unitFormViewModel?.updateDeskripsiUnit(binding.editDeskripsiUnit.text.toString())
            activity?.unitFormViewModel?.updateStokUnit(binding.editStokUnit.text.toString())
            activity?.unitFormViewModel?.updateHargaUnit(binding.editHargaUnit.text.toString())

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
}