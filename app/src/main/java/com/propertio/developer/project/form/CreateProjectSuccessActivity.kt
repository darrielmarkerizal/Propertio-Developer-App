package com.propertio.developer.project.form

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.propertio.developer.R
import com.propertio.developer.databinding.ActivityProjectSuccessBinding
import com.propertio.developer.project.form.ProjectFormActivity.Companion.IS_CREATE_NEW

class CreateProjectSuccessActivity : AppCompatActivity() {

    private val binding by lazy { ActivityProjectSuccessBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val isCreateNew = intent.getBooleanExtra(IS_CREATE_NEW, true)
        Log.v("CreateProjectSuccessActivity", "isCreateNew: $isCreateNew")
        binding.nextTxtProjectSuccess.text = if (isCreateNew)
                    getString(R.string.project_success_create_new_project)
                    else getString(R.string.project_success_edit_project)


        with(binding) {
            buttonKembaliKeHalamanProyek.setOnClickListener {
                finish()
            }
        }

    }
}