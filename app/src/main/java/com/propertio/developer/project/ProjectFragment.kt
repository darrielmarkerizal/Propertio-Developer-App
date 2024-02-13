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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.switchmaterial.SwitchMaterial
import com.propertio.developer.PropertioDeveloperApplication
import com.propertio.developer.R
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.developer.DeveloperApi
import com.propertio.developer.api.developer.projectmanagement.UpdateProjectResponse
import com.propertio.developer.database.project.ProjectTable
import com.propertio.developer.databinding.FragmentProjectBinding
import com.propertio.developer.model.StatusProject
import com.propertio.developer.project.form.ProjectFormActivity
import com.propertio.developer.project.list.ProjectAdapter
import com.propertio.developer.project.viewmodel.ProjectTabButtonViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProjectFragment : Fragment() {
    private lateinit var projectViewModel: ProjectViewModel
    private val projectAdapter by lazy {
        ProjectAdapter(
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
                            lifecycleScope.launch {
                                forceRefreshRecyclerListAdapter()
                            }

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
    }

    private fun forceRefreshRecyclerListAdapter() {
        lifecycleScope.launch {
            projectAdapter.submitList(emptyList())
            refreshRecyclerListAdapter()
        }
    }

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
            refreshRecyclerListAdapter()
        }
    }

    private fun refreshRecyclerListAdapter() {
        lifecycleScope.launch(Dispatchers.IO) {
            Log.d("ProjectFragment", "refreshRecyclerListAdapter: ")
            projectViewModel.fetchLiteProject(TokenManager(requireContext()).token!!)

            loadProjects(true)
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




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        switchButton.isChecked = if (data.status == "active") {
            switchButton.text = "Tayangkan"
            false
        } else {
            switchButton.text = "Draf"
            true
        }

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
                        forceRefreshRecyclerListAdapter()
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
                            setRecyclerListProject()
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
                            setRecyclerListProject()
                        } else {
                            Log.d("ProjectFragment", "onResponse: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<UpdateProjectResponse>, t: Throwable) {
                        Log.e("ProjectFragment", "onFailure: ", t)
                    }

                })

            }
            refreshRecyclerListAdapter()


        }

        val dpValue = 111 // width in dp
        val scale = resources.displayMetrics.density
        val px =0 - (dpValue * scale + 0.5f).toInt()
        Log.d(TAG, "horizontalMoreButtonPopUp: px: $px")
        popupWindow.showAsDropDown(button, px, 0)

        popupView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Remove the global layout listener
                popupView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Calculate the x-offset
                val xOffset = button.width - popupView.width

                popupWindow.update(button, xOffset, 0, -1, -1)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        refreshRecyclerListAdapter()

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        visibleThreshold = 5
        _isLoading.value = true

        val factory = ProjectViewModelFactory(
            (requireActivity().application as PropertioDeveloperApplication).repository
        )
        projectViewModel = ViewModelProvider(requireActivity(), factory)[ProjectViewModel::class.java]

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                setRecyclerListProject()
            }
        }


        with(binding) {
            fabAddProject.setOnClickListener {
                val intent = Intent(requireActivity(), ProjectFormActivity::class.java)
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

        tabButtonViewModel.tabActiveStatus.observe(viewLifecycleOwner) {
            if (it) {
                setTabToActive()
            } else {
                setTabToDraf()
            }
        }


        binding.textEditSearchFilter.doAfterTextChanged { text ->
            if (text.toString().isEmpty()) {
                visibleThreshold = 5
                setRecyclerListProject()
            } else {
                visibleThreshold = 5
                loadProjects()
            }
        }

        binding.textInputSearchLayout.setEndIconOnClickListener {
            binding.textEditSearchFilter.text?.clear()
            visibleThreshold = 5
            setRecyclerListProject()

            binding.textEditSearchFilter.clearFocus()
        }

        binding.swipeRefreshLayoutProjectList.setOnRefreshListener {
            lifecycleScope.launch {
                refreshRecyclerListAdapter()
                binding.swipeRefreshLayoutProjectList.isRefreshing = false
            }
        }



    }

    private fun setTabToDraf() {
        Log.w("ProjectFragment", "setTabToDraf: ")
        binding.buttonAktif.isActivated = false
        binding.buttonAktif.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.buttonDraf.isActivated = true
        binding.buttonDraf.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))


        lifecycleScope.launch {
            currentStatus = false
            loadProjects(true)
        }
    }

    private fun setTabToActive() {
        Log.w("ProjectFragment", "setTabToActive: ")
        binding.buttonAktif.isActivated = true
        binding.buttonAktif.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.buttonDraf.isActivated = false
        binding.buttonDraf.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))


        lifecycleScope.launch {
            currentStatus = true
            loadProjects(true)
        }
    }



    private fun setRecyclerListProject() {

        binding.recylerViewProjects.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = projectAdapter

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

                        loadProjects()

                    }
                }

            }
        )

    }

    private inline val searchFilter : String
        get() = binding.textEditSearchFilter.text.toString().trim()


    private fun loadProjects(forceUpdate : Boolean = false) {
        lifecycleScope.launch{
            Log.d("ProjectFragment", "loadProjects: visibleThreshold: $visibleThreshold")
            val projects = withContext(Dispatchers.IO) {
                projectViewModel.allProjectsPaginated(visibleThreshold, 0, currentStatus, searchFilter).asFlow().first()
            }
            if (projects.isEmpty()) {
                binding.frameLayoutBelumAdaProyek.visibility = View.VISIBLE
            } else {
                binding.frameLayoutBelumAdaProyek.visibility = View.GONE
            }

            if (forceUpdate) {
                projectAdapter.submitList(emptyList())
            }

            projectAdapter.submitList(projects)

            _isLoading.value = false
            binding.progressBarProject.visibility = View.GONE
            Log.d("ProjectFragment", "loadProjects: ${projects.size} ${projectAdapter.currentList.size}")
        }
    }


}