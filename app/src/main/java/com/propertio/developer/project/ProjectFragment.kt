package com.propertio.developer.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.propertio.developer.databinding.FragmentProjectBinding
import com.propertio.developer.profile.ProfileFragment


private const val APPBARTITLE_PROJECT = "param1"
class ProjectFragment : Fragment() {
    private var appBarTitle: String? = null

    private lateinit var binding: FragmentProjectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            appBarTitle = it.getString(APPBARTITLE_PROJECT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProjectBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        with(binding.toolbarContainer) {
//            textViewTitle.text = "Proyek Saya"
//        }
    }
    
    companion object {
        @JvmStatic
        fun newInstance(appBarTitle: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(APPBARTITLE_PROJECT, appBarTitle)
                }
            }
    }


}