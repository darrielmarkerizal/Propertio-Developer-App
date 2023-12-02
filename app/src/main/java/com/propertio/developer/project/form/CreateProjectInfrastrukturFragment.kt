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
import androidx.fragment.app.Fragment
import com.propertio.developer.databinding.FragmentCreateProjectInfrastrukturBinding


class CreateProjectInfrastrukturFragment : Fragment() {

    private val binding by lazy {
        FragmentCreateProjectInfrastrukturBinding.inflate(layoutInflater)
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("CreateProjectInfrastrukturFragment", "onViewCreated: RESULT_OK")
        }

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

        with(binding) {
            buttonUnggahProjectInfrastruktur.setOnClickListener {

                // go to CreateProjectTambahInfrastrukturFragment
                val intent = Intent(requireContext(), CreateProjectTambahInfrastrukturFragment::class.java)

                // launcher start acitivity
                launcher.launch(intent)
            }
        }
    }
}