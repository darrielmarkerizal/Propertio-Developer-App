package com.propertio.developer.project


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.propertio.developer.Database.projectViewModel
import com.propertio.developer.databinding.FragmentProjectBinding
import com.propertio.developer.project.list.ProjectAdapter


class ProjectFragment : Fragment() {
    val token: String?
        get() = requireActivity().getSharedPreferences("account_data", Context.MODE_PRIVATE).getString("token", null)


    private val binding by lazy {
        FragmentProjectBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        projectViewModel = ViewModelProvider(this, ProjectViewModelFactory(token!!))[ProjectViewModel::class.java]

        projectViewModel.fetchProjects(token!!)

        val projectAdapter = ProjectAdapter(requireContext(), mutableListOf())

        with(binding) {
            recylerViewProjects.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = projectAdapter
            }
        }


        Log.d("Project", "Before observing projectList")
        projectViewModel.projectList.observe(viewLifecycleOwner) { projects ->
            // Update the adapter
            projectAdapter.updateProjects(projects)
            Log.d("Project", "Project list: $projects")

            if (projects.isEmpty()) {
                binding.frameLayoutBelumAdaProyek.visibility = View.VISIBLE
            } else {
                binding.frameLayoutBelumAdaProyek.visibility = View.GONE
            }
        }


    }








}