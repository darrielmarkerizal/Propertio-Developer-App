package com.propertio.developer.project.form

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.propertio.developer.PropertioDeveloperApplication
import com.propertio.developer.R
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.projectmanagement.UpdateProjectResponse
import com.propertio.developer.api.developer.type.GeneralTypeResponse
import com.propertio.developer.database.facility.FacilityTable
import com.propertio.developer.databinding.FragmentCreateProjectFasilitasBinding
import com.propertio.developer.project.list.FacilityAndInfrastructureTypeViewModel
import com.propertio.developer.project.list.FacilityTypeAdapter
import com.propertio.developer.project.viewmodel.FacilityViewModelFactory
import com.propertio.developer.project.viewmodel.ProjectFacilityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CreateProjectFasilitasFragment : Fragment() {

    private val binding by lazy { FragmentCreateProjectFasilitasBinding.inflate(layoutInflater) }
    private val formActivity by lazy { activity as ProjectFormActivity }
    private val activityBinding by lazy { formActivity.binding }
    private val currentTab : MutableLiveData<Int> = MutableLiveData(0)
    private val projectFacilityViewModel : ProjectFacilityViewModel by activityViewModels()
    private val developerApi by lazy {
        Retro(TokenManager(requireContext()).token!!)
            .getRetroClientInstance()
            .create(DeveloperApi::class.java)
    }

    private lateinit var facilityAndInfrastructureTypeViewModel: FacilityAndInfrastructureTypeViewModel
    private val facilityTypeAdapter by lazy {
        FacilityTypeAdapter(
            context = requireContext(),
            facilityTypeList = emptyList<FacilityTable>().toMutableList(),
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


        val factory = FacilityViewModelFactory((requireActivity().application as PropertioDeveloperApplication).repository)
        facilityAndInfrastructureTypeViewModel = ViewModelProvider(requireActivity(), factory)[FacilityAndInfrastructureTypeViewModel::class.java]


        setRecyclerCategoryFacility()

        binding.buttonFasilitas.setOnClickListener {
            lifecycleScope.launch {
                if (currentTab.asFlow().first() != 1) {
                    changeCategoryFacility("fasilitas")
                    currentTab.value = 1
                } else{
                    currentTab.value = 0
                    updateRecyclerList(facilityAndInfrastructureTypeViewModel.getAllFacilityType())

                }
            }

        }
        binding.buttonKeunggulan.setOnClickListener {
            lifecycleScope.launch {
                if (currentTab.asFlow().first() != 2) {
                    changeCategoryFacility("keunggulan")
                    currentTab.value = 2
                } else{
                    currentTab.value = 0
                    updateRecyclerList(facilityAndInfrastructureTypeViewModel.getAllFacilityType())

                }
            }
        }
        binding.buttonPerabot.setOnClickListener {
            lifecycleScope.launch {
                if (currentTab.asFlow().first() != 3) {
                    changeCategoryFacility("perabot")
                    currentTab.value = 3
                } else{
                    currentTab.value = 0
                    updateRecyclerList(facilityAndInfrastructureTypeViewModel.getAllFacilityType())

                }
            }
        }
        tabObserver()
        setupSearchFeature()


        activityBinding.floatingButtonBack.setOnClickListener {
            //TODO: Back to previous fragment

            formActivity.onBackButtonProjectManagementClick()
        }

        activityBinding.floatingButtonNext.setOnClickListener {

            Log.d("CreateProjectFasilitasFragment", "next: ${projectFacilityViewModel.selectedFacilities}")

            val facilitiesMap = mutableMapOf<String, String>()
            projectFacilityViewModel.selectedFacilities.forEachIndexed { index, facility ->
                Log.d("CreateProjectFasilitasFragment", "now: $index $facility")
                facilitiesMap["facilities[$index]"] = facility
            }

            developerApi.sendFacilities(formActivity.projectId.toString(), facilitiesMap).enqueue(object : Callback<UpdateProjectResponse> {
                override fun onResponse(
                    call: Call<UpdateProjectResponse>,
                    response: Response<UpdateProjectResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("CreateProjectFasilitasFragment", "onResponse: ${response.body()}")
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

    private fun setupSearchFeature() {
        binding.textinputCariFasilitas.doAfterTextChanged {
            binding.containerButtonCategory.visibility = View.GONE

            val query = it.toString()
            if (query.isNotEmpty()) {
                lifecycleScope.launch {
                    val facilityList = withContext(Dispatchers.IO) {
                        facilityAndInfrastructureTypeViewModel.searchFacility(query)
                    }
                    updateRecyclerList(facilityList)
                }
            } else {
                lifecycleScope.launch {
                    val facilityList = withContext(Dispatchers.IO) {
                        facilityAndInfrastructureTypeViewModel.getAllFacilityType()
                    }

                    updateRecyclerList(facilityList)
                }

                binding.containerButtonCategory.visibility = View.VISIBLE
                currentTab.value = 0
            }
        }

        binding.textInputLayoutFasilitas.setEndIconOnClickListener {
            binding.textinputCariFasilitas.text?.clear()
            binding.textinputCariFasilitas.clearFocus()

            binding.containerButtonCategory.visibility = View.VISIBLE
            currentTab.value = 0
        }
    }

    private fun tabObserver() {
        // initial state
        binding.buttonPerabot.isActivated = true
        binding.buttonKeunggulan.isActivated = true
        binding.buttonFasilitas.isActivated = true

        binding.buttonPerabot.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.buttonKeunggulan.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.buttonFasilitas.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        currentTab.observe(viewLifecycleOwner) {
            when (it) {
                0 -> {
                    binding.buttonPerabot.isActivated = true
                    binding.buttonKeunggulan.isActivated = true
                    binding.buttonFasilitas.isActivated = true

                    binding.buttonPerabot.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    binding.buttonKeunggulan.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    binding.buttonFasilitas.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                1 -> {
                    binding.buttonPerabot.isActivated = false
                    binding.buttonKeunggulan.isActivated = false
                    binding.buttonFasilitas.isActivated = true

                    binding.buttonPerabot.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    binding.buttonKeunggulan.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    binding.buttonFasilitas.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                2 -> {
                    binding.buttonPerabot.isActivated = false
                    binding.buttonKeunggulan.isActivated = true
                    binding.buttonFasilitas.isActivated = false

                    binding.buttonPerabot.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    binding.buttonKeunggulan.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    binding.buttonFasilitas.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
                3 -> {
                    binding.buttonPerabot.isActivated = true
                    binding.buttonKeunggulan.isActivated = false
                    binding.buttonFasilitas.isActivated = false

                    binding.buttonPerabot.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    binding.buttonKeunggulan.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    binding.buttonFasilitas.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
            }
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

    private fun changeCategoryFacility(category: String) {
        lifecycleScope.launch {
            val facilityList = withContext(Dispatchers.IO) {
                facilityAndInfrastructureTypeViewModel.getAllFacilityByCategory(category)
            }

            updateRecyclerList(facilityList)
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

                            lifecycleScope.launch {
                                facilities.forEach {
                                    facilityAndInfrastructureTypeViewModel.insertFacility(
                                        id = it.id!!,
                                        name = it.name!!,
                                        category = it.category!!,
                                        icon = it.icon ?: "",
                                    )
                                }
                                updateRecyclerList(facilityAndInfrastructureTypeViewModel.getAllFacilityType())
                            }

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
            lifecycleScope.launch {
                recyclerViewFasilitas.apply {
                    adapter = facilityTypeAdapter
                    layoutManager = LinearLayoutManager(requireContext())

                }
            }

        }
    }

    private fun updateRecyclerList(facilityList : List<FacilityTable>) {
        lifecycleScope.launch {
            facilityTypeAdapter.updateFacilityTypeList(facilityList)
        }
    }
}