package com.propertio.developer.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BottomSheetDialogAbstract : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isDialogOpen = true
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