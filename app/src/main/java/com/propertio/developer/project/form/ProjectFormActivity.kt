package com.propertio.developer.project.form

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.propertio.developer.R
import com.propertio.developer.databinding.ActivityProjectFormBinding
import com.propertio.developer.databinding.ToolbarBinding
import com.propertio.developer.project.viewmodel.ProjectFacilityViewModel
import com.propertio.developer.project.viewmodel.ProjectInformationLocationViewModel
import com.propertio.developer.project.viewmodel.ProjectMediaViewModel
import com.propertio.developer.project_management.ButtonNavigationProjectManagementClickListener

class ProjectFormActivity : AppCompatActivity(), ButtonNavigationProjectManagementClickListener {

    val binding by lazy {
        ActivityProjectFormBinding.inflate(layoutInflater)
    }

    // ViewModels
    internal val projectInformationLocationViewModel : ProjectInformationLocationViewModel by viewModels()
    internal val projectMedia : ProjectMediaViewModel by viewModels()
    internal val projectFacility : ProjectFacilityViewModel by viewModels()
    internal var projectId: Int? = null

    internal val formsFragment = listOf(
        CreateProjectInformasiUmumFragment(),
        CreateProjectLokasiFragment(),
        CreateProjectMediaFragment(),
        CreateProjectFasilitasFragment(),
        CreateProjectInfrastrukturFragment(),
    )

    internal var currentFragmentIndex : Int = 0
        set(value) {
            if (value in formsFragment.indices) {
                Log.d("TAG", "set: $value")
                field = value
            } else {
                Log.d("TAG", "set: $value")
            }
        }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val bindingToolbar = binding.toolbarContainerProjectForm

        setToolbarToCreate(bindingToolbar)

        setInitialFragment()

    }

    private fun setInitialFragment() {
        replaceFragment(formsFragment[currentFragmentIndex])
    }

    internal fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_container_project_form, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


    private fun setToolbarToCreate(bindingToolbar: ToolbarBinding) {
        bindingToolbar.textViewTitle.text = "Tambah Proyek"
    }


    override fun onNextButtonProjectManagementClick() {
        if (currentFragmentIndex == formsFragment.size - 1) {
            Log.d("ProjectForm", "Post Project Form")

            // TODO: Post Project Form
            Toast.makeText(this@ProjectFormActivity, "Post Project Form Belum Tersedia", Toast.LENGTH_SHORT).show()
        }

        currentFragmentIndex++
        replaceFragment(formsFragment[currentFragmentIndex])
    }


    override fun onBackButtonProjectManagementClick() {
        if (currentFragmentIndex <= 0) {
            Log.d("ProjectForm", "Exit From Project Form")
            finish()
        }

        currentFragmentIndex--
        replaceFragment(formsFragment[currentFragmentIndex])
    }




}