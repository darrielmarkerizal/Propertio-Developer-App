package com.propertio.developer.project.form

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.propertio.developer.api.models.GeneralType
import com.propertio.developer.database.MasterDataDeveloperPropertio
import com.propertio.developer.databinding.FragmentCreateProjectInformasiUmumBinding
import com.propertio.developer.dialog.CertificateTypeSheetFragment
import com.propertio.developer.dialog.PropertyTypeSheetFragment
import com.propertio.developer.dialog.viewmodel.CertificateTypeSpinnerViewModel
import com.propertio.developer.dialog.viewmodel.PropertyTypeSpinnerViewModel
import com.propertio.developer.project.viewmodel.ProjectInformationLocationViewModel


class CreateProjectInformasiUmumFragment : Fragment() {

    private val binding by lazy {
        FragmentCreateProjectInformasiUmumBinding.inflate(layoutInflater)
    }
    private val formActivity by lazy { activity as ProjectFormActivity }
    private val activityBinding by lazy { formActivity.binding }

    // ViewModels
    private val projectInformationLocationViewModel : ProjectInformationLocationViewModel by activityViewModels()

    // Spinner
    private var isPropertyTypeSpinnerSelected = false
    private val propertyTypeViewModel by lazy { ViewModelProvider(requireActivity())[PropertyTypeSpinnerViewModel::class.java] }

    private var isCertificateTypeSpinnerSelected = false
    private val certificateTypeViewModel by lazy { ViewModelProvider(requireActivity())[CertificateTypeSpinnerViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Spinner
        propertyTypeSpinner()
        certificateTypeSpinner()

        projectInformationLocationViewModel.isAlreadyUploaded.observe(viewLifecycleOwner) {
            if (it) {
                Log.d("CreateProjectInformasiUmumFragment", "onViewCreated Updated: $it")
                loadTextData()
                projectInformationLocationViewModel.isUploaded = it
            }
        }




        activityBinding.floatingButtonBack.setOnClickListener {
            formActivity.onBackButtonProjectManagementClick()
        }

        activityBinding.floatingButtonNext.setOnClickListener {
            if (binding.editHeadlineProject.text.toString().isEmpty()) {
                binding.editHeadlineProject.error = "Harap isi Headline Project"
                binding.editHeadlineProject.requestFocus()
                return@setOnClickListener
            }
            if (binding.editJudulProject.text.toString().isEmpty()) {
                binding.editJudulProject.error = "Harap isi Deskripsi Project"
                binding.editJudulProject.requestFocus()
                return@setOnClickListener
            }
            if (propertyTypeViewModel.propertyTypeData.value?.id == null) {
                binding.spinnerTipeProject.error = "Harap isi Deskripsi Project"
                return@setOnClickListener
            }
            if (certificateTypeViewModel.certificateTypeData.value?.toDb == null) {
                binding.spinnerSertifikatProject.error = "Harap isi Luas Tanah"
                return@setOnClickListener
            }
            if (binding.editDeskripsiProject.text.toString().isEmpty()) {
                binding.editDeskripsiProject.error = "Harap isi Deskripsi Project"
                binding.editDeskripsiProject.requestFocus()
                return@setOnClickListener
            }

            // save data in viewModel
            projectInformationLocationViewModel.headline = binding.editHeadlineProject.text.toString().trim()
            projectInformationLocationViewModel.title = binding.editJudulProject.text.toString().trim()
            projectInformationLocationViewModel.propertyTypeId = propertyTypeViewModel.propertyTypeData.value?.id!!
            projectInformationLocationViewModel.description = binding.editDeskripsiProject.text.toString().trim()
            projectInformationLocationViewModel.completedAt = binding.editTahunProject.text.toString()
            projectInformationLocationViewModel.certificate = certificateTypeViewModel.certificateTypeData.value?.toDb!!


            formActivity.onNextButtonProjectManagementClick()
        }

    }


    private fun loadTextData() {
        projectInformationLocationViewModel.printLog()
        binding.editHeadlineProject.setText(projectInformationLocationViewModel.headline)
        binding.editJudulProject.setText(projectInformationLocationViewModel.title)
        binding.editDeskripsiProject.setText(projectInformationLocationViewModel.description)
        binding.editTahunProject.setText(projectInformationLocationViewModel.completedAt)
        if (projectInformationLocationViewModel.propertyTypeName != null) {
            propertyTypeViewModel.propertyTypeData.postValue(
                GeneralType(
                    id = projectInformationLocationViewModel.propertyTypeId!!,
                    name = projectInformationLocationViewModel.propertyTypeName!!
                )
            )
        }
        if (projectInformationLocationViewModel.certificate != null) {
            val listCertificate = MasterDataDeveloperPropertio.certificate
            val certificate = listCertificate.find { it.toDb == projectInformationLocationViewModel.certificate }
            certificateTypeViewModel.certificateTypeData.postValue(certificate)
        }
    }





    private fun certificateTypeSpinner() {
        binding.spinnerSertifikatProject.setOnClickListener {
            CertificateTypeSheetFragment().show(parentFragmentManager, "PropertyTypeSheetFragment")
            Log.d("CreateProjectInformasiUmumFragment", "propertyTypeSpinner: $isCertificateTypeSpinnerSelected")
        }

        certificateTypeViewModel.certificateTypeData.observe(viewLifecycleOwner) {
            binding.spinnerSertifikatProject.text = it.toUser
            binding.spinnerSertifikatProject.error = null

            isCertificateTypeSpinnerSelected = true
            Log.d("CreateProjectInformasiUmumFragment", "propertyTypeSpinner: $isCertificateTypeSpinnerSelected")
        }
    }

    private fun propertyTypeSpinner() {
        binding.spinnerTipeProject.setOnClickListener {
            PropertyTypeSheetFragment().show(parentFragmentManager, "PropertyTypeSheetFragment")
            Log.d("CreateProjectInformasiUmumFragment", "propertyTypeSpinner: $isPropertyTypeSpinnerSelected")
        }

        propertyTypeViewModel.propertyTypeData.observe(viewLifecycleOwner) {
            binding.spinnerTipeProject.text = it.name
            binding.spinnerTipeProject.error = null

            isPropertyTypeSpinnerSelected = true
            Log.d("CreateProjectInformasiUmumFragment", "propertyTypeSpinner: $isPropertyTypeSpinnerSelected")
        }
    }


}