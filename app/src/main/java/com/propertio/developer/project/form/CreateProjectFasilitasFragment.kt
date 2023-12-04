package com.propertio.developer.project.form

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.projectmanagement.UpdateProjectResponse
import com.propertio.developer.api.developer.type.GeneralTypeResponse
import com.propertio.developer.databinding.FragmentCreateProjectFasilitasBinding
import com.propertio.developer.project.list.FacilityTypeAdapter
import com.propertio.developer.project.viewmodel.ProjectFacilityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CreateProjectFasilitasFragment : Fragment() {

    private val binding by lazy { FragmentCreateProjectFasilitasBinding.inflate(layoutInflater) }
    private val formActivity by lazy { activity as ProjectFormActivity }
    private val activityBinding by lazy { formActivity.binding }
    private val projectFacilityViewModel : ProjectFacilityViewModel by activityViewModels()
    private val developerApi by lazy {
        Retro(TokenManager(requireContext()).token!!)
            .getRetroClientInstance()
            .create(DeveloperApi::class.java)
    }

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


        setRecyclerCategoryFacility()


        activityBinding.floatingButtonBack.setOnClickListener {
            //TODO: Back to previous fragment

            formActivity.onBackButtonProjectManagementClick()
        }

        activityBinding.floatingButtonNext.setOnClickListener {
            //TODO: Delete this two line below
            formActivity.onNextButtonProjectManagementClick()
            return@setOnClickListener

            Log.d("CreateProjectFasilitasFragment", "next: ${projectFacilityViewModel.selectedFacilities}")

            val facilitiesMap = mutableMapOf<String, String>()
            projectFacilityViewModel.selectedFacilities.forEachIndexed { index, facility ->
                Log.d("CreateProjectFasilitasFragment", "now: $index $facility")
            }

            developerApi.sendFacilities(formActivity.projectId.toString(), facilitiesMap).enqueue(object : Callback<UpdateProjectResponse> {
                override fun onResponse(
                    call: Call<UpdateProjectResponse>,
                    response: Response<UpdateProjectResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("CreateProjectFasilitasFragment", "onResponse: ${response.body()}")
                        Toast.makeText(requireActivity(), "Berhasil menambahkan fasilitas", Toast.LENGTH_SHORT).show()
                        formActivity.onNextButtonProjectManagementClick()
                    } else {
                        Log.e("CreateProjectFasilitasFragment", "onResponse: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<UpdateProjectResponse>, t: Throwable) {
                    Log.e("CreateProjectFasilitasFragment", "onFailure: ${t.message}")
                }

            })

        }
    }

    private fun setRecyclerCategoryFacility() {
        val retro = Retro(TokenManager(requireContext()).token!!)
            .getRetroClientInstance()
            .create(DeveloperApi::class.java)

        lifecycleScope.launch {
            fetchFacilityType(retro)
        }
    }

    private suspend fun fetchFacilityType(developerApi: DeveloperApi) {
        withContext(Dispatchers.IO) {
            developerApi.getFacilityType().enqueue(object : Callback<GeneralTypeResponse> {
                override fun onResponse(
                    call: Call<GeneralTypeResponse>,
                    response: Response<GeneralTypeResponse>
                ) {
                    if (response.isSuccessful) {
                        val facilities = response.body()?.data

                        if (facilities != null) {
                            Log.d("CreateProjectFasilitasFragment", "onResponse Success: $facilities")

                            projectFacilityViewModel.facilityTypeList.addAll(facilities)
                            insertRecyclerList()


                        } else {
                            Log.d("CreateProjectFasilitasFragment", "onResponse Success but data is Null")
                        }

                    } else {
                        Log.e(
                            "CreateProjectFasilitasFragment",
                            "onResponse: ${response.message()}"
                        )
                    }
                }

                override fun onFailure(call: Call<GeneralTypeResponse>, t: Throwable) {
                    Log.e("CreateProjectFasilitasFragment", "onFailure: ${t.message}")

                    // TODO: Peringati user jika internet mati

                }


            })
        }
    }

    private fun insertRecyclerList() {
        with(binding) {
            recyclerViewFasilitas.apply {
                adapter = FacilityTypeAdapter(
                    context = requireContext(),
                    facilityTypeList = projectFacilityViewModel.facilityTypeList,
                    selectedFacilities = projectFacilityViewModel.selectedFacilities,
                    onSelectItemFacilityType = { facilityType ->
                        if (!projectFacilityViewModel.selectedFacilities.contains(facilityType.id.toString())) {
                            projectFacilityViewModel.selectedFacilities.add(facilityType.id.toString())
                            Log.d("CreateProjectFasilitasFragment", "Select facilityType: $facilityType")
                        } else {
                            Log.d("CreateProjectFasilitasFragment", "Duplicated Select facilityType: $facilityType")
                        }

                    },
                    onDeselectItemFacilityType = { facilityType ->
                        val result = projectFacilityViewModel.selectedFacilities.remove(facilityType.id.toString())
                        Log.d("CreateProjectFasilitasFragment", "Deselect facilityType: $result")
                    }
                )

                layoutManager = LinearLayoutManager(requireContext())

            }
        }
    }
}