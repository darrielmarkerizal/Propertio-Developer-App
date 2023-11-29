package com.propertio.developer.project.form

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.propertio.developer.R
import com.propertio.developer.databinding.ActivityProjectFormBinding
import com.propertio.developer.databinding.ToolbarBinding

class ProjectFormActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityProjectFormBinding.inflate(layoutInflater)
    }

    private val formsFragment = listOf(
        CreateProjectInformasiUmumFragment(),
        CreateProjectLokasiFragment(),
        CreateProjectMediaFragment(),
        CreateProjectFasilitasFragment(),
        CreateProjectInfrastrukturFragment(),
    )

    private var currentFragmentIndex : Int = 0
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

        with(binding) {
            floatingButtonBack.setOnClickListener {
                if (currentFragmentIndex <= 0) {
                    Log.d("ProjectForm", "Exit From Project Form")
                    finish()
                }

                currentFragmentIndex--
                replaceFragment(formsFragment[currentFragmentIndex])
            }
            floatingButtonNext.setOnClickListener {
                if (currentFragmentIndex == formsFragment.size - 1) {
                    Log.d("ProjectForm", "Post Project Form")

                    // TODO: Post Project Form
                    Toast.makeText(this@ProjectFormActivity, "Post Project Form Belum Tersedia", Toast.LENGTH_SHORT).show()
                }

                currentFragmentIndex++
                replaceFragment(formsFragment[currentFragmentIndex])
            }

        }

    }

    private fun setInitialFragment() {
        replaceFragment(formsFragment[currentFragmentIndex])
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_container_project_form, fragment)
        fragmentTransaction.commit()
    }


    private fun setToolbarToCreate(bindingToolbar: ToolbarBinding) {
        bindingToolbar.textViewTitle.text = "Tambah Proyek"
    }


}