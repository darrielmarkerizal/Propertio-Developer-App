package com.propertio.developer.project.form

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.propertio.developer.R
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.projectmanagement.ProjectDetail
import com.propertio.developer.api.developer.projectmanagement.UpdateProjectResponse
import com.propertio.developer.databinding.FragmentCreateProjectInfrastrukturBinding
import com.propertio.developer.project.list.ProjectInfrastructureAdapter
import com.propertio.developer.project.viewmodel.ProjectInfrastructureViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CreateProjectInfrastrukturFragment : Fragment() {

    private val binding by lazy { FragmentCreateProjectInfrastrukturBinding.inflate(layoutInflater) }
    private val formActivity by lazy { activity as ProjectFormActivity }
    private val activityBinding by lazy { formActivity.binding }

    private val projectInfrastructureViewModel : ProjectInfrastructureViewModel by activityViewModels()

    private val developerApi by lazy {
        Retro(TokenManager(requireContext()).token)
            .getRetroClientInstance()
            .create(DeveloperApi::class.java)
    }

    lateinit var projectInfrastructureAdapter : ProjectInfrastructureAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        projectInfrastructureAdapter = ProjectInfrastructureAdapter(
            context = requireContext(),
            listInfrastructure = projectInfrastructureViewModel.projectInfrastructureList.value ?: listOf(),
            onItemDelete = {
                deleteProjectInfrastucture(it.id)
            }
        )
        return binding.root
    }

    private fun deleteProjectInfrastucture(id: Int?) {
        if (id == null) {
            Log.wtf("CreateProjectInfrastrukturFragment", "deleteProjectInfrastucture: id is null")
            Toast.makeText(requireContext(), "Terjadi Kesalahan Sistem", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                developerApi.deleteProjectInfrastructure(id).enqueue(object : Callback<UpdateProjectResponse> {
                    override fun onResponse(
                        call: Call<UpdateProjectResponse>,
                        response: Response<UpdateProjectResponse>
                    ) {
                        if (response.isSuccessful) {
                            lifecycleScope.launch {
                                fetchData()
                            }
                        } else {
                            Log.e("CreateProjectInfrastrukturFragment", "onResponse: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<UpdateProjectResponse>, t: Throwable) {
                        Log.e("CreateProjectInfrastrukturFragment", "onFailure: ${t.message}")
                    }


                })
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch {
            // Fetch Data
            fetchData()

            // Delete All Button
            binding.linkTextViewProjectInfrastukturReset.setOnClickListener {
                lifecycleScope.launch {
                    deleteAllProjectInfrastructure()
                }

            }
        }

        // Buton Unggah Project Infrastruktur
        binding.buttonUnggahProjectInfrastruktur.setOnClickListener {
            formActivity.replaceFragment(CreateProjectTambahInfrastrukturFragment())
        }

        // Set RecylcerView
        setupRecylcerView()


        activityBinding.floatingButtonBack.setOnClickListener {
            // TODO: Back Button

            formActivity.onBackButtonProjectManagementClick()
        }

        activityBinding.floatingButtonNext.setOnClickListener {
            // TODO: Next Button.. Publish Or Not?

            formActivity.onNextButtonProjectManagementClick()
        }

    }

    private suspend fun deleteAllProjectInfrastructure() {
        withContext(Dispatchers.IO) {
            projectInfrastructureViewModel.projectInfrastructureList.value?.forEach {
                deleteProjectInfrastucture(it.id)
            }
        }
    }

    private fun setupRecylcerView() {
        binding.recylerViewInfrastrukturProject.apply {
            adapter = projectInfrastructureAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        projectInfrastructureViewModel.projectInfrastructureList.observe(viewLifecycleOwner) {
            projectInfrastructureAdapter.listInfrastructure = it
            projectInfrastructureAdapter.notifyDataSetChanged()
        }
    }

    private suspend fun fetchData() {
        withContext(Dispatchers.IO) {
            if (formActivity.projectId == null) {
                Log.wtf("CreateProjectInfrastrukturFragment", "setupRecylcerView: SomeHow projectId is null")
                Toast.makeText(requireContext(), "Terjadi Kesalahan Sistem", Toast.LENGTH_SHORT).show()
                return@withContext
            }
            developerApi.getProjectDetail(formActivity.projectId!!).enqueue(object : Callback<ProjectDetail> {
                override fun onResponse(call: Call<ProjectDetail>, response: Response<ProjectDetail>) {
                    if (response.isSuccessful) {
                        val dataResponse = response.body()?.data
                        if (dataResponse != null) {
                            projectInfrastructureViewModel.projectInfrastructureList.value = dataResponse.projectInfrastructures
                        } else {
                            Log.e("CreateProjectInfrastrukturFragment", "onResponse: dataResponse is null")
                        }
                    } else {
                        Log.e("CreateProjectInfrastrukturFragment", "onResponse: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<ProjectDetail>, t: Throwable) {
                    Log.e("CreateProjectInfrastrukturFragment", "onFailure: ${t.message}")
                }

            })
        }

    }
}