package com.propertio.developer.unit.form

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.LiveData
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
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as? UnitFormActivity
        val activityBinding = activity?.binding

        observeLiveData(unitFormViewModel.namaUnit) { namaUnit ->
            binding.editNamaUnit.setText(namaUnit)
            Log.d("CreateUnitUmumFragment", "Nama unit: $namaUnit")
        }

        observeLiveData(unitFormViewModel.deskripsiUnit) { deskripsiUnit ->
            binding.editDeskripsiUnit.setText(deskripsiUnit)
        }

        observeLiveData(unitFormViewModel.stokUnit) { stokUnit ->
            binding.editStokUnit.setText(stokUnit)
        }

        observeLiveData(unitFormViewModel.hargaUnit) { hargaUnit ->
            binding.editHargaUnit.setText(hargaUnit)
        }

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

    private fun <T> observeLiveData(liveData: LiveData<T>, updateUI: (T) -> Unit) {
        liveData.observe(viewLifecycleOwner) { value ->
            Log.d("CreateUnitUmumFragment", "Observed value: $value")
            if (value == null) {
                Log.d("CreateUnitUmumFragment", "Value is null")
            } else {
                updateUI(value)
            }
        }
    }
}