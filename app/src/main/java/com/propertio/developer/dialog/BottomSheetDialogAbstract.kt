package com.propertio.developer.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.propertio.developer.databinding.FragmentBottomRecyclerWithSearchBarSheetBinding

abstract class BottomSheetDialogAbstract : BottomSheetDialogFragment() {
    abstract val onEmptySearchFilter : () -> Unit
    abstract val onNotEmptySearchFilter : (Editable) -> Unit

    val binding by lazy {
        FragmentBottomRecyclerWithSearchBarSheetBinding.inflate(layoutInflater)
    }

    private fun setupSearchBar() {
        binding.searchBarBottomSheet.doAfterTextChanged { text ->
            if (text.isNullOrEmpty()) {
                onEmptySearchFilter()
                return@doAfterTextChanged
            } else {
                onNotEmptySearchFilter(text)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isDialogOpen = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchBar()
    }

    override fun onDestroy() {
        super.onDestroy()
        isDialogOpen = false
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        isDialogOpen = false
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (!isDialogOpen) {
            try {
                super.show(manager, tag)
            } catch (e: IllegalStateException) {
                Log.e("BottomSheetDialogAbstract", "show: ${e.message}", e)
            }
        }
    }

    companion object {
        var isDialogOpen = false
    }

}