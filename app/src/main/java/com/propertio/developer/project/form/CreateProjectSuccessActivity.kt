package com.propertio.developer.project.form

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.propertio.developer.R
import com.propertio.developer.databinding.ActivityProjectSuccessBinding
import com.propertio.developer.project.ProjectDetailActivity
import com.propertio.developer.project.form.ProjectFormActivity.Companion.IS_CREATE_NEW

class CreateProjectSuccessActivity : AppCompatActivity() {

    private val binding by lazy { ActivityProjectSuccessBinding.inflate(layoutInflater) }

    private val launcherToRincian = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            Log.d("ProjectFragment Launcher", "Result OK")

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val projectID = intent.getIntExtra(ProjectDetailActivity.PROJECT_ID, 0)
        val propertyType = intent.getStringExtra("Property Type")

        val isCreateNew = intent.getBooleanExtra(IS_CREATE_NEW, true)
        Log.v("CreateProjectSuccessActivity", "isCreateNew: $isCreateNew")
        binding.nextTxtProjectSuccess.text = if (isCreateNew)
                    getString(R.string.project_success_create_new_project)
                    else getString(R.string.project_success_edit_project)

        binding.buttonKembaliKeHalamanProyek.text = if (isCreateNew)
            getString(R.string.kembali_ke_halaman_proyek)
        else getString(R.string.lihat_rincian_proyek)


        with(binding) {
            buttonKembaliKeHalamanProyek.setOnClickListener {
                if (isCreateNew.not() && projectID != 0) {
                    val intentToDetailProject = Intent(this@CreateProjectSuccessActivity, ProjectDetailActivity::class.java)
                    intentToDetailProject.putExtra(ProjectDetailActivity.PROJECT_ID, projectID)
                    intentToDetailProject.putExtra("Property Type", propertyType)
                    launcherToRincian.launch(intentToDetailProject)
                }

                finish()
            }
        }

    }
}