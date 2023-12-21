package com.propertio.developer.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.propertio.developer.databinding.FragmentUnitLakuBinding

typealias UnitLakuListener = (Int) -> Unit
class DialogUnitLaku(
    private val stokUnit: Int,
    private val unitLakuListener: UnitLakuListener
) : DialogFragment() {
    private val binding by lazy {
        FragmentUnitLakuBinding.inflate(layoutInflater)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val dialog = Dialog(it)
            dialog.setContentView(binding.root)

            with(binding) {

                textViewStockUnitDialog.text = "$stokUnit"

                buttonBatalDialog.setOnClickListener {
                    editTextUnitLakuDialog.clearFocus()
                    dialog.dismiss()
                }

                buttonSimpanDialog.setOnClickListener {
                    editTextUnitLakuDialog.clearFocus()
                    val text = editTextUnitLakuDialog.text
                    if (text.toString().isEmpty()) {
                        editTextUnitLakuDialog.error = "Mohon isi unit laku"
                        return@setOnClickListener
                    } else if (text.toString().isNotEmpty() && text.toString().toInt() > stokUnit) {
                        editTextUnitLakuDialog.error = "Unit laku tidak boleh lebih dari stok unit"
                        return@setOnClickListener
                    } else if (text.toString().isNotEmpty() && text.toString().toInt() < 0) {
                        editTextUnitLakuDialog.error = "Unit laku tidak boleh kurang dari 0"
                        return@setOnClickListener
                    }
                    unitLakuListener(editTextUnitLakuDialog.text.toString().toInt())
                    dialog.dismiss()
                }

                editTextUnitLakuDialog.doAfterTextChanged { text ->
                    if (text.toString().isNotEmpty() && text.toString().toInt() >= 0 && text.toString().toInt() <= stokUnit) {
                        editTextUnitLakuDialog.error = null
                    } else if (text.toString().isNotEmpty() && text.toString().toInt() > stokUnit) {
                        editTextUnitLakuDialog.error = "Unit laku tidak boleh lebih dari stok unit"
                    } else if (text.toString().isNotEmpty() && text.toString().toInt() < 0) {
                        editTextUnitLakuDialog.error = "Unit laku tidak boleh kurang dari 0"
                    }
                }
            }

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}