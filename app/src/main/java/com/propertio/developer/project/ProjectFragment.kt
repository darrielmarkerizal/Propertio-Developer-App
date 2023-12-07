package com.propertio.developer.project


import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Switch
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.WorkerThread
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.propertio.developer.PropertioDeveloperApplication
import com.propertio.developer.R
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.projectmanagement.UpdateProjectResponse
import com.propertio.developer.database.project.ProjectTable
import com.propertio.developer.databinding.FragmentProjectBinding
import com.propertio.developer.model.Project
import com.propertio.developer.model.StatusProject
import com.propertio.developer.project.form.ProjectFormActivity
import com.propertio.developer.project.list.ProjectAdapter
import com.propertio.developer.project.viewmodel.ProjectTabButtonViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create


class ProjectFragment : Fragment() {
    private lateinit var projectViewModel: ProjectViewModel
    private lateinit var projectAdapter: ProjectAdapter

    private var visibleThreshold : Int = 5
    private val cardHeight = 500
    private val cardsToLoadMore = 2
    private val loadHeighThreshold = cardHeight * cardsToLoadMore

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val binding by lazy { FragmentProjectBinding.inflate(layoutInflater) }

    // Tab Button
    private var currentStatus : Boolean = true
    private val tabButtonViewModel : ProjectTabButtonViewModel by activityViewModels()

    private val developerApi by lazy { Retro(TokenManager(requireContext()).token).getRetroClientInstance().create(DeveloperApi::class.java) }


    private val launcherAddProject = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            Log.d("ProjectFragment Launcher", "Result OK")

        }
    }

    private val launcherToRincian = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            Log.d("ProjectFragment Launcher", "Result OK")

        }
    }

    private val launcherToEdit = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            Log.d("ProjectFragment Launcher", "Result OK")

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = ProjectViewModelFactory(
            (requireActivity().application as PropertioDeveloperApplication).repository
        )
        projectViewModel = ViewModelProvider(requireActivity(), factory)[ProjectViewModel::class.java]



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        projectAdapter = ProjectAdapter(
            context = requireActivity(),
            onClickRincian = { data ->
                Log.d("ProjectAdapter", "Repost button clicked")
                val intentToDetailProject = Intent(context, ProjectDetailActivity::class.java)
                intentToDetailProject.putExtra(ProjectDetailActivity.PROJECT_ID, data.id)
                intentToDetailProject.putExtra("Property Type", data.propertyTypeName)
                launcherToRincian.launch(intentToDetailProject)
            },
            onClickMore = { data, button ->
                horizontalMoreButtonPopUp(data, button)
            },
            onClickRepost = {data ->
                developerApi.repostProject(data.id).enqueue(object : Callback<UpdateProjectResponse> {
                    override fun onResponse(
                        call: Call<UpdateProjectResponse>,
                        response: Response<UpdateProjectResponse>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("ProjectFragment", "onResponse: ${response.body()?.message}")
                        } else {
                            Log.d("ProjectFragment", "onResponse: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<UpdateProjectResponse>, t: Throwable) {
                        Log.e("ProjectFragment", "onFailure: ", t)
                    }

                })
            }
        )
        return binding.root
    }

    private fun horizontalMoreButtonPopUp(data: ProjectTable, button: View, TAG : String = "PopUpMoreButton") {
        val popupInflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = popupInflater.inflate(R.layout.dialog_pop_up_project, null)

        // Create the PopupWindow
        val popupWindow = PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)


        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true

        // Get the menu items
        val buttonEdit = popupView.findViewById<AppCompatButton>(R.id.button_edit_proyek_pop_up)
        val buttonRepost = popupView.findViewById<AppCompatButton>(R.id.button_repost_proyek_pop_up)
        val switchButton = popupView.findViewById<SwitchMaterial>(R.id.switch_proyek_pop_up)


        buttonEdit.setOnClickListener {
            Log.d(TAG, "horizontalMoreButtonPopUp: buttonEdit ${data.title}")

            val intentToEdit = Intent(context, ProjectFormActivity::class.java)
            intentToEdit.putExtra("EDIT_PROJECT", data.id)
            launcherToEdit.launch(intentToEdit)
            popupWindow.dismiss()
        }

        buttonRepost.setOnClickListener {
            popupWindow.dismiss()
            developerApi.repostProject(data.id).enqueue(object : Callback<UpdateProjectResponse> {
                override fun onResponse(
                    call: Call<UpdateProjectResponse>,
                    response: Response<UpdateProjectResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "onResponse: ${response.body()?.message}")
                    } else {
                        Log.d(TAG, "onResponse: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<UpdateProjectResponse>, t: Throwable) {
                    Log.e(TAG, "onFailure: ", t)
                }

            })


        }

        switchButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.d("ProjectFragment", "horizontalMoreButtonPopUp: switchButton ${data.title} is checked")
                switchButton.text = "Draf"
                developerApi.updateProjectStatus(data.id, StatusProject()).enqueue(object : Callback<UpdateProjectResponse> {
                    override fun onResponse(
                        call: Call<UpdateProjectResponse>,
                        response: Response<UpdateProjectResponse>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("ProjectFragment", "onResponse: ${response.body()?.message}")
                            setRecyclerListProject(currentStatus)
                        } else {
                            Log.d("ProjectFragment", "onResponse: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<UpdateProjectResponse>, t: Throwable) {
                        Log.e("ProjectFragment", "onFailure: ", t)
                    }

                })

            } else {
                Log.d("ProjectFragment", "horizontalMoreButtonPopUp: switchButton ${data.title} is unchecked")
                switchButton.text = "Tayangkan"
                developerApi.updateProjectStatus(data.id, StatusProject("active")).enqueue(object : Callback<UpdateProjectResponse> {
                    override fun onResponse(
                        call: Call<UpdateProjectResponse>,
                        response: Response<UpdateProjectResponse>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("ProjectFragment", "onResponse: ${response.body()?.message}")
                            setRecyclerListProject(currentStatus)
                        } else {
                            Log.d("ProjectFragment", "onResponse: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<UpdateProjectResponse>, t: Throwable) {
                        Log.e("ProjectFragment", "onFailure: ", t)
                    }

                })

            }


        }


        // Show the PopupWindow below the button
        popupWindow.showAsDropDown(button)

        popupView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Remove the global layout listener
                popupView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Calculate the x-offset
                val xOffset = button.width - popupView.width

                // Update the location of the PopupWindow
                popupWindow.update(button, xOffset, 0, -1, -1)
            }
        })
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        visibleThreshold = 5
        _isLoading.value = true

        with(binding) {
            fabAddProject.setOnClickListener {
                val intent = Intent(requireContext(), ProjectFormActivity::class.java)
                launcherAddProject.launch(intent)
            }

            if (isLoading.value == true) {
                progressBarProject.visibility = View.VISIBLE
            } else {
                progressBarProject.visibility = View.GONE
            }


        }

        // Tab Button
        binding.buttonAktif.setOnClickListener {
            Log.d("ProjectFragment", "onViewCreated: buttonAktif")
            tabButtonViewModel.tabActiveStatus.value = true
        }
        binding.buttonDraf.setOnClickListener {
            Log.d("ProjectFragment", "onViewCreated: buttonDraft")
            tabButtonViewModel.tabActiveStatus.value = false

        }

        lifecycleScope.launch {

            if (tabButtonViewModel.isActive()) {
                setTabToActive()

            } else {
                setTabToDraf()
            }



            tabButtonViewModel.tabActiveStatus.observe(viewLifecycleOwner) {
                if (it) {
                    setTabToActive()
                } else {
                    setTabToDraf()
                }
            }


            binding.buttonSearch.setOnClickListener {
                val filter = binding.textEditSearchFilter.text.toString()
                visibleThreshold = 5
                setRecyclerListProject(currentStatus, filter)
            }

        }



    }

    private fun setTabToDraf() {
        Log.w("ProjectFragment", "setTabToDraf: ")
        binding.buttonAktif.isActivated = false
        binding.buttonAktif.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.buttonDraf.isActivated = true
        binding.buttonDraf.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))


        currentStatus = false
        setRecyclerListProject(currentStatus)
    }

    private fun setTabToActive() {
        Log.w("ProjectFragment", "setTabToActive: ")
        binding.buttonAktif.isActivated = true
        binding.buttonAktif.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.buttonDraf.isActivated = false
        binding.buttonDraf.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))


        currentStatus = true
        setRecyclerListProject(currentStatus)
    }



    private fun setRecyclerListProject(status: Boolean, filter : String = "") {

        binding.recylerViewProjects.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = projectAdapter

            lifecycleScope.launch {
                loadProjects(status, filter)
            }

        }

        binding.nestedScrollViewProjectFragment.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->

                val isAtBottom = scrollY >= (v.getChildAt(0).measuredHeight - v.measuredHeight)
                val isNearBottom = scrollY >= (v.getChildAt(0).measuredHeight - v.measuredHeight - loadHeighThreshold)

                if (!v.canScrollVertically(1) && isAtBottom || isNearBottom) {
                    Log.d("ProjectFragment", "onViewCreated: isAtBottom: $isAtBottom , isNearBottom: $isNearBottom")
                    if (!isLoading.value!!) {
                        Log.d("ProjectFragment", "onViewCreated: isLoading: ${isLoading.value}")
                        _isLoading.value = true
                        visibleThreshold += 3

                        binding.progressBarProject.visibility = View.VISIBLE

                        lifecycleScope.launch {
                            loadProjects(status, filter)
                        }
                    }
                }

            }
        )

    }


    private suspend fun loadProjects(status : Boolean, filter : String = "") {
        withContext(Dispatchers.Main) {
            Log.d("ProjectFragment", "loadProjects: visibleThreshold: $visibleThreshold")
            val projects = withContext(Dispatchers.IO) {
                projectViewModel.allProjectsPaginated(visibleThreshold, 0, status, filter)
            }
            projects.observe(viewLifecycleOwner) { listProjects ->
                if (listProjects.isEmpty()) {
                    binding.frameLayoutBelumAdaProyek.visibility = View.VISIBLE
                } else {
                    binding.frameLayoutBelumAdaProyek.visibility = View.GONE
                }

                // Update the list of projects
                projectAdapter.submitList(listProjects)

                _isLoading.value = false
                binding.progressBarProject.visibility = View.GONE
            }
        }

    }


}