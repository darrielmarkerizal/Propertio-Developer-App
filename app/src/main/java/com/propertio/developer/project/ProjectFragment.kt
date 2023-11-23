package com.propertio.developer.project


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.propertio.developer.PropertioDeveloperApplication
import com.propertio.developer.TokenManager
import com.propertio.developer.databinding.FragmentProjectBinding
import com.propertio.developer.project.list.ProjectAdapter


class ProjectFragment : Fragment() {
    private lateinit var projectViewModel: ProjectViewModel
    private lateinit var projectAdapter: ProjectAdapter
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var tokenManager: TokenManager

    private val binding by lazy {
        FragmentProjectBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(requireContext())
        val factory = ProjectViewModelFactory(
            (requireActivity().application as PropertioDeveloperApplication).repository
        )
        projectViewModel = ViewModelProvider(requireActivity(), factory)[ProjectViewModel::class.java]

        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                Log.d("ProjectFragment Launcher", "Result OK")

            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerListProject()


    }



    private fun setRecyclerListProject() {
        projectAdapter = ProjectAdapter(requireContext())

        binding.recylerViewProjects.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = projectAdapter
        }

        projectViewModel.projectList.observe(viewLifecycleOwner) { projects ->
            if (projects.isEmpty()) {
                binding.frameLayoutBelumAdaProyek.visibility = View.VISIBLE
            } else {
                binding.frameLayoutBelumAdaProyek.visibility = View.GONE
            }

            // Update the list of projects
            projectAdapter.submitList(projects)
        }
    }








}