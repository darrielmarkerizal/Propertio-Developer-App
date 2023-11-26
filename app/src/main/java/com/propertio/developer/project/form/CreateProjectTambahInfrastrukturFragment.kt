package com.propertio.developer.project.form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.propertio.developer.databinding.FragmentCreateProjectTambahInfrastrukturBinding

/**
 * A simple [Fragment] subclass.
 * Use the [CreateProjectTambahInfrastrukturFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateProjectTambahInfrastrukturFragment : Fragment() {

    private val binding by lazy {
        FragmentCreateProjectTambahInfrastrukturBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }
}