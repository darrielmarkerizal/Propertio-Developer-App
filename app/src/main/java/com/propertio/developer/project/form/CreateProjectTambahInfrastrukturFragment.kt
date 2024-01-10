package com.propertio.developer.project.form

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.projectmanagement.PostStoreProjectInfrastructureRequest
import com.propertio.developer.api.developer.projectmanagement.UpdateProjectResponse
import com.propertio.developer.databinding.FragmentCreateProjectTambahInfrastrukturBinding
import com.propertio.developer.dialog.InfrastructureTypeSheetFragment
import com.propertio.developer.dialog.viewmodel.InfrastructureTypeSpinnerViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CreateProjectTambahInfrastrukturFragment : Fragment() {

    private val binding by lazy { FragmentCreateProjectTambahInfrastrukturBinding.inflate(layoutInflater) }
    private val formActivity by lazy { activity as ProjectFormActivity }
    private val activityBinding by lazy { formActivity.binding }

    private val developerApi by lazy {
        Retro(TokenManager(requireContext()).token)
            .getRetroClientInstance()
            .create(DeveloperApi::class.java)
    }

    //spinner
    private var isInfrastructureTypeSpinnerSelected = false
    private val infrastructureTypeViewModel by lazy { ViewModelProvider(requireActivity())[InfrastructureTypeSpinnerViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideNavigation()

        //spinner
        infrastructureTypeSpinner()

        //Simpan
        binding.buttonSimpan.setOnClickListener {
            if (isInfrastructureTypeSpinnerSelected) {
                if (binding.editNamaInfrastrukturProject.text == null || binding.editNamaInfrastrukturProject.text.toString().isEmpty()) {
                    binding.editNamaInfrastrukturProject.error = "Mohon Isi Nama Infrastruktur"
                    return@setOnClickListener
                }

                uploadInfrastructure()

            } else {
                Toast.makeText(requireContext(), "Mohon Pilih Tipe Infrastruktur", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun uploadInfrastructure() {
        val newInfrastructure = PostStoreProjectInfrastructureRequest(
            projectId = formActivity.projectId,
            infrastructureTypeId = infrastructureTypeViewModel.infrastructureTypeData.value?.id,
            name = binding.editNamaInfrastrukturProject.text.toString()
        )
        developerApi.sendInfrastructure(newInfrastructure).enqueue(object : Callback<UpdateProjectResponse> {
            override fun onResponse(
                call: Call<UpdateProjectResponse>,
                response: Response<UpdateProjectResponse>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Berhasil Menambahkan Infrastruktur", Toast.LENGTH_SHORT).show()

                    // change back to previous fragment
                    formActivity.replaceFragment(formActivity.formsFragment[formActivity.currentFragmentIndex])
                    showNavigation()
                } else {
                    Log.e("FormTambahInfrastructure", "onResponse: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<UpdateProjectResponse>, t: Throwable) {
                Log.e("FormTambahInfrastructure", "onFailure: ${t.message}")
            }


        })
    }

    private fun infrastructureTypeSpinner() {
        binding.spinnerTipeInfrastrukturProject.setOnClickListener {
            InfrastructureTypeSheetFragment().show(childFragmentManager, "INFRASTRUCTURE_TYPE")
            Log.d("FormTambahInfrastructure", "infrastructureTypeSpinner: $isInfrastructureTypeSpinnerSelected")
        }

        infrastructureTypeViewModel.infrastructureTypeData.observe(viewLifecycleOwner) {
            binding.spinnerTipeInfrastrukturProject.text = it.name
            binding.spinnerTipeInfrastrukturProject.error = null

            isInfrastructureTypeSpinnerSelected = true
            Log.d("FormTambahInfrastructure", "infrastructureTypeSpinner: $isInfrastructureTypeSpinnerSelected")
        }
    }

    private fun hideNavigation() {
        with(activityBinding) {
            floatingButtonNext.visibility = View.GONE
            floatingButtonBack.visibility = View.GONE
        }
    }

    private fun showNavigation() {
        with(activityBinding) {
            floatingButtonNext.visibility = View.VISIBLE
            floatingButtonBack.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showNavigation()
    }
}